package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.enums.NoticeDisplayLocation;
import com.e_commerce.eCommerce.enums.NoticeStatus;
import com.e_commerce.eCommerce.enums.NoticeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeListDto {

    private Long id;

    private String title;

    private String noticeText;

    private NoticeDisplayLocation displayLocation;

    private NoticeType type;

    private Integer priority;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private NoticeStatus status;
}