package com.e_commerce.eCommerce.controller;

import java.time.Duration;
import java.util.Map;

import com.e_commerce.eCommerce.config.JwtUtil;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.request.GoogleAuthRequest;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.enums.NotificationType;
import com.e_commerce.eCommerce.event.VendorNotificationEvent;
import com.e_commerce.eCommerce.service.GoogleAuthService;
import com.e_commerce.eCommerce.service.NotificationService;
import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/u1/v1/auth")
@AllArgsConstructor
//@NoArgsConstructor
public class GoogleAuthController {

    private final GoogleAuthService authService;
    private final JwtUtil jwtService;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationService notificationService;
    private final String notificationTitle = "User Login Successful";




    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(
            @Valid @RequestBody GoogleAuthRequest request,
            HttpServletResponse response) {

        try {
            String tenantId= TenantContext.getTenantId();

            User user = authService.authenticateWithGoogle(
                    request.getIdToken(),tenantId
            );

            eventPublisher.publishEvent(
                    VendorNotificationEvent.builder()
                            .tenantId(tenantId)
                            .vendorId(user.getVendorId())
                            .notificationType(NotificationType.USER_LOGIN)
                            .title(notificationTitle)
                            .message(user.getFirstName() + " has successfully logged in to your store.")
                            .build()
            );

            notificationService.saveNotification(
                    tenantId,
                    user.getVendorId(),
                    -1L,
                    NotificationType.USER_LOGIN,
                    "New User Login",
                    user.getFirstName() + " has successfully logged in to your store."
            );

            String token = jwtService.generateToken(user.getId(),user.getFirstName(),String.valueOf(user.getRole()));


            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // true in production HTTPS
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);

            response.addCookie(cookie);

            response.addHeader(
                    HttpHeaders.SET_COOKIE,
                    cookie.toString()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Logged in with Google successfully."
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage() != null
                                            ? e.getMessage()
                                            : "Google authentication failed."
                            )
                    );
        }
    }
}