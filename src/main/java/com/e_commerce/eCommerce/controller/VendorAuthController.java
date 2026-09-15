package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.config.JwtUtil;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.AuthMeResponse;
import com.e_commerce.eCommerce.dto.LoginRequestDTO;
import com.e_commerce.eCommerce.entity.Roles;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.repository.UserRepos;
import com.e_commerce.eCommerce.repository.VendorRepository;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.VendorOtpService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
public class VendorAuthController {


    private final AuthenticationManager authenticationManager;
    private final UserRepos userRepository;
   private final VendorRepository vendorRepository;
    private final JwtUtil jwtUtil;
    private final VendorOtpService vendorOtpService;

    @PostMapping("/v1/auth/vendor/login")
    public ResponseEntity<?> login(

            @RequestBody LoginRequestDTO dto,

            HttpServletRequest request,

            HttpServletResponse response) throws Exception {
        String tenanTid = TenantContext.getTenantId();

        try {


            User user11 = userRepository.findByTenantIdAndEmail(tenanTid, dto.getEmail());
            if (user11 == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "User Not found"));
            }
            if (user11.getRole() != Roles.ADMIN) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "You Do not have Sufficient Access"));
            }

            Authentication auth =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    dto.getEmail(),
                                    dto.getPassword()));
            String loginIp = getClientIp(request);
            String loginDevice = request.getHeader("User-Agent");
            CustomUserDetail user = (CustomUserDetail) auth.getPrincipal();
            Optional<User> user1 = userRepository.findByIdAndTenantId(user.getId(),tenanTid);
            if (user1.get().getRole() != Roles.ADMIN) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "You do not have Sufficient access"));
            }
            if (!user1.isPresent()) {

                throw new RuntimeException("User not found");
            }

            User user2 = user1.get();
            String otp= vendorOtpService.generateOtp();
            log.error("Otp generated");
            vendorOtpService.saveOtp(tenanTid,user2.getId(),otp);
            boolean isOtpEx=vendorOtpService.otpExists(tenanTid,user2.getId());
            log.error("Otp existencce : "+isOtpEx);
            boolean isverify=vendorOtpService.verifyOtp(tenanTid,user2.getId(),otp);
            log.error("otp verification "+isverify);

            if (!user2.getTenantId().equals(tenanTid)) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "User Not found"));
            }
            String token =

                    jwtUtil.generateToken(

                            user.getId(),

                            user.getUsername(),

                            user.getRole().name());


            Cookie cookie = new Cookie("token", token);

            cookie.setHttpOnly(true);

            cookie.setSecure(false);

            cookie.setPath("/");

            cookie.setMaxAge(60 * 60);

            response.addCookie(cookie);


            String redirectUrl = request.getParameter("continue");

            if (redirectUrl == null || redirectUrl.isBlank()) {

                redirectUrl = "";
            }
            user2.setLastLoginIp(loginIp);
            user2.setLastLoginDevice(loginDevice);
            user2.setLastLoginTime(LocalDateTime.now());
            user2.setFailedLoginAttempt(0);
            userRepository.save(user2);
            Optional<Vendor> vendor = vendorRepository.findById(user2.getVendorId());
            if (!vendor.isPresent()) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "Vendor Not found"));

            }
            Vendor v1 = vendor.get();
            String statius = String.valueOf(v1.getStatus());
            if (statius.equals("ONBOARDING")) {
                redirectUrl = "/vendor/s1/on/v1/onBoarding";

            } else {
                redirectUrl = "/vendor/s1/v1/dashboard";
            }


            return ResponseEntity.ok(

                    Map.of(

                            "success", true,

                            "message", "Login successful.",


                            "user", Map.of(

                                    "id", user.getId(),

                                    "firstName", user2.getFirstName(),

                                    "lastName", user2.getLastName(),

                                    "email", user2.getEmail(),

                                    "profileImage", user2.getProfileImage() == null ? "" : user2.getProfileImage(), "role", user.getRole().name()

                            ),

                            "redirectUrl", redirectUrl

                    )


            );

        } catch (Exception e) {
            e.printStackTrace();

            User user = userRepository.findByEmailAndTenantId(dto.getEmail(),tenanTid);

            if (user != null) {


                int failedAttempt = user.getFailedLoginAttempt() == null ? 0 : user.getFailedLoginAttempt();
                if (failedAttempt >= 2) {
                    user.setAccountLocked(true);
                    user.setAccountLockedUntil(LocalDateTime.now());
                }

                user.setFailedLoginAttempt(failedAttempt + 1);

                userRepository.save(user);


            }
            return ResponseEntity.status(401).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isBlank()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }


    @PostMapping("/vendor/logout")
    public ResponseEntity<?> logout(

            HttpServletRequest request,

            HttpServletResponse response) {

        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);

        if (session != null) {

            session.invalidate();
        }

        Cookie jwtCookie = new Cookie("token", "");

        jwtCookie.setHttpOnly(true);

        jwtCookie.setSecure(false);

        jwtCookie.setPath("/");

        jwtCookie.setMaxAge(0);

        response.addCookie(jwtCookie);


        Cookie sessionCookie = new Cookie("JSESSIONID", "");

        sessionCookie.setHttpOnly(true);

        sessionCookie.setSecure(false);

        sessionCookie.setPath("/");

        sessionCookie.setMaxAge(0);

        response.addCookie(sessionCookie);

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        response.setHeader("Pragma", "no-cache");

        response.setHeader("Expires", "0");

        return ResponseEntity.ok(

                Map.of(

                        "success", true,

                        "message", "Logout Successful",

                        "redirectUrl", "/api/vendor/v1/login"));
    }


    @GetMapping("/u1/v1/auth/vendor/me")
    public ResponseEntity<?> checkAuthentication(
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        if (userDetail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "hasSession", false,
                            "redirectUrl", "/api/vendor/v1/login",
                            "message", "Unauthorized"
                    ));
        }

        User userDetail1 = userDetail.getUser();


        if (userDetail.getRole() != Roles.ADMIN) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "hasSession", true,
                            "redirectUrl", "/api/vendor/v1/login",
                            "message", "Forbidden: insufficient role"
                    ));
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
                        "/vendor/s1/v1/dashboard"
                );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "hasSession", true,
                        "data", user
                )
        );
    }


}