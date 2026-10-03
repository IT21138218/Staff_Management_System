package com.sliit.sms.common.patterns.strategy;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Plain result object returned by a SalaryCalculationStrategy - the raw
 * numbers PayrollService then hands to PayslipBuilder.
 */
@Getter
@AllArgsConstructor
public class SalaryComputation {
    private final double proRatedBasic;
    private final double overtimePay;
    private final double grossBeforeTax;
    private final double tax;
    private final double netSalary;
}
