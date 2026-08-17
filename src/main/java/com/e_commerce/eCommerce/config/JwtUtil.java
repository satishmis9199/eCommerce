package com.e_commerce.eCommerce.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {
    private final JwtSecret jwtSecret;

    public String generateToken(
            Long id,
            String username,
            String role) {
        SecretKey key = jwtSecret.getTenantKey();
        return Jwts.builder()
                .setSubject(username)
                .claim("id", id.toString())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000L * 60 * 60
                        )
                )
                .signWith(key)
                .compact();
    }

    public Claims extractAllClaims(String token) {

        SecretKey key = jwtSecret.getTenantKey();

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }


    public Long extractId(String token) {

        String id =
                extractAllClaims(token)
                        .get("id", String.class);

        return Long.parseLong(id);
    }


    public String extractRole(String token) {
        return extractAllClaims(token)
                .get("role", String.class);
    }


    public boolean isTokenExpired(String token) {

        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }


    public boolean validateToken(String token) {

        try {

            return !isTokenExpired(token);

        } catch (Exception e) {

            return false;
        }
    }


    public static String generateJwtSecret() {

        SecretKey key =
                Keys.secretKeyFor(
                        SignatureAlgorithm.HS256
                );

        return Encoders.BASE64.encode(
                key.getEncoded()
        );
    }
}