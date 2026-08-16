package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.TenantContext;

import com.e_commerce.eCommerce.dto.BannerRequestDto;
import com.e_commerce.eCommerce.dto.BannerResponseDto;
import com.e_commerce.eCommerce.dto.CustomerDetailDTo;
import com.e_commerce.eCommerce.entity.Banner;
import com.e_commerce.eCommerce.entity.Roles;
import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.repository.BannerRepository;
import com.e_commerce.eCommerce.repository.VendorRepository;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BannerService {
    private final VendorRepository vendorRepository;
    private final BannerRepository bannerRepository;

    public String saveBanner(CustomUserDetail userDetail, BannerRequestDto bannerRequestDTO) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("No tenant");
        }
        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenantId);
        if (vendor.isEmpty()) {
            throw new RuntimeException("Tenant does Not Exists");
        }
        if (userDetail == null) {
            throw new RuntimeException("Please Login First");
        }
        if (userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException("You do not have Sufficient previlieges to Save Banner");
        }
        Banner banner = Banner.builder()
                .tenantId(tenantId)
                .vendorId(vendor.get().getId())
                .endsAt(bannerRequestDTO.getEndDate())
                .eyebrow(bannerRequestDTO.getEyebrow())
                .title(bannerRequestDTO.getTitle())
                .subtitle(bannerRequestDTO.getSubtitle())
                .ctaLabel(bannerRequestDTO.getCtaLabel())
                .displayOrder(bannerRequestDTO.getDisplayOrder())
                .ctaLink(bannerRequestDTO.getCtaLink())
                .ctaLinkType(bannerRequestDTO.getCtaLinkType())
                .imageUrl(bannerRequestDTO.getImageUrl())
                .active(bannerRequestDTO.getActive())
                .startsAt(bannerRequestDTO.getStartDate())
                .build();
        bannerRepository.save(banner);
        return "Banner Added SuccessFully";
    }

    public List<BannerResponseDto> loadBbanner(CustomUserDetail customUserDetail) {
        String tenantId = TenantContext.getTenantId();
        List<BannerResponseDto> bannerResponseDtos = new ArrayList<>();
        List<Banner> banners = bannerRepository.findAllByTenantId(tenantId);
        for (Banner banner : banners) {
            BannerResponseDto bannerResponseDto = BannerResponseDto.builder()
                    .id(banner.getId())
                    .eyebrow(banner.getEyebrow())
                    .title(banner.getTitle())

                    .imageUrl(banner.getImageUrl())


                    .active(banner.getActive())
                    .status(getBannerStatus(banner))
                    .startDate(banner.getStartsAt())
                    .endDate(banner.getEndsAt())
                    .build();
            bannerResponseDtos.add(bannerResponseDto);
        }
        return bannerResponseDtos;
    }

    private String getBannerStatus(Banner banner) {

        LocalDateTime now = LocalDateTime.now();

        // Admin ne manually inactive kiya
        if (!Boolean.TRUE.equals(banner.getActive())) {
            return "INACTIVE";
        }

        // Future me start hoga
        if (banner.getStartsAt() != null &&
                now.isBefore(banner.getStartsAt())) {
            return "SCHEDULED";
        }

        // End ho chuka hai
        if (banner.getEndsAt() != null &&
                now.isAfter(banner.getEndsAt())) {
            return "EXPIRED";
        }

        // Active period ke andar hai
        return "ACTIVE";
    }

    public String updateBanner(
            CustomUserDetail userDetail,
            BannerRequestDto bannerRequestDTO,
            Long id) {

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            throw new RuntimeException("No tenant");
        }

        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenantId);

        if (vendor.isEmpty()) {
            throw new RuntimeException("Tenant does Not Exists");
        }

        if (userDetail == null) {
            throw new RuntimeException("Please Login First");
        }

        if (userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException(
                    "You do not have Sufficient privileges to Update Banner"
            );
        }

        Banner banner = bannerRepository.findByIdAndTenantId(id, tenantId);

        if (banner == null) {
            throw new RuntimeException("Banner Does not Exist");
        }

        // Update only fields which are provided
        if (bannerRequestDTO.getEyebrow() != null) {
            banner.setEyebrow(bannerRequestDTO.getEyebrow());
        }

        if (bannerRequestDTO.getTitle() != null) {
            banner.setTitle(bannerRequestDTO.getTitle());
        }

        if (bannerRequestDTO.getSubtitle() != null) {
            banner.setSubtitle(bannerRequestDTO.getSubtitle());
        }

        if (bannerRequestDTO.getCtaLabel() != null) {
            banner.setCtaLabel(bannerRequestDTO.getCtaLabel());
        }

        if (bannerRequestDTO.getCtaLink() != null) {
            banner.setCtaLink(bannerRequestDTO.getCtaLink());
        }

        if (bannerRequestDTO.getCtaLinkType() != null) {
            banner.setCtaLinkType(bannerRequestDTO.getCtaLinkType());
        }

        if (bannerRequestDTO.getImageUrl() != null) {
            banner.setImageUrl(bannerRequestDTO.getImageUrl());
        }

        if (bannerRequestDTO.getDisplayOrder() != null) {
            banner.setDisplayOrder(bannerRequestDTO.getDisplayOrder());
        }

        if (bannerRequestDTO.getActive() != null) {
            banner.setActive(bannerRequestDTO.getActive());
        }

        if (bannerRequestDTO.getStartDate() != null) {
            banner.setStartsAt(bannerRequestDTO.getStartDate());
        }

        if (bannerRequestDTO.getEndDate() != null) {
            banner.setEndsAt(bannerRequestDTO.getEndDate());
        }

        // Keep tenant/vendor unchanged
        banner.setTenantId(tenantId);
        banner.setVendorId(vendor.get().getId());

        bannerRepository.save(banner);

        return "Banner Updated Successfully";
    }
    public String changeBannerStatus(
            CustomUserDetail userDetail,
            Long id,
            Boolean active) {

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            throw new RuntimeException("No tenant");
        }

        if (userDetail == null) {
            throw new RuntimeException("Please Login First");
        }

        if (userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException(
                    "You do not have sufficient privileges to update banner"
            );
        }

        Banner banner = bannerRepository.findByIdAndTenantId(id, tenantId);

        if (banner == null) {
            throw new RuntimeException("Banner Does not Exist");
        }

        if (active == null) {
            throw new RuntimeException("Active status is required");
        }

        banner.setActive(active);

        bannerRepository.save(banner);

        return "Banner status updated successfully";
    }
}