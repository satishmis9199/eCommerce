package com.e_commerce.eCommerce.service;

import java.util.Collections;
import java.util.Optional;

import com.e_commerce.eCommerce.dto.response.GoogleUserInfo;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.repository.UserRepos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

@Service
public class GoogleAuthService {

    private final GoogleIdTokenVerifier verifier;
    private final UserRepos userRepository;

    public GoogleAuthService(
            @Value("${google.client-id}") String googleClientId, UserRepos userRepository) {
        this.userRepository = userRepository;

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

        } catch (Exception e) {

            if (e instanceof RuntimeException
                    && e.getMessage() != null
                    && !e.getMessage().equals(
                    "Google token verification failed")) {

                throw new RuntimeException(e);
            }

            throw new RuntimeException(
                    "Google token verification failed",
                    e
            );
        }
    }

    public User authenticateWithGoogle(String idToken) {

        // 1. Google token verify
        GoogleUserInfo googleUser =
              verifyToken(idToken);


        Optional<User> existingUser =
                userRepository.findByGoogleId(
                        googleUser.getGoogleId()
                );

        if (existingUser.isPresent()) {
            return existingUser.get();
        }


        Optional<User> emailUser =
                Optional.ofNullable(userRepository.findByEmail(
                        googleUser.getEmail()
                ));

        if (emailUser.isPresent()) {

            User user = emailUser.get();

            // Google account ko existing account se link
            user.setGoogleId(googleUser.getGoogleId());

            return userRepository.save(user);
        }

        // 4. New user
        User user = new User();

        user.setEmail(googleUser.getEmail());
        user.setGoogleId(googleUser.getGoogleId());
        user.setFirstName(googleUser.getFirstName());
        user.setLastName(googleUser.getLastName());

        return userRepository.save(user);
    }
}