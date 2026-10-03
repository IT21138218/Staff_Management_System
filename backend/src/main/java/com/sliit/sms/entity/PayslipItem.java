package com.sliit.sms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * A single line item (an allowance or a deduction) that makes up a payslip.
 * Built incrementally by PayslipBuilder.
 */
@Entity
@Table(name = "payslip_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PayslipItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Back-reference only; Payslip.items already carries this relationship for
    // the client, so this side is excluded from JSON to avoid an infinite
    // Payslip <-> PayslipItem serialization loop.
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payslip_id", nullable = false)
    private Payslip payslip;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayslipItemType type;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false)
    private Double amount;
}
