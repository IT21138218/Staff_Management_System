package com.sliit.sms.common.patterns.builder;

import com.sliit.sms.entity.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DESIGN PATTERN: BUILDER
 * ----------------------------------------------------------------------
 * A Payslip is assembled from several independent pieces computed at
 * different points in PayrollServiceImpl (basic pay from the chosen
 * SalaryCalculationStrategy, then an arbitrary number of allowance/deduction
 * line items, then tax and totals). Rather than a telescoping Payslip
 * constructor or setting a dozen fields directly on the entity from service
 * code, PayrollServiceImpl uses this fluent builder to assemble a valid,
 * fully-consistent Payslip (with its PayslipItem children correctly wired
 * back to the parent) in one readable chain, then calls build().
 *
 * Note: this is a domain object builder (GoF Builder) distinct from Lombok's
 * generated @Builder on the entities themselves - this one owns the business
 * rule of keeping totals and line items consistent, e.g. recomputing
 * totalAllowances/totalDeductions as items are added.
 */
public class PayslipBuilder {

    private Employee employee;
    private Integer payMonth;
    private Integer payYear;
    private Double basicSalary = 0.0;
    private Double overtimePay = 0.0;
    private Double tax = 0.0;
    private User generatedBy;
    private final List<PayslipItem> items = new ArrayList<>();

    public PayslipBuilder forEmployee(Employee employee) {
        this.employee = employee;
        return this;
    }

    public PayslipBuilder forPeriod(int month, int year) {
        this.payMonth = month;
        this.payYear = year;
        return this;
    }

    public PayslipBuilder withBasicSalary(double basicSalary) {
        this.basicSalary = basicSalary;
        return this;
    }

    public PayslipBuilder withOvertimePay(double overtimePay) {
        this.overtimePay = overtimePay;
        return this;
    }

    public PayslipBuilder withTax(double tax) {
        this.tax = tax;
        return this;
    }

    public PayslipBuilder generatedBy(User user) {
        this.generatedBy = user;
        return this;
    }

    public PayslipBuilder addAllowance(String description, double amount) {
        items.add(PayslipItem.builder().type(PayslipItemType.ALLOWANCE).description(description).amount(amount).build());
        return this;
    }

    public PayslipBuilder addDeduction(String description, double amount) {
        items.add(PayslipItem.builder().type(PayslipItemType.DEDUCTION).description(description).amount(amount).build());
        return this;
    }

    public Payslip build() {
        if (employee == null || payMonth == null || payYear == null) {
            throw new IllegalStateException("PayslipBuilder requires employee, payMonth and payYear before build()");
        }

        double totalAllowances = items.stream()
                .filter(i -> i.getType() == PayslipItemType.ALLOWANCE)
                .mapToDouble(PayslipItem::getAmount).sum();
        double totalDeductions = items.stream()
                .filter(i -> i.getType() == PayslipItemType.DEDUCTION)
                .mapToDouble(PayslipItem::getAmount).sum();

        double netSalary = basicSalary + overtimePay + totalAllowances - totalDeductions - tax;

        Payslip payslip = Payslip.builder()
                .employee(employee)
                .payMonth(payMonth)
                .payYear(payYear)
                .basicSalary(basicSalary)
                .overtimePay(overtimePay)
                .totalAllowances(totalAllowances)
                .totalDeductions(totalDeductions)
                .tax(tax)
                .netSalary(netSalary)
                .status(PayslipStatus.GENERATED)
                .generatedDate(LocalDateTime.now())
                .generatedBy(generatedBy)
                .items(new ArrayList<>())
                .build();

        // wire each item back to its parent payslip (JPA needs both sides set)
        for (PayslipItem item : items) {
            item.setPayslip(payslip);
            payslip.getItems().add(item);
        }

        return payslip;
    }
}
