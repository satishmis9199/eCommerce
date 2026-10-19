
package com.e_commerce.eCommerce.entity;

import com.e_commerce.eCommerce.enums.FileUploadStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "file_upload_part",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_upload_part",
                columnNames = {"upload_id", "part_number"}
        )
)
@Getter
@Setter
public class FileUploadPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "tenantId")
    private String tenantId;

    @Column(name = "upload_id", nullable = false, length = 36)
    private String uploadId;

    @Column(name = "part_number", nullable = false)
    private Integer partNumber;

    @Column(name = "expected_size", nullable = false)
    private Long expectedSize;

    @Column(name = "actual_size")
    private Long actualSize;

    @Column(name = "e_tag", length = 512)
    private String eTag;
@Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)

    private FileUploadStatus status;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
