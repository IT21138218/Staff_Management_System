package com.sliit.sms.service.impl;

import com.sliit.sms.common.patterns.factory.Notifier;
import com.sliit.sms.common.patterns.factory.NotifierFactory;
import com.sliit.sms.entity.Notification;
import com.sliit.sms.entity.NotificationType;
import com.sliit.sms.entity.User;
import com.sliit.sms.exception.ResourceNotFoundException;
import com.sliit.sms.repository.NotificationRepository;
import com.sliit.sms.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * FR8 - Notifications. Uses NotifierFactory (FACTORY pattern) to pick the
 * right delivery channel, then persists a Notification row so it shows up
 * in the recipient's in-app notification list regardless of channel.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotifierFactory notifierFactory;

    @Override
    public Notification notify(User recipient, String title, String message, NotificationType type) {
        Notifier notifier = notifierFactory.getNotifier(type);
        notifier.send(recipient.getEmail(), title, message);

        Notification notification = Notification.builder()
                .recipient(recipient)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .build();

        return notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findForUser(Long userId) {
        return notificationRepository.findByRecipient_IdOrderByCreatedDateDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return notificationRepository.countByRecipient_IdAndIsReadFalse(userId);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByRecipient_IdAndIsReadFalseOrderByCreatedDateDesc(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}
