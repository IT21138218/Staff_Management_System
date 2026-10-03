package com.sliit.sms.service.impl;

import com.sliit.sms.common.patterns.observer.LeaveStatusPublisher;
import com.sliit.sms.dto.LeaveRequestForm;
import com.sliit.sms.entity.*;
import com.sliit.sms.exception.BusinessRuleException;
import com.sliit.sms.exception.ResourceNotFoundException;
import com.sliit.sms.repository.*;
import com.sliit.sms.service.LeaveService;
import com.sliit.sms.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * FR4 - Leave Management.
 * Primary users: Employees (apply); Supervisors & HR Manager (approve/reject).
 * Status changes are broadcast through LeaveStatusPublisher (OBSERVER pattern)
 * so notifications are raised without this class depending on NotificationService.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final LeaveStatusPublisher leaveStatusPublisher;

    @Override
    @Transactional(readOnly = true)
    public List<LeaveType> findAllLeaveTypes() {
        return leaveTypeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveBalance> findBalances(Long employeeId, int year) {
        return leaveBalanceRepository.findByEmployeeIdAndYear(employeeId, year);
    }

    @Override
    public LeaveRequest apply(Long employeeId, LeaveRequestForm form) {
        if (form.getEndDate().isBefore(form.getStartDate())) {
            throw new BusinessRuleException("End date cannot be before start date.");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));
        LeaveType leaveType = leaveTypeRepository.findById(form.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found: " + form.getLeaveTypeId()));

        double days = DateUtil.countWorkingDays(form.getStartDate(), form.getEndDate());
        int year = form.getStartDate().getYear();

        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveType.getId(), year)
                .orElseGet(() -> leaveBalanceRepository.save(LeaveBalance.builder()
                        .employee(employee).leaveType(leaveType).year(year)
                        .allocatedDays((double) leaveType.getDefaultDaysPerYear()).usedDays(0.0).build()));

        if (balance.getRemainingDays() < days) {
            throw new BusinessRuleException(String.format(
                    "Insufficient %s leave balance: requested %.1f day(s) but only %.1f remain for %d.",
                    leaveType.getName(), days, balance.getRemainingDays(), year));
        }

        LeaveRequest request = LeaveRequest.builder()
                .employee(employee)
                .leaveType(leaveType)
                .startDate(form.getStartDate())
                .endDate(form.getEndDate())
                .numberOfDays(days)
                .reason(form.getReason())
                .status(LeaveStatus.PENDING)
                .appliedDate(LocalDateTime.now())
                .build();

        LeaveRequest saved = leaveRequestRepository.save(request);
        leaveStatusPublisher.publish(saved);
        return saved;
    }

    @Override
    public LeaveRequest decide(Long leaveRequestId, boolean approve, String comments, Long decidingUserId) {
        LeaveRequest request = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + leaveRequestId));

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new BusinessRuleException("This leave request has already been decided.");
        }

        User decider = userRepository.findById(decidingUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + decidingUserId));

        if (approve) {
            int year = request.getStartDate().getYear();
            LeaveBalance balance = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeIdAndYear(request.getEmployee().getId(), request.getLeaveType().getId(), year)
                    .orElseThrow(() -> new BusinessRuleException("No leave balance record found for this employee/year."));
            if (balance.getRemainingDays() < request.getNumberOfDays()) {
                throw new BusinessRuleException("Cannot approve: employee's leave balance has changed and is now insufficient.");
            }
            balance.setUsedDays(balance.getUsedDays() + request.getNumberOfDays());
            leaveBalanceRepository.save(balance);
            request.setStatus(LeaveStatus.APPROVED);
        } else {
            request.setStatus(LeaveStatus.REJECTED);
        }

        request.setApprovedBy(decider);
        request.setDecisionDate(LocalDateTime.now());
        request.setComments(comments);

        LeaveRequest saved = leaveRequestRepository.save(request);
        leaveStatusPublisher.publish(saved);
        return saved;
    }

    @Override
    public void cancel(Long leaveRequestId, Long employeeId) {
        LeaveRequest request = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + leaveRequestId));

        if (!request.getEmployee().getId().equals(employeeId)) {
            throw new BusinessRuleException("You can only cancel your own leave requests.");
        }
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new BusinessRuleException("Only pending leave requests can be cancelled.");
        }

        request.setStatus(LeaveStatus.CANCELLED);
        LeaveRequest saved = leaveRequestRepository.save(request);
        leaveStatusPublisher.publish(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequest> findByEmployee(Long employeeId) {
        return leaveRequestRepository.findByEmployeeIdOrderByAppliedDateDesc(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequest> findPendingForSupervisor(Long supervisorId) {
        return leaveRequestRepository.findByEmployee_Supervisor_IdAndStatus(supervisorId, LeaveStatus.PENDING);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequest> findAllPending() {
        return leaveRequestRepository.findByStatusOrderByAppliedDateAsc(LeaveStatus.PENDING);
    }
}
