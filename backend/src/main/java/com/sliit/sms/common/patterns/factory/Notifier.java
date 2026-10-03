package com.sliit.sms.common.patterns.factory;

/**
 * Product interface for the NotifierFactory (FACTORY METHOD pattern).
 */
public interface Notifier {
    /**
     * Dispatch a notification. Returns the message actually sent/stored so the
     * caller can persist it (e.g. as a Notification entity) if needed.
     */
    void send(String recipientEmail, String title, String message);
}
