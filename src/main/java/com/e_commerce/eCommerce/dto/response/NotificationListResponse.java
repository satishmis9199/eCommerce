package com.e_commerce.eCommerce.dto.response;

import com.e_commerce.eCommerce.dto.response.NotificationDto;
import lombok.Builder;

import java.util.List;
@Builder
public class NotificationListResponse {

    private long unreadCount;
    private List<NotificationDto> data;

    public NotificationListResponse() {
    }

    public NotificationListResponse(long unreadCount, List<NotificationDto> data) {
        this.unreadCount = unreadCount;
        this.data = data;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public List<NotificationDto> getData() {
        return data;
    }

    public void setData(List<NotificationDto> data) {
        this.data = data;
    }
}