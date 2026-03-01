package com.insurance.audit.consumer;

import com.insurance.audit.model.PolicyEvent;
import com.insurance.audit.service.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PolicyEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PolicyEventConsumer.class);

    @Autowired
    private AuditLogService auditLogService;

    @KafkaListener(
        topics = "${kafka.topic.policy-events:policy.events}",
        groupId = "audit-consumer-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(PolicyEvent event) {
        try {
            log.info("Received Kafka event: {} | policy: {} | by: {}",
                event.getEventType(), event.getPolicyNumber(), event.getPerformedBy());
            
            auditLogService.saveAuditLog(event);
            
            log.info("Audit log saved: {} | policy: {} | by: {}",
                event.getEventType(), event.getPolicyNumber(), event.getPerformedBy());
        } catch (Exception e) {
            log.error("Failed to save audit log for event {}: {}", event.getEventId(), e.getMessage());
        }
    }
}
