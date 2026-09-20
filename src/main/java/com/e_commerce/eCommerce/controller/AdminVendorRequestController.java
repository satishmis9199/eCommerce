package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.request.*;
import com.e_commerce.eCommerce.enums.VendorRequestStatus;
import com.e_commerce.eCommerce.exception.VendorRequestException;
import com.e_commerce.eCommerce.service.AdminVendorRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/vendor/s1/v1/super/admin")
@RequiredArgsConstructor
public class AdminVendorRequestController {

    private final AdminVendorRequestService adminVendorRequestService;

    @GetMapping("/vendor-requests")
    public ResponseEntity<Page<VendorRequestAdminListResponse>> lists(
            @RequestParam(required = false) VendorRequestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(
                adminVendorRequestService.list(status, pageable)
        );
    }

    @GetMapping("/vendor-requests/{id}")
    public ResponseEntity<VendorRequestAdminDetailResponse> getOnes(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adminVendorRequestService.getById(id)
        );
    }

    @PatchMapping("/vendor-requests/{id}/contact")
    public ResponseEntity<VendorRequestAdminDetailResponse> markContacteds(
            @PathVariable Long id,
            @RequestBody(required = false) VendorRequestContactRequest request,
            Authentication authentication) {

        String reviewer = authentication != null
                ? authentication.getName()
                : "SUPER_ADMIN";

        return ResponseEntity.ok(
                adminVendorRequestService.markContacted(
                        id,
                        request,
                        reviewer
                )
        );
    }

    @PostMapping("/vendor/onboarding/decisions")
    public ResponseEntity<VendorOnboardingDecisionResponse> decide(
            @Valid @RequestBody VendorOnboardingDecisionRequest request,
            Authentication authentication) {

        try {
            String reviewer = authentication != null
                    ? authentication.getName()
                    : "SUPER_ADMIN";

            VendorOnboardingDecisionResponse result =
                    adminVendorRequestService.decide(request, reviewer);

            return ResponseEntity.ok(result);

        } catch (VendorRequestException e) {

            return ResponseEntity
                    .status(e.getStatus())
                    .body(
                            VendorOnboardingDecisionResponse.error(
                                    e.getMessage()
                            )
                    );

        } catch (Exception e) {

            log.error(
                    "Unable to process vendor onboarding decision",
                    e
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            VendorOnboardingDecisionResponse.error(
                                    "Unable to process vendor decision"
                            )
                    );
        }
    }
}