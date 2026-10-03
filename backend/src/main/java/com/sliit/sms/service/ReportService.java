package com.sliit.sms.service;

import java.io.ByteArrayOutputStream;

/**
 * FR9 - Reporting & Dashboards: exportable reports as PDF/Excel.
 */
public interface ReportService {
    ByteArrayOutputStream exportEmployeesToExcel();
    ByteArrayOutputStream exportAttendanceToExcel(java.time.LocalDate start, java.time.LocalDate end);
    ByteArrayOutputStream exportPayslipToPdf(Long payslipId);
}
