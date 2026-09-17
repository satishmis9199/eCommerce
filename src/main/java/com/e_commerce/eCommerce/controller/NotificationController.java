package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.response.NotificationListResponse;
import com.e_commerce.eCommerce.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vendor/s2/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<NotificationListResponse>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        String tenantId = resolveTenantId(authentication);

        NotificationListResponse response =
                notificationService.getNotifications(
                        tenantId,
                        page,
                        size
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Notifications fetched successfully",
                        response
                )
        );
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        String tenantId = resolveTenantId(authentication);

        boolean updated =
                notificationService.markAsRead(id, tenantId);

        if (!updated) {
            return ResponseEntity
                    .status(404)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    "Notification not found",
                                    null
                            )
                    );
        }

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Notification marked as read",
                        null
                )
        );
    }

    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            Authentication authentication) {

        String tenantId = resolveTenantId(authentication);

        notificationService.markAllAsRead(tenantId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "All notifications marked as read",
                        null
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable Long id,
            Authentication authentication) {

        String tenantId = resolveTenantId(authentication);

        boolean deleted =
                notificationService.delete(id, tenantId);

        if (!deleted) {
            return ResponseEntity
                    .status(404)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    "Notification not found",
                                    null
                            )
                    );
        }

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Notification deleted successfully",
                        null
                )
        );
    }

    private String resolveTenantId(Authentication authentication) {

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException(
                    "Tenant ID not found for authenticated vendor"
            );
        }

        return tenantId;
    }
}