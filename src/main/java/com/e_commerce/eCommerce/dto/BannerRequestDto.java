package com.e_commerce.eCommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BannerRequestDto {

    @NotBlank(message = "Banner title is required")
    private String title;
    private String eyebrow;
    private String subtitle;
    private String imageUrl;
    private String ctaLabel;
    private String ctaLink;
    private BannerLinkType ctaLinkType;
    @Builder.Default
    @Min(value = 0, message = "Display order cannot be negative")
    private Integer displayOrder = 0;
    @Builder.Default
    private Boolean active = true;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}

