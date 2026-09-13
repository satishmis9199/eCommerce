package com.e_commerce.eCommerce.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.e_commerce.eCommerce.enums.ShopStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "shop_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tenantId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShopStatus status;
    @Column(length = 255)
    private String reason;
    @Column(nullable = false)
    private Long changedBy;
    @Column(nullable = false)
    private LocalDateTime changedAt;
}