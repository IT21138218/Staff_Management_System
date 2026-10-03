package com.sliit.sms.common.patterns.strategy;

import com.sliit.sms.common.patterns.singleton.PayrollConfig;
import com.sliit.sms.entity.Employee;
import org.springframework.stereotype.Component;

/**
 * DESIGN PATTERN: STRATEGY (concrete strategy 1 of 2)
 * ----------------------------------------------------------------------
 * Default calculation for permanent/monthly-salaried staff: basic salary is
 * pro-rated by attendance (days present / working days in the period), plus
 * flat overtime pay, minus tax from the PayrollConfig singleton.
 * Used for every role except cases that opt into overtime-heavy calculation
 * via OvertimeSalaryStrategy (see PayrollServiceImpl for the selection logic).
 */
@Component("standardSalaryStrategy")
public class StandardSalaryStrategy implements SalaryCalculationStrategy {

    @Override
    public SalaryComputation calculate(Employee employee, double daysPresent, double workingDays, double overtimeHours) {
        PayrollConfig config = PayrollConfig.getInstance();

        double basic = employee.getBasicSalary();
        double attendanceRatio = workingDays <= 0 ? 1.0 : Math.min(daysPresent / workingDays, 1.0);
        double proRatedBasic = basic * attendanceRatio;

        double hourlyRate = basic / (workingDays <= 0 ? 1 : workingDays) / 8.0;
        double overtimePay = overtimeHours * hourlyRate * config.getOvertimeMultiplier();

        double grossBeforeTax = proRatedBasic + overtimePay;
        double tax = grossBeforeTax * config.getTaxRate();
        double netSalary = grossBeforeTax - tax;

        return new SalaryComputation(proRatedBasic, overtimePay, grossBeforeTax, tax, netSalary);
    }
}
