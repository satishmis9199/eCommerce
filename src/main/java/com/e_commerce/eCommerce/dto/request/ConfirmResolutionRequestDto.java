package com.e_commerce.eCommerce.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmResolutionRequestDto {

    @NotNull(message = "confirmed is required")
    private Boolean confirmed;

    // Optional note from the customer, e.g. why the issue isn't actually resolved
    private String note;
}