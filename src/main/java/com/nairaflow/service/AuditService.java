package com.nairaflow.service;

import com.nairaflow.model.AuditLog;
import com.nairaflow.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    
    private final AuditLogRepository auditLogRepository;
    
    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
    
    public void log(String action, String entityType, Long entityId, Long userId,
                    String oldValue, String newValue, String ipAddress, String userAgent) {
        AuditLog auditLog = new AuditLog(action, entityType, entityId, userId, oldValue, newValue, ipAddress, userAgent);
        auditLogRepository.save(auditLog);
    }
    
    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
    
    public Page<AuditLog> getAuditLogsByUser(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
}
