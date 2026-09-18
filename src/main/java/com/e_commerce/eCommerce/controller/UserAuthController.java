package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.config.JwtUtil;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.AuthMeResponse;
import com.e_commerce.eCommerce.dto.LoginRequestDTO;
import com.e_commerce.eCommerce.dto.RegisterRequestDTO;
import com.e_commerce.eCommerce.entity.Roles;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.enums.NotificationType;
import com.e_commerce.eCommerce.event.VendorNotificationEvent;
import com.e_commerce.eCommerce.repository.UserRepos;
import com.e_commerce.eCommerce.repository.VendorRepository;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.NotificationService;
import com.e_commerce.eCommerce.service.UserAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

public class UserAuthController {
    private final UserAuthService userAuthService;
    private  final String notificationTitle="User Has Logged In";

    private static final Logger logger =
            LoggerFactory.getLogger(UserAuthController.class);

    private final AuthenticationManager authenticationManager;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;
    @Autowired
    UserRepos userRepository;
    @Autowired
    VendorRepository vendorRepository;
    private final BuildProperties buildProperties;




    private final JwtUtil jwtUtil;


    @PostMapping("/u1/v1/auth/login")

    public ResponseEntity<?> login(
            @RequestBody LoginRequestDTO dto,
            HttpServletRequest request,
            HttpServletResponse response) {

        try {
            String tenantId = TenantContext.getTenantId();

            User existingUser =
                    userRepository.findByEmailAndTenantId(
                            dto.getEmail(),
                            tenantId
                    );

            if (existingUser != null
                    && existingUser.getPassword() == null
                    && "GOOGLE".equalsIgnoreCase(
                    existingUser.getAuthProvider()
            )) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(
                                Map.of(
                                        "success", false,
                                        "message",
                                        "This account uses Google Sign-In. Please reset your password to enable password login.",
                                        "action",
                                        "RESET_PASSWORD"
                                )
                        );
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(),
                            dto.getPassword()
                    )
            );

            CustomUserDetail customUser =
                    (CustomUserDetail) authentication.getPrincipal();

            User user = customUser.getUser();
            if (user.getRole() != Roles.USER) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "success", false,
                                "message", "Only customers can login from this portal."
                        ));
            }
            if (!tenantId.equals(user.getTenantId())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "success", false,
                                "message", "User not found."
                        ));
            }

            // Account Locked
            if (Boolean.TRUE.equals(user.getAccountLocked())) {
                return ResponseEntity.status(HttpStatus.LOCKED)
                        .body(Map.of(
                                "success", false,
                                "message", "Your account is locked."
                        ));
            }

            if (!Boolean.TRUE.equals(user.getActive())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "success", false,
                                "message", "Your account is inactive."
                        ));
            }

            // Update Login Details
            user.setLastLoginIp(getClientIp(request));
            user.setLastLoginDevice(request.getHeader("User-Agent"));
            user.setLastLoginTime(LocalDateTime.now());
            user.setFailedLoginAttempt(0);

            userRepository.save(user);
            String token = jwtUtil.generateToken(
                    user.getId(),
                    user.getEmail(),
                    user.getRole().name()
            );

            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // true in production HTTPS
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60);

            response.addCookie(cookie);
            logger.error("Current Thread1 "+Thread.currentThread());
            eventPublisher.publishEvent(
                    VendorNotificationEvent.builder()
                            .tenantId(tenantId)
                            .vendorId(user.getVendorId())
                            .notificationType(NotificationType.USER_LOGIN)
                            .title(notificationTitle)
                            .message(user.getFirstName() +" has logged In ")
                            .build()
            );
            logger.error("Event pulished");
            notificationService.saveNotification(tenantId,user.getVendorId(),-1L, NotificationType.USER_LOGIN,notificationTitle, user.getFirstName() +" has logged In ");

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Login successful."
                    )
            );

        } catch (BadCredentialsException ex) {

            User user = userRepository.findByEmail(dto.getEmail());

            if (user != null) {

                int failedAttempt = user.getFailedLoginAttempt() == null
                        ? 0
                        : user.getFailedLoginAttempt();

                failedAttempt++;

                user.setFailedLoginAttempt(failedAttempt);

                if (failedAttempt >= 3) {
                    user.setAccountLocked(true);
                    user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));
                }

                userRepository.save(user);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", "Invalid email or password."
                    ));

        } catch (Exception ex) {

            logger.error("Login Error", ex);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Something went wrong. Please try again."
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


    @PostMapping("/u1/v1/auth/logout")
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


    @PostMapping("u1/v1/user/register")
    public ResponseEntity<?> registerUserData(@RequestBody RegisterRequestDTO registerRequestDTO, HttpServletRequest request) {
        try {
            String url = request.getServerName();
            User user = userAuthService.registerUser(registerRequestDTO, url);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message","Registration completed successfully. Please login."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/u1/v1/auth/me")

    public ResponseEntity<?> checkAuthentication(
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        if (userDetail == null) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", "Unauthorized"
                    ));
        }
        User userDetail1 = userDetail.getUser();
        if(userDetail.getRole()!=Roles.USER){
           return ResponseEntity.badRequest()
                   .body(
                           Map.of(
                           )
                   );

        }

        AuthMeResponse.UserData user =
                new AuthMeResponse.UserData(

                        userDetail1.getId(),
                        userDetail1.getFirstName(),
                        userDetail1.getLastName(),
                        userDetail1.getEmail(),
                        userDetail1.getMobileNumber(),
                        userDetail1.getProfileImage(),
                        userDetail1.getRole(),
                        userDetail1.getTenantId(),
                        ""

                );

        return ResponseEntity.ok(
                new AuthMeResponse(true, user)
        );
    }
    @GetMapping("/version")
    public ResponseEntity<Map<String,String>> getCodeVersionDetails(){
        Map<String,String> buildDetails=new HashMap<>();
        buildDetails.put("version",buildProperties.getVersion());
        buildDetails.put("artifactName",buildProperties.getName());
        buildDetails.put("group",buildProperties.getGroup());
        buildDetails.put("date",String.valueOf(buildProperties.getTime()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        buildDetails
                );

    }





}