package com.sliit.sms.service.impl;

import com.sliit.sms.entity.Attendance;
import com.sliit.sms.entity.AttendanceStatus;
import com.sliit.sms.entity.Employee;
import com.sliit.sms.exception.BusinessRuleException;
import com.sliit.sms.exception.ResourceNotFoundException;
import com.sliit.sms.repository.AttendanceRepository;
import com.sliit.sms.repository.EmployeeRepository;
import com.sliit.sms.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * FR3 - Attendance Management.
 * Primary user: Employees (clock in/out); Supervisors & HR monitor and pull reports.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    @Value("${sms.attendance.work-start-time}")
    private String workStartTimeStr;

    @Value("${sms.attendance.late-grace-minutes}")
    private int lateGraceMinutes;

    @Override
    public Attendance clockIn(Long employeeId) {
        Employee employee = getEmployee(employeeId);
        LocalDate today = LocalDate.now();

        if (attendanceRepository.findByEmployeeIdAndAttendanceDate(employeeId, today).isPresent()) {
            throw new BusinessRuleException("You have already clocked in today.");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalTime workStart = LocalTime.parse(workStartTimeStr);
        AttendanceStatus status = now.toLocalTime().isAfter(workStart.plusMinutes(lateGraceMinutes))
                ? AttendanceStatus.LATE
                : AttendanceStatus.PRESENT;

        Attendance attendance = Attendance.builder()
                .employee(employee)
                .attendanceDate(today)
                .clockIn(now)
                .status(status)
                .build();

        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance clockOut(Long employeeId) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeIdAndAttendanceDate(employeeId, today)
                .orElseThrow(() -> new BusinessRuleException("You have not clocked in today."));

        if (attendance.getClockOut() != null) {
            throw new BusinessRuleException("You have already clocked out today.");
        }

        LocalDateTime now = LocalDateTime.now();
        attendance.setClockOut(now);
        double hours = Duration.between(attendance.getClockIn(), now).toMinutes() / 60.0;
        attendance.setHoursWorked(Math.round(hours * 100.0) / 100.0);

        return attendanceRepository.save(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Attendance> findToday(Long employeeId) {
        return attendanceRepository.findByEmployeeIdAndAttendanceDate(employeeId, LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findHistory(Long employeeId) {
        return attendanceRepository.findByEmployeeIdOrderByAttendanceDateDesc(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findByDateRange(LocalDate start, LocalDate end) {
        return attendanceRepository.findByAttendanceDateBetween(start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findByEmployeeAndDateRange(Long employeeId, LocalDate start, LocalDate end) {
        return attendanceRepository.findByEmployeeIdAndAttendanceDateBetween(employeeId, start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public double totalHoursInRange(Long employeeId, LocalDate start, LocalDate end) {
        return findByEmployeeAndDateRange(employeeId, start, end).stream()
                .filter(a -> a.getHoursWorked() != null)
                .mapToDouble(Attendance::getHoursWorked)
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public double daysPresentInRange(Long employeeId, LocalDate start, LocalDate end) {
        return findByEmployeeAndDateRange(employeeId, start, end).stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE)
                .count();
    }

    private Employee getEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));
    }
}
