package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.CustomAnnotation.RequiresFeature;
import com.e_commerce.eCommerce.dto.UploadResult;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.ProductExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/vendor/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductExcelController {

    private static final String[] ALLOWED_EXTENSIONS = {".xlsx"};

    private final ProductExcelService productExcelService;
    @GetMapping("/template")
    @RequiresFeature("BULK_PRODUCT_UPLOAD")
    public ResponseEntity<ByteArrayResource> downloadTemplate(
            @AuthenticationPrincipal CustomUserDetail userDetail) throws Exception {

        byte[] excel = productExcelService.generateTemplate(userDetail.getUser());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Product_Template.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(excel.length)
                .body(new ByteArrayResource(excel));
    }
    @RequiresFeature("BULK_PRODUCT_UPLOAD")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadExcel(
            @RequestParam MultipartFile file,
            @AuthenticationPrincipal CustomUserDetail userDetail) throws Exception {
        if (file == null || file.isEmpty()) {
            log.error("Upload failed : File is null or empty");
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Please select a file to upload."));
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !hasAllowedExtension(filename)) {
            log.error("Invalid file extension : {}", filename);
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Only .xlsx files are supported."));
        }

        UploadResult result = productExcelService.uploadProducts(file, userDetail.getUser());
        if (result.getRowErrors() != null && !result.getRowErrors().isEmpty()) {
            log.error("Validation Errors:");
            result.getRowErrors().forEach(error ->
                    log.error("Excel Row {} -> {}", error.getExcelRowNumber(), error.getMessages())
            );
        }
        if (result.getTotalRowsProcessed() == 0) {

            log.error("No product rows found in uploaded excel.");

            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of(
                            "message", "No product rows found to upload. Please fill in the template starting below the sample rows.",
                            "result", result
                    ));
        }
        if (result.getSuccessCount() == 0) {

            log.error("Every row failed validation.");

            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of(
                            "message", "No products were uploaded. Please fix the errors below and try again.",
                            "result", result
                    ));
        }
        if (result.getFailureCount() > 0) {

            log.warn("Partial Upload. Success={}, Failed={}",
                    result.getSuccessCount(),
                    result.getFailureCount());

            return ResponseEntity.ok(Map.of(
                    "message", result.getSuccessCount() + " product(s) uploaded successfully, "
                            + result.getFailureCount() + " row(s) had errors and were skipped.",
                    "result", result
            ));
        }
        log.info("All products uploaded successfully.");

        return ResponseEntity.ok(Map.of(
                "message", result.getSuccessCount() + " product(s) uploaded successfully.",
                "result", result
        ));
    }

    private boolean hasAllowedExtension(String filename) {
        String lower = filename.toLowerCase();
        for (String ext : ALLOWED_EXTENSIONS) {
            if (lower.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}