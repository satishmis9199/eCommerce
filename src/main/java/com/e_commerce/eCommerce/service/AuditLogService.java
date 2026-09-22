package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.dto.response.AuditTrailResponseDto;
import com.e_commerce.eCommerce.entity.EvaluationAuditTrail;
import com.e_commerce.eCommerce.repository.AuditLogRepository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuditLogService {

    private final AuditLogRepository evaluationAuditTrailRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAudit(Long userId, String action, String remarks, Long exec) {
        try {
            String serverIp = InetAddress.getLocalHost().getHostAddress();
            EvaluationAuditTrail audit = EvaluationAuditTrail.builder()
                    .userId(userId)
                    .action(action)
                    .remarks(remarks)
                    .createdAt(LocalDateTime.now())
                    .serverIp(serverIp)
                    .exceutionTtime(exec)
                    .build();
            evaluationAuditTrailRepository.save(audit);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Page<AuditTrailResponseDto> getAuditTrails(Long userId,
                                                      String action,
                                                      LocalDateTime fromDate,
                                                      LocalDateTime toDate,
                                                      Pageable pageable) {
        return evaluationAuditTrailRepository
                .search(userId, action, fromDate, toDate, pageable)
                .map(this::toDto);
    }

    public AuditTrailResponseDto getAuditTrailById(Long id) {
        EvaluationAuditTrail entity = evaluationAuditTrailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audit trail record not found with id: " + id));
        return toDto(entity);
    }

    public int deleteByDay(LocalDate day) {
        LocalDateTime dayStart = day.atStartOfDay();
        LocalDateTime dayEnd = day.plusDays(1).atStartOfDay();
        return evaluationAuditTrailRepository.deleteByDay(dayStart, dayEnd);
    }

    public int deleteAll() {
        return evaluationAuditTrailRepository.deleteAllRecords();
    }

    private AuditTrailResponseDto toDto(EvaluationAuditTrail e) {
        return AuditTrailResponseDto.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .action(e.getAction())
                .remarks(e.getRemarks())
                .createdAt(e.getCreatedAt())
                .serverIp(e.getServerIp())
                .executionTimeMs(e.getExceutionTtime())
                .build();
    }
}