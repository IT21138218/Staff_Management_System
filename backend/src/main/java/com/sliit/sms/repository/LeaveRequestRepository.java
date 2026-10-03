package com.sliit.sms.repository;

import com.sliit.sms.entity.LeaveRequest;
import com.sliit.sms.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeIdOrderByAppliedDateDesc(Long employeeId);
    List<LeaveRequest> findByStatusOrderByAppliedDateAsc(LeaveStatus status);
    List<LeaveRequest> findByEmployee_Supervisor_IdAndStatus(Long supervisorId, LeaveStatus status);
}
