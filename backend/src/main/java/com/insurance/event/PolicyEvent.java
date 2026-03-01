package com.insurance.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyEvent {
    
    private String eventId;
    private String eventType;
    private Long policyId;
    private String policyNumber;
    private String holderName;
    private String policyType;
    private String performedBy;
    private Instant timestamp;
}
