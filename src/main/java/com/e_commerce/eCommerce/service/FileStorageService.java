package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.R2Properties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final S3Client s3Client;
    private final R2Properties properties;
    private static final Logger logger= LoggerFactory.getLogger(FileStorageService.class);


    public String upload(MultipartFile file, String folder) {

        try {

            String original = file.getOriginalFilename();
            logger.info("Original FIle Name {}",original);

            String extension = "";
            int size= Math.toIntExact(file.getSize());
            logger.info("Size Of Uploaded File :: "+size);

            if (original != null && original.contains(".")) {
                extension = original.substring(original.lastIndexOf("."));
            }

            String objectKey = folder + "/" + UUID.randomUUID() + extension;
            logger.info("Object Key {}",objectKey);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize()
                    )
            );

            return  objectKey;

        } catch (IOException e) {
            throw new RuntimeException("File Upload Failed", e);
        }

    }


    public void delete(String objectKey) {

        logger.info("Deleting Object : {}", objectKey);

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(objectKey)
                .build();

        s3Client.deleteObject(request);

        logger.info("Deleted Object : {}", objectKey);
    }


    public String upload(byte[] fileBytes,
                         String fileName,
                         String contentType,
                         String folder) {

        String objectKey = folder + "/" + fileName;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(objectKey)
                .contentType(contentType)
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(fileBytes)
        );
        logger.error("Returning object Key fpr  Pdf"+objectKey);
        return objectKey;

    }

}