package com.sliit.sms.controller;

import com.sliit.sms.dto.RosterForm;
import com.sliit.sms.dto.ShiftForm;
import com.sliit.sms.entity.Employee;
import com.sliit.sms.entity.RosterAssignment;
import com.sliit.sms.entity.Shift;
import com.sliit.sms.service.EmployeeService;
import com.sliit.sms.service.ScheduleService;
import com.sliit.sms.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * FR7 - Shift & Roster Scheduling. Supervisors build the roster;
 * Employees view their shifts and request swaps.
 */
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final EmployeeService employeeService;
    private final SecurityUtil securityUtil;

    @GetMapping("/my")
    public List<RosterAssignment> my() {
        Employee employee = currentEmployee();
        return scheduleService.findForEmployee(employee.getId(), LocalDate.now(), LocalDate.now().plusDays(30));
    }

    @GetMapping("/colleagues")
    public List<Employee> colleagues() {
        Employee employee = currentEmployee();
        return employeeService.findAll().stream()
                .filter(e -> !e.getId().equals(employee.getId()))
                .toList();
    }

    @PostMapping("/swap-request")
    public RosterAssignment requestSwap(@RequestParam Long rosterAssignmentId, @RequestParam Long swapWithEmployeeId) {
        return scheduleService.requestSwap(rosterAssignmentId, swapWithEmployeeId);
    }

    /* ---------- Supervisor / Admin roster management ---------- */

    @GetMapping("/manage")
    public List<RosterAssignment> manage(@RequestParam(required = false) String start, @RequestParam(required = false) String end) {
        LocalDate startDate = (start != null && !start.isBlank()) ? LocalDate.parse(start) : LocalDate.now();
        LocalDate endDate = (end != null && !end.isBlank()) ? LocalDate.parse(end) : LocalDate.now().plusDays(7);
        return scheduleService.findAll(startDate, endDate);
    }

    @GetMapping("/manage/shifts")
    public List<Shift> shifts() {
        return scheduleService.findAllShifts();
    }

    @GetMapping("/manage/employees")
    public List<Employee> manageableEmployees() {
        return employeeService.findAll();
    }

    @PostMapping("/manage/assign")
    public RosterAssignment assign(@Valid @RequestBody RosterForm form) {
        return scheduleService.assign(form);
    }

    @PostMapping("/manage/shifts/new")
    public Shift newShift(@Valid @RequestBody ShiftForm form) {
        return scheduleService.createShift(form.getName(), form.getStartTime(), form.getEndTime());
    }

    @PutMapping("/manage/shifts/{id}")
    public Shift updateShift(@PathVariable Long id, @Valid @RequestBody ShiftForm form) {
        return scheduleService.updateShift(id, form.getName(), form.getStartTime(), form.getEndTime());
    }

    @DeleteMapping("/manage/shifts/{id}")
    public void deleteShift(@PathVariable Long id) {
        scheduleService.deleteShift(id);
    }

    @DeleteMapping("/manage/assign/{id}")
    public void unassign(@PathVariable Long id) {
        scheduleService.unassign(id);
    }

    @GetMapping("/manage/swaps")
    public List<RosterAssignment> pendingSwaps() {
        return scheduleService.findPendingSwaps();
    }

    @PostMapping("/manage/swaps/{id}/decide")
    public RosterAssignment decideSwap(@PathVariable Long id, @RequestParam boolean approve) {
        return scheduleService.decideSwap(id, approve);
    }

    private Employee currentEmployee() {
        return employeeService.findByUsername(securityUtil.getCurrentUsername());
    }
}
