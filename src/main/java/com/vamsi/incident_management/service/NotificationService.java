package com.vamsi.incident_management.service;

import com.vamsi.incident_management.entity.Notification;
import com.vamsi.incident_management.entity.User;
import com.vamsi.incident_management.exception.ResourceNotFoundException;
import com.vamsi.incident_management.repository.NotificationRepository;
import com.vamsi.incident_management.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ GET USER NOTIFICATIONS
    public List<Notification> getUserNotifications(Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }

    // ✅ MARK AS READ
    public Notification markAsRead(Long notificationId, Long userId) {

        if (notificationId == null) {
            throw new IllegalArgumentException("Notification ID cannot be null");
        }

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with ID: " + notificationId));

        // 🔐 Ownership check
        if (!notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not allowed to access this notification");
        }

        // Already read check
        if (notification.isRead()) {
            return notification; // no change
        }

        notification.setRead(true);

        return notificationRepository.save(notification);
    }

    // ✅ UNREAD COUNT
    public long getUnreadCount(Long userId) {

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        return notificationRepository.countByUser_IdAndIsReadFalse(userId);
    }

    // ✅ CREATE NOTIFICATION (helper for other modules)
    public Notification createNotification(Long userId, String message) {

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with ID: " + userId));

        Notification notification = Notification.builder()
                .user(user)
                .message(message.trim())
                .build();

        return notificationRepository.save(notification);
    }
}