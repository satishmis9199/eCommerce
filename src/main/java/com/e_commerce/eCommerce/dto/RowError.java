package com.e_commerce.eCommerce.dto;

import lombok.Data;

import java.util.List;

@Data
public class RowError {
    private final int excelRowNumber;
    private final List<String> messages;

    public RowError(int excelRowNumber, List<String> messages) {
        this.excelRowNumber = excelRowNumber;
        this.messages = messages;
    }
}