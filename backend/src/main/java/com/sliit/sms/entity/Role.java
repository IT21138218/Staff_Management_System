package com.sliit.sms.entity;

/**
 * The five user roles defined in the project proposal (Major Stakeholders / Users).
 * Each maps to a Spring Security authority as "ROLE_<name>".
 */
public enum Role {
    ADMIN,            // System Administrator - configures system, manages accounts/roles
    HR_MANAGER,       // Manages employee master data, leave/performance oversight, HR reports
    SUPERVISOR,       // Department Supervisor - schedules, approves attendance/leave, appraisals
    PAYROLL_OFFICER,  // Computes salaries, deductions, bonuses, payslips
    EMPLOYEE          // Updates own profile, clocks in/out, applies for leave, views payslips
}
