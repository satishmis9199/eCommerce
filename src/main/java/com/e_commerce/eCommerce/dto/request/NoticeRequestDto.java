package com.e_commerce.eCommerce.dto.request;

import com.e_commerce.eCommerce.enums.NoticeDisplayLocation;
import com.e_commerce.eCommerce.enums.NoticeStatus;
import com.e_commerce.eCommerce.enums.NoticeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class NoticeRequestDto {

    // Optional heading — used by the popup ("Testing Phase"). Can be left
    // null for bar-only notices.
    @Size(max = 150, message = "Title must not exceed 150 characters")
    private String title;

    @NotBlank(message = "Notice text is required")
    @Size(max = 500, message = "Notice text must not exceed 500 characters")
    private String noticeText;

    private NoticeStatus status;


    private NoticeDisplayLocation displayLocation;


    private NoticeType type;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    @PositiveOrZero(message = "Priority must be zero or a positive number")
    private Integer priority = 0;

    @Positive(message = "Popup duration must be a positive number of seconds")
    private Integer popupDurationSeconds = 5;

    // ---------------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------------
    public NoticeRequestDto() {
    }

    public NoticeRequestDto(String title, String noticeText, NoticeStatus status,
                            NoticeDisplayLocation displayLocation, NoticeType type,
                            LocalDateTime startAt, LocalDateTime endAt, Integer priority,
                            Integer popupDurationSeconds) {
        this.title = title;
        this.noticeText = noticeText;
        this.status = status;
        this.displayLocation = displayLocation;
        this.type = type;
        this.startAt = startAt;
        this.endAt = endAt;
        this.priority = priority;
        this.popupDurationSeconds = popupDurationSeconds;
    }

    // ---------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNoticeText() {
        return noticeText;
    }

    public void setNoticeText(String noticeText) {
        this.noticeText = noticeText;
    }

    public NoticeStatus getStatus() {
        return status;
    }

    public void setStatus(NoticeStatus status) {
        this.status = status;
    }

    public NoticeDisplayLocation getDisplayLocation() {
        return displayLocation;
    }

    public void setDisplayLocation(NoticeDisplayLocation displayLocation) {
        this.displayLocation = displayLocation;
    }

    public NoticeType getType() {
        return type;
    }

    public void setType(NoticeType type) {
        this.type = type;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPopupDurationSeconds() {
        return popupDurationSeconds;
    }

    public void setPopupDurationSeconds(Integer popupDurationSeconds) {
        this.popupDurationSeconds = popupDurationSeconds;
    }
}