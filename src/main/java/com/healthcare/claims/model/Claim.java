package com.healthcare.claims.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a healthcare insurance claim.
 * Contains all HIPAA-relevant fields for claims processing.
 */
@Entity
@Table(name = "claims")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String claimNumber;

    @Column(nullable = false)
    private String patientId;

    @Column(nullable = false)
    private String patientName;

    @Column(nullable = false)
    private String providerId;

    @Column(nullable = false)
    private String providerName;

    @Column(nullable = false)
    private String insuranceMemberId;

    @Column(nullable = false)
    private String diagnosisCode;

    @Column(nullable = false)
    private String procedureCode;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal billedAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal approvedAmount;

    @Column(nullable = false)
    private LocalDate serviceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimType claimType;

    private String rejectionReason;

    private String auditTrail;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime processedAt;

    public enum ClaimStatus {
        SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, PAID, APPEALED
    }

    public enum ClaimType {
        MEDICAL, DENTAL, VISION, PHARMACY, MENTAL_HEALTH
    }
}
