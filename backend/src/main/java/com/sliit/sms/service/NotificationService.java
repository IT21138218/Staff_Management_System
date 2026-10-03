package com.sliit.sms.service;

import com.sliit.sms.entity.Notification;
import com.sliit.sms.entity.NotificationType;
import com.sliit.sms.entity.User;

import java.util.List;

public interface NotificationService {
    Notification notify(User recipient, String title, String message, NotificationType type);
    List<Notification> findForUser(Long userId);
    long unreadCount(Long userId);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
}
