package com.e_commerce.eCommerce.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private final String SECRET = "mysecretkeymysecretkeymysecretkey123";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());


    public String generateToken(Long id, String username, String role) {

        return Jwts.builder()
                .setSubject(username)
                .claim("id", id.toString())   // UUID → String
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(key)
                .compact();
    }


    public Claims extractAllClaims(String token) {
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
        String id = extractAllClaims(token).get("id", String.class);
        return Long.parseLong(id);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }


    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }


    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}