package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.config.R2Properties;
import com.e_commerce.eCommerce.dto.UploadResponseDTO;
import com.e_commerce.eCommerce.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/vendor/file")
public class FileUploadController {

    private final FileStorageService storageService;
    private final R2Properties r2Properties;
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);


    @PostMapping("/upload/profile")
    public ResponseEntity<?> uploadProfile(
            @RequestParam("file") MultipartFile file) {
        UploadResponseDTO dto = new UploadResponseDTO();
        try {
            logger.info("Inside Profile Update");

            String url = storageService.upload(file, "profile");


            dto.setSuccess(true);
            dto.setMessage("Profile Image Uploaded Successfully");

            dto.setObjectKey(url);

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            dto.setSuccess(false);
            dto.setMessage(e.getMessage());
            dto.setObjectKey("");
            return ResponseEntity.badRequest().body(dto);

        }

    }

    @PostMapping("/upload/logo")
    public ResponseEntity<?> uploadLogo(
            @RequestParam("file") MultipartFile file) {

        String url = storageService.upload(file, "logo");

        UploadResponseDTO dto = new UploadResponseDTO();

        dto.setSuccess(true);
        dto.setMessage("Logo Uploaded Successfully");

        dto.setObjectKey(url);

        return ResponseEntity.ok(dto);

    }


    @PostMapping("/upload/banner")
    public ResponseEntity<?> uploadBanner(
            @RequestParam("file") MultipartFile file) {

        String url = storageService.upload(file, "banner");

        UploadResponseDTO dto = new UploadResponseDTO();

        dto.setSuccess(true);
        dto.setMessage("Banner Uploaded Successfully");

        dto.setObjectKey(url);

        return ResponseEntity.ok(dto);

    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUnUsedFile(@RequestParam("objectKey") String obj) {
        try {

            storageService.delete(obj);
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "File Deleted SuccessFully"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error While Deleteing"
            ));
        }
    }

    @PostMapping("/upload/category")
    public ResponseEntity<?> uplaodCategoryImage(@RequestParam("file") MultipartFile file) {
        String url = storageService.upload(file, "category");

        UploadResponseDTO dto = new UploadResponseDTO();

        dto.setSuccess(true);
        dto.setMessage("Category Uploaded Successfully");

        dto.setObjectKey(url);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/upload/product")

    public ResponseEntity<?> uploadProduct(@RequestParam("file") MultipartFile file) {
        logger.error("Inside Product Image Upload");
        String url = storageService.upload(file, "products");

        UploadResponseDTO dto = new UploadResponseDTO();

        dto.setSuccess(true);
        dto.setMessage("Product Uploaded Successfully");

        dto.setObjectKey(url);

        return ResponseEntity.ok(dto);
    }

}