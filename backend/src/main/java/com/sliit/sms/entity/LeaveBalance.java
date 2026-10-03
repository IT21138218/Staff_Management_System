package com.sliit.sms.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Tracks how many days of a given leave type an employee has left in a given year.
 * Updated automatically whenever a LeaveRequest is approved (see LeaveService).
 */
@Entity
@Table(name = "leave_balances", uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "leave_type_id", "balance_year"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    // Column named "balance_year" (not "year") because YEAR is a reserved
    // word in some SQL dialects (e.g. H2) and would fail unquoted DDL.
    @Column(name = "balance_year", nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Double allocatedDays;

    @Builder.Default
    @Column(nullable = false)
    private Double usedDays = 0.0;

    public Double getRemainingDays() {
        return allocatedDays - usedDays;
    }
}
