package com.sliit.sms.config;

import com.sliit.sms.common.patterns.singleton.PayrollConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Reads sms.payroll.* from application.properties exactly once at startup
 * and pushes the values into the PayrollConfig Singleton, so the rest of the
 * app (PayrollService, the two SalaryCalculationStrategy implementations)
 * can read them without depending on Spring's Environment directly.
 */
@Component
public class PayrollConfigInitializer {

    @Value("${sms.payroll.tax-rate}")
    private double taxRate;

    @Value("${sms.payroll.overtime-multiplier}")
    private double overtimeMultiplier;

    @PostConstruct
    public void init() {
        PayrollConfig config = PayrollConfig.getInstance();
        config.setTaxRate(taxRate);
        config.setOvertimeMultiplier(overtimeMultiplier);
    }
}
