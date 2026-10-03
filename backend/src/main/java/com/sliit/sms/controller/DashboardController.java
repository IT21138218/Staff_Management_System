package com.sliit.sms.controller;

import com.sliit.sms.entity.*;
import com.sliit.sms.service.*;
import com.sliit.sms.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Single role-aware endpoint. Rather than one response shape for every role,
 * this returns only the numbers/lists the current user's role actually needs
 * (mirrors the old Thymeleaf dashboard.html's th:if blocks, just as JSON).
 */
@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final SecurityUtil securityUtil;
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final LeaveService leaveService;
    private final AttendanceService attendanceService;
    private final PayrollService payrollService;
    private final ScheduleService scheduleService;

    @GetMapping("/api/dashboard")
    public Map<String, Object> dashboard() {
        User user = securityUtil.getCurrentUser();
        Employee employee = safeFindEmployee(user.getUsername());

        Map<String, Object> model = new LinkedHashMap<>();
        model.put("employee", employee);
        model.put("role", user.getRole().name());

        switch (user.getRole()) {
            case ADMIN -> {
                model.put("totalEmployees", employeeService.findAll().size());
                model.put("totalDepartments", departmentService.findAll().size());
                model.put("pendingLeaveCount", leaveService.findAllPending().size());
            }
            case HR_MANAGER -> {
                model.put("totalEmployees", employeeService.findAll().size());
                model.put("activeEmployees", employeeService.search(null, null, EmployeeStatus.ACTIVE).size());
                model.put("pendingLeaveCount", leaveService.findAllPending().size());
            }
            case SUPERVISOR -> {
                if (employee != null) {
                    List<Employee> team = employeeService.findBySupervisor(employee.getId());
                    model.put("teamSize", team.size());
                    model.put("pendingLeaveForTeam", leaveService.findPendingForSupervisor(employee.getId()).size());
                    model.put("pendingSwaps", scheduleService.findPendingSwaps().size());
                }
            }
            case PAYROLL_OFFICER -> {
                LocalDate now = LocalDate.now();
                model.put("payslipsThisMonth", payrollService.findByPeriod(now.getMonthValue(), now.getYear()).size());
                model.put("activeEmployees", employeeService.search(null, null, EmployeeStatus.ACTIVE).size());
            }
            case EMPLOYEE -> {
                if (employee != null) {
                    model.put("todayAttendance", attendanceService.findToday(employee.getId()).orElse(null));
                    model.put("leaveBalances", leaveService.findBalances(employee.getId(), Year.now().getValue()));
                    model.put("myRecentLeave", leaveService.findByEmployee(employee.getId()).stream().limit(5).toList());
                    model.put("myUpcomingShifts",
                            scheduleService.findForEmployee(employee.getId(), LocalDate.now(), LocalDate.now().plusDays(14)));
                }
            }
        }

        return model;
    }

    private Employee safeFindEmployee(String username) {
        try {
            return employeeService.findByUsername(username);
        } catch (Exception e) {
            return null;
        }
    }
}
