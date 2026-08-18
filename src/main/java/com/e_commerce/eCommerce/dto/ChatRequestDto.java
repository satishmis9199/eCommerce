package com.e_commerce.eCommerce.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequestDto(

        @NotBlank(message = "message must not be blank")
        String message,

        // Optional. If the client already has one (from a previous reply),
        // pass it back to keep the same conversation/memory going.
        String conversationId
) {
}
