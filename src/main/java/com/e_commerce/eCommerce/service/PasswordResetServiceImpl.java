package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.PasswordResetProperties;
import com.e_commerce.eCommerce.dto.request.EmailRequestDto;
import com.e_commerce.eCommerce.dto.response.TokenVerificationResponse;
import com.e_commerce.eCommerce.entity.PasswordResetToken;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.exception.InvalidTokenException;
import com.e_commerce.eCommerce.exception.TokenAlreadyUsedException;
import com.e_commerce.eCommerce.exception.TokenExpiredException;
import com.e_commerce.eCommerce.repository.PasswordResetTokenRepository;
import com.e_commerce.eCommerce.repository.UserRepos;
import com.e_commerce.eCommerce.util.TokenGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetServiceImpl.class);

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepos userRepository;
    private final PasswordEncoder passwordEncoder; // reuse existing bean — not redefined here
    private final TokenGenerator tokenGenerator;
    private final EmailService emailService;
    private final PasswordResetProperties properties;

    public PasswordResetServiceImpl(PasswordResetTokenRepository tokenRepository,
                                    UserRepos userRepository,
                                    PasswordEncoder passwordEncoder,
                                    TokenGenerator tokenGenerator, EmailService emailService,
                                    PasswordResetProperties properties) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.emailService = emailService;
        this.properties = properties;
    }

    @Override
    @Transactional
    public String initiatePasswordSetup(User user, HttpServletRequest request) {
        Instant now = Instant.now();

        tokenRepository.invalidateOutstandingTokensForUser(user.getId(), now);

        String rawToken = tokenGenerator.generateRawToken(properties.getTokenByteLength());
        String tokenHash = tokenGenerator.hash(rawToken);

        PasswordResetToken entity = new PasswordResetToken();
        entity.setUser(user);
        entity.setTenantId(user.getTenantId());
        entity.setTokenHash(tokenHash);
        entity.setExpiresAt(now.plus(properties.getTokenExpiry()));
        tokenRepository.save(entity);

        String serverName = request.getServerName();
        String links = "http://" + serverName + "/reset-password?token=" + rawToken;

        EmailRequestDto emailRequest = EmailRequestDto.builder()
                .to(user.getEmail())
                .subject("Set your password")
                .text("")
                .templateName("password-reset")
                .templateVariables(Map.of(
                        "name", user.getFirstName(),
                        "resetLink", links,
                        "expiryMinutes", 30
                ))
                .build();
        emailService.sendEmailAsync(emailRequest);

        return links;
    }

    @Override
    @Transactional(readOnly = true)
    public TokenVerificationResponse verifyToken(String rawToken) {
        PasswordResetToken entity = lookupByRawToken(rawToken).orElse(null);

        if (entity == null) {
            return TokenVerificationResponse.of(TokenVerificationResponse.Status.INVALID, null);
        }
        if (entity.isUsed()) {
            return TokenVerificationResponse.of(TokenVerificationResponse.Status.USED, null);
        }
        if (entity.isExpired()) {
            return TokenVerificationResponse.of(TokenVerificationResponse.Status.EXPIRED, null);
        }
        return TokenVerificationResponse.of(TokenVerificationResponse.Status.VALID, maskEmail(entity.getUser().getEmail()));
    }

    @Override
    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        String tokenHash = tokenGenerator.hash(rawToken);
        PasswordResetToken entity = tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(InvalidTokenException::new);

        if (entity.isUsed()) {
            throw new TokenAlreadyUsedException();
        }
        if (entity.isExpired()) {
            throw new TokenExpiredException();
        }

        User user = entity.getUser();
        if (entity.getTenantId() == null || !entity.getTenantId().equals(user.getTenantId())) {
            log.warn("Tenant mismatch on password reset attempt: tokenTenant={}, userTenant={}",
                    entity.getTenantId(), user.getTenantId());
            throw new InvalidTokenException();
        }
        if (user.getActive() == null || !user.getActive()) {
            throw new InvalidTokenException();
        }

        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        user.setCredentialsSetupComplete(true);
        userRepository.save(user);

        entity.setUsedAt(Instant.now());
        tokenRepository.save(entity);

        log.info("Password successfully reset for userId={}, tenantId={}", user.getId(), user.getTenantId());

    }

    private Optional<PasswordResetToken> lookupByRawToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return Optional.empty();
        }
        return tokenRepository.findByTokenHash(tokenGenerator.hash(rawToken));
    }

    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) return "***" + email.substring(at);
        return email.charAt(0) + "***" + email.substring(at);
    }


    @Transactional
    public String initiateForVendor(User user) {
        Instant now = Instant.now();

        tokenRepository.invalidateOutstandingTokensForUser(user.getId(), now);

        String rawToken = tokenGenerator.generateRawToken(properties.getTokenByteLength());
        String tokenHash = tokenGenerator.hash(rawToken);

        PasswordResetToken entity = new PasswordResetToken();
        entity.setUser(user);
        entity.setTenantId(user.getTenantId());
        entity.setTokenHash(tokenHash);
        entity.setExpiresAt(now.plus(properties.getTokenExpiry()));
        tokenRepository.save(entity);
        log.info("Password setup token issued for userId={}, tenantId={}, expiresAt={}",
                user.getId(), user.getTenantId(), entity.getExpiresAt());

        return rawToken;
    }
}