package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.entity.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthMeResponse {

    private boolean success;

    private UserData user;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserData {

        private Long id;

        private String firstName;

        private String lastName;

        private String email;

        private String mobileNumber;

        private String profileImage;

        private Roles role;

        private String tenantId;
    }
}