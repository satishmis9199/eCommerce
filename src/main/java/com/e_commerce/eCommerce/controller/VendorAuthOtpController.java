
        package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.config.JwtUtil;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.request.EmailRequestDto;
import com.e_commerce.eCommerce.entity.Roles;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.repository.UserRepos;
import com.e_commerce.eCommerce.repository.VendorRepository;
import com.e_commerce.eCommerce.service.EmailService;
import com.e_commerce.eCommerce.service.VendorOtpService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

        @RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
public class VendorAuthOtpController {

    private final UserRepos userRepository;
    private final VendorRepository vendorRepository;
    private final JwtUtil jwtUtil;
    private final VendorOtpService vendorOtpService;
    private final EmailService emailService;

    @PostMapping("/v1/auth/vendor/generateOtp")
    public ResponseEntity<ApiResponse<?>> authenticatateUsingOtp(
            @RequestBody Map<String, String> email,
            HttpServletRequest request,
            HttpServletResponse response) {

        try {
            String tenantId = TenantContext.getTenantId();
            Optional<Vendor> vendor=vendorRepository.findByTenantId(tenantId);
            if(vendor.isEmpty()){
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(
                                false,
                                "vendor not found",
                                null
                        ));

            }
            Vendor vendor1=vendor.get();

            if (email == null || email.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(new ApiResponse<>(
                                false,
                                "Email is required",
                                null
                        ));
            }

            String userEmail = email.get("email");

            if (userEmail == null || userEmail.isBlank()) {
                return ResponseEntity
                        .badRequest()
                        .body(new ApiResponse<>(
                                false,
                                "Email is required",
                                null
                        ));
            }

            userEmail = userEmail.trim().toLowerCase();



            if (tenantId == null || tenantId.isBlank()) {
                return ResponseEntity
                        .badRequest()
                        .body(new ApiResponse<>(
                                false,
                                "Tenant ID is required",
                                null
                        ));
            }

            User user = userRepository
                    .findByEmailAndTenantId(
                            userEmail,
                            tenantId
                    );

            if (user == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(
                                false,
                                "Invalid email or vendor not found",
                                null
                        ));
            }

            if (user.getRole() != Roles.ADMIN) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(
                                false,
                                "You do not have sufficient access",
                                null
                        ));
            }

            String otp = vendorOtpService.generateOtp();

            vendorOtpService.saveOtp(
                    tenantId,
                    user.getId(),
                    otp
            );

            log.info(
                    "Vendor OTP generated for tenant: {}, userId: {}",
                    tenantId,
                    user.getId()
            );
            EmailRequestDto emailRequest = EmailRequestDto.builder()
                    .to(user.getEmail())
                    .subject("Otp Verification")
                    .text("")
                    .templateName("otp-login")
                    .templateVariables(Map.of(
                            "name", user.getFirstName(),
                            "expiryMinutes", 2,
                            "vendorName",vendor1.getBussinessName(),
                            "otp",otp
                    ))
                    .build();
            emailService.sendEmailAsync(emailRequest);
            String token =

                    jwtUtil.generateToken(

                            user.getId(),

                            user.getEmail(),

                            user.getRole().name());


            Cookie cookie = new Cookie("token", token);

            cookie.setHttpOnly(true);

            cookie.setSecure(false);

            cookie.setPath("/");

            cookie.setMaxAge(60 * 60);

            response.addCookie(cookie);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "OTP sent successfully",
                            null
                    )
            );

        } catch (Exception e) {

            log.error(
                    "Vendor OTP login error",
                    e
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Unable to process vendor login",
                            null
                    ));
        }
    }

    @PostMapping("/v1/auth/vendor/verifyOtp")
    public ResponseEntity<ApiResponse<?>> verifyOtp(
            @RequestBody Map<String, String> otp,
            HttpServletRequest request,
            HttpServletResponse response) {

        try {

            if (otp == null || otp.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(new ApiResponse<>(
                                false,
                                "OTP and email are required",
                                null
                        ));
            }

            String otps = otp.get("otp");
            String email = otp.get("email");

            if (otps == null || otps.isBlank()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                false,
                                "OTP is blank. Please enter a valid OTP",
                                null
                        ));
            }

            if (email == null || email.isBlank()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                false,
                                "Email is blank. Please enter a valid email",
                                null
                        ));
            }

            otps = otps.trim();
            email = email.trim().toLowerCase();

            String tenantId = TenantContext.getTenantId();

            if (tenantId == null || tenantId.isBlank()) {
                return ResponseEntity
                        .badRequest()
                        .body(new ApiResponse<>(
                                false,
                                "Tenant ID is required",
                                null
                        ));
            }

            User user = userRepository
                    .findByEmailAndTenantId(
                            email,
                            tenantId
                    );

            if (user == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(
                                false,
                                "Invalid email or vendor not found",
                                null
                        ));
            }

            if (user.getRole() != Roles.ADMIN) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(
                                false,
                                "You do not have sufficient access",
                                null
                        ));
            }

            boolean otpExists =
                    vendorOtpService.otpExists(
                            tenantId,
                            user.getId()
                    );

            if (!otpExists) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                false,
                                "OTP has expired or does not exist. Please request a new OTP",
                                null
                        ));
            }

            boolean isVerifiedOtp =
                    vendorOtpService.verifyOtp(
                            tenantId,
                            user.getId(),
                            otps
                    );

            if (!isVerifiedOtp) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(
                                false,
                                "Invalid or expired OTP",
                                null
                        ));
            }

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            true,
                            "Login successful.",
                            Map.of(
                                    "user",
                                    Map.of(
                                            "id", user.getId(),
                                            "firstName", user.getFirstName(),
                                            "lastName", user.getLastName(),
                                            "email", user.getEmail(),
                                            "profileImage",
                                            user.getProfileImage() == null
                                                    ? ""
                                                    : user.getProfileImage(),
                                            "role",
                                            user.getRole().name()
                                    ),
                                    "redirectUrl",
                                    "/vendor/s1/v1/dashboard"
                            )
                    ));

        } catch (Exception e) {

            log.error(
                    "Error while validating OTP",
                    e
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Unable to process vendor login",
                            null
                    ));
        }
    }
}
