package com.sliit.sms.service.impl;

import com.sliit.sms.dto.GoalForm;
import com.sliit.sms.dto.PerformanceReviewForm;
import com.sliit.sms.entity.*;
import com.sliit.sms.exception.ResourceNotFoundException;
import com.sliit.sms.repository.*;
import com.sliit.sms.service.NotificationService;
import com.sliit.sms.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * FR6 - Performance Management. Primary user: Department Supervisors
 * (conduct appraisals, set goals); Employees self-view; HR oversees.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceServiceImpl implements PerformanceService {

    private final GoalRepository goalRepository;
    private final PerformanceReviewRepository performanceReviewRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public Goal createGoal(GoalForm form, Long createdByUserId) {
        Employee employee = employeeRepository.findById(form.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + form.getEmployeeId()));
        User createdBy = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + createdByUserId));

        Goal goal = Goal.builder()
                .employee(employee)
                .title(form.getTitle())
                .description(form.getDescription())
                .targetDate(form.getTargetDate())
                .status(GoalStatus.NOT_STARTED)
                .createdBy(createdBy)
                .build();

        Goal saved = goalRepository.save(goal);

        if (employee.getUser() != null) {
            notificationService.notify(employee.getUser(), "New performance goal set",
                    "A new goal has been set for you: " + goal.getTitle(), NotificationType.IN_APP);
        }
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Goal findGoalById(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found: " + id));
    }

    @Override
    public Goal updateGoal(Long id, GoalForm form) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found: " + id));
        goal.setTitle(form.getTitle());
        goal.setDescription(form.getDescription());
        goal.setTargetDate(form.getTargetDate());
        if (form.getStatus() != null) {
            goal.setStatus(GoalStatus.valueOf(form.getStatus()));
        }
        return goalRepository.save(goal);
    }

    @Override
    public void deleteGoal(Long id) {
        if (!goalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Goal not found: " + id);
        }
        goalRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Goal> findGoalsForEmployee(Long employeeId) {
        return goalRepository.findByEmployeeIdOrderByTargetDateAsc(employeeId);
    }

    @Override
    public PerformanceReview createReview(PerformanceReviewForm form, Long reviewerUserId) {
        Employee employee = employeeRepository.findById(form.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + form.getEmployeeId()));
        User reviewer = userRepository.findById(reviewerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + reviewerUserId));

        PerformanceReview review = PerformanceReview.builder()
                .employee(employee)
                .reviewer(reviewer)
                .reviewPeriod(form.getReviewPeriod())
                .score(form.getScore())
                .strengths(form.getStrengths())
                .areasForImprovement(form.getAreasForImprovement())
                .feedback(form.getFeedback())
                .status(ReviewStatus.SUBMITTED)
                .build();

        PerformanceReview saved = performanceReviewRepository.save(review);

        if (employee.getUser() != null) {
            notificationService.notify(employee.getUser(), "Performance review completed",
                    "Your performance review for " + form.getReviewPeriod() + " is now available.",
                    NotificationType.IN_APP);
        }
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public PerformanceReview findReviewById(Long id) {
        return performanceReviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Performance review not found: " + id));
    }

    @Override
    public PerformanceReview updateReview(Long id, PerformanceReviewForm form) {
        PerformanceReview review = performanceReviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Performance review not found: " + id));
        review.setReviewPeriod(form.getReviewPeriod());
        review.setScore(form.getScore());
        review.setStrengths(form.getStrengths());
        review.setAreasForImprovement(form.getAreasForImprovement());
        review.setFeedback(form.getFeedback());
        return performanceReviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long id) {
        if (!performanceReviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Performance review not found: " + id);
        }
        performanceReviewRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceReview> findReviewsForEmployee(Long employeeId) {
        return performanceReviewRepository.findByEmployeeIdOrderByReviewDateDesc(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerformanceReview> findReviewsByReviewer(Long reviewerUserId) {
        return performanceReviewRepository.findByReviewer_IdOrderByReviewDateDesc(reviewerUserId);
    }
}
