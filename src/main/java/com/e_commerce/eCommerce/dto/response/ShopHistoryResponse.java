package com.e_commerce.eCommerce.dto.response;

import com.e_commerce.eCommerce.enums.ShopStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShopHistoryResponse {

    private Long id;
    private String status;
    private String reason;
    private String changedAt;
    private Long changedBy;
    private String changedByName;

    public ShopHistoryResponse(
            Long id,
            ShopStatus status,
            String reason,
            LocalDateTime changedAt,
            Long changedBy,
            String changedByName
    ) {
        this.id = id;
        this.status = status != null ? status.name() : null;
        this.reason = reason;
        this.changedAt = changedAt != null
                ? changedAt.toString()
                : null;
        this.changedBy = changedBy;
        this.changedByName = changedByName;
    }
}