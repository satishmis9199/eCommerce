package com.e_commerce.eCommerce.dto.response;

import com.e_commerce.eCommerce.enums.SenderType;

import java.time.LocalDateTime;

public record TicketMessageResponseDto(
        Long id,
        SenderType senderType,
        Long senderId,
        String message,
        LocalDateTime createdAt
) {
}