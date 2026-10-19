
package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.IncompleteMultipartUploadDTO;
import com.e_commerce.eCommerce.dto.UploadDtos.*;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.R2MultipartUploadService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final R2MultipartUploadService service;


    @PostMapping("/initiate")
    public InitiateResponse initiate(
            @RequestBody InitiateRequest request,
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        return service.initiate(
                String.valueOf(userDetail.getId()),
                request.fileName(),
                request.fileSize(),
                resolvetenent()
        );
    }

    @PostMapping(value = "/{id}/parts/{partNumber}/url")
    public PartUrlResponse partUrl(
            @PathVariable String id,
            @PathVariable int partNumber,
            @AuthenticationPrincipal CustomUserDetail auth) {

        return service.presignPart(
                id,
                String.valueOf(auth.getId()),
                partNumber,
                resolvetenent()
        );
    }

    @PostMapping("/{id}/parts/{partNumber}/report")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void report(
            @PathVariable String id,
            @PathVariable int partNumber,
            @RequestBody PartReportRequest request,
            @AuthenticationPrincipal CustomUserDetail auth) {

        service.reportPart(
                id,
                String.valueOf(auth.getId()),
                partNumber,
                request
        );
    }

    @GetMapping("/{id}/status")
    public UploadStatusResponse status(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetail auth) {

        return service.status(id, String.valueOf(auth.getId()));
    }

    @PostMapping("/{id}/complete")
    public CompleteResponse complete(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetail auth) {

        return service.complete(id, String.valueOf(auth.getId()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void abort(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetail auth) {

        service.abort(id, String.valueOf(auth.getId()));
    }



    public String resolvetenent(){
        return TenantContext.getTenantId();
    }



//    @GetMapping("/incomplete-uploads")
//    public List<IncompleteMultipartUploadDTO> getIncompleteUploads(
//            @RequestParam String bucket
//    ) {
//        return service.findIncompleteUploads(bucket);
//    }
}
