package com.e_commerce.eCommerce.dto.response;

import com.e_commerce.eCommerce.entity.Feature;
import com.e_commerce.eCommerce.entity.PlanFeature;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean active;
    private long enabledFeatureCount;
    private long totalFeatureCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> features;
}