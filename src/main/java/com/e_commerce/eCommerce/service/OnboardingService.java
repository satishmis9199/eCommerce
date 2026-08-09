package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.controller.OnboardingController;
import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.entity.*;
import com.e_commerce.eCommerce.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingService {
    private static final Logger logger =
            LoggerFactory.getLogger(OnboardingService.class);

    private final VendorOnnBRepo vendorOnnBRepo;
//    private final VendorBankRepository vendorBankRepository;
    private final VendorRepository vendorRepository;
    private final vendorBussinesss vendorBusinessRepository;
    private final VendorAddresss vendorAddressRepository;
    private final VendorBankRepository vendorBankRepository;
    private final VendorBrandingRepository vendorBrandingRepository;
    private final VendorAddresss vendorAddressRepo;

//    private final VendorOnnBRepo vendorOnnBRepos;


    public VendorOnboardingResponseDTO getOnboarding(Long vendorId) {

        log.info("Fetching onboarding details for vendor : {}", vendorId);

        VendorOnboardingResponseDTO response = new VendorOnboardingResponseDTO();

        if (vendorId == null) {
            log.warn("vendorId is null, cannot fetch onboarding data.");
            response.setSuccess(false);
            return response;
        }

        Optional<VendorOnboardingApplication> optionalApplication =
                vendorOnnBRepo.findByVendorId(vendorId);

        if (optionalApplication.isEmpty()) {

            log.warn("Onboarding application not found for vendor : {}", vendorId);
            response.setSuccess(false);
            response.setData(buildEmptyData());
            return response;
        }

        VendorOnboardingApplication application = optionalApplication.get();
        int percent=application.getCompletionPercentage();
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

    //===========================================================
    // Basic Information
    //===========================================================

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

    //===========================================================
    // Business Details
    //===========================================================

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

    //===========================================================
    // Business Address
    //===========================================================

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

    //===========================================================
    // Bank Details
    //===========================================================

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

    //===========================================================
    // Branding Details
    //===========================================================

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




    public String saveBasicDetail(Long vendorId, BasicInfoDto dto) {


        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));
        VendorOnboardingApplication vendorOnboardingApplication=vendorOnnBRepo.findByVendorId(vendorId)
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



    public String saveBussienssddress(Long vendorId, BusinessAddressDTO dto){
        // Vendor
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));

        // Onboarding Application
        VendorOnboardingApplication onboarding =
                vendorOnnBRepo.findByVendorId(vendorId)
                        .orElseThrow(() ->
                                new RuntimeException("Onboarding application not found"));
        VendorAddress vendorAddress=vendorAddressRepo.findByVendorId(vendorId);
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
        if(vendorAddress==null){
            vendorAddress=new VendorAddress();
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

        onboarding.setCurrentStep(Math.max(onboarding.getCurrentStep(),3));
        onboarding.setCompletionPercentage(Math.max(onboarding.getCompletionPercentage(), 60));
        vendorAddressRepo.save(vendorAddress);
        vendorOnnBRepo.save(onboarding);
        return "Adress Details saved Successfully";

    }

    public String saveBankDetail(Long vendorid, BankInfoDto dto) {
        // Vendor
        Vendor vendor = vendorRepository.findById(vendorid)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));

        // Onboarding Application
        VendorOnboardingApplication onboarding =
                vendorOnnBRepo.findByVendorId(vendorid)
                        .orElseThrow(() ->
                                new RuntimeException("Onboarding application not found"));
        VendorBank vendorBank=vendorBankRepository.findByVendorId(vendorid);
        if(vendorBank==null){
            vendorBank=new VendorBank();
            vendorBank.setVendor(vendor);

        }
        vendorBank.setBankName(dto.getBankName());
        vendorBank.setAccountHolderName(dto.getAccountHolderName());
        vendorBank.setBranchName(dto.getBranchName());
        vendorBank.setIfscCode(dto.getIfscCode());
        vendorBank.setAccountNumber(dto.getAccountNumber());
        onboarding.setCompletionPercentage(Math.max(onboarding.getCompletionPercentage(),80));
        onboarding.setCurrentStep(Math.max(onboarding.getCurrentStep(),4));
        vendorBankRepository.save(vendorBank);
        return "Bank Detail saved";
    }


    public String saveBrandDetail(Long vendorid, BrandingInfoDto dto) {
        // Vendor
        Vendor vendor = vendorRepository.findById(vendorid)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));

        // Onboarding Application
        VendorOnboardingApplication onboarding =
                vendorOnnBRepo.findByVendorId(vendorid)
                        .orElseThrow(() ->
                                new RuntimeException("Onboarding application not found"));
        VendorBranding vendorBranding=vendorBrandingRepository.findByVendorId(vendorid);
        if(vendorBranding==null){
             vendorBranding=new VendorBranding();
            vendorBranding.setVendor(vendor);

        }
        vendorBranding.setLogoUrl(dto.getLogoUrl());
        vendorBranding.setBannerUrl(dto.getBannerUrl());
        vendorBranding.setPrimaryColor(dto.getPrimaryColor());
        vendorBranding.setStoreDescription(dto.getDescription());
        vendorBranding.setStoreTagline(dto.getTagline());
        onboarding.setCompletionPercentage(90);
        onboarding.setCurrentStep(Math.max(onboarding.getCurrentStep(),5));
        vendorBrandingRepository.save(vendorBranding);
        vendorOnnBRepo.save(onboarding);

        return "Brandimg Info Saved";
    }

    @Transactional
    public SubmitApplicationResponseDTO submitApplication(Long vendorId) {

        // Vendor
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));

        // Onboarding Application
        VendorOnboardingApplication onboarding = vendorOnnBRepo
                .findByVendorId(vendorId)
                .orElseThrow(() ->
                        new RuntimeException("Onboarding application not found"));

        // Update Status
        onboarding.setSubmittedAt(LocalDateTime.now());
        onboarding.setStatus(OnboardingStatus.UNDER_REVIEW);
        onboarding.setCurrentStep(6);
        onboarding.setCompletionPercentage(100);
        onboarding.setCompleted(true);

        vendor.setStatus(VendorStatus.PENDING);

        // Save
        vendorOnnBRepo.save(onboarding);
        vendorRepository.save(vendor);

        // Response
        SubmitApplicationResponseDTO response = new SubmitApplicationResponseDTO();
        response.setSuccess(true);
        response.setMessage("Your onboarding application has been submitted successfully.");
        response.setApplicationId(onboarding.getApplicationId());
        response.setStatus(OnboardingStatus.valueOf(onboarding.getStatus().name()));
//        response.setVendorStatus(vendor.getStatus().name());
        response.setSubmittedAt(onboarding.getSubmittedAt());

        return response;
    }

    public String makeDecisiion(OnBoardingDecisionDto onBoardingDecisionDto,User user) {
        // Vendor
        Vendor vendor = vendorRepository.findById(onBoardingDecisionDto.getApplicationId())
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));

        // Onboarding Application
        VendorOnboardingApplication onboarding = vendorOnnBRepo
                .findByVendorId(onBoardingDecisionDto.getApplicationId())
                .orElseThrow(() ->
                        new RuntimeException("Onboarding application not found"));
        if(onBoardingDecisionDto.getAction().equalsIgnoreCase("APPROVE")){
            onboarding.setStatus(OnboardingStatus.APPROVED);
            onboarding.setReviewedAt(LocalDateTime.now());
            onboarding.setReviewedBy(user.getId());
            onboarding.setReviewRemarks(onBoardingDecisionDto.getRemarks());

            vendor.setStatus(VendorStatus.ACTIVE);
            vendor.setReSubmit(onBoardingDecisionDto.isAllowResubmit());
            vendorRepository.save(vendor);
            vendorOnnBRepo.save(onboarding);

            return "Approved Successfully";

        }
        onboarding.setStatus(OnboardingStatus.REJECTED);
        onboarding.setReviewedAt(LocalDateTime.now());
        onboarding.setReviewedBy(user.getId());
        onboarding.setReviewRemarks(onBoardingDecisionDto.getRemarks());
        vendor.setReSubmit(onBoardingDecisionDto.isAllowResubmit());

        vendor.setStatus(VendorStatus.REJECTED);
        vendorRepository.save(vendor);
        vendorOnnBRepo.save(onboarding);
        return "Rejected Successfully";




    }

    public VenddorOnBoardingApplicationStatus getOnboardingStatus(CustomUserDetail userDetail) {
        String tenantId= TenantContext.getTenantId();
        VenddorOnBoardingApplicationStatus v2=new VenddorOnBoardingApplicationStatus();
        logger.info(" Tenant Id while Onboarding Status "+tenantId);
        Optional<Vendor> v1=vendorRepository.findByTenantId(tenantId);
        if(!v1.isPresent()){
            throw new RuntimeException("Vendpr Does Not Exist");
        }
        Vendor v3=v1.get();
        Optional<VendorOnboardingApplication> v4=vendorOnnBRepo.findByVendorId(v3.getId());
        if(!v4.isPresent()){
            throw new RuntimeException("VendorApplication Does Not Exist");
        }
        VendorOnboardingApplication v5=v4.get();
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

    public String initiateResubmit(CustomUserDetail userDetail, String applicationId) {
        String tenantid=TenantContext.getTenantId();
        VendorOnboardingApplication onboarding = vendorOnnBRepo
                .findByApplicationId(applicationId);
        if(onboarding==null){
            throw new RuntimeException("Application Doee Not Exist ..Contact Support team");
        }
        Optional<Vendor> v11=vendorRepository.findByTenantId(tenantid);
        if(!v11.isPresent()){
            throw new RuntimeException("Vendor Does not Exist ");
        }
        Vendor v1=v11.get();
        onboarding.setStatus(OnboardingStatus.DRAFT);
        v1.setStatus(VendorStatus.ONBOARDING);
        onboarding.setCompletionPercentage(90);
        onboarding.setCurrentStep(5);

        vendorOnnBRepo.save(onboarding);
        vendorRepository.save(v1);
        return "Resubmit Initiated Success";



    }
}