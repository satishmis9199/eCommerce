package com.e_commerce.eCommerce.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Body for POST /auth/password-reset/request — used when a user (or admin,
 * on the user's behalf) wants a fresh setup/reset link, e.g. "forgot
 * password" or "resend setup link". Not required for the initial
 * Super-Admin-creates-user flow (that path calls the service method
 * directly — see SUPER_ADMIN_INTEGRATION.md).
 */
public class PasswordResetRequestDto {

    @NotBlank
    @Email
    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}