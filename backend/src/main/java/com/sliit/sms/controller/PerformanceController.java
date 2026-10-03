package com.sliit.sms.controller;

import com.sliit.sms.dto.GoalForm;
import com.sliit.sms.dto.PerformanceReviewForm;
import com.sliit.sms.entity.Employee;
import com.sliit.sms.entity.Goal;
import com.sliit.sms.entity.PerformanceReview;
import com.sliit.sms.entity.Role;
import com.sliit.sms.entity.User;
import com.sliit.sms.service.EmployeeService;
import com.sliit.sms.service.PerformanceService;
import com.sliit.sms.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * FR6 - Performance Management. Supervisors set goals & run appraisals;
 * Employees view their own goals/reviews; HR oversees.
 */
@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;
    private final EmployeeService employeeService;
    private final SecurityUtil securityUtil;

    @GetMapping("/employee/{id}")
    public Employee employeeSummary(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    @GetMapping("/goals")
    public List<Goal> goals(@RequestParam(required = false) Long employeeId) {
        Employee target = resolveTarget(employeeId);
        return performanceService.findGoalsForEmployee(target.getId());
    }

    @PostMapping("/goal/new")
    public Goal createGoal(@Valid @RequestBody GoalForm form) {
        User user = securityUtil.getCurrentUser();
        return performanceService.createGoal(form, user.getId());
    }

    @GetMapping("/goal/{id}")
    public Goal goal(@PathVariable Long id) {
        return performanceService.findGoalById(id);
    }

    @PutMapping("/goal/{id}")
    public Goal updateGoal(@PathVariable Long id, @Valid @RequestBody GoalForm form) {
        return performanceService.updateGoal(id, form);
    }

    @DeleteMapping("/goal/{id}")
    public void deleteGoal(@PathVariable Long id) {
        performanceService.deleteGoal(id);
    }

    @GetMapping("/reviews")
    public List<PerformanceReview> reviews(@RequestParam(required = false) Long employeeId) {
        Employee target = resolveTarget(employeeId);
        return performanceService.findReviewsForEmployee(target.getId());
    }

    @PostMapping("/review/new")
    public PerformanceReview createReview(@Valid @RequestBody PerformanceReviewForm form) {
        User user = securityUtil.getCurrentUser();
        return performanceService.createReview(form, user.getId());
    }

    @GetMapping("/review/{id}")
    public PerformanceReview review(@PathVariable Long id) {
        return performanceService.findReviewById(id);
    }

    @PutMapping("/review/{id}")
    public PerformanceReview updateReview(@PathVariable Long id, @Valid @RequestBody PerformanceReviewForm form) {
        return performanceService.updateReview(id, form);
    }

    @DeleteMapping("/review/{id}")
    public void deleteReview(@PathVariable Long id) {
        performanceService.deleteReview(id);
    }

    /** Employees always land on their own record; anyone else may specify employeeId. */
    private Employee resolveTarget(Long employeeId) {
        User user = securityUtil.getCurrentUser();
        if (user.getRole() == Role.EMPLOYEE || employeeId == null) {
            return employeeService.findByUsername(user.getUsername());
        }
        return employeeService.findById(employeeId);
    }
}
