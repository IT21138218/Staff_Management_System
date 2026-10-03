package com.sliit.sms.common.patterns.factory;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * Concrete product: an email notification (FR8). A real deployment would wire
 * this to JavaMailSender / an SMTP relay; for this project it logs the
 * outgoing email so the workflow is demonstrable without external mail
 * infrastructure. Swapping in real SMTP later only touches this one class.
 */
@Slf4j
@Component
public class EmailNotifier implements Notifier {
    @Override
    public void send(String recipientEmail, String title, String message) {
        log.info("[EMAIL notification] to={} subject='{}' body='{}'", recipientEmail, title, message);
    }
}
