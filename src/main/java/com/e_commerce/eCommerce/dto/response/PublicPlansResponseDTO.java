package com.e_commerce.eCommerce.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicPlansResponseDTO {

    private Long id;

    private String name;

    private BigDecimal price;

    private String billingCycle;

    private String description;

    private List<String> features;
}