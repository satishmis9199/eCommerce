package com.e_commerce.eCommerce.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.MediaType;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Configuration
public class SecurityConfig {
    private static final Logger log= LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JwtFilter jwtFilter;
    @Autowired TenantFilter tenantFilter;

//    @Autowired
//    private CustomOAuth2SuccessHandler successHandler;
    private final String SECRET_KEY="satishmishra";
    @Bean
    public RequestCache requestCache() {

        return new HttpSessionRequestCache() {

            @Override
            public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
//                String serverName=request.getServerName();
//                logger.info(" fetching a Server Name While entering "+serverName);

                String uri = request.getRequestURI();
                if (

                        uri.startsWith("/ws")

                                || uri.contains("firebase")

                                || uri.contains(".js")

                                || uri.contains(".css")

                                || uri.contains(".png")

                                || uri.contains(".jpg")

                                || uri.contains(".jpeg")

                                || uri.contains(".svg")

                                || uri.contains(".ico")

                                || uri.startsWith("/api/")

                                || uri.contains("sockjs")

                                || uri.contains("favicon")

                ) {

                    return;
                }

                super.saveRequest(request, response);
            }
        };
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))


                .requestCache(cache -> cache.requestCache(requestCache()))

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(

                                "/super/admin",
                                "/api/v1/auth/super-admin/login",
                                "/api/**",

                                "/css/**",
                                "/style.css",
                                "/api.js",
                                "/script.js",
                                "/api/vendor/v1/login",

                                "/js/**",
                                "/ai",

                                "/images/**",

                                "/uploads/**",

                                "/firebase-messaging-sw.js",

                                "/ws/**",

                                "/favicon.ico",
                                "/tenant-not-found",
                                "/tenant-not-found.html",
                                "/employee.html",


                                "/api/access-denied"

                        ).permitAll()

//                        .requestMatchers(("/vendor/**")).hasRole("ADMIN")
                        .requestMatchers(("/s4/**")).permitAll()
                        .requestMatchers("/s1/**").hasRole("SUPER_ADMIN")
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated())


                .exceptionHandling(ex -> ex

                        .authenticationEntryPoint(

                                (request, response, authException) -> {

                                    String uri = request.getRequestURI();

                                    boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
                                    String redirectUrl="";
                                    String tenantId=TenantContext.getTenantId();
                                    if(tenantId!=null){
                                        redirectUrl="/api/vendor/v1/login";

                                    }else{
                                        redirectUrl="/super/admin";
                                    }


                                    if (

                                            isAjax


                                    ) {

                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                                        response.getWriter().write("""
                                                {
                                                    "success": false,
                                                    "message": "Please Login First",
                                                    "redirectUrl": "/api/login"
                                                }
                                                """);

                                    } else {

                                        response.sendRedirect(redirectUrl);
                                    }
                                })

                        .accessDeniedHandler(

                                (request, response, accessDeniedException) -> {

                                    String uri = request.getRequestURI();

                                    boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));


                                    if (

                                            isAjax



                                    ) {

                                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                                        response.getWriter().write("""
                                                {
                                                    "success": false,
                                                    "message": "Access Denied",
                                                    "redirectUrl": "/api/access-denied"
                                                }
                                                """);

                                    } else {
                                        response.sendRedirect("/api/access-denied");
                                    }
                                }))



//                .oauth2Login(oauth -> oauth.successHandler(successHandler))
                .addFilterBefore(tenantFilter,
                        UsernamePasswordAuthenticationFilter.class)


                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // =====================================================
    // AUTH MANAGER
    // =====================================================
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }



}
