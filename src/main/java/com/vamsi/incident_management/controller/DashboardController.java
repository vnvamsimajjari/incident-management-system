package com.vamsi.incident_management.controller;

import com.vamsi.incident_management.dto.DashboardSummaryResponse;
import com.vamsi.incident_management.service.DashboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import com.vamsi.incident_management.dto.DashboardStatsResponse;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public DashboardSummaryResponse getDashboard() {
        return dashboardService.getDashboard();
    }
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER')")
    public DashboardStatsResponse getStats() {
        return dashboardService.getStats();
    }
}