package com.sliit.sms.controller;

import com.sliit.sms.entity.Attendance;
import com.sliit.sms.entity.Employee;
import com.sliit.sms.entity.Role;
import com.sliit.sms.entity.User;
import com.sliit.sms.service.AttendanceService;
import com.sliit.sms.service.EmployeeService;
import com.sliit.sms.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * FR3 - Attendance Management. Employees clock in/out through the portal;
 * Supervisors, HR Manager and Admin monitor attendance across the team/org.
 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;
    private final SecurityUtil securityUtil;

    @GetMapping("/my")
    public Map<String, Object> my() {
        Employee employee = currentEmployee();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("today", attendanceService.findToday(employee.getId()).orElse(null));
        response.put("history", attendanceService.findHistory(employee.getId()));
        return response;
    }

    @PostMapping("/clock-in")
    public Attendance clockIn() {
        Employee employee = currentEmployee();
        return attendanceService.clockIn(employee.getId());
    }

    @PostMapping("/clock-out")
    public Attendance clockOut() {
        Employee employee = currentEmployee();
        return attendanceService.clockOut(employee.getId());
    }

    /**
     * Team/organisation-wide view for Supervisor, HR Manager and Admin.
     * A Supervisor only ever sees their own reports; HR/Admin see everyone.
     */
    @GetMapping("/list")
    public List<Attendance> list(@RequestParam(required = false) String start,
                                  @RequestParam(required = false) String end) {
        LocalDate startDate = (start != null && !start.isBlank()) ? LocalDate.parse(start) : LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = (end != null && !end.isBlank()) ? LocalDate.parse(end) : LocalDate.now();

        User user = securityUtil.getCurrentUser();
        if (user.getRole() == Role.SUPERVISOR) {
            Employee supervisor = currentEmployee();
            var teamIds = employeeService.findBySupervisor(supervisor.getId()).stream().map(Employee::getId).toList();
            return attendanceService.findByDateRange(startDate, endDate).stream()
                    .filter(a -> teamIds.contains(a.getEmployee().getId()))
                    .toList();
        }
        return attendanceService.findByDateRange(startDate, endDate);
    }

    private Employee currentEmployee() {
        return employeeService.findByUsername(securityUtil.getCurrentUsername());
    }
}
