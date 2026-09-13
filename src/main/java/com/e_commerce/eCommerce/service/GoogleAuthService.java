package com.e_commerce.eCommerce.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.e_commerce.eCommerce.dto.RegisterRequestDTO;
import com.e_commerce.eCommerce.dto.response.GoogleUserInfo;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.repository.UserRepos;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

@Service
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

            if (googleClientId == null || googleClientId.isBlank()) {
                throw new RuntimeException(
                        "Google Client ID is not configured."
                );
            }

            this.verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(
                            Collections.singletonList(googleClientId)
                    )
                    .build();

        } catch (Exception e) {

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

        try {

            // ----------------------------------------------------
            // Token validation
            // ----------------------------------------------------

            if (idToken == null || idToken.isBlank()) {

                throw new RuntimeException(
                        "Google ID token is required"
                );
            }

            // ----------------------------------------------------
            // Verify token
            // ----------------------------------------------------

            GoogleIdToken googleIdToken =
                    verifier.verify(idToken);

            if (googleIdToken == null) {

                throw new RuntimeException(
                        "Invalid Google ID token"
                );
            }

            // ----------------------------------------------------
            // Payload
            // ----------------------------------------------------

            GoogleIdToken.Payload payload =
                    googleIdToken.getPayload();

            if (payload == null) {

                throw new RuntimeException(
                        "Google token payload not found"
                );
            }

            String googleId = payload.getSubject();
            String email = payload.getEmail();
            Boolean emailVerified = payload.getEmailVerified();

            // ----------------------------------------------------
            // Google ID validation
            // ----------------------------------------------------

            if (googleId == null || googleId.isBlank()) {

                throw new RuntimeException(
                        "Google account ID not found"
                );
            }

            // ----------------------------------------------------
            // Email validation
            // ----------------------------------------------------

            if (email == null || email.isBlank()) {

                throw new RuntimeException(
                        "Google account email not found"
                );
            }

            email = email.trim().toLowerCase();

            // ----------------------------------------------------
            // Email verification
            // ----------------------------------------------------

            if (!Boolean.TRUE.equals(emailVerified)) {

                throw new RuntimeException(
                        "Google email is not verified"
                );
            }

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

            return userInfo;

        } catch (RuntimeException e) {

            throw e;

        } catch (Exception e) {

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

        try {

            // ----------------------------------------------------
            // Tenant validation
            // ----------------------------------------------------

            if (tenantId == null || tenantId.isBlank()) {

                throw new RuntimeException(
                        "Invalid tenant"
                );
            }

            tenantId = tenantId.trim();

            // ----------------------------------------------------
            // Verify Google token
            // ----------------------------------------------------

            GoogleUserInfo googleUser =
                    verifyToken(idToken);

            if (googleUser == null) {

                throw new RuntimeException(
                        "Unable to retrieve Google user information"
                );
            }

            String googleId =
                    googleUser.getGoogleId();

            String email =
                    googleUser.getEmail();

            if (googleId == null || googleId.isBlank()) {

                throw new RuntimeException(
                        "Google account ID not found"
                );
            }

            if (email == null || email.isBlank()) {

                throw new RuntimeException(
                        "Google account email not found"
                );
            }

            googleId = googleId.trim();
            email = email.trim().toLowerCase();

            // ----------------------------------------------------
            // Search by Google ID + Tenant
            // ----------------------------------------------------

            Optional<User> existingUser =
                    userRepository.findByGoogleIdAndTenantId(
                            googleId,
                            tenantId
                    );

            if (existingUser.isPresent()) {

                User user = existingUser.get();

                return user;
            }

            // ----------------------------------------------------
            // Search by Email + Tenant
            // ----------------------------------------------------

            User emailUser =
                    userRepository.findByEmailAndTenantId(
                            email,
                            tenantId
                    );

            if (emailUser != null) {

                // ------------------------------------------------
                // Link Google account
                // ------------------------------------------------

                emailUser.setGoogleId(googleId);

                User savedUser =
                        userRepository.save(emailUser);

                return savedUser;
            }

            // ----------------------------------------------------
            // IMPORTANT:
            // No user found by Google ID
            // AND
            // No user found by Email + Tenant
            // ----------------------------------------------------

            // ----------------------------------------------------
            // Registration DTO
            // ----------------------------------------------------

            RegisterRequestDTO registerRequestDTO =
                    new RegisterRequestDTO();

            registerRequestDTO.setEmail(email);

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

            // ----------------------------------------------------
            // Register new user
            // ----------------------------------------------------

            User savedUser =
                    userAuthService.registerUser(
                            registerRequestDTO,
                            ""
                    );

            return savedUser;

        } catch (RuntimeException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Google authentication failed",
                    e
            );
        }
    }
}
