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
        String tenantId= TenantContext.getTenantId();
        if(tenantId==null){
            throw new RuntimeException("No tenant");
        }
        Optional<Vendor> vendor=vendorRepository.findByTenantId(tenantId);
        if(vendor.isEmpty()){
            throw new RuntimeException("Tenant does Not Exists");
        }
        if(userDetail==null){
            throw new RuntimeException("Please Login First");
        }
        if(userDetail.getRole()!= Roles.ADMIN){
            throw new RuntimeException("You do not have Sufficient previlieges to Save Banner");
        }
        Banner banner=Banner.builder()
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
        String tenantId=TenantContext.getTenantId();
        List<BannerResponseDto> bannerResponseDtos=new ArrayList<>();
        List<Banner> banners=bannerRepository.findAllByTenantId(tenantId);
        for(Banner banner: banners){
            BannerResponseDto bannerResponseDto=BannerResponseDto.builder()
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
}
