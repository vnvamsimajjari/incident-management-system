package com.vamsi.incident_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

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

        } catch (Exception e) {

            log.error("Failed to send email for incident {}", incidentId, e);
        }
    }
}