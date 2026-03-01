package com.insurance.service;

import com.insurance.dto.*;
import com.insurance.event.PolicyEvent;
import com.insurance.model.Policy;
import com.insurance.model.PolicyStatus;
import com.insurance.model.PolicyType;
import com.insurance.repository.PolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PolicyService {

    private static final Logger log = LoggerFactory.getLogger(PolicyService.class);

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private KafkaTemplate<String, PolicyEvent> kafkaTemplate;

    @Value("${kafka.topic.policy-events:policy.events}")
    private String kafkaTopic;

    public Page<PolicyResponse> getAllPolicies(int page, int size, String sortBy, String sortDir,
                                                PolicyStatus status, PolicyType type, String search) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Policy> policies;

        if (search != null && !search.isEmpty()) {
            policies = policyRepository.findByHolderNameContainingIgnoreCase(search, pageable);
        } else if (status != null && type != null) {
            policies = policyRepository.findByStatusAndType(status, type, pageable);
        } else if (status != null) {
            policies = policyRepository.findByStatus(status, pageable);
        } else if (type != null) {
            policies = policyRepository.findByType(type, pageable);
        } else {
            policies = policyRepository.findAll(pageable);
        }

        return policies.map(this::convertToResponse);
    }

    public PolicyResponse getPolicyById(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + id));
        return convertToResponse(policy);
    }

    @Transactional
    @CacheEvict(value = "policyStats", allEntries = true)
    public PolicyResponse createPolicy(PolicyRequest request) {
        Policy policy = new Policy();
        policy.setPolicyNumber(generatePolicyNumber());
        policy.setHolderName(request.getHolderName());
        policy.setType(request.getType());
        policy.setStatus(request.getStatus());
        policy.setPremiumAmount(request.getPremiumAmount());
        policy.setStartDate(request.getStartDate());
        policy.setEndDate(request.getEndDate());

        Policy savedPolicy = policyRepository.save(policy);
        publishPolicyEvent("POLICY_CREATED", savedPolicy);
        return convertToResponse(savedPolicy);
    }

    @Transactional
    @CacheEvict(value = "policyStats", allEntries = true)
    public PolicyResponse updatePolicy(Long id, PolicyRequest request) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + id));

        policy.setHolderName(request.getHolderName());
        policy.setType(request.getType());
        policy.setStatus(request.getStatus());
        policy.setPremiumAmount(request.getPremiumAmount());
        policy.setStartDate(request.getStartDate());
        policy.setEndDate(request.getEndDate());

        Policy updatedPolicy = policyRepository.save(policy);
        publishPolicyEvent("POLICY_UPDATED", updatedPolicy);
        return convertToResponse(updatedPolicy);
    }

    @Transactional
    @CacheEvict(value = "policyStats", allEntries = true)
    public void deletePolicy(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + id));
        policy.setStatus(PolicyStatus.CANCELLED);
        policyRepository.save(policy);
        publishPolicyEvent("POLICY_CANCELLED", policy);
    }

    @Cacheable(value = "policyStats", key = "'global'")
    public PolicyStatsResponse getPolicyStats() {
        long total = policyRepository.count();
        long active = policyRepository.countByStatus(PolicyStatus.ACTIVE);
        long expired = policyRepository.countByStatus(PolicyStatus.EXPIRED);
        long pending = policyRepository.countByStatus(PolicyStatus.PENDING);
        long cancelled = policyRepository.countByStatus(PolicyStatus.CANCELLED);

        Map<String, Long> byType = new HashMap<>();
        byType.put("HEALTH", policyRepository.countByType(PolicyType.HEALTH));
        byType.put("LIFE", policyRepository.countByType(PolicyType.LIFE));
        byType.put("VEHICLE", policyRepository.countByType(PolicyType.VEHICLE));
        byType.put("PROPERTY", policyRepository.countByType(PolicyType.PROPERTY));

        Map<String, Long> byStatus = new HashMap<>();
        byStatus.put("ACTIVE", active);
        byStatus.put("EXPIRED", expired);
        byStatus.put("PENDING", pending);
        byStatus.put("CANCELLED", cancelled);

        return new PolicyStatsResponse(total, active, expired, pending, cancelled, byType, byStatus);
    }

    private String generatePolicyNumber() {
        int year = LocalDate.now().getYear();
        String prefix = "POL-" + year + "-";
        long count = policyRepository.countByPolicyNumberPrefix(prefix);
        return String.format("%s%04d", prefix, count + 1);
    }

    private PolicyResponse convertToResponse(Policy policy) {
        return new PolicyResponse(
                policy.getId(),
                policy.getPolicyNumber(),
                policy.getHolderName(),
                policy.getType(),
                policy.getStatus(),
                policy.getPremiumAmount(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getCreatedAt(),
                policy.getUpdatedAt()
        );
    }

    private void publishPolicyEvent(String eventType, Policy policy) {
        try {
            PolicyEvent event = new PolicyEvent(
                UUID.randomUUID().toString(),
                eventType,
                policy.getId(),
                policy.getPolicyNumber(),
                policy.getHolderName(),
                policy.getType().toString(),
                getCurrentUsername(),
                Instant.now()
            );
            kafkaTemplate.send(kafkaTopic, policy.getId().toString(), event);
            log.info("Published Kafka event: {} for policy: {}", eventType, policy.getPolicyNumber());
        } catch (Exception e) {
            log.error("Failed to publish Kafka event for policy {}: {}", policy.getPolicyNumber(), e.getMessage());
        }
    }

    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return authentication.getName();
            }
        } catch (Exception e) {
            log.error("Failed to get current username: {}", e.getMessage());
        }
        return "system";
    }
}
