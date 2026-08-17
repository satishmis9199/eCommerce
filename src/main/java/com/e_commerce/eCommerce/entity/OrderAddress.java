package com.e_commerce.eCommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_address")
@Setter
@Getter
public class OrderAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private Long userId;

    private String tenantId;

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

    private LocalDateTime createdAt;
}