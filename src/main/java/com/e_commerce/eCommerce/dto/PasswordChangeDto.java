package com.e_commerce.eCommerce.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PasswordChangeDto {
    //    private String email;
    private String currentPassword;
    private String newPassword;
}
