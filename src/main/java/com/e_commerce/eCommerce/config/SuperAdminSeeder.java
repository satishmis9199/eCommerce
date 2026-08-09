package com.e_commerce.eCommerce.config;


import com.e_commerce.eCommerce.entity.Roles;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.repository.UserRepos;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SuperAdminSeeder implements CommandLineRunner {
     private static final Logger logger= LoggerFactory.getLogger(SuperAdminSeeder.class);
    private final UserRepos userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String[] args) {
        try {

            String email = "admin@mystore.com";

            if (userRepository.existsByEmail(email)) {
                logger.info("✅ Super Admin already exists.");
                return;
            }

            User admin = new User();

            admin.setFirstName("Super");
            admin.setLastName("Admin");

            admin.setEmail(email);

            admin.setPassword(passwordEncoder.encode("Admin@123"));

            admin.setRole(Roles.SUPER_ADMIN);

            admin.setActive(true);

            admin.setEmailVerified(true);

            admin.setAccountLocked(false);

            admin.setFailedLoginAttempt(0);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            admin.setCreatedBy("SATISH");
            admin.setUpdatedBy("Rahul");
            admin.setTenantId("0");

            admin.setProfileImage("https://pub-4fa6214a201a4173bb0677e8da8390b1.r2.dev/b1b657ce-98c1-4a19-99c4-7c287cb3a222.jpg");

            // Super Admin doesn't belong to any tenant

            userRepository.save(admin);

            logger.info("===========================================");
            logger.info("SUPER ADMIN CREATED SUCCESSFULLY");
            logger.info("===========================================");

    }catch(Exception exception){
            exception.printStackTrace();
            logger.info("Error Occured while Saving a SSuoer ADMin");
        }

    }
}