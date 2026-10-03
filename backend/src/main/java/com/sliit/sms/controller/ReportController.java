package com.sliit.sms.controller;

import com.sliit.sms.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * FR9 - Reporting & Dashboards: exportable reports as PDF/Excel.
 * Restricted to ADMIN, HR_MANAGER, PAYROLL_OFFICER in SecurityConfig.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/employees/export")
    public ResponseEntity<byte[]> exportEmployees() {
        byte[] bytes = reportService.exportEmployeesToExcel().toByteArray();
        return excelResponse(bytes, "employees-report.xlsx");
    }

    @GetMapping("/attendance/export")
    public ResponseEntity<byte[]> exportAttendance(@RequestParam(required = false) String start,
                                                     @RequestParam(required = false) String end) {
        LocalDate startDate = (start != null && !start.isBlank()) ? LocalDate.parse(start) : LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = (end != null && !end.isBlank()) ? LocalDate.parse(end) : LocalDate.now();
        byte[] bytes = reportService.exportAttendanceToExcel(startDate, endDate).toByteArray();
        return excelResponse(bytes, "attendance-report.xlsx");
    }

    private ResponseEntity<byte[]> excelResponse(byte[] bytes, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }
}
