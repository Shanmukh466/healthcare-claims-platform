package com.healthcare.claims.repository;

import com.healthcare.claims.model.Claim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, UUID> {

    Optional<Claim> findByClaimNumber(String claimNumber);

    Page<Claim> findByPatientId(String patientId, Pageable pageable);

    Page<Claim> findByStatus(Claim.ClaimStatus status, Pageable pageable);

    Page<Claim> findByProviderId(String providerId, Pageable pageable);

    List<Claim> findByStatusAndServiceDateBetween(
        Claim.ClaimStatus status,
        LocalDate startDate,
        LocalDate endDate
    );

    @Query("SELECT c FROM Claim c WHERE c.patientId = :patientId AND c.status = :status")
    List<Claim> findByPatientIdAndStatus(
        @Param("patientId") String patientId,
        @Param("status") Claim.ClaimStatus status
    );

    @Query("SELECT COUNT(c) FROM Claim c WHERE c.status = :status")
    long countByStatus(@Param("status") Claim.ClaimStatus status);

    @Query("SELECT SUM(c.billedAmount) FROM Claim c WHERE c.serviceDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal sumBilledAmountBetweenDates(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
