package com.e_commerce.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderTrackingHistoryDto {

    private String status;        // "placed" | "confirmed" | "shipped" | "out_for_delivery" | "delivered"
    private String label;         // "Order Placed", "Shipped", etc.
    private String timestamp;     // ISO-8601, e.g. "2026-08-02T09:30:00Z" — null if not reached yet
    private String description;
    private boolean completed;



}