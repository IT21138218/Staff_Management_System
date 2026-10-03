package com.sliit.sms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * FR7 - Shift & Roster Scheduling: assigns one employee to one shift on one date.
 * Supports the swap-request workflow described in the proposal (minor function:
 * employees can request a swap; a Supervisor approves/rejects it).
 */
@Entity
@Table(name = "roster_assignments", uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "roster_date"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RosterAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @Column(name = "roster_date", nullable = false)
    private LocalDate rosterDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SwapStatus swapStatus = SwapStatus.NONE;

    // The colleague this assignment has been proposed to swap with, if any
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swap_with_employee_id")
    private Employee swapWithEmployee;
}
