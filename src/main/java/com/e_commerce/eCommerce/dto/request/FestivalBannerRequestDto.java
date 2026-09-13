package com.e_commerce.eCommerce.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FestivalBannerRequestDto {

    private String title;

    private String message;

    private String bannerImage;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private Boolean active;
}