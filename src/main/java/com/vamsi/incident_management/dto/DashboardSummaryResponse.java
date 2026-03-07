package com.vamsi.incident_management.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardSummaryResponse {

    private Long totalIncidents;

    private Long openIncidents;

    private Long resolvedIncidents;

    private Long breachedIncidents;

}