package com.sliit.sms.repository;

import com.sliit.sms.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient_IdOrderByCreatedDateDesc(Long recipientId);
    List<Notification> findByRecipient_IdAndIsReadFalseOrderByCreatedDateDesc(Long recipientId);
    long countByRecipient_IdAndIsReadFalse(Long recipientId);
}
