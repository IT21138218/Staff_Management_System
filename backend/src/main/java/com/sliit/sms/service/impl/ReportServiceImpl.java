package com.sliit.sms.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sliit.sms.entity.Attendance;
import com.sliit.sms.entity.Employee;
import com.sliit.sms.entity.Payslip;
import com.sliit.sms.entity.PayslipItem;
import com.sliit.sms.exception.ResourceNotFoundException;
import com.sliit.sms.repository.AttendanceRepository;
import com.sliit.sms.repository.EmployeeRepository;
import com.sliit.sms.repository.PayslipRepository;
import com.sliit.sms.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * FR9 - Reporting & Dashboards: role-based summaries, exportable as PDF/Excel.
 * Apache POI builds the .xlsx workbooks; OpenPDF builds the payslip PDF.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final PayslipRepository payslipRepository;

    @Override
    public ByteArrayOutputStream exportEmployeesToExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Employees");
            CellStyle headerStyle = headerStyle(workbook);

            String[] headers = {"Employee Code", "Full Name", "Email", "Department", "Designation", "Date Joined", "Status", "Basic Salary"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            List<Employee> employees = employeeRepository.findAll();
            int rowIdx = 1;
            for (Employee e : employees) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(e.getEmployeeCode());
                row.createCell(1).setCellValue(e.getFullName());
                row.createCell(2).setCellValue(e.getEmail());
                row.createCell(3).setCellValue(e.getDepartment() != null ? e.getDepartment().getName() : "");
                row.createCell(4).setCellValue(e.getDesignation() != null ? e.getDesignation() : "");
                row.createCell(5).setCellValue(e.getDateJoined() != null ? e.getDateJoined().toString() : "");
                row.createCell(6).setCellValue(e.getStatus().name());
                row.createCell(7).setCellValue(e.getBasicSalary());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Employee Excel report", e);
        }
    }

    @Override
    public ByteArrayOutputStream exportAttendanceToExcel(LocalDate start, LocalDate end) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Attendance");
            CellStyle headerStyle = headerStyle(workbook);

            String[] headers = {"Employee Code", "Employee Name", "Date", "Clock In", "Clock Out", "Hours Worked", "Status"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            List<Attendance> records = attendanceRepository.findByAttendanceDateBetween(start, end);
            int rowIdx = 1;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Attendance a : records) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(a.getEmployee().getEmployeeCode());
                row.createCell(1).setCellValue(a.getEmployee().getFullName());
                row.createCell(2).setCellValue(a.getAttendanceDate().toString());
                row.createCell(3).setCellValue(a.getClockIn() != null ? a.getClockIn().format(fmt) : "");
                row.createCell(4).setCellValue(a.getClockOut() != null ? a.getClockOut().format(fmt) : "");
                row.createCell(5).setCellValue(a.getHoursWorked() != null ? a.getHoursWorked() : 0.0);
                row.createCell(6).setCellValue(a.getStatus().name());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Attendance Excel report", e);
        }
    }

    @Override
    public ByteArrayOutputStream exportPayslipToPdf(Long payslipId) {
        Payslip payslip = payslipRepository.findById(payslipId)
                .orElseThrow(() -> new ResourceNotFoundException("Payslip not found: " + payslipId));

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(document, out);
            document.open();

            com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            com.lowagie.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            com.lowagie.text.Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

            Paragraph title = new Paragraph("Staff Management System - Payslip", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            Employee employee = payslip.getEmployee();
            document.add(new Paragraph("Employee: " + employee.getFullName() + " (" + employee.getEmployeeCode() + ")", normalFont));
            document.add(new Paragraph("Department: " + (employee.getDepartment() != null ? employee.getDepartment().getName() : "-"), normalFont));
            document.add(new Paragraph("Pay Period: " + payslip.getPayMonth() + "/" + payslip.getPayYear(), normalFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            addRow(table, "Basic Salary", String.format("%.2f", payslip.getBasicSalary()), boldFont, normalFont);
            addRow(table, "Overtime Pay", String.format("%.2f", payslip.getOvertimePay()), boldFont, normalFont);

            for (PayslipItem item : payslip.getItems()) {
                String label = (item.getType().name().equals("ALLOWANCE") ? "+ " : "- ") + item.getDescription();
                addRow(table, label, String.format("%.2f", item.getAmount()), normalFont, normalFont);
            }

            addRow(table, "Tax", String.format("%.2f", payslip.getTax()), boldFont, normalFont);
            addRow(table, "NET SALARY", String.format("%.2f", payslip.getNetSalary()), boldFont, boldFont);

            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Generated on: " + payslip.getGeneratedDate(), normalFont));

            document.close();
            return out;
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate payslip PDF", e);
        }
    }

    private void addRow(PdfPTable table, String label, String value, com.lowagie.text.Font labelFont, com.lowagie.text.Font valueFont) {
        table.addCell(new Phrase(label, labelFont));
        table.addCell(new Phrase(value, valueFont));
    }

    private CellStyle headerStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}
