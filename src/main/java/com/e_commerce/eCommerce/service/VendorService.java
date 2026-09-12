package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.JwtUtil;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.controller.VendorController;
import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.dto.request.*;
import com.e_commerce.eCommerce.dto.response.VenodorBusinessProfile;
import com.e_commerce.eCommerce.entity.*;
import com.e_commerce.eCommerce.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor

public class VendorService {
    private static final Logger logger = LoggerFactory.getLogger(VendorController.class);


    private final VendorRepository vendorRepository;
    private final PasswordEncoder passwordEncoder;
    private final VendorOnnBRepo vendorOnnBRepo;
    private final UserRepos userRepos;
    private final vendorBussinesss vendorBussinessAddress;
    private final PasswordResetServiceImpl passwordResetService;
    private final EmailService emailService;
    private final VendorAddresss vendorAddresssRepo;
    private final VendorBrandingRepository vendorBrandingRepository;
    private final VendorBankRepository vendorBankRepository;
    @CacheEvict(value = "AllVendors", allEntries = true)
    @Transactional
    public Boolean createVendor(VendorRequestDto vendorRequestDto, String requesst) {

        StringBuilder errorMessage = new StringBuilder();


        if (vendorRepository.existsByEmail(vendorRequestDto.getEmail())) {
            errorMessage.append("Email already exists. ");
        }

        if (vendorRepository.existsByMobile(vendorRequestDto.getPhone())) {
            errorMessage.append("Mobile number already exists. ");
        }

        if (vendorRepository.existsByStoreName(vendorRequestDto.getBusinessName())) {
            errorMessage.append("Store name already exists. ");
        }

        if (vendorRepository.existsBySubDomain(vendorRequestDto.getSubDomain())) {
            errorMessage.append("Sub domain already exists. ");
        }
        String[] vendorEmails = vendorRequestDto.getVendorEmail().split("@");
        if (!vendorEmails[1].equalsIgnoreCase("mystore.com")) {
            errorMessage.append("Vendor Email must start with a @mystore.com");
        }

        if (!errorMessage.isEmpty()) {
            throw new RuntimeException(errorMessage.toString().trim());
        }

        Vendor vendor = new Vendor();

        vendor.setBussinessName(vendorRequestDto.getBusinessName());
        vendor.setFirstName(vendorRequestDto.getFirstName());
        vendor.setLastName(vendorRequestDto.getLastName());
        vendor.setEmail(vendorRequestDto.getEmail());
        vendor.setMobile(vendorRequestDto.getPhone());
        vendor.setStoreName(vendorRequestDto.getBusinessName());
        vendor.setVendorEmail(vendorRequestDto.getVendorEmail());
        vendor.setPlan(vendorRequestDto.getPlan());
        vendor.setSubDomain(vendorRequestDto.getSubDomain() + requesst);
        vendor.setPassword(passwordEncoder.encode("satish123"));
        vendor.setJwtSecret(JwtUtil.generateJwtSecret());
        vendorRepository.save(vendor);
        VendorOnboardingApplication vendorOnboardingApplication = new VendorOnboardingApplication();
        vendorOnboardingApplication.setVendor(vendor);
        vendorOnnBRepo.save(vendorOnboardingApplication);
        User user = new User();
        user.setEmail(vendorRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(vendorRequestDto.getFirstName() + "@" + 123));
        user.setRole(Roles.ADMIN);
        user.setMobileNumber(vendor.getMobile());
        user.setTenantId(vendor.getTenantId());
        user.setFirstName(vendor.getFirstName());
        user.setLastName(vendor.getLastName());
        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy("SUPER_ADMIN");
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy("Satish");
        user.setVendorId(vendor.getId());
        User user1 = userRepos.save(user);
        String token = passwordResetService.initiateForVendor(user1);

        String resetLinks = "https://" + requesst + "/reset-password?token=" + token;
        String Loginlinks = "https://" + requesst;


        EmailRequestDto vendorEmail = EmailRequestDto.builder().
                to(vendor.getEmail()).
                subject("Welcome to " + "KUMAR Store Online" + " – Your Vendor Account Details").
                templateName("vendor-welcome-email").
                templateVariables(Map.of("vendorName", vendor.getFirstName(),
                        "vendorEmail", vendor.getEmail() + " " + vendor.getLastName(),
                        "tempPassword", vendorRequestDto.getFirstName() + "@" + 123, "shopName", "Kumar Store",
                        "loginLink", Loginlinks, "resetLink",
                        resetLinks, "expiryMinutes", 30,
                        "supportEmail",
                        "support@kumarstore.online")).build();

        emailService.sendEmailAsync(vendorEmail);
        return true;
    }

    @Cacheable(value = "AllVendors")
    public List<VendorResponseDto> getAllVendors() {
        logger.info("Fist hit in DB for  Vendor");


        List<VendorResponseDto> responseDtoList = new ArrayList<>();

        List<Vendor> vendors = vendorRepository.findByStatusNot(VendorStatus.ONBOARDING);

        if (vendors.isEmpty()) {
            logger.info("No vendors found.");
            return responseDtoList;
        }

        for (Vendor vendor : vendors) {

            VendorResponseDto vendorResponseDto = new VendorResponseDto();

            vendorResponseDto.setVendorId(vendor.getId());
            vendorResponseDto.setVendorName(vendor.getFirstName() + " " + vendor.getLastName());
            vendorResponseDto.setVendorEmail(vendor.getVendorEmail());
            vendorResponseDto.setBusinessName(vendor.getBussinessName());
            vendorResponseDto.setStatus(vendor.getStatus().name());
            vendorResponseDto.setSubscriptionPlan(vendor.getPlan().name());
            vendorResponseDto.setTotalOrders(0);
            vendorResponseDto.setTotalRevenue(0L);

            responseDtoList.add(vendorResponseDto);
        }

        return responseDtoList;
    }

    @Cacheable(value = "vendorDetail", key = "#vendorId")
    public VendorDetailsResponseDto getVendorDetails(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(() -> new RuntimeException("Vendor not found."));
        VendorBusiness vendorBusiness = vendorBussinessAddress.findByVendorId(vendorId);
        VendorDetailsResponseDto dto = new VendorDetailsResponseDto();
        dto.setVendorId(vendor.getId());
        dto.setFullName(valueOrNA(vendor.getFirstName()) + " " + valueOrNA(vendor.getLastName()));
        dto.setEmail(valueOrNA(vendor.getEmail()));
        dto.setMobileNumber(valueOrNA(vendor.getMobile()));
        dto.setStatus(vendor.getStatus());
        dto.setRegistrationDate(vendor.getCreatedAt());
        dto.setBusinessName(valueOrNA(vendor.getBussinessName()));
        dto.setStoreName(valueOrNA(vendor.getStoreName()));
        if (vendorBusiness != null) {
            dto.setBusinessType(valueOrNA(String.valueOf(vendorBusiness.getBusinessType())));
            dto.setBusinessCategory(valueOrNA(vendorBusiness.getBusinessCategory()));
            dto.setBusinessDescription(valueOrNA(vendorBusiness.getBusinessDescription()));
            dto.setPanNumber(maskPan(vendorBusiness.getPanNumber()));
            dto.setGstNumber(maskGSTIN(vendorBusiness.getGstNumber()));
        } else {
            dto.setStoreName("N/A");
            dto.setBusinessType("N/A");
            dto.setBusinessCategory("N/A");
            dto.setBusinessDescription("N/A");
            dto.setPanNumber("N/A");
            dto.setGstNumber("N/A");

        }
        return dto;
    }

    private String valueOrNA(String value) {

        return (value == null || value.trim().isEmpty()) ? "N/A" : value;
    }


    public static String maskPan(String pan) {

        if (pan == null || pan.length() != 10) {
            return pan;
        }

        return pan.substring(0, 5) + "****" + pan.substring(9);
    }

    public static String maskGSTIN(String gstin) {
        if (gstin == null || gstin.length() != 15) {
            return gstin;
        }
        return gstin.substring(0, 2) + "*".repeat(10) + gstin.substring(12);
    }

    @Caching(evict = {
            @CacheEvict(value = "vendorDetail", allEntries = true),
            @CacheEvict(value = "AllVendors", allEntries = true)
    })
    @Transactional
    public String editProfile(VendorEditResponse request, User loggedInUser) {

        User user = userRepos.findById(loggedInUser.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        Vendor v1 = vendorRepository.findById(user.getVendorId()).orElseThrow(() -> new RuntimeException("Vendor not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMobileNumber(request.getMobile());
        user.setProfileImage(request.getProfileImage());
        v1.setMobile(request.getMobile());
        v1.setFirstName(request.getFirstName());
        v1.setLastName(request.getLastName());
        vendorRepository.save(v1);

        userRepos.save(user);

        return "Profile updated successfully";
    }

    @Transactional
    public String changeCurrentUserPassword(PasswordChangeDto pas, CustomUserDetail userDetail) {
        String tenantId = TenantContext.getTenantId();
        User user = userDetail.getUser();
        if (!user.getTenantId().equalsIgnoreCase(tenantId)) {
            throw new RuntimeException("Try From Required Vendor Profile");
        }
        if (!passwordEncoder.matches(pas.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current Password does not match.");
        }
        user.setPassword(passwordEncoder.encode(pas.getNewPassword()));
        userRepos.save(user);
        return "Password Updated Successfully";


    }

    public List<CustomerListResponseDTO> findAllCustomer(CustomUserDetail userDetail) {
        String tenantid = TenantContext.getTenantId();
        if (userDetail == null) {
            throw new RuntimeException("Please login again");
        }
        if (userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorizee access");
        }
        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenantid);
        if (vendor == null) {
            throw new RuntimeException("Invalid vendor");
        }
        List<User> users = userRepos.findAllByTenantIdAndVendorId(tenantid, vendor.get().getId());
        List<CustomerListResponseDTO> customerListResponseDTOS = userRepos.getCustomerList(tenantid, vendor.get().getId());
        return customerListResponseDTOS;

    }


    public VenodorBusinessProfile loadVendorBusinessProfile(
            CustomUserDetail userDetail) {

        if (userDetail == null) {
            throw new RuntimeException("Please login again");
        }

        if (userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorized access");
        }

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Invalid tenant");
        }

        return vendorRepository
                .findVendorBusinessProfile(tenantId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vendor business profile not found"
                        ));
    }



    public VenodorBusinessProfile updatevendorBusinessProfile(
            CustomUserDetail userDetail,
            VenodorBusinessProfile venodorBusinessProfiles) {

        String tenantId = TenantContext.getTenantId();

        if (userDetail == null) {
            throw new RuntimeException("Please login again");
        }

        if (userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorized access");
        }

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Invalid tenant");
        }

        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));

        VendorBusiness vendorBusiness =
                vendorBussinessAddress.findByVendorId(vendor.getId());
        VendorBranding vendorBranding=vendorBrandingRepository.findByVendorId(vendor.getId());
        if(vendorBranding!=null){
            vendorBranding.setStoreDescription(venodorBusinessProfiles.getDescription());
            vendorBranding.setUpdatedAt(LocalDateTime.now());
            vendorBrandingRepository.save(vendorBranding);

        }

        vendor.setBussinessName(
                venodorBusinessProfiles.getBusinessName()
        );

        vendor.setFirstName(
                venodorBusinessProfiles.getOwnerName()
        );

        vendorBusiness.setGstNumber(
                venodorBusinessProfiles.getGstNumber()
        );

        vendorBusiness.setPanNumber(
                venodorBusinessProfiles.getPanNumber()
        );

        vendorBusiness.setBusinessDescription(
                venodorBusinessProfiles.getDescription()
        );

        vendorBusiness.setCinNumber(
                venodorBusinessProfiles.getRegistrationNumber()
        );
        vendor.setUpdatedAt(LocalDateTime.now());
        vendorBusiness.setUpdatedAt(LocalDateTime.now());
        vendor.setUpdatedBy(userDetail.getId());

        vendorRepository.save(vendor);
        vendorBussinessAddress.save(vendorBusiness);

        return VenodorBusinessProfile.builder()
                .businessName(vendor.getBussinessName())
                .ownerName(vendor.getFirstName())
                .gstNumber(vendorBusiness.getGstNumber())
                .panNumber(vendorBusiness.getPanNumber())
                .description(vendorBusiness.getBusinessDescription())
                .registrationNumber(vendorBusiness.getCinNumber())
                .build();
    }

    public VendorBusinessAddressDTO loadVendorAddress(CustomUserDetail userDetail) {
        String tenantId=TenantContext.getTenantId();

        if (userDetail == null) {
            throw new RuntimeException("Please login again");
        }

        if (userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorized access");
        }

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Invalid tenant");
        }

        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));
        Optional<VendorBusinessAddressDTO> vendorAddress=vendorAddresssRepo.findVendorBusiness(vendor.getId());
        if(vendorAddress==null){
            throw new RuntimeException("Address Could not Found");
        }
        VendorBusinessAddressDTO v1=vendorAddress.get();
        return v1;

    }

    public VendorBusinessAddressDTO editVendorAddress(CustomUserDetail userDetail, VendorBusinessAddressDTO vendorBusinessAddressDTO) {
        String tenantId = TenantContext.getTenantId();

        if (userDetail == null) {
            throw new RuntimeException("Please login again");
        }

        if (userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorized access");
        }

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Invalid tenant");
        }

        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));
        VendorAddress vendorAddress=vendorAddresssRepo.findByVendorId(vendor.getId());
        if(vendorAddress==null){
            throw new RuntimeException("Address Could not Found");
        }
        if (vendorBusinessAddressDTO.getAddressLine1() != null) {
            vendorAddress.setAddressLine1(vendorBusinessAddressDTO.getAddressLine1());
        }

        if (vendorBusinessAddressDTO.getAddressLine2() != null) {
            vendorAddress.setAddressLine2(vendorBusinessAddressDTO.getAddressLine2());
        }

        if (vendorBusinessAddressDTO.getCity() != null) {
            vendorAddress.setCity(vendorBusinessAddressDTO.getCity());
        }

        if (vendorBusinessAddressDTO.getState() != null) {
            vendorAddress.setState(vendorBusinessAddressDTO.getState());
        }

        if (vendorBusinessAddressDTO.getCountry() != null) {
            vendorAddress.setCountry(vendorBusinessAddressDTO.getCountry());
        }

        if (vendorBusinessAddressDTO.getPincode() != null) {
            vendorAddress.setPostalCode(vendorBusinessAddressDTO.getPincode());
        }
           vendorAddresssRepo.save(vendorAddress);

        VendorBusinessAddressDTO vendorBusinessAddressDTOs=VendorBusinessAddressDTO.builder()
                .addressLine1(vendorBusinessAddressDTO.getAddressLine1())
                .addressLine2(vendorBusinessAddressDTO.getAddressLine2())
                .pincode(vendorBusinessAddressDTO.getPincode())
                .city(vendorBusinessAddressDTO.getCity())
                .state(vendorBusinessAddressDTO.getState())
                .country(vendorBusinessAddressDTO.getCountry())
                .build();
        return vendorBusinessAddressDTOs;
    }

    public VendorBrandingRequestDTO loadBrandingDetails(CustomUserDetail userDetail) {
        String tenantId = TenantContext.getTenantId();

        if (userDetail == null) {
            throw new RuntimeException("Please login again");
        }

        if (userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorized access");
        }

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Invalid tenant");
        }

        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));


        Optional<VendorBrandingRequestDTO> vendorBrandingRequestDTO=vendorBrandingRepository.loaddBrandingDetail(vendor.getId());
        if(vendorBrandingRequestDTO.isEmpty()){
            throw new RuntimeException("Branding data doesn't esist for "+vendor.getStoreName());
        }
        VendorBrandingRequestDTO vendorBrandingRequestDTO1=vendorBrandingRequestDTO.get();;
        return vendorBrandingRequestDTO1;
    }

    public VendorBrandingRequestDTO editBranding(CustomUserDetail userDetail, VendorBrandingRequestDTO vendorBrandingRequestDTO) {
        String tenantId = TenantContext.getTenantId();

        if (userDetail == null) {
            throw new RuntimeException("Please login again");
        }

        if (userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorized access");
        }

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Invalid tenant");
        }

        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));
        VendorBranding vendorBranding1=vendorBrandingRepository.findByVendorId(vendor.getId());
        if(vendorBranding1==null){
            throw new RuntimeException("Brandding data is Not availble for Vendor");
        }


        if (vendorBrandingRequestDTO.getPrimaryColor() != null) {
            vendorBranding1.setPrimaryColor(
                    vendorBrandingRequestDTO.getPrimaryColor()
            );
        }

        if (vendorBrandingRequestDTO.getSecondaryColor() != null) {
            vendorBranding1.setSecondaryColor(
                    vendorBrandingRequestDTO.getSecondaryColor()
            );
        }

        if (vendorBrandingRequestDTO.getFaviconUrl() != null) {
            vendorBranding1.setFaviconUrl(
                    vendorBrandingRequestDTO.getFaviconUrl()
            );
        }

        if (vendorBrandingRequestDTO.getBannerUrl() != null) {
            vendorBranding1.setBannerUrl(
                    vendorBrandingRequestDTO.getBannerUrl()
            );
        }

        if (vendorBrandingRequestDTO.getLogoUrl() != null) {
            vendorBranding1.setLogoUrl(
                    vendorBrandingRequestDTO.getLogoUrl()
            );
        }
        vendorBranding1.setUpdatedAt(LocalDateTime.now());
        vendorBrandingRepository.save(vendorBranding1);
        return VendorBrandingRequestDTO.builder()
                .bannerUrl(vendorBrandingRequestDTO.getBannerUrl())
                .logoUrl(vendorBrandingRequestDTO.getLogoUrl())
                .faviconUrl(vendorBrandingRequestDTO.getFaviconUrl())
                .primaryColor(vendorBrandingRequestDTO.getSecondaryColor())
                .secondaryColor(vendorBrandingRequestDTO.getSecondaryColor())
                .build();

    }

    public VendorContactSocialRequestDTO editContactDetails(CustomUserDetail userDetail, VendorContactSocialRequestDTO vendorContactSocialRequestDTO) {
        String tenantId = TenantContext.getTenantId();

        if (userDetail == null) {
            throw new RuntimeException("Please login again");
        }

        if (userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorized access");
        }

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Invalid tenant");
        }

        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));
        VendorBranding vendorBranding1=vendorBrandingRepository.findByVendorId(vendor.getId());
        if(vendorBranding1==null){
            throw new RuntimeException("Brandding data is Not availble for Vendor");
        }
        if (vendorContactSocialRequestDTO.getSupportEmail() != null
                && !vendorContactSocialRequestDTO.getSupportEmail().isBlank()) {
            vendorBranding1.setSupportEmail(
                    vendorContactSocialRequestDTO.getSupportEmail()
            );
        }

        if (vendorContactSocialRequestDTO.getSupportPhone() != null
                && !vendorContactSocialRequestDTO.getSupportPhone().isBlank()) {
            vendorBranding1.setSupportPhone(
                    vendorContactSocialRequestDTO.getSupportPhone()
            );
        }

        if (vendorContactSocialRequestDTO.getWebsite() != null
                && !vendorContactSocialRequestDTO.getWebsite().isBlank()) {
            vendorBranding1.setWebsite(
                    vendorContactSocialRequestDTO.getWebsite()
            );
        }

        if (vendorContactSocialRequestDTO.getWhatsappNumber() != null
                && !vendorContactSocialRequestDTO.getWhatsappNumber().isBlank()) {
            vendorBranding1.setWhatsApp(
                    vendorContactSocialRequestDTO.getWhatsappNumber()
            );
        }

        if (vendorContactSocialRequestDTO.getFacebook() != null
                && !vendorContactSocialRequestDTO.getFacebook().isBlank()) {
            vendorBranding1.setFacebookUrl(
                    vendorContactSocialRequestDTO.getFacebook()
            );
        }

        if (vendorContactSocialRequestDTO.getInstagram() != null
                && !vendorContactSocialRequestDTO.getInstagram().isBlank()) {
            vendorBranding1.setInstagramUrl(
                    vendorContactSocialRequestDTO.getInstagram()
            );
        }

        if (vendorContactSocialRequestDTO.getYoutube() != null
                && !vendorContactSocialRequestDTO.getYoutube().isBlank()) {
            vendorBranding1.setYoutubeUrl(
                    vendorContactSocialRequestDTO.getYoutube()
            );
        }

        if (vendorContactSocialRequestDTO.getLinkedin() != null
                && !vendorContactSocialRequestDTO.getLinkedin().isBlank()) {
            vendorBranding1.setLinkedinUrl(
                    vendorContactSocialRequestDTO.getLinkedin()
            );
        }
        vendorBrandingRepository.save(vendorBranding1);
        return vendorContactSocialRequestDTO;


    }

    public VendorContactSocialRequestDTO loadContactInfo(CustomUserDetail userDetail) {
        String tenantId = TenantContext.getTenantId();

        if (userDetail == null) {
            throw new RuntimeException("Please login again");
        }

        if (userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorized access");
        }

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Invalid tenant");
        }

        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));
        VendorContactSocialRequestDTO vendorContactSocialRequestDTO=vendorBrandingRepository.loadVendorContactInfo(vendor.getId());
        return vendorContactSocialRequestDTO;
    }


    public BankAccountRequestDto loadBankVendorData(CustomUserDetail userDetail) {

        // Validate logged-in user
        if (userDetail == null) {
            throw new RuntimeException("Please login again");
        }

        // Validate user role
        if (userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorized access");
        }

        // Get tenant ID
        String tenantId = TenantContext.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Invalid tenant");
        }

        // Fetch vendor
        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));

        // Fetch vendor bank details
        Optional<VendorBank> vendorBank1 = vendorBankRepository.findByVendor(vendor);
        if(vendorBank1.isEmpty()){
            throw new RuntimeException("Bank Detail not exist ...Please Add");
        }
        VendorBank vendorBank=vendorBank1.get();


        // Build response DTO
        return BankAccountRequestDto.builder()
                .accountHolderName(vendorBank.getAccountHolderName())
                .bankName(vendorBank.getBankName())
                .accountNumber(vendorBank.getAccountNumber())
                .ifscCode(vendorBank.getIfscCode())
                .branchName(vendorBank.getBranchName())
                .upiId(vendorBank.getUpiId())
                .build();
    }


    public BankAccountRequestDto updateBankVendorData(
            CustomUserDetail userDetail,
            BankAccountRequestDto request) {

        String tenantId = TenantContext.getTenantId();
        if (userDetail == null) {
            throw new RuntimeException("Please login again");
        }

        if (userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorized access");
        }
        if (request == null) {
            throw new RuntimeException("Bank details are required");
        }

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Invalid tenant");
        }

        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));

        VendorBank vendorBank =
                vendorBankRepository.findByVendorId(vendor.getId());
        if(vendorBank==null){
            throw new RuntimeException("Bank Details Not found");
        }

        if (request.getAccountHolderName() == null ||
                request.getAccountHolderName().isBlank()) {

            throw new RuntimeException(
                    "Account holder name is required");
        }

        if (request.getBankName() == null ||
                request.getBankName().isBlank()) {

            throw new RuntimeException(
                    "Bank name is required");
        }

        if (request.getAccountNumber() == null ||
                request.getAccountNumber().isBlank()) {

            throw new RuntimeException(
                    "Account number is required");
        }

        if (request.getIfscCode() == null ||
                request.getIfscCode().isBlank()) {

            throw new RuntimeException(
                    "IFSC code is required");
        }


        vendorBank.setAccountHolderName(
                request.getAccountHolderName().trim());

        vendorBank.setBankName(
                request.getBankName().trim());

        vendorBank.setAccountNumber(
                request.getAccountNumber().trim());

        vendorBank.setIfscCode(
                request.getIfscCode().trim().toUpperCase());

        vendorBank.setBranchName(
                request.getBranchName() != null
                        ? request.getBranchName().trim()
                        : null);

        vendorBank.setUpiId(
                request.getUpiId() != null
                        ? request.getUpiId().trim()
                        : null);
        vendorBank.setUpdatedAt(LocalDateTime.now());

        VendorBank savedBank =
                vendorBankRepository.save(vendorBank);

        return BankAccountRequestDto.builder()
                .accountHolderName(
                        savedBank.getAccountHolderName())
                .bankName(
                        savedBank.getBankName())
                .accountNumber(
                        savedBank.getAccountNumber())
                .ifscCode(
                        savedBank.getIfscCode())
                .branchName(
                        savedBank.getBranchName())
                .upiId(
                        savedBank.getUpiId())
                .build();
    }
}