package com.e_commerce.eCommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_email_tenant",
                        columnNames = {"email", "tenant_id"}
                ),

                @UniqueConstraint(
                        name = "uk_users_google_id_tenant",
                        columnNames = {"google_id", "tenant_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Roles role;

    @Column
    private String phone;

    @Column(
            name = "tenant_id",
            nullable = false,
            length = 255
    )
    private String tenantId;

    @Column(name = "vendorid")
    private Long vendorId;

    @Column(
            name = "google_id",
            nullable = true,
            length = 255
    )
    private String googleId;

    @Column(
            name = "auth_provider",
            length = 30
    )
    private String authProvider;

    @Column(nullable = true)
    private String password;

    @Column(
            nullable = true,
            length = 100
    )
    private String firstName;

    @Column(
            nullable = true,
            length = 100
    )
    private String lastName;

    @Column(length = 20)
    private String mobileNumber;

    @Column(length = 255)
    private String profileImage;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean accountLocked = false;

    @Column(nullable = false)
    private Boolean emailVerified = true;

    @Column(nullable = false)
    private Integer failedLoginAttempt = 0;

    @Column
    private LocalDateTime accountLockedUntil;

    @Column
    private LocalDateTime lastLoginTime;

    @Column(length = 100)
    private String lastLoginIp;

    @Column(length = 255)
    private String lastLoginDevice;

    @Column(
            name = "credentials_setup_complete",
            nullable = false
    )
    private Boolean credentialsSetupComplete = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(length = 100)
    private String createdBy;

    @Column(length = 100)
    private String updatedBy;
}

