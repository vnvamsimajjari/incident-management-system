package com.vamsi.incident_management.controller;

import com.vamsi.incident_management.entity.Notification;
import com.vamsi.incident_management.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // ✅ GET USER NOTIFICATIONS
    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(
            @RequestParam Long userId) {

        List<Notification> notifications =
                notificationService.getUserNotifications(userId);

        return ResponseEntity.ok(notifications);
    }

    // ✅ MARK AS READ
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id,
            @RequestParam Long userId) {

        Notification notification =
                notificationService.markAsRead(id, userId);

        return ResponseEntity.ok(
                Map.of(
                        "status", 200,
                        "message", "Notification marked as read",
                        "id", notification.getId()
                )
        );
    }

    // ✅ UNREAD COUNT
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(
            @RequestParam Long userId) {

        long count = notificationService.getUnreadCount(userId);

        return ResponseEntity.ok(
                Map.of(
                        "status", 200,
                        "unreadCount", count
                )
        );
    }
}