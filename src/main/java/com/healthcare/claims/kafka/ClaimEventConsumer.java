package com.healthcare.claims.kafka;

import com.healthcare.claims.dto.ClaimDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for processing claim events.
 * Listens to claim topics and handles downstream processing.
 */
@Component
@Slf4j
public class ClaimEventConsumer {

    @KafkaListener(
        topics = "${kafka.topics.claims-submitted}",
        groupId = "claims-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleClaimSubmitted(
        @Payload ClaimDto.Response claim,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
        @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("Received claim submitted event - claimNumber: {}, topic: {}, partition: {}, offset: {}",
            claim.getClaimNumber(), topic, partition, offset);

        // Trigger downstream processing — eligibility check, fraud detection, etc.
        processSubmittedClaim(claim);
    }

    @KafkaListener(
        topics = "${kafka.topics.claims-approved}",
        groupId = "claims-group"
    )
    public void handleClaimApproved(
        @Payload ClaimDto.Response claim,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic
    ) {
        log.info("Received claim approved event - claimNumber: {}", claim.getClaimNumber());
        // Trigger payment processing
        triggerPaymentProcessing(claim);
    }

    @KafkaListener(
        topics = "${kafka.topics.claims-rejected}",
        groupId = "claims-group"
    )
    public void handleClaimRejected(
        @Payload ClaimDto.Response claim,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic
    ) {
        log.info("Received claim rejected event - claimNumber: {}, reason: {}",
            claim.getClaimNumber(), claim.getRejectionReason());
        // Trigger notification to patient and provider
        triggerRejectionNotification(claim);
    }

    private void processSubmittedClaim(ClaimDto.Response claim) {
        log.debug("Processing submitted claim: {}", claim.getClaimNumber());
        // Eligibility verification, duplicate check, fraud detection would go here
    }

    private void triggerPaymentProcessing(ClaimDto.Response claim) {
        log.debug("Triggering payment for approved claim: {}", claim.getClaimNumber());
        // Payment processing logic would go here
    }

    private void triggerRejectionNotification(ClaimDto.Response claim) {
        log.debug("Sending rejection notification for claim: {}", claim.getClaimNumber());
        // Notification logic would go here
    }
}
