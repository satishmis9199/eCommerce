package com.e_commerce.eCommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FestivalBannerResponseDto {

    private Long id;

    private String title;

    private String message;

    private String bannerImage;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private Boolean active;

    private LocalDateTime createdAt;

    private String createdBy;

    private LocalDateTime updatedAt;

    private String updatedBy;
}