
package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileUploadRepository
        extends JpaRepository<FileUpload, String> {

    Optional<FileUpload> findByIdAndOwnerId(
            String id,
            String ownerId
    );
}
