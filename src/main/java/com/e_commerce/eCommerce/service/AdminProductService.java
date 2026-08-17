package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.entity.CategorySpecification;
import com.e_commerce.eCommerce.entity.CategoryStatus;
import com.e_commerce.eCommerce.entity.ProductCategory;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.repository.CategoryRepository;
import com.e_commerce.eCommerce.repository.CategorySpecificationRepository;
import com.e_commerce.eCommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategorySpecificationRepository categorySpecificationRepository;

    @Transactional
    public CategoryResponseDTO createCategory(CustomUserDetail userDetail,
                                              CategoryRequestDTO categoryRequestDTO) {
        String tenanatId = TenantContext.getTenantId();


        User user = userDetail.getUser();

        if (categoryRepository.existsByTenantIdAndCategoryNameIgnoreCase(
                TenantContext.getTenantId(),
                categoryRequestDTO.getCategoryName())) {

            throw new RuntimeException("Category already exists.");
        }

        ProductCategory productCategory = ProductCategory.builder()
                .tenantId(TenantContext.getTenantId())
                .vendorId(user.getVendorId())
                .categoryName(categoryRequestDTO.getCategoryName())
                .description(categoryRequestDTO.getDescription())
                .imageUrl(categoryRequestDTO.getImageUrl())
                .status(categoryRequestDTO.getStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ProductCategory savedCategory = categoryRepository.save(productCategory);

        // Save Specifications
        if (categoryRequestDTO.getSpecifications() != null
                && !categoryRequestDTO.getSpecifications().isEmpty()) {

            List<CategorySpecification> specifications = categoryRequestDTO.getSpecifications()
                    .stream()
                    .map(specDto -> CategorySpecification.builder()
                            .categoryId(savedCategory.getId())
                            .specificationName(specDto.getSpecificationName())
                            .required(Boolean.TRUE.equals(specDto.getRequired()))
                            .displayOrder(specDto.getDisplayOrder())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .tenantId(tenanatId)
                            .vendorId(user.getVendorId())
                            .build())
                    .toList();

            categorySpecificationRepository.saveAll(specifications);
        }

        return CategoryResponseDTO.builder()
                .id(savedCategory.getId())
                .categoryName(savedCategory.getCategoryName())
                .description(savedCategory.getDescription())
                .imageUrl(savedCategory.getImageUrl())
                .status(savedCategory.getStatus())
                .createdAt(savedCategory.getCreatedAt())
                .updatedAt(savedCategory.getUpdatedAt())
                .build();
    }

    @Transactional
    public List<CategoryResponseDTO> getAllCategory(CustomUserDetail userDetail) {

        User user = userDetail.getUser();

        String tenantId = TenantContext.getTenantId();

        if (!tenantId.equalsIgnoreCase(user.getTenantId())) {
            throw new RuntimeException("Invalid Tenant");
        }

        List<ProductCategory> productCategories =
                categoryRepository.findAllByTenantIdAndVendorId(
                        tenantId,
                        user.getVendorId());

        List<CategoryResponseDTO> response = new ArrayList<>();

        for (ProductCategory productCategory : productCategories) {

            int productCount = productRepository.countByCategoryIdAndTenantId(
                    productCategory.getId(),
                    tenantId
            );

            List<CategorySpecification> specifications =
                    categorySpecificationRepository.findByCategoryIdAndTenantId(
                            productCategory.getId(),
                            tenantId
                    );

            List<CategorySpecificationRequestDTO> specificationResponse =
                    specifications.stream()
                            .map(spec -> CategorySpecificationRequestDTO.builder()
                                    .id(spec.getId())
                                    .specificationName(spec.getSpecificationName())
                                    .required(spec.isRequired())
                                    .displayOrder(spec.getDisplayOrder())
                                    .build())
                            .toList();

            CategoryResponseDTO dto = CategoryResponseDTO.builder()
                    .id(productCategory.getId())
                    .categoryName(productCategory.getCategoryName())
                    .description(productCategory.getDescription())
                    .productCount(productCount)
                    .imageUrl(productCategory.getImageUrl())
                    .status(productCategory.getStatus())
                    .createdAt(productCategory.getCreatedAt())
                    .updatedAt(productCategory.getUpdatedAt())
                    .specifications(specificationResponse)
                    .build();

            response.add(dto);
        }

        return response;
    }

    @Transactional
    public void updateCategory(Long categoryId,
                               CategoryRequestDTO dto,
                               CustomUserDetail userDetail) {

        User user = userDetail.getUser();

        String tenantId = TenantContext.getTenantId();

        // Validate Tenant
        if (!tenantId.equalsIgnoreCase(user.getTenantId())) {
            throw new RuntimeException("Invalid Tenant.");
        }

        // Find Category
        ProductCategory category = categoryRepository
                .findByIdAndVendorId(categoryId, user.getVendorId())
                .orElseThrow(() ->
                        new RuntimeException("Category does not exist."));

        // Duplicate Category Check
        boolean exists = categoryRepository
                .existsByTenantIdAndCategoryNameIgnoreCaseAndIdNot(
                        tenantId,
                        dto.getCategoryName(),
                        categoryId);

        if (exists) {
            throw new RuntimeException("Category already exists.");
        }

        // Update Data
        category.setCategoryName(dto.getCategoryName());
        category.setDescription(dto.getDescription());
        category.setStatus(dto.getStatus());

        // Update Image only if new image is uploaded
        if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
            category.setImageUrl(dto.getImageUrl());

        }

        category.setUpdatedAt(LocalDateTime.now());

        categoryRepository.save(category);
    }

    @Transactional
    public void updateCategoryStatus(UpdateCategoryDTO updateCategoryDTO, CustomUserDetail userDetail) {
        User user = userDetail.getUser();
        String tenant = TenantContext.getTenantId();
        if (!tenant.equalsIgnoreCase(user.getTenantId())) {
            throw new RuntimeException("Tenant Not Found");
        }
        ProductCategory productCategory = categoryRepository.findByIdAndVendorIdAndTenantId(updateCategoryDTO.getId(),
                user.getVendorId(), tenant
        );
        if (productCategory == null) {
            throw new RuntimeException("Category Does not Exist");

        }
        productCategory.setStatus(updateCategoryDTO.getStatus());
        categoryRepository.save(productCategory);
    }

    public String deleteCategory(Long categoryId, CustomUserDetail userDetail) {
        User user = userDetail.getUser();
        String tenant = TenantContext.getTenantId();
        if (!tenant.equalsIgnoreCase(user.getTenantId())) {
            throw new RuntimeException("Tenant Not Found");
        }
        int count = productRepository.countByCategoryIdAndTenantIdAndVendorId(categoryId, tenant, user.getVendorId());
        if (count > 0) {
            throw new RuntimeException("There Are Already " + count + " product Mapped exists...Kindly move them Manually into some Other Category ");
        }
        ProductCategory productCategory = categoryRepository.findByIdAndVendorIdAndTenantId(categoryId, user.getVendorId(), tenant);
        if (productCategory == null) {
            throw new RuntimeException("Category doenot exist");

        }
        categoryRepository.delete(productCategory);
        return "Category Deleted Succesfully";


    }

    @Transactional
    public String moveAndDeleteCategory(CustomUserDetail userDetail,
                                        MoveAndDeleteDto dto) {

        User user = userDetail.getUser();

        String tenantId = TenantContext.getTenantId();

        if (!tenantId.equalsIgnoreCase(user.getTenantId())) {
            throw new RuntimeException("Invalid Tenant.");
        }

        if (dto.getOldCategoryId().equals(dto.getNewCategoryId())) {
            throw new RuntimeException("Old Category and New Category cannot be same.");
        }

        ProductCategory oldCategory = categoryRepository
                .findByIdAndVendorId(dto.getOldCategoryId(), user.getVendorId())
                .orElseThrow(() ->
                        new RuntimeException("Old Category does not exist."));

        ProductCategory newCategory = categoryRepository
                .findByIdAndVendorId(dto.getNewCategoryId(), user.getVendorId())
                .orElseThrow(() ->
                        new RuntimeException("Destination Category does not exist."));

        if (newCategory.getStatus() != CategoryStatus.ACTIVE) {
            throw new RuntimeException("Destination Category is inactive.");
        }

        int productCount = productRepository.countByCategoryIdAndTenantId(
                oldCategory.getId(),
                tenantId);

        if (productCount == 0) {
            throw new RuntimeException("No products found in this category.");
        }

        productRepository.moveProductsToCategory(
                newCategory.getId(),
                oldCategory.getId(),
                tenantId,
                user.getVendorId());

        categoryRepository.delete(oldCategory);

        return productCount + " Products moved successfully. Category deleted successfully.";
    }
}
