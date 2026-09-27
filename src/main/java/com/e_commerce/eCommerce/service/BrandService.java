package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.dto.request.BrandrequestDTO;
import com.e_commerce.eCommerce.dto.request.BransStatusToggle;
import com.e_commerce.eCommerce.dto.response.BrandResponseDTO;
import com.e_commerce.eCommerce.entity.Brands;
import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.enums.BrandStatus;
import com.e_commerce.eCommerce.exception.ProductAlreadyExist;
import com.e_commerce.eCommerce.exception.VendorRequestException;
import com.e_commerce.eCommerce.exception.vendorNotFoundException;
import com.e_commerce.eCommerce.repository.BrandRepository;
import com.e_commerce.eCommerce.repository.ProductRepository;
import com.e_commerce.eCommerce.repository.VendorRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final VendorRepository vendorRepository;
    private final ProductRepository productRepository;
@Transactional
    public BrandrequestDTO saveBrand(
            CustomUserDetail userDetail,
            BrandrequestDTO brandRequestDTO,
            String tenantId) {

        StringBuilder errorMessage = new StringBuilder();

        if (userDetail == null) {
            errorMessage.append("Invalid user. ");
        } else if (userDetail.getId() == null) {
            errorMessage.append("User ID is required. ");
        }

        if (brandRequestDTO == null) {
            errorMessage.append("Brand request cannot be null. ");
        }

        if (tenantId == null || tenantId.isBlank()) {
            errorMessage.append("Tenant ID is required. ");
        }

        if (brandRequestDTO != null) {

            if (brandRequestDTO.getBrandName() == null ||
                    brandRequestDTO.getBrandName().isBlank()) {
                errorMessage.append("Brand name is required. ");
            }

            if (brandRequestDTO.getDescription() == null ||
                    brandRequestDTO.getDescription().isBlank()) {
                errorMessage.append("Brand description is required. ");
            }
        }

        if (errorMessage.length() > 0) {
            throw new VendorRequestException(
                    HttpStatus.BAD_REQUEST,
                    errorMessage.toString().trim()
            );
        }

        Optional<Vendor> vendorOptional =
                vendorRepository.findByTenantId(tenantId);

        if (vendorOptional == null || vendorOptional.isEmpty()) {
            throw new vendorNotFoundException("Vendor does not exist");
        }

        Vendor vendor = vendorOptional.get();

        if (vendor.getId() == null) {
            throw new vendorNotFoundException("Vendor ID does not exist");
        }

        Brands brands = Brands.builder()
                .vendorId(vendor.getId())
                .tenantId(tenantId)
                .brandStatus(BrandStatus.ACTIVE)
                .brandName(brandRequestDTO.getBrandName().trim())
                .brandDesc(brandRequestDTO.getDescription().trim())
                .createdBy(userDetail.getId())
                .updatedBy(userDetail.getId())
                .build();

        Brands savedBrand = brandRepository.save(brands);

        if (savedBrand == null) {
            throw new VendorRequestException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unable to save brand"
            );
        }

        return brandRequestDTO;
    }
    @Transactional(readOnly = true)
    public List<BrandResponseDTO> loadbrands(
            CustomUserDetail userDetail,
            String tenantId) {

        if (userDetail == null) {
            throw new VendorRequestException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid user"
            );
        }

        if (tenantId == null || tenantId.isBlank()) {
            throw new VendorRequestException(
                    HttpStatus.BAD_REQUEST,
                    "Tenant ID is required"
            );
        }

        return brandRepository.getAllbrandByTenantId(tenantId);
    }
@Transactional
    public BrandrequestDTO editBrand(
            CustomUserDetail userDetail,
            String tenantId,
            Long id,
            BrandrequestDTO brandrequestDTO) {

        StringBuilder errorMessage = new StringBuilder();

        if (userDetail == null) {
            errorMessage.append("Invalid user. ");
        } else if (userDetail.getId() == null) {
            errorMessage.append("User ID is required. ");
        }

        if (tenantId == null || tenantId.isBlank()) {
            errorMessage.append("Tenant ID is required. ");
        }

        if (id == null) {
            errorMessage.append("Brand ID is required. ");
        }

        if (brandrequestDTO == null) {
            errorMessage.append("Brand request cannot be null. ");
        }

        if (brandrequestDTO != null) {

            if (brandrequestDTO.getBrandName() == null ||
                    brandrequestDTO.getBrandName().isBlank()) {
                errorMessage.append("Brand name is required. ");
            }

            if (brandrequestDTO.getDescription() == null ||
                    brandrequestDTO.getDescription().isBlank()) {
                errorMessage.append("Brand description is required. ");
            }
        }

        if (errorMessage.length() > 0) {
            throw new VendorRequestException(
                    HttpStatus.BAD_REQUEST,
                    errorMessage.toString().trim()
            );
        }

        Brands brands = brandRepository.findByTenantIdAndId(tenantId, id);

        if (brands == null) {
            throw new vendorNotFoundException("Brand does not exist");
        }

        brands.setBrandName(brandrequestDTO.getBrandName().trim());
        brands.setBrandDesc(brandrequestDTO.getDescription().trim());
        brands.setUpdatedBy(userDetail.getId());

        if (brands.getBrandStatus() == null) {
            brands.setBrandStatus(BrandStatus.ACTIVE);
        }

        brandRepository.save(brands);

        return brandrequestDTO;
    }
@Transactional
    public String deleteBrand(CustomUserDetail userDetail, Long id,String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new VendorRequestException(
                    HttpStatus.BAD_REQUEST,
                    "Tenant ID is required"
            );
        }
        long count=productRepository.findCountOfBrandInTenant(id,tenantId);
        if(count>0){
            throw new ProductAlreadyExist("Product Exist In this Brand..Kindly Detag first");
        }
        brandRepository.deleteAllByProduct(id,tenantId);
        return "Brand has Been Deleted";
    }
    public boolean isActivebrand(Long id,String tenantId){
    Brands brands=brandRepository.findByTenantIdAndIdAndBrandStatus(tenantId,id,BrandStatus.ACTIVE);
    if(brands!=null ){
        return true;
    }
    return false;
    }

    public Map<Long, String> getBrandNameAndId(String tenantId) {
        List<Brands> brands = brandRepository.findAllByTenantId(tenantId);

        Map<Long, String> map = new HashMap<>();

        for (Brands brands1 : brands) {
            map.put(brands1.getId(), brands1.getBrandName());
        }

        return map;
    }


    public String toggleStatus(CustomUserDetail userDetail, BransStatusToggle bransStatusToggle, String tenantid) {

        if (tenantid == null || tenantid.isBlank()) {
            throw new VendorRequestException(
                    HttpStatus.BAD_REQUEST,
                    "Tenant ID is required"
            );
        }
        long count=productRepository.findCountOfBrandInTenant(bransStatusToggle.getId(),tenantid);
        if(count>0){
            throw new ProductAlreadyExist("Product Exist In this Brand..Kindly Detag first");
        }
        Brands brands=brandRepository.findByTenantIdAndId(tenantid, bransStatusToggle.getId());
        String prevStats=brands.getBrandStatus().toString().toLowerCase();
        if(bransStatusToggle.getStatus()!=null){
            if(bransStatusToggle.getStatus().equalsIgnoreCase("INACTIVE")){
                brands.setBrandStatus(BrandStatus.INACTIVE);
            }else{
                brands.setBrandStatus(BrandStatus.ACTIVE);
            }


            brands.setUpdatedAt(LocalDateTime.now());
            brands.setUpdatedBy(userDetail.getId());
            brandRepository.save(brands);
            return "Status Updated from "+prevStats+" to "+bransStatusToggle.getStatus().toString();
        }

        throw new ProductAlreadyExist("Status Cannot be null");
    }
}