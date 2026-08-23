package com.e_commerce.eCommerce.dto.request;

import com.e_commerce.eCommerce.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketStatusUpdateDto {

    @NotNull(message = "status is required")
    private TicketStatus status;

    // Required when status = RESOLVED, optional otherwise
    private String resolutionNote;
}