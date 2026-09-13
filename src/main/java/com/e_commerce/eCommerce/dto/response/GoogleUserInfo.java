package com.e_commerce.eCommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GoogleUserInfo {

    private String googleId;
    private String email;
    private String firstName;
    private String lastName;
    private String picture;
    private boolean emailVerified;

    // getters and setters
}