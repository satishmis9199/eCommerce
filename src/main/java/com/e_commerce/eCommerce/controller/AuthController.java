package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.config.JwtUtil;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.LoginRequestDTO;
import com.e_commerce.eCommerce.entity.Roles;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.repository.UserRepos;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    @Autowired
    UserRepos userRepository;


    private final JwtUtil jwtUtil;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil
    ) {

        this.authenticationManager =
                authenticationManager;

        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/v1/auth/super-admin/login")
    public ResponseEntity<?> login(

            @RequestBody LoginRequestDTO dto,

            HttpServletRequest request,

            HttpServletResponse response
    ) throws Exception {

        try {


            Authentication auth =

                    authenticationManager.authenticate(

                            new UsernamePasswordAuthenticationToken(

                                    dto.getEmail(),

                                    dto.getPassword()
                            )
                    );
            String loginIp = getClientIp(request);
            String loginDevice = request.getHeader("User-Agent");
            logger.info("Loginn Ip --- :: {}", loginIp);
            logger.info("Loginn Device --- :: {}", loginDevice);
            logger.info("Requet data --- :: {}", request);
            CustomUserDetail user =

                    (CustomUserDetail) auth.getPrincipal();
            Optional<User> user1 = userRepository.findById(user.getId());


            if (!user1.isPresent()) {

                throw new RuntimeException("User not found");

                // use user

            }
            User user2 = user1.get();
            String token =

                    jwtUtil.generateToken(

                            user.getId(),

                            user.getUsername(),

                            user.getRole().name()
                    );


            Cookie cookie =
                    new Cookie("token", token);

            cookie.setHttpOnly(true);

            cookie.setSecure(false);

            cookie.setPath("/");

            cookie.setMaxAge(60 * 60);

            response.addCookie(cookie);


            String redirectUrl =
                    request.getParameter("continue");

            if (
                    redirectUrl == null
                            || redirectUrl.isBlank()
            ) {

                redirectUrl =
                        "";
            }
            user2.setLastLoginIp(loginIp);
            user2.setLastLoginDevice(loginDevice);
            user2.setLastLoginTime(LocalDateTime.now());
            user2.setFailedLoginAttempt(0);
            userRepository.save(user2);
            Roles role = user.getRole();
            logger.info("Authentic Person role is :: " + role);
            String currentTenat = TenantContext.getTenantId();
            logger.info(" tenant id is " + currentTenat);
            if (role != Roles.SUPER_ADMIN) {
                return ResponseEntity.status(401)
                        .body(Map.of(
                                "success", false,
                                "message", "Please Login through a Vendor Portal"
                        ));

            }

            redirectUrl = "/s1/super/admin/v1/dashboard";
            return ResponseEntity.ok(

                    Map.of(

                            "status", "SUCCESS",

                            "message", "Login successful.",

                            "role", user.getRole().name(),

                            "user", Map.of(

                                    "id", user.getId(),

                                    "firstName", user2.getFirstName(),

                                    "lastName", user2.getLastName(),

                                    "email", user2.getEmail(),

                                    "profileImage", user2.getProfileImage() == null ? "" : user2.getProfileImage()

                            ),

                            "redirectUrl", redirectUrl

                    )

            );

        } catch (Exception e) {

            User user =
                    userRepository.findByEmail(dto.getEmail());

            if (user != null) {


                int failedAttempt =
                        user.getFailedLoginAttempt() == null
                                ? 0
                                : user.getFailedLoginAttempt();
                if (failedAttempt >= 2) {
                    user.setAccountLocked(true);
                }

                user.setFailedLoginAttempt(failedAttempt + 1);

                userRepository.save(user);

                logger.info("Failed Login Count : {}",
                        user.getFailedLoginAttempt());
            }

            logger.info("LOGIN ERROR", e);

            return ResponseEntity.status(401)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isBlank()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }


    @PostMapping("/super/admin/logout")
    public ResponseEntity<?> logout(

            HttpServletRequest request,

            HttpServletResponse response
    ) {

        SecurityContextHolder.clearContext();

        HttpSession session =
                request.getSession(false);

        if (session != null) {

            session.invalidate();
        }

        Cookie jwtCookie =
                new Cookie("token", "");

        jwtCookie.setHttpOnly(true);

        jwtCookie.setSecure(false);

        jwtCookie.setPath("/");

        jwtCookie.setMaxAge(0);

        response.addCookie(jwtCookie);


        Cookie sessionCookie =
                new Cookie("JSESSIONID", "");

        sessionCookie.setHttpOnly(true);

        sessionCookie.setSecure(false);

        sessionCookie.setPath("/");

        sessionCookie.setMaxAge(0);

        response.addCookie(sessionCookie);

        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        response.setHeader(
                "Pragma",
                "no-cache"
        );

        response.setHeader(
                "Expires",
                "0"
        );

        return ResponseEntity.ok(

                Map.of(

                        "success", true,

                        "message",
                        "Logout Successful",

                        "redirectUrl",
                        "/api/login"
                )
        );
    }


}