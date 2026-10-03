package com.sliit.sms.service;

import com.sliit.sms.dto.LeaveRequestForm;
import com.sliit.sms.entity.LeaveBalance;
import com.sliit.sms.entity.LeaveRequest;
import com.sliit.sms.entity.LeaveType;

import java.util.List;

public interface LeaveService {
    List<LeaveType> findAllLeaveTypes();
    List<LeaveBalance> findBalances(Long employeeId, int year);
    LeaveRequest apply(Long employeeId, LeaveRequestForm form);
    LeaveRequest decide(Long leaveRequestId, boolean approve, String comments, Long decidingUserId);
    void cancel(Long leaveRequestId, Long employeeId);
    List<LeaveRequest> findByEmployee(Long employeeId);
    List<LeaveRequest> findPendingForSupervisor(Long supervisorId);
    List<LeaveRequest> findAllPending();
}
