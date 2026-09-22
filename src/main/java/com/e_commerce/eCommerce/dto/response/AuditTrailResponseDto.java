package com.e_commerce.eCommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditTrailResponseDto {
    private Long id;
    private Long userId;
    private String action;
    private String remarks;
    private LocalDateTime createdAt;
    private String serverIp;
    private Long executionTimeMs;
}