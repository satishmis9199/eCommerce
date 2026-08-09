package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.RegisterRequestDTO;
import com.e_commerce.eCommerce.entity.Roles;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.repository.UserRepos;
import com.e_commerce.eCommerce.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final VendorRepository vendorRepository;
    private final UserRepos userRepos;
    private final PasswordEncoder passwordEncoder;

    public String registerUser(RegisterRequestDTO registerRequestDTO) {

        String tenantId = TenantContext.getTenantId();

        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RuntimeException("Vendor does not exist."));

        Long vendorId = vendor.getId();

        User existingEmail = userRepos.findByEmailAndVendorIdAndTenantId(
                registerRequestDTO.getEmail(),
                vendorId,
                tenantId
        );

        if (existingEmail != null) {
            throw new RuntimeException("Email is already registered.");
        }

        User existingMobile = userRepos.findByMobileNumberAndVendorIdAndTenantId(
                registerRequestDTO.getMobileNumber(),
                vendorId,
                tenantId
        );

        if (existingMobile != null) {
            throw new RuntimeException("Mobile number is already registered.");
        }

        User user = new User();

        user.setFirstName(registerRequestDTO.getFirstName());
        user.setLastName(registerRequestDTO.getLastName());
        user.setEmail(registerRequestDTO.getEmail());
        user.setMobileNumber(registerRequestDTO.getMobileNumber());

        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));

        user.setRole(Roles.USER);

        user.setTenantId(tenantId);
        user.setVendorId(vendorId);

        user.setActive(true);
        user.setEmailVerified(false);

        user.setFailedLoginAttempt(0);
        user.setAccountLocked(false);
        user.setAccountLockedUntil(null);

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        user.setCreatedBy("SELF_REGISTER");
        user.setUpdatedBy("SELF_REGISTER");

        userRepos.save(user);

        return "Registration completed successfully. Please login.";
    }
}