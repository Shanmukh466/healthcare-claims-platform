package com.healthcare.claims.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String memberId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false, unique = true)
    private String email;

    private String phoneNumber;

    private String address;

    @Column(nullable = false)
    private String insuranceProvider;

    @Column(nullable = false)
    private String policyNumber;

    @Column(nullable = false)
    private LocalDate policyStartDate;

    private LocalDate policyEndDate;

    @Enumerated(EnumType.STRING)
    private InsurancePlan insurancePlan;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum InsurancePlan {
        BRONZE, SILVER, GOLD, PLATINUM, MEDICAID, MEDICARE
    }
}
