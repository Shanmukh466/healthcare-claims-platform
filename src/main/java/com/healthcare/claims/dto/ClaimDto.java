package com.healthcare.claims.dto;

import com.healthcare.claims.model.Claim;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ClaimDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubmitRequest {

        @NotBlank(message = "Patient ID is required")
        private String patientId;

        @NotBlank(message = "Patient name is required")
        private String patientName;

        @NotBlank(message = "Provider ID is required")
        private String providerId;

        @NotBlank(message = "Provider name is required")
        private String providerName;

        @NotBlank(message = "Insurance member ID is required")
        private String insuranceMemberId;

        @NotBlank(message = "Diagnosis code is required")
        private String diagnosisCode;

        @NotBlank(message = "Procedure code is required")
        private String procedureCode;

        @NotNull(message = "Billed amount is required")
        @DecimalMin(value = "0.01", message = "Billed amount must be greater than 0")
        private BigDecimal billedAmount;

        @NotNull(message = "Service date is required")
        @PastOrPresent(message = "Service date cannot be in the future")
        private LocalDate serviceDate;

        @NotNull(message = "Claim type is required")
        private Claim.ClaimType claimType;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private UUID id;
        private String claimNumber;
        private String patientId;
        private String patientName;
        private String providerName;
        private String diagnosisCode;
        private String procedureCode;
        private BigDecimal billedAmount;
        private BigDecimal approvedAmount;
        private LocalDate serviceDate;
        private Claim.ClaimStatus status;
        private Claim.ClaimType claimType;
        private String rejectionReason;
        private LocalDateTime createdAt;
        private LocalDateTime processedAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProcessRequest {
        @NotNull
        private UUID claimId;

        @NotNull
        private Claim.ClaimStatus newStatus;

        private BigDecimal approvedAmount;

        private String rejectionReason;
    }
}
