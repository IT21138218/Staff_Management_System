package com.sliit.sms.common.patterns.factory;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * Concrete product: an in-app notification. In this project's scope, "sending"
 * an in-app notification means it will be persisted as a Notification entity
 * and shown in the recipient's notification bell (see NotificationService).
 * This class itself only handles the "delivery channel" concern (logging /
 * would-be push mechanism), keeping it decoupled from persistence.
 */
@Slf4j
@Component
public class InAppNotifier implements Notifier {
    @Override
    public void send(String recipientEmail, String title, String message) {
        log.info("[IN_APP notification] to={} title='{}' message='{}'", recipientEmail, title, message);
    }
}
