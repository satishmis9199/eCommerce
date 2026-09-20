package com.e_commerce.eCommerce.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Separate from VendorOnboardingDecisionRequest on purpose: marking a request as
 * CONTACTED is just "admin has reached out", not an approve/reject decision.
 * Remarks are optional here (e.g. "called, asked for GST doc").
 */
@Getter
@Setter
public class VendorRequestContactRequest {
    private String remarks;
}