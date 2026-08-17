package com.e_commerce.eCommerce.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UploadResult {
    private int totalRowsProcessed;
    private int successCount;
    private int failureCount;
    private List<RowError> rowErrors;
}