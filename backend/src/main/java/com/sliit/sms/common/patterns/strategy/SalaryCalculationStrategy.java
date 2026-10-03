package com.sliit.sms.common.patterns.strategy;

import com.sliit.sms.entity.Employee;

/**
 * Strategy interface for the STRATEGY pattern used by PayrollService.
 * Different employee types/contracts can be paid under different rules
 * without PayrollService needing an if/else chain per case.
 */
public interface SalaryCalculationStrategy {

    /**
     * @param employee        the employee being paid
     * @param daysPresent     attendance days worked in the pay period
     * @param workingDays     total working days in the pay period
     * @param overtimeHours   overtime hours worked in the pay period
     * @return a populated SalaryComputation with the breakdown this strategy produced
     */
    SalaryComputation calculate(Employee employee, double daysPresent, double workingDays, double overtimeHours);
}
