package com.vamsi.incident_management.repository;

import com.vamsi.incident_management.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository
        extends JpaRepository<NotificationLog, Long> {
}