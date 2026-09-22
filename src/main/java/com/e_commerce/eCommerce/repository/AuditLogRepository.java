package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.EvaluationAuditTrail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<EvaluationAuditTrail, Long> {

    // Delete every record created on a given day (pass start-of-day / start-of-next-day)
    @Modifying
    @Transactional
    @Query("DELETE FROM EvaluationAuditTrail e WHERE e.createdAt >= :dayStart AND e.createdAt < :dayEnd")
    int deleteByDay(@Param("dayStart") LocalDateTime dayStart, @Param("dayEnd") LocalDateTime dayEnd);

    // Delete everything (bulk statement - faster than deleteAll() for large tables)
    @Modifying
    @Transactional
    @Query("DELETE FROM EvaluationAuditTrail e")
    int deleteAllRecords();

    @Query("""
        SELECT e FROM EvaluationAuditTrail e
        WHERE (:userId IS NULL OR e.userId = :userId)
        AND (:action IS NULL OR LOWER(e.action) LIKE LOWER(CONCAT('%', :action, '%')))
        AND (:fromDate IS NULL OR e.createdAt >= :fromDate)
        AND (:toDate IS NULL OR e.createdAt <= :toDate)
        ORDER BY e.createdAt DESC
        """)
    Page<EvaluationAuditTrail> search(
            @Param("userId") Long userId,
            @Param("action") String action,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );
}