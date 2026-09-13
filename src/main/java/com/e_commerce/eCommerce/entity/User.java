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

    /**
     * Email is unique per tenant.
     * Same email can exist in different tenants.
     */
    @Column(nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Roles role;

    @Column
    private String phone;

    /**
     * Every user belongs to a tenant.
     */
    @Column(name = "tenant_id", nullable = false, length = 255)
    private String tenantId;

    @Column(name = "vendorid")
    private Long vendorId;

    /**
     * Google account unique ID (Google 'sub' claim).
     *
     * NULL for normal email/password users.
     */
    @Column(
            name = "google_id",
            unique = true,
            nullable = true,
            length = 255
    )
    private String googleId;

    /**
     * Authentication provider.
     *
     * Examples:
     * GOOGLE
     * LOCAL
     */
    @Column(name = "auth_provider", length = 30)
    private String authProvider;

    /**
     * Password is nullable because Google users
     * may not have a local password.
     */
    @Column(nullable = true)
    private String password;

    @Column(nullable = true, length = 100)
    private String firstName;

    @Column(nullable = true, length = 100)
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