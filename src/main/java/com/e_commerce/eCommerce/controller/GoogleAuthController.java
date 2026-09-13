package com.e_commerce.eCommerce.controller;

import java.time.Duration;
import java.util.Map;

import com.e_commerce.eCommerce.config.JwtUtil;
import com.e_commerce.eCommerce.dto.request.GoogleAuthRequest;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.service.GoogleAuthService;
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
public class GoogleAuthController {

    private final GoogleAuthService authService;
    private final JwtUtil jwtService;

    public GoogleAuthController(
            GoogleAuthService authService,
            JwtUtil jwtService) {

        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(
            @Valid @RequestBody GoogleAuthRequest request,
            HttpServletResponse response) {

        try {

            User user = authService.authenticateWithGoogle(
                    request.getIdToken()
            );

            // Existing application JWT generate hoga
            String jwt = jwtService.generateToken(user.getId(),user.getFirstName(),String.valueOf(user.getRole()));


            // Existing frontend cookie-based authentication
            ResponseCookie cookie = ResponseCookie
                    .from("JWT", jwt)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(Duration.ofDays(7))
                    .build();

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