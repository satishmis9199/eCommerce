
package com.e_commerce.eCommerce.entity;

import com.e_commerce.eCommerce.enums.FileUploadStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_upload")
@Getter
@Setter
public class FileUpload {

    @Id
    @Column(length = 36)
    private String id;
    @Column(name="tenant_id")
    private String tenantId;

    @Column(name = "owner_id", nullable = false, length = 150)
    private String ownerId;

    @Column(name = "bucket_name", nullable = false)
    private String bucketName;

    @Column(name = "object_key", nullable = false, length = 1024)
    private String objectKey;

    @Column(name = "s3_upload_id", nullable = false, length = 1024)
    private String s3UploadId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "part_size", nullable = false)
    private Long partSize;

    @Column(name = "total_parts", nullable = false)
    private Integer totalParts;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileUploadStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
