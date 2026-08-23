package com.e_commerce.eCommerce.dto.response;

import com.e_commerce.eCommerce.dto.response.TicketMessageResponseDto;
import com.e_commerce.eCommerce.enums.TicketCategory;
import com.e_commerce.eCommerce.enums.TicketStatus;

import java.time.LocalDateTime;
import java.util.List;

public record TicketDetailResponseDto(
        Long id,
        String ticketNumber,
        TicketCategory category,
        String subject,
        String description,
        TicketStatus status,
        String orderNumber,
        String resolutionNote,
        Boolean customerConfirmedResolved,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime resolvedAt,
        LocalDateTime closedAt,
        List<TicketMessageResponseDto> messages
) {
}