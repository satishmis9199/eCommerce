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
public class FeatureResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private boolean active;
    private long assignedPlanCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}