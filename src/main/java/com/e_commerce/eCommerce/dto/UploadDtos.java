
package com.e_commerce.eCommerce.dto;

import java.util.List;

public final class UploadDtos {

    private UploadDtos() {
    }

    public record InitiateRequest(
            String fileName,
            long fileSize
    ) {
    }

    public record InitiateResponse(
            String uploadId,
            String objectKey,
            long partSize,
            int totalParts
    ) {
    }

    public record PartUrlResponse(
            int partNumber,
            String url
    ) {
    }

    public record PartReportRequest(
            String eTag,
            long size,
            Integer attemptNumber,
            Long durationMs
    ) {
    }

    public record UploadedPartDto(
            int partNumber,
            long size,
            String eTag
    ) {
    }

    public record UploadStatusResponse(
            String uploadId,
            String status,
            int totalParts,
            long fileSize,
            List<UploadedPartDto> parts
    ) {
    }

    public record CompleteResponse(
            String uploadId,
            String objectKey,
            String eTag,
            String status
    ) {
    }
}
