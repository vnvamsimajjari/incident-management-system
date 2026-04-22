package com.vamsi.incident_management.service.impl;

import java.util.Map;
import java.util.HashMap;

import com.vamsi.incident_management.repository.IncidentRepository;
import com.vamsi.incident_management.dto.DashboardStatsResponse;
import com.vamsi.incident_management.dto.DashboardSummaryResponse;
import com.vamsi.incident_management.service.DashboardService;
import com.vamsi.incident_management.entity.Status;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final IncidentRepository incidentRepository;

    @Override
    public DashboardSummaryResponse getDashboard() {

        return DashboardSummaryResponse.builder()
                .totalIncidents(incidentRepository.count())
                .openIncidents(incidentRepository.countByStatus(Status.OPEN))
                .closedIncidents(incidentRepository.countByStatus(Status.CLOSED))
                .resolvedIncidents(incidentRepository.countByStatus(Status.RESOLVED))
                .breachedIncidents(incidentRepository.countByBreached(true))
                .build();
    }

    @Override
    public DashboardStatsResponse getStats() {

        Map<String, Long> statusMap = new HashMap<>();
        Map<String, Long> priorityMap = new HashMap<>();

        // STATUS GROUPING
        for (Object[] row : incidentRepository.countByStatusGroup()) {
            statusMap.put(row[0].toString(), (Long) row[1]);
        }

        // PRIORITY GROUPING
        for (Object[] row : incidentRepository.countByPriorityGroup()) {
            priorityMap.put(row[0].toString(), (Long) row[1]);
        }

        return DashboardStatsResponse.builder()
                .byStatus(statusMap)
                .byPriority(priorityMap)
                .build();
    }
}