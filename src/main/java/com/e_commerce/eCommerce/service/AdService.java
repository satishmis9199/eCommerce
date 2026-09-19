package com.e_commerce.eCommerce.service;
import com.e_commerce.eCommerce.dto.request.AdRequestDto;
import com.e_commerce.eCommerce.dto.response.AdResponseDto;
import com.e_commerce.eCommerce.dto.response.PublicAdDto;
import com.e_commerce.eCommerce.entity.Ad;
import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.enums.AdSlotKey;
import com.e_commerce.eCommerce.enums.AdStatus;
import com.e_commerce.eCommerce.enums.NotificationType;
import com.e_commerce.eCommerce.event.VendorNotificationEvent;
import com.e_commerce.eCommerce.repository.AdRepository;
import com.e_commerce.eCommerce.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final VendorRepository vendorRepository;
    private final NotificationService notificationService;

    private static final int DEFAULT_ACTIVE_DAYS = 30;

    public AdResponseDto createAdRequest(String tenantId, AdRequestDto dto) {

        Ad ad = Ad.builder()
                .tenantId(tenantId)
                .title(dto.getTitle())
                .imageUrl(dto.getImageUrl())
                .targetUrl(dto.getTargetUrl())
                .slotKey(dto.getSlotKey())
                .subtext(dto.getSubtext())
                .ctaLabel(dto.getCtaLabel())
                .advertiserName(dto.getAdvertiserName())
                .internal(dto.isInternal())
                .dismissible(dto.isDismissible())
                .priority(0)
                .impressions(0L)
                .clicks(0L)
                .status(AdStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return AdResponseDto.from(adRepository.save(ad));
    }

    public List<AdResponseDto> getVendorAds(String tenantId) {

        return adRepository.findByTenantIdOrderByCreatedAtDesc(tenantId)
                .stream()
                .map(AdResponseDto::from)
                .collect(Collectors.toList());
    }

    public List<AdResponseDto> getPendingAds() {

        return adRepository.findByStatusOrderByCreatedAtAsc(AdStatus.PENDING)
                .stream()
                .map(AdResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean approveAd(Long id, Integer activeDays, Integer priority) {

        Ad ad = adRepository.findById(id).orElse(null);

        if (ad == null) {
            return false;
        }

        String tenantId = ad.getTenantId();
        Long vendorId = getVendorId(tenantId);

        int days = (activeDays != null && activeDays > 0)
                ? activeDays
                : DEFAULT_ACTIVE_DAYS;

        LocalDateTime now = LocalDateTime.now();

        ad.setStatus(AdStatus.APPROVED);
        ad.setApprovedAt(now);
        ad.setExpiresAt(now.plusDays(days));
        ad.setRejectionReason(null);

        if (priority != null) {
            ad.setPriority(priority);
        }

        Ad approvedAd = adRepository.save(ad);

        String title = "Ad Request Approved";

        String message = "Your ad request (Ad #" + approvedAd.getId()
                + ") has been approved by the Super Admin "
                + "and is now active for " + days + " days.";

        notificationService.saveNotification(
                tenantId,
                vendorId,
                0L,
                NotificationType.APPROOVE,
                title,
                message
        );

        VendorNotificationEvent notificationEvent =
                new VendorNotificationEvent(
                        tenantId,
                        vendorId,
                        NotificationType.APPROOVE,
                        title,
                        message
                );

        eventPublisher.publishEvent(notificationEvent);

        return true;
    }

    public boolean approveAd(Long id, Integer activeDays) {
        return approveAd(id, activeDays, null);
    }

    @Transactional
    public boolean rejectAd(Long id, String reason) {

        Ad ad = adRepository.findById(id).orElse(null);

        if (ad == null) {
            return false;
        }

        String tenantId = ad.getTenantId();
        Long vendorId = getVendorId(tenantId);

        ad.setStatus(AdStatus.REJECTED);
        ad.setRejectionReason(reason);

        Ad rejectedAd = adRepository.save(ad);

        String title = "Ad Request Rejected";

        String message = "Your ad request (Ad #" + rejectedAd.getId()
                + ") has been rejected by the Super Admin.";

        if (reason != null && !reason.isBlank()) {
            message += " Reason: " + reason;
        }

        notificationService.saveNotification(
                tenantId,
                vendorId,
                0L,
                NotificationType.REJECTED,
                title,
                message
        );

        VendorNotificationEvent notificationEvent =
                new VendorNotificationEvent(
                        tenantId,
                        vendorId,
                        NotificationType.REJECTED,
                        title,
                        message
                );

        eventPublisher.publishEvent(notificationEvent);

        return true;
    }

    public List<AdResponseDto> getAdsBySlot(AdSlotKey slotKey) {

        return adRepository.findBySlotKeyOrderByPriorityDesc(slotKey)
                .stream()
                .map(AdResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PublicAdDto getRandomActiveAd() {

        List<Ad> pool = adRepository.findAllServable(LocalDateTime.now());

        if (pool.isEmpty()) {
            return null;
        }

        Ad picked = pool.get(
                ThreadLocalRandom.current().nextInt(pool.size())
        );

        return PublicAdDto.from(picked);
    }

    @Transactional(readOnly = true)
    public PublicAdDto getAdForSlot(AdSlotKey slotKey) {

        List<Ad> candidates = adRepository.findServableBySlot(
                slotKey,
                LocalDateTime.now(),
                Pageable.ofSize(1)
        );

        if (candidates.isEmpty()) {
            return null;
        }

        return PublicAdDto.from(candidates.get(0));
    }

    @Transactional
    public void recordImpression(Long adId) {

        if (adRepository.incrementImpressions(adId) == 0) {
            return;
        }
    }

    @Transactional
    public void recordClick(Long adId) {

        if (adRepository.incrementClicks(adId) == 0) {
            return;
        }
    }

    public Long getVendorId(String tenantId) {

        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenantId);

        return vendor.map(Vendor::getId).orElse(null);
    }
}
