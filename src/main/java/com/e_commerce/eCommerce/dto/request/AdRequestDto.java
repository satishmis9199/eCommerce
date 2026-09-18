package com.e_commerce.eCommerce.dto.request;

import com.e_commerce.eCommerce.enums.AdSlotKey;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdRequestDto {

    @NotBlank(message = "Title is required")
    @Size(max = 120, message = "Title must be 120 characters or less")
    private String title;

    @NotBlank(message = "Image URL is required")
    @Size(max = 1000)
    private String imageUrl;

    @NotBlank(message = "Target URL is required")
    @Size(max = 1000)
    private String targetUrl;
    private AdSlotKey slotKey;
    @Size(max = 250)
    private String subtext;
    @Size(max = 40)
    private String ctaLabel;
    @Size(max = 120)
    private String advertiserName;
    @JsonSetter(nulls = Nulls.SKIP)
    @Setter(AccessLevel.NONE)
    private Boolean internal = Boolean.FALSE;

    @JsonSetter(nulls = Nulls.SKIP)
    @Setter(AccessLevel.NONE)
    private Boolean dismissible = Boolean.FALSE;
    public boolean isInternal() {
        return internal != null && internal;
    }

    public boolean isDismissible() {
        return dismissible != null && dismissible;
    }

    public void setInternal(Boolean internal) {
        this.internal = internal == null ? Boolean.FALSE : internal;
    }

    public void setDismissible(Boolean dismissible) {
        this.dismissible = dismissible == null ? Boolean.FALSE : dismissible;
    }
}