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

        String tenantId = TenantContext.getTenantId();
        log.info("Tenant Id : {}", tenantId);

        if (tenantId == null) {
            throw new RuntimeException("No tenant");
        }

        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenantId);

        if (vendor.isEmpty()) {
            throw new RuntimeException("Tenant Does Not Exist");
        }


        if (userDetail == null) {
            throw new RuntimeException("Please Login First");
        }

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



        if (!page.getContent().isEmpty()) {
            log.info("First Object Class : {}", page.getContent().get(0).getClass().getName());

        }

        return page;
    }

    public String updateReviewStatus(
            Map<String, ReviewStatus> action,
            CustomUserDetail userDetail,
            Long id) {


        String tenantId = TenantContext.getTenantId();


        if (tenantId == null) {
            throw new RuntimeException("No tenant");
        }

        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenantId);

        if (vendor.isEmpty()) {
            throw new RuntimeException("Tenant Does Not Exist");
        }


        if (userDetail == null) {
            throw new RuntimeException("Please Login First");
        }

        if (userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorized");
        }

        ReviewStatus reviewStatus = action.get("action");


        ProductReview productReview =
                productReviewRepository.findByIdAndTenantId(id, tenantId);

        if (productReview == null) {
            throw new RuntimeException("Review Does Not Exist");
        }


        productReview.setStatus(reviewStatus);
        productReview.setUpdatedBy(userDetail.getId());

        productReviewRepository.save(productReview);

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