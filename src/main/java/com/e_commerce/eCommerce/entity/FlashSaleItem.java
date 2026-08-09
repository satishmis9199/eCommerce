package com.e_commerce.eCommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import com.e_commerce.eCommerce.entity.FlashSale;


@Entity
@Table(
        name = "flash_sale_item",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_flash_sale_product",
                        columnNames = {"flash_sale_id", "product_id"}
                )
        },
        indexes = {
                @Index(name = "idx_flash_sale_item_product", columnList = "product_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashSaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flash_sale_id", nullable = false)
    private FlashSale flashSale;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "original_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "sale_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal salePrice;

}