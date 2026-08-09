package com.e_commerce.eCommerce.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InvoiceItemsDTO {

    private String productName;
    private String description;
    private String imageUrl;
    private String hsnCode;
    private String quantity;
    private String unit;
    private String mrp;
    private String discountAmount;
    private String sellingPrice;
    private String taxAmount;
    private String totalAmount;

}