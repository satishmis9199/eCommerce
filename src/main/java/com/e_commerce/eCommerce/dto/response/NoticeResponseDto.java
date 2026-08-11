package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.enums.NoticeDisplayLocation;
import com.e_commerce.eCommerce.enums.NoticeStatus;
import com.e_commerce.eCommerce.enums.NoticeType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
public class NoticeResponseDto {
    private Long id;
    private String title;
    private String noticeText;
    private NoticeStatus status;
    private NoticeDisplayLocation displayLocation;
    private NoticeType type;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer priority;
    private Integer popupDurationSeconds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}