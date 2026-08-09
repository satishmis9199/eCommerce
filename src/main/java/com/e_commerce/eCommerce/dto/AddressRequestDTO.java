package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.entity.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {

    @NotBlank(message = "Label is required.")
    @Size(max = 50, message = "Label cannot exceed 50 characters.")
    private String label;

    @NotBlank(message = "Full name is required.")
    @Size(max = 100, message = "Full name cannot exceed 100 characters.")
    private String fullName;

    @NotBlank(message = "Mobile number is required.")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number.")
    private String mobileNumber;

    @Pattern(regexp = "^$|^[6-9]\\d{9}$", message = "Invalid alternate mobile number.")
    private String alternateMobile;

    @NotBlank(message = "Address Line 1 is required.")
    @Size(max = 255, message = "Address Line 1 cannot exceed 255 characters.")
    private String addressLine1;

    @Size(max = 255, message = "Address Line 2 cannot exceed 255 characters.")
    private String addressLine2;

    @Size(max = 150, message = "Landmark cannot exceed 150 characters.")
    private String landmark;

    @NotBlank(message = "City is required.")
    @Size(max = 100, message = "City cannot exceed 100 characters.")
    private String city;

    @NotBlank(message = "State is required.")
    @Size(max = 100, message = "State cannot exceed 100 characters.")
    private String state;

    @NotBlank(message = "Postal code is required.")
    @Pattern(regexp = "^\\d{6}$", message = "Postal code must be 6 digits.")
    private String postalCode;

    @NotBlank(message = "Country is required.")
    @Size(max = 100, message = "Country cannot exceed 100 characters.")
    private String country;

    private AddressType addressType;

    private Boolean isDefault;
}