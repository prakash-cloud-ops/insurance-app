package com.insurance.audit.service;

import com.insurance.audit.model.AuditLog;
import com.insurance.audit.model.PolicyEvent;
import com.insurance.audit.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Transactional
    public void saveAuditLog(PolicyEvent event) {
        if (auditLogRepository.existsByEventId(event.getEventId())) {
            log.warn("Duplicate event detected, skipping: {}", event.getEventId());
            return;
        }

        AuditLog auditLog = new AuditLog();
        auditLog.setEventId(event.getEventId());
        auditLog.setEventType(event.getEventType());
        auditLog.setPolicyId(event.getPolicyId());
        auditLog.setPolicyNumber(event.getPolicyNumber());
        auditLog.setHolderName(event.getHolderName());
        auditLog.setPolicyType(event.getPolicyType());
        auditLog.setPerformedBy(event.getPerformedBy());
        auditLog.setEventTimestamp(event.getTimestamp());
        auditLog.setProcessedAt(Instant.now());

        auditLogRepository.save(auditLog);
        log.info("Audit log saved for event: {} | policy: {}", event.getEventType(), event.getPolicyNumber());
    }
}
