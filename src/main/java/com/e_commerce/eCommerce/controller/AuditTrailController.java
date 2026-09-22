package com.e_commerce.eCommerce.controller;


import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.response.AuditTrailResponseDto;

import com.e_commerce.eCommerce.repository.AuditLogRepository;
import com.e_commerce.eCommerce.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/s1/v1/audit-trail")
@RequiredArgsConstructor
public class AuditTrailController {

    private final AuditLogService auditTrailService;

    // GET /admin/s1/v1/audit-trail?userId=&action=&fromDate=&toDate=&page=&size=
    @GetMapping
    public ApiResponse<Page<AuditTrailResponseDto>> getAuditTrails(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditTrailResponseDto> result =
                auditTrailService.getAuditTrails(userId, action, fromDate, toDate, pageable);
        return new ApiResponse<>(true, "Audit trail fetched successfully", result);
    }

    // GET /admin/s1/v1/audit-trail/{id}
    @GetMapping("/{id}")
    public ApiResponse<AuditTrailResponseDto> getAuditTrailById(@PathVariable Long id) {
        AuditTrailResponseDto dto = auditTrailService.getAuditTrailById(id);
        return new ApiResponse<>(true, "Audit trail record fetched successfully", dto);
    }

    // DELETE /admin/s1/v1/audit-trail/day?date=2026-09-22  (deletes every record created that day)
    @DeleteMapping("/day")
    public ApiResponse<Integer> deleteByDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        int deleted = auditTrailService.deleteByDay(date);
        return new ApiResponse<>(true, deleted + " audit trail record(s) deleted for " + date, deleted);
    }

    // DELETE /admin/s1/v1/audit-trail/all  (wipes the entire audit trail)
    @DeleteMapping("/all")
    public ApiResponse<Integer> deleteAll() {
        int deleted = auditTrailService.deleteAll();
        return new ApiResponse<>(true, "All audit trail records deleted (" + deleted + " total)", deleted);
    }
}