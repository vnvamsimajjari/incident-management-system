package com.vamsi.incident_management.service.impl;

import java.util.Map;
import java.util.HashMap;

import com.vamsi.incident_management.repository.IncidentRepository;
import com.vamsi.incident_management.dto.DashboardStatsResponse;
import com.vamsi.incident_management.dto.DashboardSummaryResponse;
import com.vamsi.incident_management.service.DashboardService;
import com.vamsi.incident_management.service.IncidentService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final IncidentService incidentService;

    // ✅ MISSING FIELD — ADD THIS
    private final IncidentRepository incidentRepository;

    @Override
    public DashboardSummaryResponse getDashboard() {

        return DashboardSummaryResponse.builder()
                .totalIncidents((long) incidentService.getTotalIncidents())
                .openIncidents((long) incidentService.getOpenIncidents())
                .closedIncidents((long) incidentService.getClosedIncidents())
                .resolvedIncidents((long) incidentService.getResolvedIncidents())
                .breachedIncidents((long) incidentService.getBreachedCount())
                .build();
    }

    @Override
    public DashboardStatsResponse getStats() {

        Map<String, Long> statusMap = new HashMap<>();
        Map<String, Long> priorityMap = new HashMap<>();

        // STATUS
        for (Object[] row : incidentRepository.countByStatusGroup()) {
            statusMap.put(row[0].toString(), (Long) row[1]);
        }

        // PRIORITY
        for (Object[] row : incidentRepository.countByPriorityGroup()) {
            priorityMap.put(row[0].toString(), (Long) row[1]);
        }

        return DashboardStatsResponse.builder()
                .byStatus(statusMap)
                .byPriority(priorityMap)
                .build();
    }
}