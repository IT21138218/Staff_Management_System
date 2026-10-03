package com.sliit.sms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * FR5 - Payroll Management.
 * Built via PayslipBuilder (common.patterns.builder) and populated using a
 * SalaryCalculationStrategy (common.patterns.strategy) chosen by PayrollService.
 */
@Entity
@Table(name = "payslips", uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "pay_month", "pay_year"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "pay_month", nullable = false)
    private Integer payMonth; // 1-12

    @Column(name = "pay_year", nullable = false)
    private Integer payYear;

    @Column(nullable = false)
    private Double basicSalary;

    @Column(nullable = false)
    private Double totalAllowances;

    @Column(nullable = false)
    private Double overtimePay;

    @Column(nullable = false)
    private Double totalDeductions;

    @Column(nullable = false)
    private Double tax;

    @Column(nullable = false)
    private Double netSalary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PayslipStatus status = PayslipStatus.GENERATED;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime generatedDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by")
    private User generatedBy;

    @OneToMany(mappedBy = "payslip", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PayslipItem> items = new ArrayList<>();
}
