package com.vamsi.incident_management.scheduler;

import com.vamsi.incident_management.service.EscalationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EscalationScheduler {

    private final EscalationService escalationService;

    /**
     * Runs every 1 minute
     * - Handles escalation only
     */
    @Scheduled(fixedRate = 60000)
    public void runEscalationJob() {

        log.info("Escalation Scheduler started...");

        try {
            escalationService.processEscalations();
        } catch (Exception ex) {
            log.error("Error while running escalation scheduler", ex);
        }

        log.info("Escalation Scheduler completed.");
    }
}