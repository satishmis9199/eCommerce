package com.e_commerce.eCommerce.service;


import com.e_commerce.eCommerce.dto.VendorPolicyRequestDto;
import com.e_commerce.eCommerce.dto.VendorPolicyResponseDto;
import com.e_commerce.eCommerce.entity.VendorPolicy;
import com.e_commerce.eCommerce.enums.PolicyType;
import com.e_commerce.eCommerce.repository.VendorPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VendorPolicyService {

    private final VendorPolicyRepository vendorPolicyRepository;

    /**
     * Create or Update — since (tenantId, vendorId, policyType) is unique,
     * this acts as an upsert. Admin doesn't need to know whether the
     * policy already exists; they just fill the form and save.
     */
    @Transactional
    public VendorPolicyResponseDto saveOrUpdatePolicy(
            String tenantId, Long vendorId, VendorPolicyRequestDto dto) {

        VendorPolicy policy = vendorPolicyRepository
                .findByTenantIdAndVendorIdAndPolicyType(tenantId, vendorId, dto.getPolicyType())
                .orElse(VendorPolicy.builder()
                        .tenantId(tenantId)
                        .vendorId(vendorId)
                        .policyType(dto.getPolicyType())
                        .createdAt(LocalDateTime.now())
                        .build());

        policy.setTitle(dto.getTitle());
        policy.setContent(dto.getContent());
        policy.setStatus(dto.getStatus());
        policy.setUpdatedAt(LocalDateTime.now());

        VendorPolicy saved = vendorPolicyRepository.save(policy);
        return toResponseDto(saved);
    }

    public List<VendorPolicyResponseDto> getAllPolicies(String tenantId, Long vendorId) {
        return vendorPolicyRepository.findByTenantIdAndVendorId(tenantId, vendorId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    public VendorPolicyResponseDto getPolicyByType(
            String tenantId, Long vendorId, PolicyType policyType) {

        Optional<VendorPolicy> policy1 = vendorPolicyRepository
                .findByTenantIdAndVendorIdAndPolicyType(tenantId, vendorId, policyType);
        if(policy1.isEmpty()){
           throw new RuntimeException(
                    "Policy of type " + policyType + " not found for this vendor");
        }
        VendorPolicy policy=policy1.get();
        return toResponseDto(policy);
    }

    @Transactional
    public void deletePolicy(String tenantId, Long vendorId, PolicyType policyType) {
        vendorPolicyRepository.deleteByTenantIdAndVendorIdAndPolicyType(tenantId, vendorId, policyType);
    }

    private VendorPolicyResponseDto toResponseDto(VendorPolicy policy) {
        return VendorPolicyResponseDto.builder()
                .id(policy.getId())
                .policyType(policy.getPolicyType())
                .title(policy.getTitle())
                .content(policy.getContent())
                .status(policy.getStatus())
                .createdAt(policy.getCreatedAt())
                .updatedAt(policy.getUpdatedAt())
                .build();
    }
}