package com.e_commerce.eCommerce.dto;

import java.time.Instant;

public record IncompleteMultipartUploadDTO(
        String bucket,
        String objectKey,
        String uploadId,
        Instant initiatedAt,
        int uploadedParts,
        long uploadedBytes
) {}