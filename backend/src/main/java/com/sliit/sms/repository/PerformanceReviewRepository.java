package com.sliit.sms.repository;

import com.sliit.sms.entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    List<PerformanceReview> findByEmployeeIdOrderByReviewDateDesc(Long employeeId);
    List<PerformanceReview> findByReviewer_IdOrderByReviewDateDesc(Long reviewerId);
}
