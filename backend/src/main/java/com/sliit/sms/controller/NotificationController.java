package com.sliit.sms.controller;

import com.sliit.sms.entity.Notification;
import com.sliit.sms.entity.User;
import com.sliit.sms.service.NotificationService;
import com.sliit.sms.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * FR8 - Notifications (minor function: in-app notification list / bell icon).
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    @GetMapping
    public List<Notification> list() {
        User user = securityUtil.getCurrentUser();
        return notificationService.findForUser(user.getId());
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount() {
        User user = securityUtil.getCurrentUser();
        return Map.of("count", notificationService.unreadCount(user.getId()));
    }

    @PostMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }

    @PostMapping("/read-all")
    public void markAllRead() {
        User user = securityUtil.getCurrentUser();
        notificationService.markAllAsRead(user.getId());
    }
}
