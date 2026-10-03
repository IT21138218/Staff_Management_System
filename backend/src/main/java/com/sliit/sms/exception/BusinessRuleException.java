package com.sliit.sms.exception;

/**
 * Thrown when a request is well-formed but violates a domain rule,
 * e.g. applying for more leave days than remain in the employee's balance,
 * or clocking in twice on the same day.
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
