package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.VendorOnboardingApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Repository
public interface VendorOnnBRepo extends JpaRepository<VendorOnboardingApplication,Long> {
    Optional<VendorOnboardingApplication> findByVendorId(Long vendorId);

    VendorOnboardingApplication findByApplicationId(String applicationId);
}
