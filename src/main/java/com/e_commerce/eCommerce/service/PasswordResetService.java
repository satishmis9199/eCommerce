package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.dto.response.TokenVerificationResponse;
import com.e_commerce.eCommerce.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface PasswordResetService {

    String initiatePasswordSetup(User user, HttpServletRequest request);

    /**
     * GET /auth/password-reset/verify — read-only, does not consume the token.
     */
    TokenVerificationResponse verifyToken(String rawToken);

    /**
     * POST /auth/password-reset — validates token + password, updates the
     * user's password, marks the token used. Throws InvalidTokenException /
     * TokenExpiredException / TokenAlreadyUsedException as appropriate.
     */
    void resetPassword(String rawToken, String newPassword);
}
