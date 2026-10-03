package com.sliit.sms.repository;

import com.sliit.sms.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate date);
    List<Attendance> findByEmployeeIdOrderByAttendanceDateDesc(Long employeeId);
    List<Attendance> findByAttendanceDateBetween(LocalDate start, LocalDate end);
    List<Attendance> findByEmployeeIdAndAttendanceDateBetween(Long employeeId, LocalDate start, LocalDate end);
}
