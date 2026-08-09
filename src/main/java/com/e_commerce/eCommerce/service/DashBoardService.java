package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.R2Properties;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.MyProfileDTO;
import com.e_commerce.eCommerce.dto.VendorDashboardResponseDTO;
import com.e_commerce.eCommerce.dto.VendorProfileDTO;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.entity.VendorBranding;
import com.e_commerce.eCommerce.repository.UserRepos;
import com.e_commerce.eCommerce.repository.VendorBrandingRepository;
import com.e_commerce.eCommerce.repository.VendorRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DashBoardService {
    private final UserRepos userRepos;
    private final VendorRepository vendorRepository;
    private final VendorBrandingRepository vendorBrandingRepository;
    private final R2Properties r2Properties;

    public DashBoardService(UserRepos userRepos, VendorRepository vendorRepository, VendorBrandingRepository vendorBrandingRepository, R2Properties r2Properties) {
        this.userRepos = userRepos;
        this.vendorRepository = vendorRepository;
        this.vendorBrandingRepository = vendorBrandingRepository;
//        this.vendorBranding = (VendorBrandingRepository) vendorBranding;
        this.r2Properties = r2Properties;
    }

    public VendorProfileDTO loadDashBoardData(User user) {
//        {
//            "vendorId": 2,
//                "tenantId": "TENANT-100001",
//                "firstName": "Satish",
//
//                "businessName": "Kumar Traders",
//                "storeName": "Kumar Traders",
//                "email": "satish.mishra@example.com",
//                "mobile": "9876543210",
//                "logo": "https://cdn.mystore.com/vendors/logos/vendor-2.png",
//                "subscriptionPlan": "PREMIUM",
//                "status": "ACTIVE",
//                "role": "VENDOR",
//                "emailVerified": true,
//                "mobileVerified": true,
//                "lastLogin": "2026-07-05T14:35:22"
//        }

//        User user1=userRepos.findById(user.getId());
        VendorProfileDTO vendorProfileDTO=new VendorProfileDTO();
        Optional<Vendor> vendor=vendorRepository.findById(user.getVendorId());
        VendorBranding vendorBranding=vendorBrandingRepository.findByVendorId(user.getVendorId());
        if(!vendor.isPresent()){
            throw new RuntimeException("Vendor Not Found");
        }
        if(vendorBranding==null){
            throw new RuntimeException(" Branding data Not Availble");
        }
        Vendor v2=vendor.get();
        vendorProfileDTO.setVendorId(user.getVendorId());
        vendorProfileDTO.setTenantId(TenantContext.getTenantId());
        vendorProfileDTO.setFullName(user.getFirstName()+" "+user.getLastName());
        vendorProfileDTO.setEmailVerified(user.getEmailVerified());
        vendorProfileDTO.setEmail(user.getEmail());
        vendorProfileDTO.setMobile(user.getMobileNumber());
        vendorProfileDTO.setRole(String.valueOf(user.getRole()));
        vendorProfileDTO.setLastLogin(user.getLastLoginTime());
        vendorProfileDTO.setStatus(v2.getStatus());
        vendorProfileDTO.setBusinessName(v2.getBussinessName());
        vendorProfileDTO.setSubscriptionPlan(v2.getPlan());
//        vendorProfileDTO.setLogo(vendorBranding.getLogoUrl());
        System.out.println("Url Binding is "+r2Properties.getPublicUrl()+"/"+vendorBranding.getLogoUrl());
        vendorProfileDTO.setLogo(r2Properties.getPublicUrl()+"/"+vendorBranding.getLogoUrl());
        vendorProfileDTO.setStoreName(v2.getStoreName());
        return vendorProfileDTO;






    }

    public MyProfileDTO getPrrofileData(User userDetail) {
        MyProfileDTO vendorProfileDTO=new MyProfileDTO();
        Optional<Vendor> v1=vendorRepository.findById(userDetail.getVendorId() );
        if(!v1.isPresent()){
            throw new RuntimeException("Not Present");
        }
        Vendor v2=v1.get();

        vendorProfileDTO.setUserId(userDetail.getId());
        vendorProfileDTO.setFirstName(userDetail.getFirstName());
        vendorProfileDTO.setLastName(userDetail.getLastName());
        vendorProfileDTO.setEmail(userDetail.getEmail());
        vendorProfileDTO.setMobile(userDetail.getMobileNumber());
        vendorProfileDTO.setRole(String.valueOf(userDetail.getRole()));
        vendorProfileDTO.setStatus("ACTIVE");
        vendorProfileDTO.setProfileImage(r2Properties.getPublicUrl()+"/"+userDetail.getProfileImage());
        vendorProfileDTO.setMemberSince(userDetail.getCreatedAt());
        vendorProfileDTO.setLastLogin(userDetail.getLastLoginTime());
        return vendorProfileDTO;


    }
}
