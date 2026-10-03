package com.sliit.sms.service.impl;

import com.sliit.sms.dto.RosterForm;
import com.sliit.sms.entity.*;
import com.sliit.sms.exception.BusinessRuleException;
import com.sliit.sms.exception.ResourceNotFoundException;
import com.sliit.sms.repository.*;
import com.sliit.sms.service.NotificationService;
import com.sliit.sms.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * FR7 - Shift & Roster Scheduling. Primary user: Department Supervisors
 * build the roster; Employees view their shifts and request swaps.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    private final ShiftRepository shiftRepository;
    private final RosterAssignmentRepository rosterAssignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public List<Shift> findAllShifts() {
        return shiftRepository.findAll();
    }

    @Override
    public Shift createShift(String name, LocalTime start, LocalTime end) {
        return shiftRepository.save(Shift.builder().name(name).startTime(start).endTime(end).build());
    }

    @Override
    public Shift updateShift(Long id, String name, LocalTime start, LocalTime end) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + id));
        shift.setName(name);
        shift.setStartTime(start);
        shift.setEndTime(end);
        return shiftRepository.save(shift);
    }

    @Override
    public void deleteShift(Long id) {
        if (!shiftRepository.existsById(id)) {
            throw new ResourceNotFoundException("Shift not found: " + id);
        }
        shiftRepository.deleteById(id);
    }

    @Override
    public RosterAssignment assign(RosterForm form) {
        Employee employee = employeeRepository.findById(form.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + form.getEmployeeId()));
        Shift shift = shiftRepository.findById(form.getShiftId())
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + form.getShiftId()));

        RosterAssignment assignment = RosterAssignment.builder()
                .employee(employee)
                .shift(shift)
                .rosterDate(form.getRosterDate())
                .swapStatus(SwapStatus.NONE)
                .build();

        RosterAssignment saved = rosterAssignmentRepository.save(assignment);

        if (employee.getUser() != null) {
            notificationService.notify(employee.getUser(), "New shift assigned",
                    String.format("You have been assigned to the %s shift on %s.", shift.getName(), form.getRosterDate()),
                    NotificationType.IN_APP);
        }
        return saved;
    }

    @Override
    public void unassign(Long rosterAssignmentId) {
        if (!rosterAssignmentRepository.existsById(rosterAssignmentId)) {
            throw new ResourceNotFoundException("Roster assignment not found: " + rosterAssignmentId);
        }
        rosterAssignmentRepository.deleteById(rosterAssignmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RosterAssignment> findForEmployee(Long employeeId, LocalDate start, LocalDate end) {
        return rosterAssignmentRepository.findByEmployeeIdAndRosterDateBetweenOrderByRosterDateAsc(employeeId, start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RosterAssignment> findAll(LocalDate start, LocalDate end) {
        return rosterAssignmentRepository.findByRosterDateBetweenOrderByRosterDateAsc(start, end);
    }

    @Override
    public RosterAssignment requestSwap(Long rosterAssignmentId, Long swapWithEmployeeId) {
        RosterAssignment assignment = rosterAssignmentRepository.findById(rosterAssignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Roster assignment not found: " + rosterAssignmentId));
        Employee swapWith = employeeRepository.findById(swapWithEmployeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + swapWithEmployeeId));

        if (assignment.getSwapStatus() == SwapStatus.REQUESTED) {
            throw new BusinessRuleException("A swap request is already pending for this shift.");
        }

        assignment.setSwapWithEmployee(swapWith);
        assignment.setSwapStatus(SwapStatus.REQUESTED);
        return rosterAssignmentRepository.save(assignment);
    }

    @Override
    public RosterAssignment decideSwap(Long rosterAssignmentId, boolean approve) {
        RosterAssignment assignment = rosterAssignmentRepository.findById(rosterAssignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Roster assignment not found: " + rosterAssignmentId));

        if (assignment.getSwapStatus() != SwapStatus.REQUESTED) {
            throw new BusinessRuleException("This assignment has no pending swap request.");
        }

        if (approve) {
            // swap the employee on the assignment with the requested colleague
            Employee original = assignment.getEmployee();
            Employee swapWith = assignment.getSwapWithEmployee();
            assignment.setEmployee(swapWith);
            assignment.setSwapWithEmployee(original);
            assignment.setSwapStatus(SwapStatus.APPROVED);
        } else {
            assignment.setSwapStatus(SwapStatus.REJECTED);
        }

        return rosterAssignmentRepository.save(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RosterAssignment> findPendingSwaps() {
        return rosterAssignmentRepository.findBySwapStatus(SwapStatus.REQUESTED);
    }
}
