package com.sliit.sms.repository;

import com.sliit.sms.entity.RosterAssignment;
import com.sliit.sms.entity.SwapStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RosterAssignmentRepository extends JpaRepository<RosterAssignment, Long> {
    List<RosterAssignment> findByEmployeeIdAndRosterDateBetweenOrderByRosterDateAsc(Long employeeId, LocalDate start, LocalDate end);
    List<RosterAssignment> findByRosterDateBetweenOrderByRosterDateAsc(LocalDate start, LocalDate end);
    List<RosterAssignment> findBySwapStatus(SwapStatus swapStatus);
}
