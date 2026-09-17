package com.e_commerce.eCommerce.dto.response;

import com.e_commerce.eCommerce.entity.Notification;
import java.time.LocalDateTime;


public class NotificationDto {

    private Long id;
    private String type;
    private String title;
    private String message;
    private Long orderId;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationDto() {
    }

    public static NotificationDto fromEntity(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.id = n.getId();
        dto.type = n.getType();
        dto.title = n.getTitle();
        dto.message = n.getMessage();
        dto.orderId = n.getOrderId();
        dto.isRead = n.isRead();
        dto.createdAt = n.getCreatedAt();
        return dto;
    }

    // ---- Getters & Setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}