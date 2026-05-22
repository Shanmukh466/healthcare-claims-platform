package com.healthcare.claims;

import com.healthcare.claims.dto.ClaimDto;
import com.healthcare.claims.model.Claim;
import com.healthcare.claims.repository.ClaimRepository;
import com.healthcare.claims.service.ClaimsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimsServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private com.healthcare.claims.kafka.ClaimEventProducer claimEventProducer;

    @InjectMocks
    private ClaimsService claimsService;

    private ClaimDto.SubmitRequest submitRequest;

    @BeforeEach
    void setUp() {
        submitRequest = ClaimDto.SubmitRequest.builder()
            .patientId("PAT-001")
            .patientName("John Doe")
            .providerId("PROV-001")
            .providerName("Dallas Medical Center")
            .insuranceMemberId("MBR-12345")
            .diagnosisCode("J06.9")
            .procedureCode("99213")
            .billedAmount(new BigDecimal("350.00"))
            .serviceDate(LocalDate.now().minusDays(5))
            .claimType(Claim.ClaimType.MEDICAL)
            .build();
    }

    @Test
    void submitClaim_shouldCreateClaimWithSubmittedStatus() {
        Claim savedClaim = Claim.builder()
            .claimNumber("CLM-TEST-001")
            .patientId("PAT-001")
            .patientName("John Doe")
            .providerId("PROV-001")
            .providerName("Dallas Medical Center")
            .insuranceMemberId("MBR-12345")
            .diagnosisCode("J06.9")
            .procedureCode("99213")
            .billedAmount(new BigDecimal("350.00"))
            .serviceDate(LocalDate.now().minusDays(5))
            .claimType(Claim.ClaimType.MEDICAL)
            .status(Claim.ClaimStatus.SUBMITTED)
            .build();

        when(claimRepository.save(any(Claim.class))).thenReturn(savedClaim);

        ClaimDto.Response response = claimsService.submitClaim(submitRequest);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Claim.ClaimStatus.SUBMITTED);
        assertThat(response.getPatientId()).isEqualTo("PAT-001");
        assertThat(response.getBilledAmount()).isEqualTo(new BigDecimal("350.00"));

        verify(claimRepository, times(1)).save(any(Claim.class));
        verify(claimEventProducer, times(1)).publishClaimSubmitted(any());
    }

    @Test
    void submitClaim_shouldPublishKafkaEvent() {
        Claim savedClaim = Claim.builder()
            .claimNumber("CLM-TEST-002")
            .patientId("PAT-001")
            .patientName("John Doe")
            .providerId("PROV-001")
            .providerName("Dallas Medical Center")
            .insuranceMemberId("MBR-12345")
            .diagnosisCode("J06.9")
            .procedureCode("99213")
            .billedAmount(new BigDecimal("350.00"))
            .serviceDate(LocalDate.now().minusDays(5))
            .claimType(Claim.ClaimType.MEDICAL)
            .status(Claim.ClaimStatus.SUBMITTED)
            .build();

        when(claimRepository.save(any(Claim.class))).thenReturn(savedClaim);

        claimsService.submitClaim(submitRequest);

        verify(claimEventProducer, times(1)).publishClaimSubmitted(any(ClaimDto.Response.class));
    }
}
