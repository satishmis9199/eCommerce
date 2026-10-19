
package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.FileUploadPart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileUploadPartRepository
        extends JpaRepository<FileUploadPart, Long> {

    Optional<FileUploadPart> findByUploadIdAndPartNumber(
            String uploadId,
            Integer partNumber
    );

    List<FileUploadPart> findByUploadIdOrderByPartNumberAsc(
            String uploadId
    );
}
