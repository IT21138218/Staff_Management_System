package com.sliit.sms.common.patterns.strategy;

import com.sliit.sms.common.patterns.singleton.PayrollConfig;
import com.sliit.sms.entity.Employee;
import org.springframework.stereotype.Component;

/**
 * DESIGN PATTERN: STRATEGY (concrete strategy 2 of 2)
 * ----------------------------------------------------------------------
 * Alternative calculation for shift-based / hourly-heavy roles where
 * overtime should be weighted more strongly and basic pay is NOT pro-rated
 * down for partial attendance (e.g. because absence is already handled via
 * approved leave deductions elsewhere). Swapping this in for a given payroll
 * run is a one-line change in PayrollServiceImpl - none of the calling code
 * or the Payslip/PayslipBuilder classes need to change.
 */
@Component("overtimeSalaryStrategy")
public class OvertimeSalaryStrategy implements SalaryCalculationStrategy {

    @Override
    public SalaryComputation calculate(Employee employee, double daysPresent, double workingDays, double overtimeHours) {
        PayrollConfig config = PayrollConfig.getInstance();

        double basic = employee.getBasicSalary();
        double hourlyRate = basic / (workingDays <= 0 ? 1 : workingDays) / 8.0;
        // Overtime weighted at double the configured multiplier for this strategy
        double overtimePay = overtimeHours * hourlyRate * (config.getOvertimeMultiplier() * 2.0);

        double grossBeforeTax = basic + overtimePay;
        double tax = grossBeforeTax * config.getTaxRate();
        double netSalary = grossBeforeTax - tax;

        return new SalaryComputation(basic, overtimePay, grossBeforeTax, tax, netSalary);
    }
}
