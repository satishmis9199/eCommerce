package com.e_commerce.eCommerce.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String tenantId;

    private String label;

    private String fullName;

    private String mobileNumber;

    private String alternateMobile;

    private String addressLine1;

    private String addressLine2;

    private String landmark;

    private String city;

    private String state;

    private String postalCode;

    private String country;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;

    private Boolean isDefault;

    private Integer rowState;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}