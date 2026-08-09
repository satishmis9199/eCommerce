package com.e_commerce.eCommerce.controller;


import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.VendorPolicyRequestDto;
import com.e_commerce.eCommerce.dto.VendorPolicyResponseDto;
import com.e_commerce.eCommerce.enums.PolicyType;
import com.e_commerce.eCommerce.service.VendorPolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/u1/v1/policies")
@RequiredArgsConstructor
public class VendorPolicyController {

    private final VendorPolicyService vendorPolicyService;

    // TODO: tenantId/vendorId nikalne ka actual tareeka — apke SecurityContext
    // ya TenantContext ke hisaab se replace karo. Yahan placeholder rakha hai.
    private String getTenantId() { return "tenant_id_from_context"; }
    private Long getVendorId() { return 1L; }

    @PostMapping
    public ResponseEntity<ApiResponse<VendorPolicyResponseDto>> saveOrUpdatePolicy(@Valid @RequestBody VendorPolicyRequestDto dto) {
        VendorPolicyResponseDto result = vendorPolicyService.saveOrUpdatePolicy(getTenantId(), getVendorId(), dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Updated Successfully",
                                result

                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VendorPolicyResponseDto>>> getAllPolicies() {
        List<VendorPolicyResponseDto> result = vendorPolicyService.getAllPolicies(getTenantId(), getVendorId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Updated Successfully",
                                result

                        )
                );
    }

    @GetMapping("/{policyType}")
    public ResponseEntity<ApiResponse<VendorPolicyResponseDto>>getPolicyByType(@PathVariable PolicyType policyType) {
        VendorPolicyResponseDto result = vendorPolicyService.getPolicyByType(getTenantId(), getVendorId(), policyType);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Updated Successfully",
                                result

                        )
                );
    }

    @DeleteMapping("/{policyType}")
    public ResponseEntity<ApiResponse<?>> deletePolicy(@PathVariable PolicyType policyType) {
        vendorPolicyService.deletePolicy(getTenantId(), getVendorId(), policyType);
       return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Updated Successfully"


                        )
                );
    }
}