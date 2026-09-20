package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.entity.EvaluationAuditTrail;
import com.e_commerce.eCommerce.repository.AuditLogRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuditLogService {
    private final AuditLogRepository evaluationAuditTrailRepository;
    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    public void saveAudit(EvaluationAuditTrail audit) {
        evaluationAuditTrailRepository.save(audit);
    }
}
