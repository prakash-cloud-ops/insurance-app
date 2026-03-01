package com.insurance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyStatsResponse {
    private Long totalPolicies;
    private Long activePolicies;
    private Long expiredPolicies;
    private Long pendingPolicies;
    private Long cancelledPolicies;
    private Map<String, Long> policiesByType;
    private Map<String, Long> policiesByStatus;
}
