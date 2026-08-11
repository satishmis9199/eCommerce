package com.e_commerce.eCommerce.mapper;

import com.e_commerce.eCommerce.dto.NoticeResponseDto;
import com.e_commerce.eCommerce.entity.Notice;

public class NoticeMapper {

    private NoticeMapper() {}

    public static NoticeResponseDto toDto(Notice n) {
        return NoticeResponseDto.builder()
                .id(n.getId())
                .title(n.getTitle())
                .noticeText(n.getNoticeText())
                .status(n.getStatus())
                .displayLocation(n.getDisplayLocation())
                .type(n.getType())
                .startAt(n.getStartAt())
                .endAt(n.getEndAt())
                .priority(n.getPriority())
                .popupDurationSeconds(n.getPopupDurationSeconds())
                .createdAt(n.getCreatedAt())
                .updatedAt(n.getUpdatedAt())
                .build();
    }
}