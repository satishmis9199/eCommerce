package com.e_commerce.eCommerce.config;

import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.repository.VendorRepository;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
@AllArgsConstructor
@Service
@Slf4j
public class JwtSecret {
    private static final String SECRET =
            "myeCommereceuhjhkhnkadjaskjdkasdjoasjdoasdjoasdjoasdjoasd";

    private final SecretKey defaultKey =
            Keys.hmacShaKeyFor(
                    SECRET.getBytes(StandardCharsets.UTF_8)
            );
    private final VendorRepository vendorRepository;
    @Cacheable(value="jwtSecret",key = "T(com.e_commerce.eCommerce.config.TenantContext).getTenantId()")
    public  SecretKey getTenantKey() {

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            throw new RuntimeException("Tenant not found");
        }

        Optional<Vendor> vendorOpt =
                vendorRepository.findByTenantId(tenantId);

        if (vendorOpt.isPresent()) {

            String tenantSecret =
                    vendorOpt.get().getJwtSecret();

            if (tenantSecret != null
                    && !tenantSecret.isBlank()) {
                log.error("Secret key found for Tenant");
                return Keys.hmacShaKeyFor(
                        Decoders.BASE64.decode(tenantSecret)
                );
            }
        }
        log.error("using default key");
        return defaultKey;
    }
}
