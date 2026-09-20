package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.VendorRequest;

import com.e_commerce.eCommerce.enums.VendorRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;

public interface VendorRequestRepository extends JpaRepository<VendorRequest, Long> {

    // duplicate check: same email / phone already has an open request
    boolean existsByEmailIgnoreCaseAndStatusIn(String email, Collection<VendorRequestStatus> statuses);

    boolean existsByPhoneAndStatusIn(String phone, Collection<VendorRequestStatus> statuses);

    // rate limit: how many requests this IP made since a given time
    long countByIpAddressAndCreatedAtAfter(String ipAddress, LocalDateTime since);

    VendorRequest findByRequestCode(String requestCode);

    Page<VendorRequest> findByStatus(VendorRequestStatus status, Pageable pageable);
}