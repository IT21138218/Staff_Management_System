package com.sliit.sms.common.patterns.factory;

import com.sliit.sms.entity.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * DESIGN PATTERN: FACTORY (Factory Method)
 * ----------------------------------------------------------------------
 * Centralises the decision of which Notifier implementation to use for a
 * given NotificationType, so calling services (LeaveService, PayrollService,
 * ScheduleService, ...) never need to know about InAppNotifier/EmailNotifier
 * directly - they just ask the factory for "a notifier for this type".
 * Adding a new channel (e.g. SMS) later means adding one class + one case
 * here, with zero changes anywhere else in the codebase (Open/Closed).
 */
@Component
@RequiredArgsConstructor
public class NotifierFactory {

    private final InAppNotifier inAppNotifier;
    private final EmailNotifier emailNotifier;

    public Notifier getNotifier(NotificationType type) {
        return switch (type) {
            case IN_APP -> inAppNotifier;
            case EMAIL -> emailNotifier;
        };
    }
}
