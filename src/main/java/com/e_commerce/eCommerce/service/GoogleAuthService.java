package com.e_commerce.eCommerce.service;

import java.util.Collections;
import java.util.Optional;

import com.e_commerce.eCommerce.dto.RegisterRequestDTO;
import com.e_commerce.eCommerce.dto.response.GoogleUserInfo;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.repository.UserRepos;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

@Service
@Slf4j
public class GoogleAuthService {

    private final GoogleIdTokenVerifier verifier;
    private final UserRepos userRepository;
    private final UserAuthService userAuthService;

    public GoogleAuthService(
            @Value("${google.client-id}") String googleClientId,
            UserRepos userRepository,
            UserAuthService userAuthService) {

        this.userRepository = userRepository;
        this.userAuthService = userAuthService;

        try {

            log.info("==============================================");
            log.info("Initializing Google Authentication Service");
            log.info("Google Client ID configured: {}",
                    googleClientId != null && !googleClientId.isBlank());

            this.verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(
                            Collections.singletonList(googleClientId)
                    )
                    .build();

            log.info("Google Authentication Service initialized successfully");
            log.info("==============================================");

        } catch (Exception e) {

            log.error(
                    "Failed to initialize Google authentication",
                    e
            );

            throw new RuntimeException(
                    "Failed to initialize Google authentication",
                    e
            );
        }
    }

    // ============================================================
    // VERIFY GOOGLE TOKEN
    // ============================================================

    public GoogleUserInfo verifyToken(String idToken) {

        log.info("========== GOOGLE TOKEN VERIFICATION START ==========");

        try {

            // ----------------------------------------------------
            // Token validation
            // ----------------------------------------------------

            if (idToken == null || idToken.isBlank()) {

                log.error("Google ID token is NULL or BLANK");

                throw new RuntimeException(
                        "Google ID token is required"
                );
            }

            log.info("Google ID token received successfully");
            log.info("Token length: {}", idToken.length());

            // ----------------------------------------------------
            // Verify token
            // ----------------------------------------------------

            GoogleIdToken googleIdToken =
                    verifier.verify(idToken);

            if (googleIdToken == null) {

                log.error("Google token verification returned NULL");

                throw new RuntimeException(
                        "Invalid Google ID token"
                );
            }

            log.info("Google ID token verified successfully");

            // ----------------------------------------------------
            // Payload
            // ----------------------------------------------------

            GoogleIdToken.Payload payload =
                    googleIdToken.getPayload();

            if (payload == null) {

                log.error("Google token payload is NULL");

                throw new RuntimeException(
                        "Google token payload not found"
                );
            }

            String googleId = payload.getSubject();
            String email = payload.getEmail();
            Boolean emailVerified = payload.getEmailVerified();

            log.info(
                    "Google payload received | email={} | googleIdPresent={} | emailVerified={}",
                    email,
                    googleId != null && !googleId.isBlank(),
                    emailVerified
            );

            // ----------------------------------------------------
            // Google ID validation
            // ----------------------------------------------------

            if (googleId == null || googleId.isBlank()) {

                log.error("Google account ID is missing");

                throw new RuntimeException(
                        "Google account ID not found"
                );
            }

            // ----------------------------------------------------
            // Email validation
            // ----------------------------------------------------

            if (email == null || email.isBlank()) {

                log.error("Google account email is missing");

                throw new RuntimeException(
                        "Google account email not found"
                );
            }

            email = email.trim();

            // ----------------------------------------------------
            // Email verification
            // ----------------------------------------------------

            if (!Boolean.TRUE.equals(emailVerified)) {

                log.error(
                        "Google email is NOT verified | email={}",
                        email
                );

                throw new RuntimeException(
                        "Google email is not verified"
                );
            }

            log.info(
                    "Google email verified successfully | email={}",
                    email
            );

            // ----------------------------------------------------
            // User info
            // ----------------------------------------------------

            GoogleUserInfo userInfo =
                    new GoogleUserInfo();

            userInfo.setGoogleId(googleId);

            userInfo.setEmail(email);

            userInfo.setEmailVerified(true);

            userInfo.setFirstName(
                    payload.get("given_name") != null
                            ? payload.get("given_name").toString()
                            : null
            );

            userInfo.setLastName(
                    payload.get("family_name") != null
                            ? payload.get("family_name").toString()
                            : null
            );

            userInfo.setPicture(
                    payload.get("picture") != null
                            ? payload.get("picture").toString()
                            : null
            );

            log.info(
                    "Google user information prepared | email={} | firstName={} | lastName={} | picturePresent={}",
                    userInfo.getEmail(),
                    userInfo.getFirstName(),
                    userInfo.getLastName(),
                    userInfo.getPicture() != null
            );

            log.info("========== GOOGLE TOKEN VERIFICATION SUCCESS ==========");

            return userInfo;

        } catch (RuntimeException e) {

            log.error(
                    "Google token verification failed: {}",
                    e.getMessage(),
                    e
            );

            throw e;

        } catch (Exception e) {

            log.error(
                    "Unexpected error while verifying Google token",
                    e
            );

            throw new RuntimeException(
                    "Google token verification failed",
                    e
            );
        }
    }

    // ============================================================
    // GOOGLE AUTHENTICATION
    // ============================================================

    public User authenticateWithGoogle(
            String idToken,
            String tenantId) {

        log.info("================================================");
        log.info("GOOGLE AUTHENTICATION START");
        log.info("================================================");

        try {

            // ----------------------------------------------------
            // Tenant validation
            // ----------------------------------------------------

            log.info(
                    "Google authentication received | tenantId={}",
                    tenantId
            );

            if (tenantId == null || tenantId.isBlank()) {

                log.error(
                        "Google authentication failed because tenantId is NULL or BLANK"
                );

                throw new RuntimeException(
                        "Invalid tenant"
                );
            }

            tenantId = tenantId.trim();

            // ----------------------------------------------------
            // Verify Google token
            // ----------------------------------------------------

            log.info("Step 1: Verifying Google ID token");

            GoogleUserInfo googleUser =
                    verifyToken(idToken);

            if (googleUser == null) {

                log.error(
                        "verifyToken() returned NULL GoogleUserInfo"
                );

                throw new RuntimeException(
                        "Unable to retrieve Google user information"
                );
            }

            String googleId =
                    googleUser.getGoogleId();

            String email =
                    googleUser.getEmail();

            log.info(
                    "Step 1 SUCCESS | email={} | googleIdPresent={} | tenantId={}",
                    email,
                    googleId != null && !googleId.isBlank(),
                    tenantId
            );

            // ----------------------------------------------------
            // Search by Google ID + Tenant
            // ----------------------------------------------------

            log.info(
                    "Step 2: Searching existing user by Google ID + Tenant"
            );

            log.info(
                    "Google ID present: {}",
                    googleId != null && !googleId.isBlank()
            );

            log.info(
                    "Tenant ID: {}",
                    tenantId
            );

            Optional<User> existingUser =
                    userRepository.findByGoogleIdAndTenantId(
                            googleId,
                            tenantId
                    );

            if (existingUser.isPresent()) {

                User user = existingUser.get();

                log.info(
                        "USER FOUND BY GOOGLE ID + TENANT"
                );

                log.info(
                        "Existing user ID: {}",
                        user.getId()
                );

                log.info(
                        "Existing user email: {}",
                        user.getEmail()
                );

                log.info(
                        "Returning existing Google user. Registration will NOT happen."
                );

                log.info("================================================");
                log.info("GOOGLE AUTHENTICATION SUCCESS");
                log.info("================================================");

                return user;
            }

            log.info(
                    "No user found by Google ID + Tenant"
            );

            // ----------------------------------------------------
            // Search by Email + Tenant
            // ----------------------------------------------------

            log.info(
                    "Step 3: Searching existing user by Email + Tenant"
            );

            log.info(
                    "Searching email: {}",
                    email
            );

            log.info(
                    "Searching tenantId: {}",
                    tenantId
            );

            User emailUser =
                    userRepository.findByEmailAndTenantId(
                            email,
                            tenantId
                    );

            if (emailUser != null) {

                log.info(
                        "USER FOUND BY EMAIL + TENANT"
                );

                log.info(
                        "Existing user ID: {}",
                        emailUser.getId()
                );

                log.info(
                        "Existing user email: {}",
                        emailUser.getEmail()
                );

                log.info(
                        "Existing user Google ID present: {}",
                        emailUser.getGoogleId() != null
                                && !emailUser.getGoogleId().isBlank()
                );

                // ------------------------------------------------
                // Link Google account
                // ------------------------------------------------

                log.info(
                        "Step 4: Linking Google ID with existing email user"
                );

                emailUser.setGoogleId(
                        googleId
                );

                User savedUser =
                        userRepository.save(emailUser);

                log.info(
                        "Existing email user updated successfully"
                );

                log.info(
                        "Saved user ID: {}",
                        savedUser.getId()
                );

                log.info(
                        "Google ID linked successfully. Registration will NOT happen."
                );

                log.info("================================================");
                log.info("GOOGLE AUTHENTICATION SUCCESS");
                log.info("================================================");

                return savedUser;
            }

            // ----------------------------------------------------
            // IMPORTANT:
            // No user found by Google ID
            // AND
            // No user found by Email + Tenant
            // ----------------------------------------------------

            log.warn(
                    "NO EXISTING USER FOUND"
            );

            log.warn(
                    "Google ID lookup: NOT FOUND"
            );

            log.warn(
                    "Email + Tenant lookup: NOT FOUND"
            );

            log.warn(
                    "User will now be sent to registerUser()"
            );

            // ----------------------------------------------------
            // Registration DTO
            // ----------------------------------------------------

            RegisterRequestDTO registerRequestDTO =
                    new RegisterRequestDTO();

            registerRequestDTO.setEmail(
                    email
            );

            registerRequestDTO.setAuthProvider(
                    "GOOGLE"
            );

            registerRequestDTO.setFirstName(
                    googleUser.getFirstName()
            );

            registerRequestDTO.setLastName(
                    googleUser.getLastName()
            );

            registerRequestDTO.setGoogleId(
                    googleId
            );

            registerRequestDTO.setMobileNumber(
                    "0000000000"
            );

            log.warn(
                    "Registration DTO created for email={}",
                    registerRequestDTO.getEmail()
            );

            log.warn(
                    "Calling userAuthService.registerUser() now..."
            );

            // ----------------------------------------------------
            // Register new user
            // ----------------------------------------------------

            User savedUser =
                    userAuthService.registerUser(
                            registerRequestDTO,
                            ""
                    );

            log.info(
                    "New Google user registered successfully"
            );

            log.info(
                    "New user ID: {}",
                    savedUser != null
                            ? savedUser.getId()
                            : null
            );

            log.info("================================================");
            log.info("GOOGLE AUTHENTICATION SUCCESS");
            log.info("================================================");

            return savedUser;

        } catch (RuntimeException e) {

            log.error("================================================");
            log.error("GOOGLE AUTHENTICATION FAILED");
            log.error("Error: {}", e.getMessage(), e);
            log.error("================================================");

            throw e;

        } catch (Exception e) {

            log.error("================================================");
            log.error("UNEXPECTED GOOGLE AUTHENTICATION ERROR");
            log.error("Error: {}", e.getMessage(), e);
            log.error("================================================");

            throw new RuntimeException(
                    "Google authentication failed",
                    e
            );
        }
    }
}