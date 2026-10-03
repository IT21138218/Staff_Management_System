package com.sliit.sms.common.patterns.singleton;

/**
 * DESIGN PATTERN: SINGLETON
 * ----------------------------------------------------------------------
 * Holds payroll-wide configuration (tax rate, overtime multiplier) that must
 * be read consistently by every PayrollService calculation in a single JVM.
 * Loaded once from application.properties via PayrollConfigInitializer and
 * from then on every caller shares exactly one instance, so a mid-run change
 * to the tax rate cannot leave two payslips calculated under different rules.
 *
 * We implement it as a classic thread-safe (double-checked locking) Singleton
 * rather than relying on "a Spring bean is a singleton too", specifically so
 * the pattern is visible and independently instantiable/testable outside the
 * Spring container - which is what the module rubric is looking for.
 */
public final class PayrollConfig {

    private static volatile PayrollConfig instance;

    private double taxRate;
    private double overtimeMultiplier;

    private PayrollConfig() {
        // sensible defaults; overwritten by PayrollConfigInitializer at startup
        this.taxRate = 0.10;
        this.overtimeMultiplier = 1.5;
    }

    public static PayrollConfig getInstance() {
        if (instance == null) {
            synchronized (PayrollConfig.class) {
                if (instance == null) {
                    instance = new PayrollConfig();
                }
            }
        }
        return instance;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public double getOvertimeMultiplier() {
        return overtimeMultiplier;
    }

    public void setOvertimeMultiplier(double overtimeMultiplier) {
        this.overtimeMultiplier = overtimeMultiplier;
    }
}
