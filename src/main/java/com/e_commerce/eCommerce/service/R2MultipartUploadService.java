
package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.dto.IncompleteMultipartUploadDTO;
import com.e_commerce.eCommerce.dto.UploadDtos.*;
import com.e_commerce.eCommerce.entity.FileUpload;
import com.e_commerce.eCommerce.entity.FileUploadPart;
import com.e_commerce.eCommerce.enums.FileUploadStatus;
import com.e_commerce.eCommerce.repository.FileUploadRepository;
import com.e_commerce.eCommerce.repository.FileUploadPartRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class R2MultipartUploadService {

    private final S3Client r2;
    private final S3Presigner presigner;

    private final FileUploadRepository uploads;
    private final FileUploadPartRepository parts;
    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;
    @Value("${app.upload.presign-folderName}")
    private String foldername;

    @Value("${app.upload.part-size:16777216}")
    private long partSize;

    @Value("${app.upload.max-file-size:5368709120}")
    private long maxFileSize;

    @Value("${app.upload.presign-expiry-minutes:15}")
    private long expiryMinutes;


    @Transactional
    public InitiateResponse initiate(
            String ownerId,
            String fileName,
            long fileSize,String tenantId) {
        try {
            if (ownerId == null || ownerId.isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authentication required"
                );
            }
            checkFileConstraint(fileName, fileSize);
            if (partSize <= 0) {
                throw new IllegalStateException(
                        "Part size must be greater than zero"
                );
            }
            if (expiryMinutes <= 0 || expiryMinutes > 10080) {
                throw new IllegalStateException(
                        "Invalid presigned URL expiry"
                );
            }

            long totalParts = (fileSize - 1) / partSize + 1;

            if (totalParts > 10000) {
                throw new IllegalArgumentException(
                        "Maximum 10000 parts allowed"
                );
            }

            String objectKey =
                    tenantId+"-"+foldername + UUID.randomUUID() + "/" + fileName;
            CreateMultipartUploadResponse response =
                    r2.createMultipartUpload(
                            CreateMultipartUploadRequest.builder()
                                    .bucket(bucket)
                                    .key(objectKey)
                                    .build()
                    );
            try {
                FileUpload upload = new FileUpload();
                upload.setId(UUID.randomUUID().toString());
                upload.setOwnerId(ownerId);
                upload.setTenantId(tenantId);
                upload.setBucketName(bucket);
                upload.setObjectKey(objectKey);
                upload.setS3UploadId(response.uploadId());
                upload.setFileName(fileName);
                upload.setFileSize(fileSize);
                upload.setPartSize(partSize);
                upload.setTotalParts((int) totalParts);
                upload.setStatus(FileUploadStatus.IN_PROGRESS);
                uploads.saveAndFlush(upload);
                return new InitiateResponse(
                        upload.getId(),
                        upload.getObjectKey(),
                        upload.getPartSize(),
                        upload.getTotalParts()
                );
            } catch (RuntimeException ex) {
                log.error(
                        "Database save failed after R2 initiation. objectKey={}",
                        objectKey, ex
                );

                try {
                    r2.abortMultipartUpload(
                            AbortMultipartUploadRequest.builder()
                                    .bucket(bucket)
                                    .key(objectKey)
                                    .uploadId(response.uploadId())
                                    .build()
                    );
                } catch (RuntimeException abortEx) {
                    ex.addSuppressed(abortEx);
                    log.error(
                            "Failed to abort R2 upload after DB failure",
                            abortEx
                    );
                }

                throw ex;
            }

        } catch (RuntimeException ex) {
            log.error(
                    "Upload initiation failed. fileName={}, fileSize={}",
                    fileName, fileSize, ex
            );
            throw ex;
        }
    }




    @Transactional
    public PartUrlResponse presignPart(
            String appId,
            String ownerId,
            int partNumber,
            String tenantId) {

        FileUpload upload = activeUpload(appId, ownerId);
        validatePartNumber(upload, partNumber);
        long expectedSize =
                expectedPartSize(upload, partNumber);

        FileUploadPart part =
                parts.findByUploadIdAndPartNumber(
                        appId, partNumber
                ).orElseGet(() -> {
                    FileUploadPart p = new FileUploadPart();
                    p.setUploadId(appId);
                    p.setTenantId(tenantId);
                    p.setPartNumber(partNumber);
                    p.setExpectedSize(expectedSize);
                    p.setStatus(FileUploadStatus.PENDING);
                    p.setUpdatedAt(LocalDateTime.now());
                    return p;
                });

        part.setExpectedSize(expectedSize);
        parts.saveAndFlush(part);

        UploadPartRequest uploadPartRequest =
                UploadPartRequest.builder()
                        .bucket(upload.getBucketName())
                        .key(upload.getObjectKey())
                        .uploadId(upload.getS3UploadId())
                        .partNumber(partNumber)
                        .build();

        UploadPartPresignRequest presignRequest =
                UploadPartPresignRequest.builder()
                        .signatureDuration(
                                Duration.ofMinutes(expiryMinutes)
                        )
                        .uploadPartRequest(uploadPartRequest)
                        .build();

        String url = presigner
                .presignUploadPart(presignRequest)
                .url()
                .toString();
        return new PartUrlResponse(partNumber, url);
    }


    @Transactional
    public void reportPart(
            String appId,
            String ownerId,
            int partNumber,
            PartReportRequest request) {

        FileUpload upload = activeUpload(appId, ownerId);

        validatePartNumber(upload, partNumber);

        if (request == null
                || request.eTag() == null
                || request.eTag().isBlank()) {
            throw new IllegalArgumentException("ETag is required");
        }

        long expectedSize =
                expectedPartSize(upload, partNumber);

        if (request.size() != expectedSize) {
            throw new IllegalArgumentException(
                    "Unexpected part size"
            );
        }

        FileUploadPart part =
                parts.findByUploadIdAndPartNumber(
                        appId, partNumber
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Part record not found"
                        )
                );

        part.setActualSize(request.size());
        part.setETag(request.eTag());
        part.setStatus(FileUploadStatus.CLIENT_REPORTED);
        part.setUpdatedAt(LocalDateTime.now());

        parts.save(part);




    }


    @Transactional
    public UploadStatusResponse status(
            String appId,
            String ownerId) {


        FileUpload upload = findUpload(appId, ownerId);

        if (upload.getStatus() != FileUploadStatus.IN_PROGRESS) {
            return new UploadStatusResponse(
                    upload.getId(),
                    upload.getStatus().name(),
                    upload.getTotalParts(),
                    upload.getFileSize(),
                    List.of()
            );
        }

        List<Part> actualParts = listAllParts(upload);

        actualParts.sort(
                Comparator.comparingInt(Part::partNumber)
        );

        List<UploadedPartDto> result = new ArrayList<>();

        for (Part actual : actualParts) {
            int number = actual.partNumber();
            long size = actual.size();
            String eTag = actual.eTag();

            result.add(new UploadedPartDto(number, size, eTag));

            parts.findByUploadIdAndPartNumber(
                    appId, number
            ).ifPresent(dbPart -> {
                boolean changed =
                        !FileUploadStatus.UPLOADED.equals(
                                dbPart.getStatus())
                                || !Objects.equals(
                                dbPart.getETag(), eTag)
                                || !Objects.equals(
                                dbPart.getActualSize(), size);

                dbPart.setActualSize(size);
                dbPart.setETag(eTag);
                dbPart.setStatus(FileUploadStatus.UPLOADED);

                parts.save(dbPart);


            });
        }
        return new UploadStatusResponse(
                upload.getId(),
                upload.getStatus().name(),
                upload.getTotalParts(),
                upload.getFileSize(),
                result
        );
    }
    @Transactional
    public CompleteResponse complete(
            String appId,
            String ownerId) {
        try {
            FileUpload upload = activeUpload(appId, ownerId);
            List<Part> actualParts = listAllParts(upload);
            updateUploadedPart(actualParts, upload);

            actualParts.sort(
                    Comparator.comparingInt(Part::partNumber)
            );

            if (actualParts.size() != upload.getTotalParts()) {
                throw new IllegalStateException(
                        "Some parts have not been uploaded. Expected="
                                + upload.getTotalParts()
                                + ", actual=" + actualParts.size()
                );
            }

            long totalBytes = 0;

            List<CompletedPart> completedParts =
                    new ArrayList<>();

            for (int i = 0; i < actualParts.size(); i++) {
                Part actual = actualParts.get(i);
                int expectedNumber = i + 1;

                if (actual.partNumber() != expectedNumber) {
                    throw new IllegalStateException(
                            "Missing part " + expectedNumber
                    );
                }
                long expectedSize =
                        expectedPartSize(upload, expectedNumber);
                if (actual.size() != expectedSize) {
                    throw new IllegalStateException(
                            "Unexpected size for part "
                                    + expectedNumber
                                    + ". Expected=" + expectedSize
                                    + ", actual=" + actual.size()
                    );
                }
                if (actual.eTag() == null
                        || actual.eTag().isBlank()) {
                    throw new IllegalStateException(
                            "Missing ETag for part "
                                    + expectedNumber
                    );
                }
                totalBytes += actual.size();
                completedParts.add(
                        CompletedPart.builder()
                                .partNumber(actual.partNumber())
                                .eTag(actual.eTag())
                                .build()
                );
            }
            if (totalBytes != upload.getFileSize()) {
                throw new IllegalStateException(
                        "Uploaded file size does not match. Expected="
                                + upload.getFileSize()
                                + ", actual=" + totalBytes
                );
            }


            CompleteMultipartUploadResponse response =
                    r2.completeMultipartUpload(
                            CompleteMultipartUploadRequest.builder()
                                    .bucket(upload.getBucketName())
                                    .key(upload.getObjectKey())
                                    .uploadId(upload.getS3UploadId())
                                    .multipartUpload(
                                            CompletedMultipartUpload.builder()
                                                    .parts(completedParts)
                                                    .build()
                                    )
                                    .build()
                    );


            upload.setUpdatedAt(LocalDateTime.now());
            upload.setStatus(FileUploadStatus.COMPLETED);
            uploads.saveAndFlush(upload);


            return new CompleteResponse(
                    upload.getId(),
                    upload.getObjectKey(),
                    response.eTag(),
                    String.valueOf(upload.getStatus())
            );

        } catch (RuntimeException ex) {
            log.error(
                    "Multipart completion failed. appUploadId={}, ownerId={}",
                    appId, ownerId, ex
            );

            throw ex;
        }
    }
    private void updateUploadedPart(List<Part> result, FileUpload upload) {
        for (Part part : result) {
            try {
                Optional<FileUploadPart> dbPart = parts.findByUploadIdAndPartNumber(
                        upload.getId(),
                        part.partNumber()
                );

                if (dbPart.isPresent()) {
                    FileUploadPart fileUploadPart = dbPart.get();
                    fileUploadPart.setStatus(FileUploadStatus.UPLOADED);
                    fileUploadPart.setActualSize(part.size());
                    fileUploadPart.setETag(part.eTag());

                    parts.save(fileUploadPart);
                }

            } catch (RuntimeException ex) {
                log.error(
                        "Failed to update part. appUploadId={}, partNumber={}",
                        upload.getId(),
                        part.partNumber(),
                        ex
                );
            }
        }
    }
    @Transactional
    public void abort(
            String appId,
            String ownerId) {


        FileUpload upload = activeUpload(appId, ownerId);

        r2.abortMultipartUpload(
                AbortMultipartUploadRequest.builder()
                        .bucket(upload.getBucketName())
                        .key(upload.getObjectKey())
                        .uploadId(upload.getS3UploadId())
                        .build()
        );

        upload.setStatus(FileUploadStatus.ABORTED);
        uploads.saveAndFlush(upload);



    }

    private FileUpload activeUpload(
            String appId,
            String ownerId) {

        FileUpload upload = findUpload(appId, ownerId);

        if (upload.getStatus() != FileUploadStatus.IN_PROGRESS) {
            throw new IllegalStateException("Upload is not active");
        }

        return upload;
    }
    private FileUpload findUpload(
            String appId,
            String ownerId) {

        return uploads.findByIdAndOwnerId(appId, ownerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Upload session not found"
                ));
    }


    private void validatePartNumber(
            FileUpload upload,
            int partNumber) {

        if (partNumber < 1
                || partNumber > upload.getTotalParts()) {
            throw new IllegalArgumentException(
                    "Invalid part number"
            );
        }
    }


    private long expectedPartSize(
            FileUpload upload,
            int partNumber) {

        long start =
                (long) (partNumber - 1) * upload.getPartSize();

        return Math.min(
                upload.getPartSize(),
                upload.getFileSize() - start
        );
    }


    private List<Part> listAllParts(FileUpload upload) {

        List<Part> result = new ArrayList<>();
        Integer marker = null;

        do {
            ListPartsRequest.Builder builder =
                    ListPartsRequest.builder()
                            .bucket(upload.getBucketName())
                            .key(upload.getObjectKey())
                            .uploadId(upload.getS3UploadId());

            if (marker != null) {
                builder.partNumberMarker(marker);
            }

            ListPartsResponse response =
                    r2.listParts(builder.build());

            result.addAll(response.parts());

            marker = Boolean.TRUE.equals(response.isTruncated())
                    ? response.nextPartNumberMarker()
                    : null;

        } while (marker != null);
        return result;
    }



    private void checkFileConstraint(
            String fileName,
            long fileSize) {

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "File name is required"
            );
        }

        fileName = fileName.replace("\\", "/");

        if (fileName.contains("/")) {
            fileName =
                    fileName.substring(fileName.lastIndexOf('/') + 1);
        }

        if (fileName.isBlank() || fileName.length() > 255) {
            throw new IllegalArgumentException(
                    "Invalid file name"
            );
        }

        if (fileSize <= 0 || fileSize > maxFileSize) {
            throw new IllegalArgumentException(
                    "Invalid file size"
            );
        }
    }

    public List<IncompleteMultipartUploadDTO> findIncompleteUploads(
            String bucket
    ) {
        List<IncompleteMultipartUploadDTO> result =
                new ArrayList<>();

        String keyMarker = null;
        String uploadIdMarker = null;

        do {
            ListMultipartUploadsRequest.Builder request =
                    ListMultipartUploadsRequest.builder()
                            .bucket(bucket)
                            .maxUploads(1000);

            if (keyMarker != null) {
                request.keyMarker(keyMarker);
            }

            if (uploadIdMarker != null) {
                request.uploadIdMarker(uploadIdMarker);
            }

            ListMultipartUploadsResponse response =
                    s3Client.listMultipartUploads(request.build());

            for (MultipartUpload upload : response.uploads()) {

                int partCount = 0;
                long totalBytes = 0;

                String partNumberMarker = null;

                // Uploaded parts ko page-by-page count karo.
                do {
                    ListPartsRequest.Builder partsRequest =
                            ListPartsRequest.builder()
                                    .bucket(bucket)
                                    .key(upload.key())
                                    .uploadId(upload.uploadId())
                                    .maxParts(1000);

                    if (partNumberMarker != null) {
                        partsRequest.partNumberMarker(
                                Integer.parseInt(partNumberMarker)
                        );
                    }

                    ListPartsResponse partsResponse =
                            s3Client.listParts(partsRequest.build());

                    for (Part part : partsResponse.parts()) {
                        partCount++;
                        totalBytes += part.size();
                    }

                    partNumberMarker =
                            partsResponse.isTruncated()
                                    ? String.valueOf(
                                    partsResponse.nextPartNumberMarker()
                            )
                                    : null;

                } while (partNumberMarker != null);

                result.add(new IncompleteMultipartUploadDTO(
                        bucket,
                        upload.key(),
                        upload.uploadId(),
                        upload.initiated(),
                        partCount,
                        totalBytes
                ));
            }

            keyMarker = response.isTruncated()
                    ? response.nextKeyMarker() : null;

            uploadIdMarker = response.isTruncated()
                    ? response.nextUploadIdMarker() : null;

        } while (keyMarker != null);

        return result;
    }




}