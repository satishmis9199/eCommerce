package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.entity.OnboardingStatus;
import com.e_commerce.eCommerce.entity.VendorOnboardingHistory;
import com.e_commerce.eCommerce.exception.vendorNotFoundException;

import com.e_commerce.eCommerce.repository.VendorOnboardingHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class OnboardingHistoryService {
    private final VendorOnboardingHistoryRepository vendorOnbaordingRepository;
    public void saveVendorOnBoardingHistory(String tenantid, Long vendorId, String remarks, OnboardingStatus toStatus, OnboardingStatus fromStatus){
        if(tenantid==null){
            throw new vendorNotFoundException("tenant Id Not found");
        }
        VendorOnboardingHistory vendorOnboardingHistory=VendorOnboardingHistory.builder()
                .tenantId(tenantid)
                .vendorId(vendorId)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .remarks(remarks)
                .createdAt(LocalDateTime.now())
                .build();
        vendorOnbaordingRepository.save(vendorOnboardingHistory);
    }
}
