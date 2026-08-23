package com.e_commerce.eCommerce.dto.response;

import com.e_commerce.eCommerce.enums.TicketCategory;
import com.e_commerce.eCommerce.enums.TicketStatus;

import java.time.LocalDateTime;

public record TicketSummaryResponseDto(
        Long id,
        String ticketNumber,
        TicketCategory category,
        String subject,
        TicketStatus status,
        String orderNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}