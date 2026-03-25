package com.vamsi.incident_management.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DashboardStatsResponse {

    private Map<String, Long> byStatus;
    private Map<String, Long> byPriority;
}