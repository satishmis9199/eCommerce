package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.dto.request.*;
import com.e_commerce.eCommerce.entity.VendorRequest;
import com.e_commerce.eCommerce.enums.VendorRequestStatus;
import com.e_commerce.eCommerce.exception.VendorRequestException;
import com.e_commerce.eCommerce.repository.VendorRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminVendorRequestServiceImpl implements AdminVendorRequestService {

    private final VendorRequestRepository vendorRequestRepository;
    private final VendorRequestService planService;

    @Override
    public Page<VendorRequestAdminListResponse> list(VendorRequestStatus status, Pageable pageable) {
        Page<VendorRequest> page = (status == null)
                ? vendorRequestRepository.findAll(pageable)
                : vendorRequestRepository.findByStatus(status, pageable);

        return page.map(this::toListResponse);
    }

    @Override
    public VendorRequestAdminDetailResponse getById(Long id) {
        VendorRequest vr = vendorRequestRepository.findById(id)
                .orElseThrow(() -> new VendorRequestException( HttpStatus.NOT_FOUND,"Vendor request not found"));
        return toDetailResponse(vr);
    }

    @Override
    @Transactional
    public VendorOnboardingDecisionResponse decide(VendorOnboardingDecisionRequest request, String reviewerName) {
        VendorRequest vr = vendorRequestRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new VendorRequestException( HttpStatus.NOT_FOUND,"Vendor request not found"));

        if (vr.getStatus() == VendorRequestStatus.APPROVED || vr.getStatus() == VendorRequestStatus.REJECTED) {
            throw new VendorRequestException(
                    HttpStatus.BAD_REQUEST,
                    "This request has already been " + vr.getStatus().name()
            );

        }

        String action = request.getAction() == null ? "" : request.getAction().trim().toUpperCase();

        switch (action) {
            case "APPROVE" -> {
                vr.setStatus(VendorRequestStatus.APPROVED);
                vr.setAdminRemarks(request.getRemarks());
                vr.setReviewedBy(reviewerName);
                vr.setReviewedAt(LocalDateTime.now());
                VendorRequest saved=vendorRequestRepository.save(vr);
                planService.saveTracking(
                        saved,
                        VendorRequestStatus.APPROVED,
                        null,
                        reviewerName
                );

                // TODO: this is where the Super Admin flow should create the actual
                // Vendor/tenant record and assign a subscription plan, per the
                // VendorRequestStatus.APPROVED contract in the enum.

                log.info("Vendor request {} approved by {}", vr.getRequestCode(), reviewerName);
                return VendorOnboardingDecisionResponse.ok(
                        "Vendor request approved successfully.", vr.getStatus().name());
            }
            case "REJECT" -> {
                vr.setStatus(VendorRequestStatus.REJECTED);
                vr.setAdminRemarks(request.getRemarks());
                vr.setReviewedBy(reviewerName);
                vr.setReviewedAt(LocalDateTime.now());
               VendorRequest saved= vendorRequestRepository.save(vr);
                planService.saveTracking(
                        saved,
                        VendorRequestStatus.REJECTED,
                        null,
                        reviewerName
                );

                log.info("Vendor request {} rejected by {} (allowResubmit={})",
                        vr.getRequestCode(), reviewerName, request.isAllowResubmit());

                return VendorOnboardingDecisionResponse.ok(
                        "Vendor request rejected successfully.", vr.getStatus().name());
            }
            default -> throw new VendorRequestException(
                    HttpStatus.BAD_REQUEST,"action must be either APPROVE or REJECT");
        }
    }

    @Override
    @Transactional
    public VendorRequestAdminDetailResponse markContacted(Long id, VendorRequestContactRequest request, String reviewerName) {
        VendorRequest vr = vendorRequestRepository.findById(id)
                .orElseThrow(() -> new VendorRequestException( HttpStatus.NOT_FOUND,"Vendor request not found"));

        if (vr.getStatus() == VendorRequestStatus.APPROVED || vr.getStatus() == VendorRequestStatus.REJECTED) {
            throw new VendorRequestException(HttpStatus.CONFLICT,
                    "This request has already been " + vr.getStatus().name().toLowerCase() +
                            " and can no longer be marked as contacted."
            );
        }

        vr.setStatus(VendorRequestStatus.CONTACTED);
        if (request != null && request.getRemarks() != null && !request.getRemarks().isBlank()) {
            vr.setAdminRemarks(request.getRemarks());
        }
        vr.setReviewedBy(reviewerName);
        vr.setReviewedAt(LocalDateTime.now());
        VendorRequest saved=vendorRequestRepository.save(vr);

        planService.saveTracking(
                saved,
                VendorRequestStatus.CONTACTED,
                null,
                reviewerName
        );

        log.info("Vendor request {} marked CONTACTED by {}", vr.getRequestCode(), reviewerName);
        return toDetailResponse(vr);
    }

    private VendorRequestAdminListResponse toListResponse(VendorRequest vr) {
        return VendorRequestAdminListResponse.builder()
                .id(vr.getId())
                .requestCode(vr.getRequestCode())
                .businessName(vr.getBusinessName())
                .ownerName(vr.getOwnerName())
                .email(vr.getEmail())
                .phone(vr.getPhone())
                .city(vr.getCity())
                .state(vr.getState())
                .businessCategory(vr.getBusinessCategory())
                .status(vr.getStatus())
                .createdAt(vr.getCreatedAt())
                .build();
    }

    private VendorRequestAdminDetailResponse toDetailResponse(VendorRequest vr) {
        return VendorRequestAdminDetailResponse.builder()
                .id(vr.getId())
                .requestCode(vr.getRequestCode())
                .businessName(vr.getBusinessName())
                .ownerName(vr.getOwnerName())
                .email(vr.getEmail())
                .phone(vr.getPhone())
                .city(vr.getCity())
                .state(vr.getState())
                .businessCategory(vr.getBusinessCategory())
                .message(vr.getMessage())
                .status(vr.getStatus())
                .adminRemarks(vr.getAdminRemarks())
                .reviewedBy(vr.getReviewedBy())
                .reviewedAt(vr.getReviewedAt())
                .ipAddress(vr.getIpAddress())
                .createdAt(vr.getCreatedAt())
                .updatedAt(vr.getUpdatedAt())
                .build();
    }
}