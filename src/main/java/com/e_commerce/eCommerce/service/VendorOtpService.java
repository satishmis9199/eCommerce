
        package com.e_commerce.eCommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VendorOtpService {

    private static final String OTP_CACHE_NAME = "vendorLoginOtp";
    private static final long OTP_EXPIRY_MINUTES = 2;

    private final CacheManager cacheManager;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateOtp() {
        return String.format(
                "%06d",
                secureRandom.nextInt(1_000_000)
        );
    }

    public void saveOtp(
            String tenantId,
            Long userId,
            String otp) {

        validateTenantAndUser(tenantId, userId);

        if (otp == null || otp.isBlank()) {
            throw new IllegalArgumentException("OTP is required");
        }

        String cacheKey = buildCacheKey(
                tenantId,
                userId
        );

        String otpHash = passwordEncoder.encode(otp);

        LocalDateTime expiryTime =
                LocalDateTime.now()
                        .plusMinutes(OTP_EXPIRY_MINUTES);

        String cacheValue =
                otpHash + "|" + expiryTime;

        getOtpCache().put(
                cacheKey,
                cacheValue
        );
    }

    public boolean verifyOtp(
            String tenantId,
            Long userId,
            String enteredOtp) {

        validateTenantAndUser(tenantId, userId);

        if (enteredOtp == null || enteredOtp.isBlank()) {
            return false;
        }

        String cacheKey =
                buildCacheKey(
                        tenantId,
                        userId
                );

        Cache cache = getOtpCache();

        String cacheValue =
                cache.get(
                        cacheKey,
                        String.class
                );

        if (cacheValue == null) {
            return false;
        }

        String[] parts =
                cacheValue.split("\\|", 2);

        if (parts.length != 2) {
            cache.evict(cacheKey);
            return false;
        }

        String storedOtpHash = parts[0];

        LocalDateTime expiryTime;

        try {
            expiryTime =
                    LocalDateTime.parse(parts[1]);

        } catch (Exception e) {
            cache.evict(cacheKey);
            return false;
        }

        if (LocalDateTime.now().isAfter(expiryTime)) {
            cache.evict(cacheKey);
            return false;
        }

        boolean valid =
                passwordEncoder.matches(
                        enteredOtp,
                        storedOtpHash
                );

        if (!valid) {
            return false;
        }

        cache.evict(cacheKey);

        return true;
    }

    public void deleteOtp(
            String tenantId,
            Long userId) {

        if (tenantId == null
                || tenantId.isBlank()
                || userId == null) {

            return;
        }

        String cacheKey =
                buildCacheKey(
                        tenantId,
                        userId
                );

        getOtpCache().evict(cacheKey);
    }

    public boolean otpExists(
            String tenantId,
            Long userId) {

        if (tenantId == null
                || tenantId.isBlank()
                || userId == null) {

            return false;
        }

        String cacheKey =
                buildCacheKey(
                        tenantId,
                        userId
                );

        return getOtpCache()
                .get(cacheKey) != null;
    }

    private String buildCacheKey(
            String tenantId,
            Long userId) {

        return tenantId + ":" + userId;
    }

    private Cache getOtpCache() {

        Cache cache =
                cacheManager.getCache(
                        OTP_CACHE_NAME
                );

        if (cache == null) {
            throw new IllegalStateException(
                    "OTP cache '"
                            + OTP_CACHE_NAME
                            + "' is not configured"
            );
        }

        return cache;
    }

    private void validateTenantAndUser(
            String tenantId,
            Long userId) {

        if (tenantId == null
                || tenantId.isBlank()) {

            throw new IllegalArgumentException(
                    "Tenant ID is required"
            );
        }

        if (userId == null) {

            throw new IllegalArgumentException(
                    "User ID is required"
            );
        }
    }
}
