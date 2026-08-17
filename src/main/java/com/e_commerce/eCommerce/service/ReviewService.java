package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.ReviewResponseAdminDto;
import com.e_commerce.eCommerce.dto.UserReviewResponseDTO;
import com.e_commerce.eCommerce.entity.ProductReview;
import com.e_commerce.eCommerce.entity.ReviewStatus;
import com.e_commerce.eCommerce.entity.Roles;
import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.repository.ProductReviewRepository;
import com.e_commerce.eCommerce.repository.UserRepos;
import com.e_commerce.eCommerce.repository.VendorRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class ReviewService {

    private final VendorRepository vendorRepository;
    private final ProductReviewRepository productReviewRepository;
    private final UserRepos userRepository;

    public Page<ReviewResponseAdminDto> getAllReviewForAdmin(
            CustomUserDetail userDetail,
            ReviewStatus status,
            Pageable pageable) {

        log.info("========================================================");
        log.info("START :: ReviewService.getAllReviewForAdmin()");
        log.info("Requested Status : {}", status);
        log.info("Requested Page   : {}", pageable.getPageNumber());
        log.info("Requested Size   : {}", pageable.getPageSize());

        String tenantId = TenantContext.getTenantId();
        log.info("Tenant Id : {}", tenantId);

        if (tenantId == null) {
            throw new RuntimeException("No tenant");
        }

        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenantId);

        if (vendor.isEmpty()) {
            throw new RuntimeException("Tenant Does Not Exist");
        }

        log.info("Vendor Id : {}", vendor.get().getId());

        if (userDetail == null) {
            throw new RuntimeException("Please Login First");
        }

        log.info("Logged In User Id : {}", userDetail.getId());
        log.info("Logged In Role    : {}", userDetail.getRole());

        if (userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorized");
        }

        Page<ReviewResponseAdminDto> page =
                productReviewRepository.findReviews(
                        tenantId,
                        vendor.get().getId(),
                        status,
                        pageable
                );

        log.info("Total Elements : {}", page.getTotalElements());
        log.info("Total Pages    : {}", page.getTotalPages());
        log.info("Returned Count : {}", page.getContent().size());

        if (!page.getContent().isEmpty()) {
            log.info("First Object Class : {}", page.getContent().get(0).getClass().getName());
            log.info("First Object       : {}", page.getContent().get(0));
        }

        log.info("END :: ReviewService.getAllReviewForAdmin()");
        log.info("========================================================");

        return page;
    }

    public String updateReviewStatus(
            Map<String, ReviewStatus> action,
            CustomUserDetail userDetail,
            Long id) {

        log.info("========================================================");
        log.info("START :: ReviewService.updateReviewStatus()");

        String tenantId = TenantContext.getTenantId();
        log.info("Tenant Id : {}", tenantId);

        if (tenantId == null) {
            throw new RuntimeException("No tenant");
        }

        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenantId);

        if (vendor.isEmpty()) {
            throw new RuntimeException("Tenant Does Not Exist");
        }

        log.info("Vendor Id : {}", vendor.get().getId());

        if (userDetail == null) {
            throw new RuntimeException("Please Login First");
        }

        log.info("User Id : {}", userDetail.getId());
        log.info("Role    : {}", userDetail.getRole());

        if (userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorized");
        }

        ReviewStatus reviewStatus = action.get("action");

        log.info("Review Id      : {}", id);
        log.info("Requested Status : {}", reviewStatus);

        ProductReview productReview =
                productReviewRepository.findByIdAndTenantId(id, tenantId);

        if (productReview == null) {
            throw new RuntimeException("Review Does Not Exist");
        }

        log.info("Old Status : {}", productReview.getStatus());

        productReview.setStatus(reviewStatus);
        productReview.setUpdatedBy(userDetail.getId());

        productReviewRepository.save(productReview);

        log.info("New Status : {}", productReview.getStatus());
        log.info("Review Updated Successfully.");
        log.info("END :: ReviewService.updateReviewStatus()");
        log.info("========================================================");

        return reviewStatus + " Successfully";
    }

    public List<UserReviewResponseDTO> findVerifiedReviewed() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("No tenant");
        }
        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenantId);

        if (vendor.isEmpty()) {
            throw new RuntimeException("Tenant Does Not Exist");
        }
        List<ProductReview> pro = productReviewRepository.findTop10ByTenantIdAndStatusOrderByCreatedAtDesc(tenantId, ReviewStatus.APPROVE);
        List<Long> userIds = new ArrayList<>();

        for (ProductReview productReview : pro) {
            userIds.add(productReview.getUserId());
        }
        Map<Long, String> userNames = findUserNameWithUserIdInTenant(tenantId, userIds);
        log.error("All Username Wit id For Review {}", userNames);
        List<UserReviewResponseDTO> userReviewResponseDTOS = new ArrayList<>();
        for (ProductReview productReview : pro) {
            UserReviewResponseDTO userReviewResponseDTO = new UserReviewResponseDTO();
            userReviewResponseDTO.setReviewId(String.valueOf(productReview.getId()));
            userReviewResponseDTO.setCustomerName(userNames.get(productReview.getUserId()));
            userReviewResponseDTO.setComment(productReview.getReviewText());
            userReviewResponseDTO.setVerified(true);
            if (productReview.getRating() < 3) {
                userReviewResponseDTO.setRating(3);

            } else {
                userReviewResponseDTO.setRating(productReview.getRating());
            }
            userReviewResponseDTOS.add(userReviewResponseDTO);

        }
        return userReviewResponseDTOS;
    }

    public Map<Long, String> findUserNameWithUserIdInTenant(
            String tenantId,
            List<Long> userIds) {

        List<Object[]> rows = userRepository.findUserNames(tenantId, userIds);

        Map<Long, String> result = new HashMap<>();

        for (Object[] row : rows) {
            result.put((Long) row[0], (String) row[1]);
        }

        return result;
    }
}