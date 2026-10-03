package com.sliit.sms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * FR6 - Performance Management (appraisal half): scoring + written feedback
 * for an employee for a given review period, conducted by a Supervisor.
 */
@Entity
@Table(name = "performance_reviews")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @Column(nullable = false, length = 50)
    private String reviewPeriod; // e.g. "2026-H1"

    @Column(nullable = false)
    private Integer score; // 1-5 scale

    @Column(length = 1000)
    private String strengths;

    @Column(length = 1000)
    private String areasForImprovement;

    @Column(length = 1000)
    private String feedback;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.DRAFT;

    @Builder.Default
    private LocalDate reviewDate = LocalDate.now();
}
