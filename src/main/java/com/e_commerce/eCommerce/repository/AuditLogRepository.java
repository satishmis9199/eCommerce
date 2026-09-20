package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.EvaluationAuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<EvaluationAuditTrail,Long> {
}
