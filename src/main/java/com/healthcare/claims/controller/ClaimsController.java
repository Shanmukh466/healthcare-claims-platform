package com.healthcare.claims.controller;

import com.healthcare.claims.dto.ClaimDto;
import com.healthcare.claims.model.Claim;
import com.healthcare.claims.service.ClaimsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Claims", description = "Healthcare claims processing API")
@SecurityRequirement(name = "bearerAuth")
public class ClaimsController {

    private final ClaimsService claimsService;

    @PostMapping
    @Operation(summary = "Submit a new healthcare claim")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN', 'CLAIMS_PROCESSOR')")
    public ResponseEntity<ClaimDto.Response> submitClaim(
        @Valid @RequestBody ClaimDto.SubmitRequest request
    ) {
        log.info("Received claim submission for patient: {}", request.getPatientId());
        ClaimDto.Response response = claimsService.submitClaim(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/process")
    @Operation(summary = "Process a claim — approve or reject")
    @PreAuthorize("hasAnyRole('CLAIMS_PROCESSOR', 'ADMIN')")
    public ResponseEntity<ClaimDto.Response> processClaim(
        @Valid @RequestBody ClaimDto.ProcessRequest request
    ) {
        ClaimDto.Response response = claimsService.processClaim(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get claim by ID")
    @PreAuthorize("hasAnyRole('CLAIMS_PROCESSOR', 'ADMIN', 'AUDITOR')")
    public ResponseEntity<ClaimDto.Response> getClaimById(@PathVariable UUID id) {
        return ResponseEntity.ok(claimsService.getClaimById(id));
    }

    @GetMapping("/number/{claimNumber}")
    @Operation(summary = "Get claim by claim number")
    public ResponseEntity<ClaimDto.Response> getClaimByNumber(@PathVariable String claimNumber) {
        return ResponseEntity.ok(claimsService.getClaimByNumber(claimNumber));
    }

    @GetMapping
    @Operation(summary = "Get all claims with pagination")
    @PreAuthorize("hasAnyRole('CLAIMS_PROCESSOR', 'ADMIN', 'AUDITOR')")
    public ResponseEntity<Page<ClaimDto.Response>> getAllClaims(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "DESC") String direction
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        PageRequest pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(claimsService.getAllClaims(pageable));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get claims by patient ID")
    public ResponseEntity<Page<ClaimDto.Response>> getClaimsByPatient(
        @PathVariable String patientId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(claimsService.getClaimsByPatient(patientId, pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get claims by status")
    @PreAuthorize("hasAnyRole('CLAIMS_PROCESSOR', 'ADMIN')")
    public ResponseEntity<Page<ClaimDto.Response>> getClaimsByStatus(
        @PathVariable Claim.ClaimStatus status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(claimsService.getClaimsByStatus(status, pageable));
    }
}
