package com.e_commerce.eCommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "cart",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cart_tenant_vendor_user",
                        columnNames = {
                                "tenantId",
                                "vendorId",
                                "userId"
                        }
                )
        }
)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private Long vendorId;

    @Column(nullable = false)
    private Long userId;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private CartStatus status;

    @Column(nullable = false)
    private Integer rowState;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}