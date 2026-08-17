package com.e_commerce.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyProfileDTO {

    // Basic Information
    private Long userId;
    private String firstName;
    private String lastName;
    String profileImage;


    // Contact Information
    private String email;
    private String mobile;

    // Account Information
    private String role;
    private String status;


    // Account Details
    private LocalDateTime memberSince;
    private LocalDateTime lastLogin;


}