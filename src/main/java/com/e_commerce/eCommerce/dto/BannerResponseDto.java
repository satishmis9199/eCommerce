package com.e_commerce.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerResponseDto {

    private Long id;

    private String title;

    private String eyebrow;

    private String imageUrl;

    private Integer displayOrder;

    private Boolean active;

    private String status;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}