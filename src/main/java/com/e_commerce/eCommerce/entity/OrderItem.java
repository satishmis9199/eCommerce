package com.e_commerce.eCommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private Long productId;

    private String productName;

    private String brandName;
    private String tenantId;

    private String imageUrl;
    private String description;
    private String hsnCode;

    @Column(precision = 12, scale = 2)
    private BigDecimal unitPrice;

    private Integer quantity;
    private boolean isReview;

    @Column(precision = 12, scale = 2)
    private BigDecimal lineTotal;

    @Column(precision = 12, scale = 2)
    private BigDecimal mrp;

    @Column(precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    private Integer rowState;

    @CreationTimestamp
    private LocalDateTime createdAt;
}