package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.RegisterRequestDTO;
import com.e_commerce.eCommerce.dto.request.EmailRequestDto;
import com.e_commerce.eCommerce.entity.Roles;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.repository.UserRepos;
import com.e_commerce.eCommerce.repository.VendorRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final VendorRepository vendorRepository;
    private final UserRepos userRepos;
    private final PasswordEncoder passwordEncoder;
    private final EmailService service;

    public User registerUser(
            RegisterRequestDTO registerRequestDTO,
            String url) {

        if (registerRequestDTO == null) {
            throw new RuntimeException("Registration data is required.");
        }

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Tenant does not exist.");
        }

        String email = registerRequestDTO.getEmail();

        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email is required.");
        }

        email = email.trim().toLowerCase();

        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor does not exist."));

        Long vendorId = vendor.getId();

        if (vendorId == null) {
            throw new RuntimeException("Vendor does not exist.");
        }

        String googleId = registerRequestDTO.getGoogleId();

        boolean isGoogleUser =
                googleId != null && !googleId.isBlank();

        String authProvider =
                registerRequestDTO.getAuthProvider();

        boolean isGoogleProvider =
                authProvider != null
                        && !authProvider.isBlank()
                        && "GOOGLE".equalsIgnoreCase(authProvider);

        if (isGoogleUser != isGoogleProvider) {
            throw new RuntimeException(
                    "Invalid authentication provider."
            );
        }

        User existingEmail =
                userRepos.findByEmailAndVendorIdAndTenantId(
                        email,
                        vendorId,
                        tenantId
                );

        if (existingEmail != null) {
            throw new RuntimeException(
                    "Email is already registered."
            );
        }

        if (isGoogleUser) {

            Optional<User> existingGoogleUser =
                    userRepos.findByGoogleIdAndTenantId(
                            googleId.trim(),
                            tenantId
                    );

            if (existingGoogleUser != null) {
                throw new RuntimeException(
                        "Google account is already registered."
                );
            }

        } else {

            String mobileNumber =
                    registerRequestDTO.getMobileNumber();

            if (mobileNumber == null || mobileNumber.isBlank()) {
                throw new RuntimeException(
                        "Mobile number is required."
                );
            }

            User existingMobile =
                    userRepos.findByMobileNumberAndVendorIdAndTenantId(
                            mobileNumber,
                            vendorId,
                            tenantId
                    );

            if (existingMobile != null) {
                throw new RuntimeException(
                        "Mobile number is already registered."
                );
            }

            String password =
                    registerRequestDTO.getPassword();

            if (password == null || password.isBlank()) {
                throw new RuntimeException(
                        "Password is required."
                );
            }
        }

        User user = new User();

        user.setFirstName(
                registerRequestDTO.getFirstName()
        );

        user.setLastName(
                registerRequestDTO.getLastName()
        );

        user.setEmail(email);

        user.setMobileNumber(
                registerRequestDTO.getMobileNumber()
        );

        if (isGoogleUser) {

            user.setPassword(null);
            user.setGoogleId(googleId.trim());
            user.setAuthProvider("GOOGLE");

        } else {

            user.setPassword(
                    passwordEncoder.encode(
                            registerRequestDTO.getPassword()
                    )
            );

            user.setGoogleId(null);
            user.setAuthProvider("PASSWORD");
        }

        user.setRole(Roles.USER);

        user.setTenantId(tenantId);
        user.setVendorId(vendorId);

        user.setActive(true);

        user.setEmailVerified(isGoogleUser);

        user.setFailedLoginAttempt(0);

        user.setAccountLocked(false);

        user.setAccountLockedUntil(null);

        LocalDateTime now = LocalDateTime.now();

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        user.setCreatedBy(
                isGoogleUser
                        ? "GOOGLE"
                        : "SELF_REGISTER"
        );

        user.setUpdatedBy(
                isGoogleUser
                        ? "GOOGLE"
                        : "SELF_REGISTER"
        );

        User savedUser = userRepos.save(user);

        try {

            String loginLink =
                    url != null && !url.isBlank()
                            ? "https://" + url
                            : "";

            String name =
                    user.getFirstName();

            if (name == null || name.isBlank()) {
                name = "Customer";
            }

            EmailRequestDto welcomeEmail =
                    EmailRequestDto.builder()
                            .to(user.getEmail())
                            .subject(
                                    "Welcome to "
                                            + vendor.getStoreName()
                                            + " 🎉"
                            )
                            .templateName("welcome")
                            .templateVariables(
                                    Map.of(
                                            "name",
                                            name,
                                            "loginLink",
                                            loginLink,
                                            "supportEmail",
                                            "support@yourapp.com"
                                    )
                            )
                            .build();

            service.sendEmailAsync(
                    welcomeEmail
            );

        } catch (Exception ignored) {
        }

        return savedUser;
    }
}