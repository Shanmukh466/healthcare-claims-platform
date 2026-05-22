package com.healthcare.claims.kafka;

import com.healthcare.claims.dto.ClaimDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka producer for publishing claim events to topics.
 * Uses idempotent production to prevent duplicate messages.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ClaimEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.claims-submitted}")
    private String claimsSubmittedTopic;

    @Value("${kafka.topics.claims-processed}")
    private String claimsProcessedTopic;

    @Value("${kafka.topics.claims-approved}")
    private String claimsApprovedTopic;

    @Value("${kafka.topics.claims-rejected}")
    private String claimsRejectedTopic;

    public void publishClaimSubmitted(ClaimDto.Response claim) {
        publishEvent(claimsSubmittedTopic, claim.getClaimNumber(), claim);
        log.info("Published claim submitted event for claimNumber: {}", claim.getClaimNumber());
    }

    public void publishClaimProcessed(ClaimDto.Response claim) {
        publishEvent(claimsProcessedTopic, claim.getClaimNumber(), claim);
        log.info("Published claim processed event for claimNumber: {}", claim.getClaimNumber());
    }

    public void publishClaimApproved(ClaimDto.Response claim) {
        publishEvent(claimsApprovedTopic, claim.getClaimNumber(), claim);
        log.info("Published claim approved event for claimNumber: {}", claim.getClaimNumber());
    }

    public void publishClaimRejected(ClaimDto.Response claim) {
        publishEvent(claimsRejectedTopic, claim.getClaimNumber(), claim);
        log.info("Published claim rejected event for claimNumber: {}", claim.getClaimNumber());
    }

    private void publishEvent(String topic, String key, Object payload) {
        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(topic, key, payload);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish event to topic {}: {}", topic, ex.getMessage());
            } else {
                log.debug("Event published to topic {} partition {} offset {}",
                    topic,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
            }
        });
    }
}
