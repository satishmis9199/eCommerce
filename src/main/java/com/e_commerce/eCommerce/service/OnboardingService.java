package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.dto.request.EmailRequestDto;
import com.e_commerce.eCommerce.dto.response.OnboardHistoryDTO;
import com.e_commerce.eCommerce.entity.*;
import com.e_commerce.eCommerce.exception.VendorRequestException;
import com.e_commerce.eCommerce.exception.vendorNotFoundException;
import com.e_commerce.eCommerce.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingService {
    private static final Logger logger =
            LoggerFactory.getLogger(OnboardingService.class);

    private final VendorOnnBRepo vendorOnnBRepo;
    private final EmailService emailService;
    private final VendorOnboardingHistoryRepository vendorOnbaordingRepository;
    private final VendorRepository vendorRepository;
    private final vendorBussinesss vendorBusinessRepository;
    private final VendorAddresss vendorAddressRepository;
    private final VendorBankRepository vendorBankRepository;
    private final VendorBrandingRepository vendorBrandingRepository;
    private final VendorAddresss vendorAddressRepo;
    private final OnboardingHistoryService onboardingHistoryService;

    public VendorOnboardingResponseDTO getOnboarding(Long vendorId) {
        VendorOnboardingResponseDTO response = new VendorOnboardingResponseDTO();
        if (vendorId == null) {
            response.setSuccess(false);
            return response;
        }

        Optional<VendorOnboardingApplication> optionalApplication =
                vendorOnnBRepo.findByVendorId(vendorId);

        if (optionalApplication.isEmpty()) {
            response.setSuccess(false);
            response.setData(buildEmptyData());
            return response;
        }

        VendorOnboardingApplication application = optionalApplication.get();
        int percent = application.getCompletionPercentage();
        response.setSuccess(true);


        response.setCurrentStep(application.getCurrentStep());
        response.setProfileCompleted(
                application.getCompletionPercentage() != null
                        && application.getCompletionPercentage() == 100
        );
        response.setApplicationId(application.getApplicationId());

        VendorOnboardingDataDTO data = new VendorOnboardingDataDTO();

        data.setBasic(getBasicInfo(vendorId));
        data.setBusiness(getBusinessDetails(vendorId));
        data.setAddress(getBusinessAddress(vendorId));
        data.setBank(getBankDetails(vendorId));
        data.setBranding(getBrandingDetails(vendorId));

        response.setData(data);

        log.info("Onboarding details loaded successfully for vendor : {}", vendorId);

        return response;
    }

    private VendorOnboardingDataDTO buildEmptyData() {
        VendorOnboardingDataDTO data = new VendorOnboardingDataDTO();
        data.setBasic(new BasicInfoDto());
        data.setBusiness(new BusinessDetailsDTO());
        data.setAddress(new BusinessAddressDTO());
        data.setBank(new BankInfoDto());
        data.setBranding(new BrandingDTO());
        return data;
    }
    private BasicInfoDto getBasicInfo(Long vendorId) {

        BasicInfoDto dto = new BasicInfoDto();

        if (vendorId == null) return dto;

        Optional<Vendor> optionalVendor = vendorRepository.findById(vendorId);

        if (optionalVendor.isEmpty()) {
            log.warn("Vendor basic info not found for vendorId : {}", vendorId);
            return dto;
        }

        Vendor vendor = optionalVendor.get();

        dto.setFirstName(vendor.getFirstName());
        dto.setLastName(vendor.getLastName());
        dto.setBusinessName(vendor.getBussinessName());
        dto.setStoreName(vendor.getStoreName());
        dto.setBusinessEmail(vendor.getVendorEmail());
        dto.setMobile(vendor.getMobile());

        return dto;
    }

    private BusinessDetailsDTO getBusinessDetails(Long vendorId) {

        BusinessDetailsDTO dto = new BusinessDetailsDTO();

        if (vendorId == null) return dto;

        VendorBusiness business;
        try {
            business = vendorBusinessRepository.findByVendorId(vendorId);
        } catch (Exception e) {
            log.error("Error fetching business details for vendorId {} : {}", vendorId, e.getMessage());
            return dto;
        }

        if (business == null) {
            log.warn("Business details not found for vendorId : {}", vendorId);
            return dto;
        }

        if (business.getBusinessType() != null) {
            dto.setBusinessType(business.getBusinessType().name());
        }

        dto.setCategory(business.getBusinessCategory());
        dto.setDescription(business.getBusinessDescription());
        dto.setGstNumber(business.getGstNumber());
        dto.setPanNumber(business.getPanNumber());

        return dto;
    }

    private BusinessAddressDTO getBusinessAddress(Long vendorId) {

        BusinessAddressDTO dto = new BusinessAddressDTO();

        if (vendorId == null) return dto;

        VendorAddress address;
        try {
            address = vendorAddressRepository.findByVendorId(vendorId);
        } catch (Exception e) {
            log.error("Error fetching address for vendorId {} : {}", vendorId, e.getMessage());
            return dto;
        }

        if (address == null) {
            log.warn("Address not found for vendorId : {}", vendorId);
            return dto;
        }

        dto.setAddressLine1(address.getAddressLine1());
        dto.setAddressLine2(address.getAddressLine2());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setCountry(address.getCountry());
        dto.setPincode(address.getPostalCode());

        return dto;
    }

    private BankInfoDto getBankDetails(Long vendorId) {

        BankInfoDto dto = new BankInfoDto();

        if (vendorId == null) return dto;

        VendorBank bank;
        try {
            bank = vendorBankRepository.findByVendorId(vendorId);
        } catch (Exception e) {
            log.error("Error fetching bank details for vendorId {} : {}", vendorId, e.getMessage());
            return dto;
        }

        if (bank == null) {
            log.warn("Bank details not found for vendorId : {}", vendorId);
            return dto;
        }

        dto.setAccountHolderName(bank.getAccountHolderName());
        dto.setBankName(bank.getBankName());
        dto.setAccountNumber(bank.getAccountNumber());
        dto.setIfscCode(bank.getIfscCode());
        dto.setBranchName(bank.getBranchName());

        return dto;
    }

    private BrandingDTO getBrandingDetails(Long vendorId) {

        BrandingDTO dto = new BrandingDTO();

        if (vendorId == null) return dto;

        VendorBranding branding;
        try {
            branding = vendorBrandingRepository.findByVendorId(vendorId);
        } catch (Exception e) {
            log.error("Error fetching branding details for vendorId {} : {}", vendorId, e.getMessage());
            return dto;
        }

        if (branding == null) {
            log.warn("Branding details not found for vendorId : {}", vendorId);
            return dto;
        }

        dto.setLogoUrl(branding.getLogoUrl());
        dto.setBannerUrl(branding.getBannerUrl());
        dto.setPrimaryColor(branding.getPrimaryColor());
        dto.setTagline(branding.getStoreTagline());
        dto.setDescription(branding.getStoreDescription());

        return dto;
    }


    @Caching(evict = {
            @CacheEvict(value = "vendorDetail", allEntries = true),
            @CacheEvict(value = "AllVendors", allEntries = true)
    })
    public String saveBasicDetail(Long vendorId, BasicInfoDto dto) {


        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));
        VendorOnboardingApplication vendorOnboardingApplication = vendorOnnBRepo.findByVendorId(vendorId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));


        vendor.setFirstName(dto.getFirstName());
        vendor.setLastName(dto.getLastName());
        vendor.setBussinessName(dto.getBusinessName());
        vendor.setStoreName(dto.getStoreName());
        vendor.setVendorEmail(dto.getBusinessEmail());
        vendor.setMobile(dto.getMobile());
        vendorOnboardingApplication.setCurrentStep(1);
        vendorOnboardingApplication.setCompletionPercentage(20);

        vendorRepository.save(vendor);

        return "Basic information saved successfully.";
    }

    @Transactional
    public String saveBussinessDetail(Long vendorId, BusinessDetailsDTO dto) {

        // Vendor
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));

        // Onboarding Application
        VendorOnboardingApplication onboarding =
                vendorOnnBRepo.findByVendorId(vendorId)
                        .orElseThrow(() ->
                                new RuntimeException("Onboarding application not found"));

        // Vendor Business
        VendorBusiness vendorBusiness =
                vendorBusinessRepository.findByVendorId(vendorId);

        // First time create
        if (vendorBusiness == null) {

            vendorBusiness = new VendorBusiness();

            vendorBusiness.setVendor(vendor);

            vendorBusiness.setTenant_Id(vendor.getTenantId());
        }

        try {

            logger.info("Business Type : {}", dto.getBusinessType());

            vendorBusiness.setBusinessType(
                    BusinessType.valueOf(dto.getBusinessType().trim().toUpperCase())
            );

        } catch (IllegalArgumentException e) {

            throw new RuntimeException("Invalid Business Type : " + dto.getBusinessType());

        }

        vendorBusiness.setBusinessCategory(dto.getCategory());
        vendorBusiness.setBusinessDescription(dto.getDescription());
        vendorBusiness.setPanNumber(dto.getPanNumber());
        vendorBusiness.setGstNumber(dto.getGstNumber());

        vendorBusinessRepository.save(vendorBusiness);

        // Update onboarding progress
        onboarding.setCurrentStep(2);
        onboarding.setCompletionPercentage(
                Math.max(onboarding.getCompletionPercentage(), 40)
        );

        vendorOnnBRepo.save(onboarding);

        return "Business details saved successfully.";
    }


    public String saveBussienssddress(Long vendorId, BusinessAddressDTO dto) {
        // Vendor
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));

        // Onboarding Application
        VendorOnboardingApplication onboarding =
                vendorOnnBRepo.findByVendorId(vendorId)
                        .orElseThrow(() ->
                                new RuntimeException("Onboarding application not found"));
        VendorAddress vendorAddress = vendorAddressRepo.findByVendorId(vendorId);
        if (dto.getAddressLine1() == null || dto.getAddressLine1().isBlank()) {
            throw new RuntimeException("Address Line 1 is required");
        }

        if (dto.getCity() == null || dto.getCity().isBlank()) {
            throw new RuntimeException("City is required");
        }

        if (dto.getState() == null || dto.getState().isBlank()) {
            throw new RuntimeException("State is required");
        }

        if (dto.getCountry() == null || dto.getCountry().isBlank()) {
            throw new RuntimeException("Country is required");
        }

        if (dto.getPincode() == null || dto.getPincode().isBlank()) {
            throw new RuntimeException("Pincode is required");
        }
        if (vendorAddress == null) {
            vendorAddress = new VendorAddress();
            vendorAddress.setVendor(vendor);
        }
        vendorAddress.setAddressLine1(dto.getAddressLine1());
        vendorAddress.setAddressLine2(dto.getAddressLine2());
        vendorAddress.setCity(dto.getCity());
        vendorAddress.setState(dto.getState());
        vendorAddress.setCountry(dto.getCountry());
        vendorAddress.setPostalCode(dto.getPincode());
        vendorAddress.setAddressType(AddressType.WAREHOUSE);
        vendorAddress.setDefaultAddress(true);

        onboarding.setCurrentStep(Math.max(onboarding.getCurrentStep(), 3));
        onboarding.setCompletionPercentage(Math.max(onboarding.getCompletionPercentage(), 60));
        vendorAddressRepo.save(vendorAddress);
        vendorOnnBRepo.save(onboarding);
        return "Adress Details saved Successfully";

    }

    public String saveBankDetail(Long vendorid, BankInfoDto dto) {

        Vendor vendor = vendorRepository.findById(vendorid)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));

        VendorOnboardingApplication onboarding =
                vendorOnnBRepo.findByVendorId(vendorid)
                        .orElseThrow(() ->
                                new RuntimeException("Onboarding application not found"));
        VendorBank vendorBank = vendorBankRepository.findByVendorId(vendorid);
        if (vendorBank == null) {
            vendorBank = new VendorBank();
            vendorBank.setVendor(vendor);

        }
        vendorBank.setBankName(dto.getBankName());
        vendorBank.setAccountHolderName(dto.getAccountHolderName());
        vendorBank.setBranchName(dto.getBranchName());
        vendorBank.setIfscCode(dto.getIfscCode());
        vendorBank.setAccountNumber(dto.getAccountNumber());
        onboarding.setCompletionPercentage(Math.max(onboarding.getCompletionPercentage(), 80));
        onboarding.setCurrentStep(Math.max(onboarding.getCurrentStep(), 4));
        vendorBankRepository.save(vendorBank);
        return "Bank Detail saved";
    }


    public String saveBrandDetail(Long vendorid, BrandingInfoDto dto) {
        // Vendor
        Vendor vendor = vendorRepository.findById(vendorid)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));

        VendorOnboardingApplication onboarding =
                vendorOnnBRepo.findByVendorId(vendorid)
                        .orElseThrow(() ->
                                new RuntimeException("Onboarding application not found"));
        VendorBranding vendorBranding = vendorBrandingRepository.findByVendorId(vendorid);
        if (vendorBranding == null) {
            vendorBranding = new VendorBranding();
            vendorBranding.setVendor(vendor);

        }
        vendorBranding.setLogoUrl(dto.getLogoUrl());
        vendorBranding.setBannerUrl(dto.getBannerUrl());
        vendorBranding.setPrimaryColor(dto.getPrimaryColor());
        vendorBranding.setStoreDescription(dto.getDescription());
        vendorBranding.setStoreTagline(dto.getTagline());
        vendorBranding.setSupportPhone(vendor.getEmail());
        vendorBranding.setSupportEmail(vendor.getMobile());
        onboarding.setCompletionPercentage(90);
        onboarding.setCurrentStep(Math.max(onboarding.getCurrentStep(), 5));
        vendorBrandingRepository.save(vendorBranding);
        vendorOnnBRepo.save(onboarding);

        return "Branding Info Saved";
    }

    @Caching(evict = {
            @CacheEvict(value = "vendorDetail", allEntries = true),
            @CacheEvict(value = "AllVendors", allEntries = true)
    })
    @Transactional
    public SubmitApplicationResponseDTO submitApplication(Long vendorId) {

        String tenantId = TenantContext.getTenantId();

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new vendorNotFoundException("Vendor not found"));

        VendorOnboardingApplication onboarding = vendorOnnBRepo
                .findByVendorId(vendorId)
                .orElseThrow(() -> new vendorNotFoundException("Onboarding application not found"));

        OnboardingStatus prevStatus = onboarding.getStatus();
        OnboardingStatus nextStatus = OnboardingStatus.UNDER_REVIEW;

        onboarding.setSubmittedAt(LocalDateTime.now());
        onboarding.setStatus(nextStatus);
        onboarding.setCurrentStep(6);
        onboarding.setCompletionPercentage(100);
        onboarding.setCompleted(true);

        vendor.setStatus(VendorStatus.PENDING);
        vendorOnnBRepo.save(onboarding);
        vendorRepository.save(vendor);
        String remarks = String.format(
                "Vendor submitted the onboarding application. Status changed from %s to %s.",
                prevStatus,
                nextStatus
        );

        onboardingHistoryService.saveVendorOnBoardingHistory(
                tenantId,
                vendorId,
                remarks,
                prevStatus,
                nextStatus
        );

        SubmitApplicationResponseDTO response = new SubmitApplicationResponseDTO();
        response.setSuccess(true);
        response.setMessage("Your onboarding application has been submitted successfully.");
        response.setApplicationId(onboarding.getApplicationId());
        response.setStatus(onboarding.getStatus());
        response.setSubmittedAt(onboarding.getSubmittedAt());

        EmailRequestDto emailRequest = EmailRequestDto.builder()
                .to(vendor.getEmail())
                .subject("Onboarding Application Submitted – Kumar Store Online")
                .templateName("vendor-application-submitted")
                .templateVariables(Map.of(
                        "vendorName", vendor.getFirstName(),
                        "vendorEmail", vendor.getEmail(),
                        "shopName", vendor.getBussinessName(),
                        "applicationId", onboarding.getApplicationId(),
                        "status", onboarding.getStatus().name(),
                        "supportEmail", "support@kumarstore.online"
                ))
                .build();

        emailService.sendEmailAsync(emailRequest);

        return response;
    }

    @Caching(evict = {
            @CacheEvict(value = "vendorDetail", allEntries = true),
            @CacheEvict(value = "AllVendors", allEntries = true)
    })
    @Transactional
    public String makeDecisiion(
            OnBoardingDecisionDto onBoardingDecisionDto,
            User user) {
        log.error("Inside service makeDecision");

        String tenantId = TenantContext.getTenantId();

        Vendor vendor = vendorRepository.findById(
                        onBoardingDecisionDto.getApplicationId()
                )
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        VendorOnboardingApplication onboarding = vendorOnnBRepo
                .findByVendorId(onBoardingDecisionDto.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Onboarding application not found"));

        OnboardingStatus prevStatus = onboarding.getStatus();
        OnboardingStatus nextStatus;

        boolean isApprove = "APPROVE".equalsIgnoreCase(
                onBoardingDecisionDto.getAction()
        );

        if (isApprove) {

            nextStatus = OnboardingStatus.APPROVED;

            onboarding.setStatus(nextStatus);
            onboarding.setReviewedAt(LocalDateTime.now());
            onboarding.setReviewedBy(user.getId());
            onboarding.setReviewRemarks(onBoardingDecisionDto.getRemarks());

            vendor.setStatus(VendorStatus.ACTIVE);
            vendor.setReSubmit(onBoardingDecisionDto.isAllowResubmit());

            vendorRepository.save(vendor);
            vendorOnnBRepo.save(onboarding);

            String remarks = String.format(
                    "Vendor onboarding application approved. Status changed from %s to %s.",
                    prevStatus,
                    nextStatus
            );

            onboardingHistoryService.saveVendorOnBoardingHistory(
                    tenantId,
                    vendor.getId(),
                    remarks,
                    prevStatus,
                    nextStatus
            );

            EmailRequestDto approvalEmail = EmailRequestDto.builder()
                    .to(vendor.getEmail())
                    .subject("🎉 Your Vendor Application is Approved – "
                            + vendor.getBussinessName())
                    .templateName("vendor-application-approved")
                    .templateVariables(Map.of(
                            "vendorName", vendor.getFirstName(),
                            "shopName", vendor.getBussinessName(),
                            "applicationId", onboarding.getApplicationId(),
                            "remarks",
                            onBoardingDecisionDto.getRemarks() != null
                                    ? onBoardingDecisionDto.getRemarks()
                                    : "",
                            "loginLink", "#",
                            "supportEmail", "support@kumarstore.online"
                    ))
                    .build();

            emailService.sendEmailAsync(approvalEmail);

            return "Approved Successfully";
        }
        nextStatus = OnboardingStatus.REJECTED;

        onboarding.setStatus(nextStatus);
        onboarding.setReviewedAt(LocalDateTime.now());
        onboarding.setReviewedBy(user.getId());
        onboarding.setReviewRemarks(onBoardingDecisionDto.getRemarks());

        vendor.setStatus(VendorStatus.REJECTED);
        vendor.setReSubmit(onBoardingDecisionDto.isAllowResubmit());

        vendorRepository.save(vendor);
        vendorOnnBRepo.save(onboarding);

        String remarks = String.format(
                "Vendor onboarding application rejected. Status changed from %s to %s.",
                prevStatus,
                nextStatus
        );

        onboardingHistoryService.saveVendorOnBoardingHistory(
                tenantId,
                vendor.getId(),
                remarks,
                prevStatus,
                nextStatus
        );

        EmailRequestDto rejectionEmail = EmailRequestDto.builder()
                .to(vendor.getEmail())
                .subject("Update on Your Vendor Application – "
                        + vendor.getBussinessName())
                .templateName("vendor-application-rejected")
                .templateVariables(Map.of(
                        "vendorName", vendor.getFirstName(),
                        "shopName", vendor.getBussinessName(),
                        "applicationId", onboarding.getApplicationId(),
                        "remarks",
                        onBoardingDecisionDto.getRemarks() != null
                                ? onBoardingDecisionDto.getRemarks()
                                : "",
                        "allowResubmit",
                        onBoardingDecisionDto.isAllowResubmit(),
                        "resubmitLink", "#",
                        "supportEmail", "support@kumarstore.online"
                ))
                .build();

        emailService.sendEmailAsync(rejectionEmail);

        return "Rejected Successfully";
    }
    public VenddorOnBoardingApplicationStatus getOnboardingStatus(CustomUserDetail userDetail) {
        String tenantId = TenantContext.getTenantId();
        VenddorOnBoardingApplicationStatus v2 = new VenddorOnBoardingApplicationStatus();
        logger.info(" Tenant Id while Onboarding Status " + tenantId);
        Optional<Vendor> v1 = vendorRepository.findByTenantId(tenantId);
        if (!v1.isPresent()) {
            throw new RuntimeException("Vendpr Does Not Exist");
        }
        Vendor v3 = v1.get();
        Optional<VendorOnboardingApplication> v4 = vendorOnnBRepo.findByVendorId(v3.getId());
        if (!v4.isPresent()) {
            throw new RuntimeException("VendorApplication Does Not Exist");
        }
        VendorOnboardingApplication v5 = v4.get();
        v2.setApplicationId(v5.getApplicationId());
        v2.setStatus(String.valueOf(v5.getStatus()));
        v2.setSuccess(true);
        v2.setStoreName(v3.getStoreName());
        v2.setBusinessName(v3.getBussinessName());
        v2.setSubmittedAt(v5.getSubmittedAt());
        v2.setReviewedAt(v5.getReviewedAt());
        v2.setRemarks(v5.getReviewRemarks());
        v2.setResubmit(v3.isReSubmit());
        return v2;


    }

    @Transactional
    public String initiateResubmit(
            CustomUserDetail userDetail,
            String applicationId) {

        String tenantId = TenantContext.getTenantId();

        VendorOnboardingApplication onboarding =
                vendorOnnBRepo.findByApplicationId(applicationId);

        if (onboarding == null) {
            throw new RuntimeException(
                    "Application does not exist. Please contact the support team."
            );
        }

        Optional<Vendor> vendorOptional =
                vendorRepository.findByTenantId(tenantId);

        if (vendorOptional.isEmpty()) {
            throw new vendorNotFoundException("Vendor does not exist.");
        }

        Vendor vendor = vendorOptional.get();
        OnboardingStatus prevStatus = onboarding.getStatus();
        OnboardingStatus nextStatus = OnboardingStatus.DRAFT;
        onboarding.setStatus(nextStatus);
        onboarding.setCompletionPercentage(90);
        onboarding.setCurrentStep(5);
        vendor.setStatus(VendorStatus.ONBOARDING);
        vendorOnnBRepo.save(onboarding);
        vendorRepository.save(vendor);
        String remarks = String.format(
                "Vendor initiated resubmission of the onboarding application. "
                        + "Status changed from %s to %s.",
                prevStatus,
                nextStatus
        );

        onboardingHistoryService.saveVendorOnBoardingHistory(
                tenantId,
                vendor.getId(),
                remarks,
                prevStatus,
                nextStatus
        );

        return "Resubmit Initiated Successfully";
    }

    public List<OnboardHistoryDTO> getHistory(Long vendorId) {

        List<VendorOnboardingHistory> rows = (vendorId != null)
                ? vendorOnbaordingRepository.findByVendorIdOrderByCreatedAtDesc(vendorId)
                : vendorOnbaordingRepository.findAllByOrderByCreatedAtDesc();
        Set<Long> vendorIds = rows.stream()
                .map(VendorOnboardingHistory::getVendorId)
                .collect(Collectors.toSet());
        Map<Long, String> vendorNames = vendorRepository.findAllById(vendorIds).stream()
                .collect(Collectors.toMap(Vendor::getId, Vendor::getBussinessName, (a, b) -> a));
        return rows.stream()
                .map(h -> toDto(h, vendorNames.get(h.getVendorId())))
                .toList();
    }

    private OnboardHistoryDTO toDto(VendorOnboardingHistory h, String vendorName) {
        boolean isAdmin = "0".equals(h.getTenantId());
        return OnboardHistoryDTO.builder()
                .id(h.getId())
                .vendorId(h.getVendorId())
                .vendorName(vendorName)
                .fromStatus(h.getFromStatus() == null ? null : h.getFromStatus().name())
                .toStatus(h.getToStatus() == null ? null : h.getToStatus().name())
                .remarks(h.getRemarks())
                .tenantId(h.getTenantId())
                .actedBy(isAdmin ? "Super Admin" : "Vendor")
                .createdAt(h.getCreatedAt())
                .build();
    }

}