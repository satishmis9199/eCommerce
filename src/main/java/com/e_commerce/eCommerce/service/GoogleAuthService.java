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
            UserRepos userRepository, UserAuthService userAuthService) {

        this.userRepository = userRepository;
        this.userAuthService = userAuthService;

        try {

            this.verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(
                            Collections.singletonList(googleClientId)
                    )
                    .build();

            log.info("Google Auth Service initialized successfully");

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

    public GoogleUserInfo verifyToken(String idToken) {

        try {

            log.info("Google ID token verification started");

            if (idToken == null || idToken.isBlank()) {

                log.error("Google ID token is null or blank");

                throw new RuntimeException(
                        "Google ID token is required"
                );
            }

            GoogleIdToken googleIdToken =
                    verifier.verify(idToken);

            if (googleIdToken == null) {

                log.error("Google ID token verification returned null");

                throw new RuntimeException(
                        "Invalid Google ID token"
                );
            }

            GoogleIdToken.Payload payload =
                    googleIdToken.getPayload();

            log.info(
                    "Google ID token verified successfully"
            );

            log.debug(
                    "Google ID Payload: {}",
                    payload
            );

            String googleId = payload.getSubject();
            String email = payload.getEmail();
            Boolean emailVerified = payload.getEmailVerified();

            log.info(
                    "Google account received. email={}, emailVerified={}",
                    email,
                    emailVerified
            );

            if (googleId == null || googleId.isBlank()) {

                log.error(
                        "Google account ID (subject) not found"
                );

                throw new RuntimeException(
                        "Google account ID not found"
                );
            }

            if (email == null || email.isBlank()) {

                log.error(
                        "Google account email not found"
                );

                throw new RuntimeException(
                        "Google account email not found"
                );
            }

            if (!Boolean.TRUE.equals(emailVerified)) {

                log.error(
                        "Google email is not verified. email={}",
                        email
                );

                throw new RuntimeException(
                        "Google email is not verified"
                );
            }

            GoogleUserInfo userInfo = new GoogleUserInfo();

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
                    "Google user information prepared successfully for email={}",
                    email
            );

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

    public User authenticateWithGoogle(String idToken,String tenantId) {

        log.info("Google authentication process started");

        GoogleUserInfo googleUser =
                verifyToken(idToken);

        log.info(
                "Searching user by Google IDs. googleId={}",
                googleUser.getGoogleId()
        );

        Optional<User> existingUser =
                userRepository.findByGoogleIdAndTenantId(
                        googleUser.getGoogleId(),tenantId
                );

        if (existingUser.isPresent()) {

            User user = existingUser.get();

            log.info(
                    "Existing Google user found. userId={}, email={}",
                    user.getId(),
                    user.getEmail()
            );

            return user;
        }

        log.info(
                "Google ID not found. Searching user by email={}",
                googleUser.getEmail()
        );

        Optional<User> emailUser =
                Optional.ofNullable(
                        userRepository.findByEmailAndTenantId(
                                googleUser.getEmail(),tenantId
                        )
                );

        if (emailUser.isPresent()) {

            User user = emailUser.get();

            log.info(
                    "Existing user found by email. Linking Google account. userId={}, email={}",
                    user.getId(),
                    user.getEmail()
            );

            user.setGoogleId(
                    googleUser.getGoogleId()
            );

            User savedUser =
                    userRepository.save(user);

            log.info(
                    "Google account linked successfully. userId={}, email={}",
                    savedUser.getId(),
                    savedUser.getEmail()
            );

            return savedUser;
        }

        log.info(
                "No existing user found. Creating new Google user. email={}",
                googleUser.getEmail()
        );
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO();

        registerRequestDTO.setEmail(googleUser.getEmail());
        registerRequestDTO.setFirstName(googleUser.getFirstName());
        registerRequestDTO.setLastName(googleUser.getLastName());
        registerRequestDTO.setGoogleId(googleUser.getGoogleId());

        registerRequestDTO.setMobileNumber("0000000000");
       User savedUser= userAuthService.registerUser(registerRequestDTO,"");





        return savedUser;
    }
}