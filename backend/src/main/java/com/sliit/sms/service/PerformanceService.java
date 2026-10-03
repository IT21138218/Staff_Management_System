package com.sliit.sms.service;

import com.sliit.sms.dto.GoalForm;
import com.sliit.sms.dto.PerformanceReviewForm;
import com.sliit.sms.entity.Goal;
import com.sliit.sms.entity.PerformanceReview;

import java.util.List;

public interface PerformanceService {
    Goal createGoal(GoalForm form, Long createdByUserId);
    Goal findGoalById(Long id);
    Goal updateGoal(Long id, GoalForm form);
    void deleteGoal(Long id);
    List<Goal> findGoalsForEmployee(Long employeeId);
    PerformanceReview createReview(PerformanceReviewForm form, Long reviewerUserId);
    PerformanceReview findReviewById(Long id);
    PerformanceReview updateReview(Long id, PerformanceReviewForm form);
    void deleteReview(Long id);
    List<PerformanceReview> findReviewsForEmployee(Long employeeId);
    List<PerformanceReview> findReviewsByReviewer(Long reviewerUserId);
}
