package com.sliit.sms.service;

import com.sliit.sms.entity.Attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceService {
    Attendance clockIn(Long employeeId);
    Attendance clockOut(Long employeeId);
    Optional<Attendance> findToday(Long employeeId);
    List<Attendance> findHistory(Long employeeId);
    List<Attendance> findByDateRange(LocalDate start, LocalDate end);
    List<Attendance> findByEmployeeAndDateRange(Long employeeId, LocalDate start, LocalDate end);
    double totalHoursInRange(Long employeeId, LocalDate start, LocalDate end);
    double daysPresentInRange(Long employeeId, LocalDate start, LocalDate end);
}
