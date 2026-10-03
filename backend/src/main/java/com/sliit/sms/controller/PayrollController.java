package com.sliit.sms.controller;

import com.sliit.sms.entity.Employee;
import com.sliit.sms.entity.EmployeeStatus;
import com.sliit.sms.entity.Payslip;
import com.sliit.sms.entity.Role;
import com.sliit.sms.entity.User;
import com.sliit.sms.exception.BusinessRuleException;
import com.sliit.sms.service.EmployeeService;
import com.sliit.sms.service.PayrollService;
import com.sliit.sms.service.ReportService;
import com.sliit.sms.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * FR5 - Payroll Management. Payroll Officer generates payslips;
 * every employee can view their own.
 */
@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;
    private final EmployeeService employeeService;
    private final ReportService reportService;
    private final SecurityUtil securityUtil;

    @GetMapping("/my")
    public List<Payslip> my() {
        Employee employee = currentEmployee();
        return payrollService.findByEmployee(employee.getId());
    }

    @GetMapping("/{id}")
    public Payslip view(@PathVariable Long id) {
        Payslip payslip = payrollService.findById(id);
        assertCanView(payslip);
        return payslip;
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {
        Payslip payslip = payrollService.findById(id);
        assertCanView(payslip);
        byte[] bytes = reportService.exportPayslipToPdf(id).toByteArray();
        String filename = "payslip-" + payslip.getEmployee().getEmployeeCode() + "-" + payslip.getPayMonth() + "-" + payslip.getPayYear() + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }

    @GetMapping("/eligible-employees")
    public List<Employee> eligibleEmployees() {
        return employeeService.search(null, null, EmployeeStatus.ACTIVE);
    }

    @PostMapping("/generate")
    public Payslip generateForOne(@RequestParam Long employeeId, @RequestParam int month, @RequestParam int year) {
        User user = securityUtil.getCurrentUser();
        return payrollService.generateForEmployee(employeeId, month, year, user.getId());
    }

    @PostMapping("/run/all")
    public List<Payslip> generateForAll(@RequestParam int month, @RequestParam int year) {
        User user = securityUtil.getCurrentUser();
        return payrollService.generateForAll(month, year, user.getId());
    }

    @GetMapping("/all")
    public List<Payslip> allForPeriod(@RequestParam(required = false) Integer month, @RequestParam(required = false) Integer year) {
        LocalDate now = LocalDate.now();
        int m = month != null ? month : now.getMonthValue();
        int y = year != null ? year : now.getYear();
        return payrollService.findByPeriod(m, y);
    }

    private Employee currentEmployee() {
        return employeeService.findByUsername(securityUtil.getCurrentUsername());
    }

    private void assertCanView(Payslip payslip) {
        User user = securityUtil.getCurrentUser();
        if (user.getRole() == Role.PAYROLL_OFFICER || user.getRole() == Role.ADMIN || user.getRole() == Role.HR_MANAGER) {
            return;
        }
        Employee employee = currentEmployee();
        if (!payslip.getEmployee().getId().equals(employee.getId())) {
            throw new BusinessRuleException("You can only view your own payslips.");
        }
    }
}
