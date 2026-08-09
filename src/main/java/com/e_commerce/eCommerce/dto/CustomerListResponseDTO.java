package com.e_commerce.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@AllArgsConstructor
@Getter
@Setter
public class CustomerListResponseDTO
{
    private Long id;
        private String customerName;
        private String phone;
        private Long totalOrders;
        private String email;
        private BigDecimal totalSpent;
        private LocalDateTime lastOrderDate;
    public CustomerListResponseDTO(
            Long id,
            String customerName,
            String email,
            String phone,
            Long totalOrders,
            BigDecimal totalSpent,
            LocalDateTime lastOrderDate
    ) {
        this.id = id;
        this.customerName = customerName;
        this.email = email;
        this.phone = phone;
        this.totalOrders = totalOrders;
        this.totalSpent = totalSpent;
        this.lastOrderDate = lastOrderDate;
    }



}
