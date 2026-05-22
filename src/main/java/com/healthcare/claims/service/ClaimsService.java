package com.healthcare.claims.service;

import com.healthcare.claims.dto.ClaimDto;
import com.healthcare.claims.exception.ClaimNotFoundException;
import com.healthcare.claims.exception.DuplicateClaimException;
import com.healthcare.claims.kafka.ClaimEventProducer;
import com.healthcare.claims.model.Claim;
import com.healthcare.claims.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Core claims processing service.
 * Handles claim submission, processing, and status management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClaimsService {

    private final ClaimRepository claimRepository;
    private final ClaimEventProducer claimEventProducer;

    /**
     * Submit a new healthcare claim.
     * Validates for duplicates, generates claim number, and publishes to Kafka.
     */
    public ClaimDto.Response submitClaim(ClaimDto.SubmitRequest request) {
        log.info("Submitting claim for patient: {}", request.getPatientId());

        // Generate unique claim number
        String claimNumber = generateClaimNumber();

        Claim claim = Claim.builder()
            .claimNumber(claimNumber)
            .patientId(request.getPatientId())
            .patientName(request.getPatientName())
            .providerId(request.getProviderId())
            .providerName(request.getProviderName())
            .insuranceMemberId(request.getInsuranceMemberId())
            .diagnosisCode(request.getDiagnosisCode())
            .procedureCode(request.getProcedureCode())
            .billedAmount(request.getBilledAmount())
            .serviceDate(request.getServiceDate())
            .claimType(request.getClaimType())
            .status(Claim.ClaimStatus.SUBMITTED)
            .auditTrail("Claim submitted at " + LocalDateTime.now())
            .build();

        Claim saved = claimRepository.save(claim);
        log.info("Claim saved with number: {}", claimNumber);

        ClaimDto.Response response = toResponse(saved);

        // Publish event to Kafka
        claimEventProducer.publishClaimSubmitted(response);

        return response;
    }

    /**
     * Process a claim — approve or reject with reason.
     */
    public ClaimDto.Response processClaim(ClaimDto.ProcessRequest request) {
        Claim claim = claimRepository.findById(request.getClaimId())
            .orElseThrow(() -> new ClaimNotFoundException("Claim not found: " + request.getClaimId()));

        log.info("Processing claim: {} -> status: {}", claim.getClaimNumber(), request.getNewStatus());

        claim.setStatus(request.getNewStatus());
        claim.setProcessedAt(LocalDateTime.now());
        claim.setAuditTrail(claim.getAuditTrail() + " | Status updated to "
            + request.getNewStatus() + " at " + LocalDateTime.now());

        if (request.getNewStatus() == Claim.ClaimStatus.APPROVED) {
            claim.setApprovedAmount(request.getApprovedAmount());
        }

        if (request.getNewStatus() == Claim.ClaimStatus.REJECTED) {
            claim.setRejectionReason(request.getRejectionReason());
        }

        Claim updated = claimRepository.save(claim);
        ClaimDto.Response response = toResponse(updated);

        // Publish event based on status
        if (request.getNewStatus() == Claim.ClaimStatus.APPROVED) {
            claimEventProducer.publishClaimApproved(response);
        } else if (request.getNewStatus() == Claim.ClaimStatus.REJECTED) {
            claimEventProducer.publishClaimRejected(response);
        } else {
            claimEventProducer.publishClaimProcessed(response);
        }

        return response;
    }

    @Transactional(readOnly = true)
    public ClaimDto.Response getClaimById(UUID id) {
        return claimRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new ClaimNotFoundException("Claim not found: " + id));
    }

    @Transactional(readOnly = true)
    public ClaimDto.Response getClaimByNumber(String claimNumber) {
        return claimRepository.findByClaimNumber(claimNumber)
            .map(this::toResponse)
            .orElseThrow(() -> new ClaimNotFoundException("Claim not found: " + claimNumber));
    }

    @Transactional(readOnly = true)
    public Page<ClaimDto.Response> getClaimsByPatient(String patientId, Pageable pageable) {
        return claimRepository.findByPatientId(patientId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ClaimDto.Response> getClaimsByStatus(Claim.ClaimStatus status, Pageable pageable) {
        return claimRepository.findByStatus(status, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ClaimDto.Response> getAllClaims(Pageable pageable) {
        return claimRepository.findAll(pageable).map(this::toResponse);
    }

    private String generateClaimNumber() {
        return "CLM-" + System.currentTimeMillis() + "-"
            + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ClaimDto.Response toResponse(Claim claim) {
        return ClaimDto.Response.builder()
            .id(claim.getId())
            .claimNumber(claim.getClaimNumber())
            .patientId(claim.getPatientId())
            .patientName(claim.getPatientName())
            .providerName(claim.getProviderName())
            .diagnosisCode(claim.getDiagnosisCode())
            .procedureCode(claim.getProcedureCode())
            .billedAmount(claim.getBilledAmount())
            .approvedAmount(claim.getApprovedAmount())
            .serviceDate(claim.getServiceDate())
            .status(claim.getStatus())
            .claimType(claim.getClaimType())
            .rejectionReason(claim.getRejectionReason())
            .createdAt(claim.getCreatedAt())
            .processedAt(claim.getProcessedAt())
            .build();
    }
}
