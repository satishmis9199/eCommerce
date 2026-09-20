package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.dto.request.*;
import com.e_commerce.eCommerce.enums.VendorRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminVendorRequestService {

    Page<VendorRequestAdminListResponse> list(VendorRequestStatus status, Pageable pageable);

    VendorRequestAdminDetailResponse getById(Long id);

    VendorOnboardingDecisionResponse decide(VendorOnboardingDecisionRequest request, String reviewerName);

    // Separate from decide(): just flips PENDING -> CONTACTED, no approve/reject semantics.
    VendorRequestAdminDetailResponse markContacted(Long id, VendorRequestContactRequest request, String reviewerName);
}