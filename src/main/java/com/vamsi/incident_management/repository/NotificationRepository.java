package com.vamsi.incident_management.repository;

import com.vamsi.incident_management.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Get notifications by user (latest first)
    List<Notification> findByUser_IdOrderByCreatedAtDesc(Long userId);

    // Count unread
    long countByUser_IdAndIsReadFalse(Long userId);
}