package com.sliit.sms.controller;

import com.sliit.sms.dto.LeaveDecisionForm;
import com.sliit.sms.dto.LeaveRequestForm;
import com.sliit.sms.entity.Employee;
import com.sliit.sms.entity.LeaveRequest;
import com.sliit.sms.entity.LeaveType;
import com.sliit.sms.entity.Role;
import com.sliit.sms.entity.User;
import com.sliit.sms.service.EmployeeService;
import com.sliit.sms.service.LeaveService;
import com.sliit.sms.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * FR4 - Leave Management. Employees apply; Supervisors & HR Manager approve/reject.
 */
@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;
    private final EmployeeService employeeService;
    private final SecurityUtil securityUtil;

    @GetMapping("/my")
    public Map<String, Object> my() {
        Employee employee = currentEmployee();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("requests", leaveService.findByEmployee(employee.getId()));
        response.put("balances", leaveService.findBalances(employee.getId(), Year.now().getValue()));
        return response;
    }

    @GetMapping("/types")
    public List<LeaveType> leaveTypes() {
        return leaveService.findAllLeaveTypes();
    }

    @PostMapping("/apply")
    public LeaveRequest apply(@Valid @RequestBody LeaveRequestForm form) {
        Employee employee = currentEmployee();
        return leaveService.apply(employee.getId(), form);
    }

    @PostMapping("/{id}/cancel")
    public void cancel(@PathVariable Long id) {
        Employee employee = currentEmployee();
        leaveService.cancel(id, employee.getId());
    }

    /**
     * A Supervisor sees only their team's pending requests; HR Manager sees
     * every pending request org-wide.
     */
    @GetMapping("/approvals")
    public List<LeaveRequest> approvals() {
        User user = securityUtil.getCurrentUser();
        if (user.getRole() == Role.SUPERVISOR) {
            Employee supervisor = currentEmployee();
            return leaveService.findPendingForSupervisor(supervisor.getId());
        }
        return leaveService.findAllPending();
    }

    @PostMapping("/decide")
    public LeaveRequest decide(@Valid @RequestBody LeaveDecisionForm form) {
        User user = securityUtil.getCurrentUser();
        return leaveService.decide(form.getLeaveRequestId(), form.isApprove(), form.getComments(), user.getId());
    }

    private Employee currentEmployee() {
        return employeeService.findByUsername(securityUtil.getCurrentUsername());
    }
}
