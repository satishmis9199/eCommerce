package com.e_commerce.eCommerce.config;

import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.repository.UserRepos;

import com.e_commerce.eCommerce.service.CustomUserDetail;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    private UserRepos userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
//        if (uri.startsWith("/ws") || uri.startsWith("/api") ) {
//
//            filterChain.doFilter(request, response);
//
//            return;
//        }

        try {

            String token = extractToken(request);
            if (token == null || token.isBlank()) {

                logger.warn("⚠️ No JWT token found");

                SecurityContextHolder.clearContext();

                filterChain.doFilter(request, response);

                return;
            }
            if (!jwtUtil.validateToken(token)) {

                logger.warn("❌ Invalid JWT Token");

                SecurityContextHolder.clearContext();

                filterChain.doFilter(request, response);

                return;
            }


            Long userId = jwtUtil.extractId(token);

            String username = jwtUtil.extractUsername(token);


            // Load user
            User user = userRepository.findById(userId).orElse(null);

            if (user == null) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }
            CustomUserDetail userDetails = new CustomUserDetail(user);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                    null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);

            logger.info(" AUTHENTICATION SET : {}", username);

        } catch (Exception e) {

            logger.info("JWT PROCESSING ERROR : {}", e.getMessage(), e);

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        if (request.getCookies() != null) {

            for (Cookie cookie : request.getCookies()) {

                if ("token".equals(cookie.getName())) {

                    return cookie.getValue();
                }
            }
        }


        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            logger.info("TOKEN FOUND IN HEADER");

            return authHeader.substring(7);
        }

        return null;
    }
}