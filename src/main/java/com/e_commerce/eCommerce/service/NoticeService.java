package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.NoticeListDto;
import com.e_commerce.eCommerce.dto.NoticeResponseDto;
import com.e_commerce.eCommerce.dto.request.NoticeRequestDto;
import com.e_commerce.eCommerce.entity.Notice;
import com.e_commerce.eCommerce.entity.Roles;
import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.enums.NoticeStatus;
import com.e_commerce.eCommerce.mapper.NoticeMapper;
import com.e_commerce.eCommerce.repository.NoticeRepository;
import com.e_commerce.eCommerce.repository.VendorRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.AccessType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor

public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final GlobalService globalService;
    private final VendorRepository vendorRepository;

    public String addNotices(NoticeRequestDto noticeRequestDto, CustomUserDetail userDetail) {
        String tenantId= TenantContext.getTenantId();
       Long vendorid= globalService.validateVendors(tenantId,userDetail.getId());
        if(userDetail.getRole()!= Roles.ADMIN){
            throw new RuntimeException("Unauthorized Access");
        }
        Notice notice=Notice.builder()
                .tenantId(tenantId)
                .vendorId(vendorid)
                .noticeText(noticeRequestDto.getNoticeText())
                .title(noticeRequestDto.getTitle())
                .status(noticeRequestDto.getStatus())
                .displayLocation(noticeRequestDto.getDisplayLocation())
                .startAt(noticeRequestDto.getStartAt())
                .type(noticeRequestDto.getType())
                .endAt(noticeRequestDto.getEndAt())
                .priority(noticeRequestDto.getPriority())
                .createdBy(userDetail.getId())

                .build();
        noticeRepository.save(notice);
        return noticeRequestDto.getTitle()+"  Added Successfuly";
    }

    public List<NoticeListDto> findAllNotices(CustomUserDetail userDetail) {

        String tenantId = TenantContext.getTenantId();

        Long v1id=globalService.validateVendors(tenantId, userDetail.getId());

        List<Notice> notices = noticeRepository.findByTenantId(tenantId);
       List<NoticeListDto> noticeListDto=new ArrayList<>();

        return notices.stream()
                .map(notice -> new NoticeListDto(
                        notice.getId(),
                        notice.getTitle(),
                        notice.getNoticeText(),
                        notice.getDisplayLocation(),
                        notice.getType(),
                        notice.getPriority(),
                        notice.getStartAt(),
                        notice.getEndAt(),
                        notice.getStatus()
                ))
                .toList();
    }

    public List<NoticeResponseDto> getActiveNotices() {
        String tenantId = TenantContext.getTenantId();
        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenantId);
        if (vendor.isEmpty()) {
            throw new RuntimeException("Vendor does not exist");
        }

        Long vendorId = vendor.get().getId();

        List<Notice> notices = noticeRepository.findActiveNotices(
                tenantId,
                vendorId,
                NoticeStatus.ACTIVE,
                LocalDateTime.now()
        );

        return notices.stream()
                .map(NoticeMapper::toDto)
                .toList();
    }

    public String updateNotice(
            Long noticeId,
            NoticeRequestDto dto,
            CustomUserDetail userDetail) {


        String tenantId = TenantContext.getTenantId();

        Optional<Notice> notice1 = noticeRepository
                .findByIdAndTenantId(noticeId, tenantId);
        if(notice1==null){
            throw  new RuntimeException("Notice not found");
        }
        Notice notice=notice1.get();

        notice.setTitle(dto.getTitle());
        notice.setNoticeText(dto.getNoticeText());
        notice.setDisplayLocation(dto.getDisplayLocation());
        notice.setType(dto.getType());
        notice.setPriority(dto.getPriority());
        notice.setPopupDurationSeconds(dto.getPopupDurationSeconds());
        notice.setStartAt(dto.getStartAt());
        notice.setEndAt(dto.getEndAt());
        notice.setStatus(dto.getStatus());

        noticeRepository.save(notice);

        return "Notice updated successfully";
    }
    @Transactional
    public String deleteNotice(
            Long noticeId,
            CustomUserDetail userDetail) {

        String tenantId = TenantContext.getTenantId();

        Notice notice = noticeRepository
                .findByIdAndTenantId(noticeId, tenantId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Notice not found with id: " + noticeId
                        )
                );

        noticeRepository.delete(notice);

        return "Notice deleted successfully";
    }
    @Transactional
    public String changeNoticeStatus(
            Long noticeId,
            NoticeStatus status,
            CustomUserDetail userDetail) {

        String tenantId = TenantContext.getTenantId();

        Notice notice = noticeRepository
                .findByIdAndTenantId(noticeId, tenantId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Notice not found with id: " + noticeId
                        )
                );

        if (!"ACTIVE".equalsIgnoreCase(String.valueOf(status))
                && !"INACTIVE".equalsIgnoreCase(String.valueOf(status))) {

            throw new RuntimeException(
                    "Invalid notice status: " + status
            );
        }

        notice.setStatus(status);

        noticeRepository.save(notice);

        return "Notice status changed to " + notice.getStatus();
    }
}
