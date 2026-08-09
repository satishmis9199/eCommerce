package com.e_commerce.eCommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trending_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrendingProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String tenantId;

    @Column(nullable = false)
    private Long vendorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_rank", nullable = false)
    private Integer rank;

    @Column(nullable = false)
    private Double score;

    @Column(nullable = false)
    private LocalDateTime calculatedAt;

}