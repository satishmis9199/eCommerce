package com.e_commerce.eCommerce.service;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.request.FestivalBannerRequestDto;
import com.e_commerce.eCommerce.dto.response.FestivalBannerResponseDto;
import com.e_commerce.eCommerce.entity.FestivalBanner;
import com.e_commerce.eCommerce.repository.FestivalBannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class FestivalBannerService {

    private final FestivalBannerRepository festivalBannerRepository;
    @Transactional
    public String saveFestivalBanner(
            CustomUserDetail userDetail,
            FestivalBannerRequestDto request
    ) {

        validateUser(userDetail);
        validateRequest(request);

        String tenantId = getTenantId();

        validateDates(
                request.getStartAt(),
                request.getEndAt()
        );
        if (Boolean.TRUE.equals(request.getActive())) {

            boolean activeExists =
                    festivalBannerRepository
                            .existsByTenantIdAndActiveTrue(tenantId);

            if (activeExists) {
                throw new IllegalStateException(
                        "Is tenant ke liye pehle se ek active festival banner hai."
                );
            }
        }

        LocalDateTime now = LocalDateTime.now();

        FestivalBanner banner = new FestivalBanner();
        banner.setTenantId(tenantId);
        banner.setTitle(request.getTitle());
        banner.setMessage(request.getMessage());
        banner.setBannerImage(request.getBannerImage());
        banner.setStartAt(request.getStartAt());
        banner.setEndAt(request.getEndAt());
        banner.setActive(
                Boolean.TRUE.equals(request.getActive())
        );
        banner.setCreatedAt(now);
        banner.setCreatedBy(getUsername(userDetail));

        banner.setUpdatedAt(now);
        banner.setUpdatedBy(getUsername(userDetail));

        festivalBannerRepository.save(banner);

        return "Festival banner successfully saved.";
    }
    @Transactional(readOnly = true)
    public List<FestivalBannerResponseDto> loadFestivalBanner(
            CustomUserDetail userDetail
    ) {

        validateUser(userDetail);

        String tenantId = getTenantId();

        List<FestivalBanner> banners =
                festivalBannerRepository
                        .findAllByTenantIdOrderByCreatedAtDesc(tenantId);

        return banners.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    @Transactional
    public String updateFestivalBanner(
            CustomUserDetail userDetail,
            FestivalBannerRequestDto request,
            Long id
    ) {

        validateUser(userDetail);
        validateRequest(request);

        if (id == null) {
            throw new IllegalArgumentException(
                    "Festival banner id is required."
            );
        }

        String tenantId = getTenantId();

        FestivalBanner banner =
                festivalBannerRepository
                        .findByIdAndTenantId(id, tenantId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Festival banner not found."
                                )
                        );

        validateDates(
                request.getStartAt(),
                request.getEndAt()
        );
        if (Boolean.TRUE.equals(request.getActive())
                && !banner.isActive()) {

            boolean activeExists =
                    festivalBannerRepository
                            .existsByTenantIdAndActiveTrue(tenantId);

            if (activeExists) {
                throw new IllegalStateException(
                        "Is tenant ke liye pehle se ek active festival banner hai."
                );
            }
        }
        banner.setTenantId(tenantId);

        banner.setTitle(request.getTitle());
        banner.setMessage(request.getMessage());
        banner.setBannerImage(request.getBannerImage());
        banner.setStartAt(request.getStartAt());
        banner.setEndAt(request.getEndAt());

        banner.setActive(
                Boolean.TRUE.equals(request.getActive())
        );

        banner.setUpdatedAt(LocalDateTime.now());
        banner.setUpdatedBy(getUsername(userDetail));

        festivalBannerRepository.save(banner);

        return "Festival banner successfully updated.";
    }
    @Transactional
    public String changeFestivalBannerStatus(
            CustomUserDetail userDetail,
            Long id,
            Boolean active
    ) {

        validateUser(userDetail);

        if (id == null) {
            throw new IllegalArgumentException(
                    "Festival banner id is required."
            );
        }

        if (active == null) {
            throw new IllegalArgumentException(
                    "Active status is required."
            );
        }

        String tenantId = getTenantId();

        FestivalBanner banner =
                festivalBannerRepository
                        .findByIdAndTenantId(id, tenantId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Festival banner not found."
                                )
                        );
        if (banner.isActive() == active) {
            return active
                    ? "Festival banner is already active."
                    : "Festival banner is already inactive.";
        }

        if (active) {

            boolean activeExists =
                    festivalBannerRepository
                            .existsByTenantIdAndActiveTrue(tenantId);

            if (activeExists) {
                throw new IllegalStateException(
                        "Is tenant ke liye pehle se ek active festival banner hai. "
                                + "Pehle current active banner ko deactivate karein."
                );
            }
        }

        banner.setActive(active);
        banner.setUpdatedAt(LocalDateTime.now());
        banner.setUpdatedBy(getUsername(userDetail));

        festivalBannerRepository.save(banner);

        return active
                ? "Festival banner activated successfully."
                : "Festival banner deactivated successfully.";
    }
    @Transactional(readOnly = true)
    public FestivalBannerResponseDto getActiveFestivalBanner(
            String tenantId
    ) {

        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException(
                    "Tenant id is required."
            );
        }

        LocalDateTime now = LocalDateTime.now();

        return festivalBannerRepository
                .findActiveBannerForNow(tenantId, now)
                .map(this::convertToResponse)
                .orElse(null);
    }
    @Transactional(readOnly = true)
    public FestivalBannerResponseDto getFestivalBannerById(
            CustomUserDetail userDetail,
            Long id
    ) {

        validateUser(userDetail);

        if (id == null) {
            throw new IllegalArgumentException(
                    "Festival banner id is required."
            );
        }

        String tenantId = getTenantId();

        FestivalBanner banner =
                festivalBannerRepository
                        .findByIdAndTenantId(id, tenantId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Festival banner not found."
                                )
                        );

        return convertToResponse(banner);
    }

    @Transactional
    public String deleteFestivalBanner(
            CustomUserDetail userDetail,
            Long id
    ) {

        validateUser(userDetail);

        if (id == null) {
            throw new IllegalArgumentException(
                    "Festival banner id is required."
            );
        }

        String tenantId = getTenantId();

        FestivalBanner banner =
                festivalBannerRepository
                        .findByIdAndTenantId(id, tenantId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Festival banner not found."
                                )
                        );

        festivalBannerRepository.delete(banner);

        return "Festival banner deleted successfully.";
    }
    private void validateRequest(
            FestivalBannerRequestDto request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Festival banner request is required."
            );
        }

        if (request.getTitle() == null
                || request.getTitle().isBlank()) {

            throw new IllegalArgumentException(
                    "Festival title is required."
            );
        }

        if (request.getTitle().length() > 150) {
            throw new IllegalArgumentException(
                    "Festival title must not exceed 150 characters."
            );
        }

        if (request.getMessage() != null
                && request.getMessage().length() > 500) {

            throw new IllegalArgumentException(
                    "Festival message must not exceed 500 characters."
            );
        }

        if (request.getBannerImage() != null
                && request.getBannerImage().length() > 500) {

            throw new IllegalArgumentException(
                    "Banner image URL must not exceed 500 characters."
            );
        }
    }

    private void validateDates(
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {

        if (startAt != null
                && endAt != null
                && !startAt.isBefore(endAt)) {

            throw new IllegalArgumentException(
                    "Start date must be before end date."
            );
        }
    }
    private void validateUser(
            CustomUserDetail userDetail
    ) {

        if (userDetail == null) {
            throw new IllegalStateException(
                    "Please login again."
            );
        }

        if (userDetail.getRole() == null) {
            throw new IllegalStateException(
                    "User role not found."
            );
        }
    }

    private String getTenantId() {

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException(
                    "Tenant information not found."
            );
        }

        return tenantId;
    }

    private String getUsername(
            CustomUserDetail userDetail
    ) {

        String username = userDetail.getUsername();

        if (username == null || username.isBlank()) {
            return "SYSTEM";
        }

        return username;
    }
    private FestivalBannerResponseDto convertToResponse(
            FestivalBanner banner
    ) {

        FestivalBannerResponseDto response =
                new FestivalBannerResponseDto();

        response.setId(banner.getId());
        response.setTitle(banner.getTitle());
        response.setMessage(banner.getMessage());
        response.setBannerImage(banner.getBannerImage());

        response.setStartAt(banner.getStartAt());
        response.setEndAt(banner.getEndAt());

        response.setActive(banner.isActive());

        response.setCreatedAt(banner.getCreatedAt());
        response.setCreatedBy(banner.getCreatedBy());

        response.setUpdatedAt(banner.getUpdatedAt());
        response.setUpdatedBy(banner.getUpdatedBy());

        return response;
    }
}
