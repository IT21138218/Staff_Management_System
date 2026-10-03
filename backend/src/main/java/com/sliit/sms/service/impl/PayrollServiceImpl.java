package com.sliit.sms.service.impl;

import com.sliit.sms.common.patterns.builder.PayslipBuilder;
import com.sliit.sms.common.patterns.strategy.SalaryCalculationStrategy;
import com.sliit.sms.common.patterns.strategy.SalaryComputation;
import com.sliit.sms.entity.*;
import com.sliit.sms.exception.ResourceNotFoundException;
import com.sliit.sms.repository.EmployeeRepository;
import com.sliit.sms.repository.PayslipRepository;
import com.sliit.sms.repository.UserRepository;
import com.sliit.sms.service.AttendanceService;
import com.sliit.sms.service.NotificationService;
import com.sliit.sms.service.PayrollService;
import com.sliit.sms.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FR5 - Payroll Management. Primary user: Payroll Officer (generates);
 * Employees view their own payslips.
 *
 * Demonstrates three patterns working together:
 *  - STRATEGY:  the actual salary math is delegated to a SalaryCalculationStrategy,
 *               chosen per employee (see chooseStrategy()).
 *  - SINGLETON: both strategies read shared config from PayrollConfig.getInstance().
 *  - BUILDER:   the resulting Payslip + its line items are assembled with PayslipBuilder.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PayrollServiceImpl implements PayrollService {

    private final PayslipRepository payslipRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final AttendanceService attendanceService;
    private final NotificationService notificationService;

    // Spring auto-populates this with every SalaryCalculationStrategy bean,
    // keyed by bean name ("standardSalaryStrategy", "overtimeSalaryStrategy") -
    // this is what lets chooseStrategy() pick a concrete STRATEGY at runtime
    // without an if/else chain listing every implementation by class.
    private final Map<String, SalaryCalculationStrategy> salaryStrategies;

    @Value("${sms.attendance.standard-hours-per-day}")
    private double standardHoursPerDay;

    @Override
    public Payslip generateForEmployee(Long employeeId, int month, int year, Long generatedByUserId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));
        User generatedBy = userRepository.findById(generatedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + generatedByUserId));

        // Regenerating for the same period replaces the previous payslip (e.g. after correcting attendance)
        payslipRepository.findByEmployeeIdAndPayMonthAndPayYear(employeeId, month, year)
                .ifPresent(payslipRepository::delete);

        YearMonth ym = YearMonth.of(year, month);
        LocalDate periodStart = ym.atDay(1);
        LocalDate periodEnd = ym.atEndOfMonth();

        double workingDays = DateUtil.countWorkingDays(periodStart, periodEnd);
        double daysPresent = attendanceService.daysPresentInRange(employeeId, periodStart, periodEnd);
        double totalHours = attendanceService.totalHoursInRange(employeeId, periodStart, periodEnd);
        double expectedHours = daysPresent * standardHoursPerDay;
        double overtimeHours = Math.max(totalHours - expectedHours, 0);

        SalaryCalculationStrategy strategy = chooseStrategy(employee);
        SalaryComputation computation = strategy.calculate(employee, daysPresent, workingDays, overtimeHours);

        double transportAllowance = 5000.0;
        double mealAllowance = 3000.0;
        double epfDeduction = Math.round(computation.getProRatedBasic() * 0.08 * 100.0) / 100.0;

        Payslip payslip = new PayslipBuilder()
                .forEmployee(employee)
                .forPeriod(month, year)
                .withBasicSalary(round(computation.getProRatedBasic()))
                .withOvertimePay(round(computation.getOvertimePay()))
                .withTax(round(computation.getTax()))
                .generatedBy(generatedBy)
                .addAllowance("Transport Allowance", transportAllowance)
                .addAllowance("Meal Allowance", mealAllowance)
                .addDeduction("EPF Contribution (8%)", epfDeduction)
                .build();

        Payslip saved = payslipRepository.save(payslip);

        if (employee.getUser() != null) {
            String title = "Payslip generated for " + ym.getMonth() + " " + year;
            String message = String.format("Your payslip for %s %d is ready. Net salary: %.2f",
                    ym.getMonth(), year, saved.getNetSalary());
            notificationService.notify(employee.getUser(), title, message, NotificationType.IN_APP);
        }

        return saved;
    }

    @Override
    public List<Payslip> generateForAll(int month, int year, Long generatedByUserId) {
        List<Payslip> results = new ArrayList<>();
        List<Employee> activeEmployees = employeeRepository.findByStatus(EmployeeStatus.ACTIVE);
        for (Employee employee : activeEmployees) {
            try {
                results.add(generateForEmployee(employee.getId(), month, year, generatedByUserId));
            } catch (Exception e) {
                log.error("Failed to generate payslip for employee {}: {}", employee.getEmployeeCode(), e.getMessage());
            }
        }
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payslip> findByEmployee(Long employeeId) {
        return payslipRepository.findByEmployeeIdOrderByPayYearDescPayMonthDesc(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Payslip findById(Long id) {
        return payslipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payslip not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payslip> findByPeriod(int month, int year) {
        return payslipRepository.findByPayMonthAndPayYear(month, year);
    }

    /**
     * Business rule for STRATEGY selection: shift-based departments (Operations)
     * are paid under the overtime-weighted strategy; everyone else under the
     * standard pro-rated strategy. A real system might instead key this off an
     * "employment type" field - the point demonstrated here is that
     * PayrollServiceImpl never contains the arithmetic itself, only the choice
     * of which SalaryCalculationStrategy to delegate to.
     */
    private SalaryCalculationStrategy chooseStrategy(Employee employee) {
        if (employee.getDepartment() != null && "Operations".equalsIgnoreCase(employee.getDepartment().getName())) {
            return salaryStrategies.get("overtimeSalaryStrategy");
        }
        return salaryStrategies.get("standardSalaryStrategy");
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
