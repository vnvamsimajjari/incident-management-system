package com.vamsi.incident_management.service;

import com.vamsi.incident_management.entity.NotificationLog;
import com.vamsi.incident_management.repository.NotificationLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final NotificationLogRepository notificationLogRepository;

    //  SLA Breach Email (existing)
    public void sendSlaBreachEmail(String toEmail, Long incidentId) {

        try {

            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom("incident.alerts.system@gmail.com");
            message.setTo(toEmail);
            message.setSubject("SLA BREACH ALERT - Incident " + incidentId);

            message.setText(
                    "⚠ SLA BREACH DETECTED\n\n" +
                            "Incident ID: " + incidentId + "\n\n" +
                            "The SLA deadline has been breached.\n" +
                            "Immediate action is required.\n\n" +
                            "Incident Management System"
            );

            mailSender.send(message);

            log.info("SLA breach email sent for incident {}", incidentId);

            // Save success log
            NotificationLog logEntry = NotificationLog.builder()
                    .incidentId(incidentId)
                    .emailTo(toEmail)
                    .status("SENT")
                    .sentAt(LocalDateTime.now())
                    .build();

            notificationLogRepository.save(logEntry);

        } catch (Exception e) {

            log.error("Failed to send email for incident {}", incidentId, e);

            // Save failure log
            NotificationLog logEntry = NotificationLog.builder()
                    .incidentId(incidentId)
                    .emailTo(toEmail)
                    .status("FAILED")
                    .sentAt(LocalDateTime.now())
                    .build();

            notificationLogRepository.save(logEntry);
        }
    }

    //  NEW: Generic Email Method (FIXED ERROR)
    public void sendEmail(String toEmail, String subject, String body) {

        try {

            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom("incident.alerts.system@gmail.com");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            log.info("Email sent successfully to {}", toEmail);

        } catch (Exception e) {

            log.error("Failed to send email to {}", toEmail, e);
        }
    }
}