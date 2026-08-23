package com.e_commerce.eCommerce.dto.request;

import com.e_commerce.eCommerce.enums.TicketCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTicketRequestDto {

    @NotNull(message = "category is required")
    private TicketCategory category;

    @NotBlank(message = "subject is required")
    @Size(max = 150, message = "subject must be at most 150 characters")
    private String subject;

    @NotBlank(message = "description is required")
    private String description;

    // Optional - fill this in when the issue is about a specific order
    private String orderNumber;
}