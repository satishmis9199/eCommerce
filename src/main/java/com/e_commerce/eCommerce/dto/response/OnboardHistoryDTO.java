package com.e_commerce.eCommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class OnboardHistoryDTO {

    private Long id;
    private Long vendorId;
    private String vendorName;
    private String fromStatus;
    private String toStatus;
    private String remarks;
    private String tenantId;
    private String actedBy;
    private LocalDateTime createdAt;
}