package com.e_commerce.eCommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketMessageRequestDto {

    @NotBlank(message = "message must not be blank")
    private String message;
}