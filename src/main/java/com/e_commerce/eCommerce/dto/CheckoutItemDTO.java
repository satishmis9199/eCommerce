package com.e_commerce.eCommerce.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CheckoutItemDTO {

    private Long itemId;

    private Long productId;

    private Integer qty;
}