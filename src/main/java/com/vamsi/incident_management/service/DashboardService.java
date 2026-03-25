package com.vamsi.incident_management.service;

import com.vamsi.incident_management.dto.DashboardSummaryResponse;
import com.vamsi.incident_management.dto.DashboardStatsResponse;

public interface DashboardService {

    DashboardSummaryResponse getDashboard();

    DashboardStatsResponse getStats();
}