package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.controller.VendorController;
import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.entity.*;
import com.e_commerce.eCommerce.repository.UserRepos;

import com.e_commerce.eCommerce.repository.VendorOnnBRepo;
import com.e_commerce.eCommerce.repository.VendorRepository;
import com.e_commerce.eCommerce.repository.vendorBussinesss;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VendorService {
    private static final Logger logger= LoggerFactory.getLogger(VendorController.class);

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    VendorOnnBRepo vendorOnnBRepo;
    @Autowired
    UserRepos userRepos;
    @Autowired
    vendorBussinesss vendorBussinessAddress;


    @Transactional
    public Boolean createVendor(VendorRequestDto vendorRequestDto,String requesst) {

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
        String[] vendorEmails=vendorRequestDto.getVendorEmail().split("@");

        System.out.println("Venderpr "+vendorEmails[1]);
        if(!vendorEmails[1].equalsIgnoreCase("mystore.com")){
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
        logger.info("WHile Creating a Vendor subdomain "+vendorRequestDto.getSubDomain()+requesst);
        vendor.setSubDomain(vendorRequestDto.getSubDomain()+requesst);
        vendor.setPassword(passwordEncoder.encode("satish123"));

        vendorRepository.save(vendor);
        VendorOnboardingApplication vendorOnboardingApplication=new VendorOnboardingApplication();
//        vendorOnboardingApplication.setApplicationId("ONB"+vendor.getId());
        vendorOnboardingApplication.setVendor(vendor);

        vendorOnnBRepo.save(vendorOnboardingApplication);

        User user=new User();
        user.setEmail(vendorRequestDto.getVendorEmail());
        user.setPassword(passwordEncoder.encode("satish"));
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
        userRepos.save(user);





        return true;
    }

    public List<VendorResponseDto> getAllVendors() {

        List<VendorResponseDto> responseDtoList = new ArrayList<>();

        List<Vendor> vendors = vendorRepository
                .findByStatusNot(VendorStatus.ONBOARDING);

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

            // TODO: Replace these with actual values
            vendorResponseDto.setTotalOrders(0);
            vendorResponseDto.setTotalRevenue(0L);

            responseDtoList.add(vendorResponseDto);
        }

        return responseDtoList;
    }
    public VendorDetailsResponseDto getVendorDetails(Long vendorId) {

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found."));

        VendorBusiness vendorBusiness = vendorBussinessAddress.findByVendorId(vendorId);

        VendorDetailsResponseDto dto = new VendorDetailsResponseDto();

        // =====================================================
        // Basic Information
        // =====================================================

        dto.setVendorId(vendor.getId());
//

        dto.setFullName(
                valueOrNA(vendor.getFirstName()) + " " +
                        valueOrNA(vendor.getLastName())
        );

        dto.setEmail(valueOrNA(vendor.getEmail()));
        dto.setMobileNumber(valueOrNA(vendor.getMobile()));

        dto.setStatus(vendor.getStatus());

        dto.setRegistrationDate(vendor.getCreatedAt());
//        dto.setLastLogin(vendor.getLastLogin());

        // =====================================================
        // Business Information
        // =====================================================

        dto.setBusinessName(valueOrNA(vendor.getBussinessName()));
        dto.setStoreName(valueOrNA(vendor.getStoreName()));

        if (vendorBusiness != null) {



            dto.setBusinessType(valueOrNA(String.valueOf(vendorBusiness.getBusinessType())));

            dto.setBusinessCategory(valueOrNA(vendorBusiness.getBusinessCategory()));

            dto.setBusinessDescription(
                    valueOrNA(vendorBusiness.getBusinessDescription())
            );

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

        return (value == null || value.trim().isEmpty())
                ? "N/A"
                : value;
    }


    public static String maskPan(String pan) {

        if (pan == null || pan.length() != 10) {
            return pan;
        }

        return pan.substring(0, 5)
                + "****"
                + pan.substring(9);
    }
    public static String maskGSTIN(String gstin) {
        if (gstin == null || gstin.length() != 15) {
            return gstin;
        }
        return gstin.substring(0, 2)
                + "*".repeat(10)
                + gstin.substring(12);
    }
    @Transactional
    public String editProfile(VendorEditResponse request, User loggedInUser) {

        User user = userRepos.findById(loggedInUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Vendor v1=vendorRepository.findById(user.getVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

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
        String tenantId= TenantContext.getTenantId();
        User user=userDetail.getUser();
        if(!user.getTenantId().equalsIgnoreCase(tenantId)){
            throw  new RuntimeException("Try From Required Vendor Profile");
        }
        if (!passwordEncoder.matches(pas.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current Password does not match.");
        }
        user.setPassword(passwordEncoder.encode(pas.getNewPassword()));
        userRepos.save(user);
        return "Password Updated Successfully";


    }

    public List<CustomerListResponseDTO> findAllCustomer(CustomUserDetail userDetail) {
        String tenantid=TenantContext.getTenantId();
        if(userDetail==null){
            throw new RuntimeException("Please login again");
        }
        if(userDetail.getRole()!=Roles.ADMIN){
            throw new RuntimeException("Unauthorizee access");
        }
        Optional<Vendor> vendor=vendorRepository.findByTenantId(tenantid);
        if(vendor==null){
            throw new RuntimeException("Invalid vendor");
        }
        List<User> users=userRepos.findAllByTenantIdAndVendorId(tenantid,vendor.get().getId());
        List<CustomerListResponseDTO> customerListResponseDTOS=userRepos.getCustomerList(tenantid,vendor.get().getId());
        return customerListResponseDTOS;

    }
}