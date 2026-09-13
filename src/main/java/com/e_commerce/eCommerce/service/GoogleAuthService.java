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

            this.verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(
                            Collections.singletonList(googleClientId)
                    )
                    .build();

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

            if (idToken == null || idToken.isBlank()) {

                throw new RuntimeException(
                        "Google ID token is required"
                );
            }

            GoogleIdToken googleIdToken =
                    verifier.verify(idToken);

            if (googleIdToken == null) {

                throw new RuntimeException(
                        "Invalid Google ID token"
                );
            }

            GoogleIdToken.Payload payload =
                    googleIdToken.getPayload();

            String googleId = payload.getSubject();
            String email = payload.getEmail();
            Boolean emailVerified = payload.getEmailVerified();

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

            if (!Boolean.TRUE.equals(emailVerified)) {

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

    public User authenticateWithGoogle(
            String idToken,
            String tenantId) {

        GoogleUserInfo googleUser =
                verifyToken(idToken);

        Optional<User> existingUser =
                userRepository.findByGoogleIdAndTenantId(
                        googleUser.getGoogleId(),
                        tenantId
                );

        if (existingUser.isPresent()) {

            User user = existingUser.get();

            return user;
        }

        Optional<User> emailUser =
                Optional.ofNullable(
                        userRepository.findByEmailAndTenantId(
                                googleUser.getEmail(),
                                tenantId
                        )
                );

        if (emailUser.isPresent()) {

            User user = emailUser.get();

            user.setGoogleId(
                    googleUser.getGoogleId()
            );

            User savedUser =
                    userRepository.save(user);

            return savedUser;
        }

        RegisterRequestDTO registerRequestDTO =
                new RegisterRequestDTO();

        registerRequestDTO.setEmail(
                googleUser.getEmail()
        );
        registerRequestDTO.setAuthProvider("GOOGLE");

        registerRequestDTO.setFirstName(
                googleUser.getFirstName()
        );

        registerRequestDTO.setLastName(
                googleUser.getLastName()
        );

        registerRequestDTO.setGoogleId(
                googleUser.getGoogleId()
        );

        registerRequestDTO.setMobileNumber(
                "0000000000"
        );

        User savedUser =
                userAuthService.registerUser(
                        registerRequestDTO,
                        ""
                );

        return savedUser;
    }
}