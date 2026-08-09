package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.entity.PaymentMethod;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutRequestDTO {

    private PaymentMethod paymentMethod;

    private Long addressId;

    private List<CheckoutItemDTO> items;
}