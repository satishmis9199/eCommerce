package com.e_commerce.eCommerce.entity;

import com.e_commerce.eCommerce.enums.NoticeDisplayLocation;
import com.e_commerce.eCommerce.enums.NoticeStatus;
import com.e_commerce.eCommerce.enums.NoticeType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    // Optional heading — used by the popup ("Testing Phase"). Not shown
    // in the announcement bar, so this can be left null for bar-only notices.
    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "notice_text", nullable = false, columnDefinition = "TEXT")
    private String noticeText;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeStatus status;

    // Where this notice should render: announcement bar, popup, or both.
    @Enumerated(EnumType.STRING)
    @Column(name = "display_location", nullable = false)
    private NoticeDisplayLocation displayLocation;

    // Visual style hint for the frontend (warning/info/promo colors).
    @Enumerated(EnumType.STRING)
    @Column(name = "notice_type")
    private NoticeType type;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "priority")
    private Integer priority = 0;

    @Column(name = "popup_duration_seconds")
    private Integer popupDurationSeconds = 5;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = NoticeStatus.ACTIVE;
        }
        if (this.displayLocation == null) {
            this.displayLocation = NoticeDisplayLocation.BOTH;
        }
        if (this.priority == null) {
            this.priority = 0;
        }

        if (this.popupDurationSeconds == null) {
            this.popupDurationSeconds = 5;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}