package com.sliit.sms.service;

import com.sliit.sms.dto.RosterForm;
import com.sliit.sms.entity.RosterAssignment;
import com.sliit.sms.entity.Shift;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    List<Shift> findAllShifts();
    Shift createShift(String name, java.time.LocalTime start, java.time.LocalTime end);
    Shift updateShift(Long id, String name, java.time.LocalTime start, java.time.LocalTime end);
    void deleteShift(Long id);
    RosterAssignment assign(RosterForm form);
    void unassign(Long rosterAssignmentId);
    List<RosterAssignment> findForEmployee(Long employeeId, LocalDate start, LocalDate end);
    List<RosterAssignment> findAll(LocalDate start, LocalDate end);
    RosterAssignment requestSwap(Long rosterAssignmentId, Long swapWithEmployeeId);
    RosterAssignment decideSwap(Long rosterAssignmentId, boolean approve);
    List<RosterAssignment> findPendingSwaps();
}
