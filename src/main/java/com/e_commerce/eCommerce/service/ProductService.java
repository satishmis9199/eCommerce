package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.R2Properties;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.entity.*;
import com.e_commerce.eCommerce.repository.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final VendorRepository vendorRepos;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CategorySpecificationRepository categorySpecificationRepository;
    private final ProductSpecificationValueRepository productSpecificationValueRepository;
    private final CartsService cartsService;
    private final VendorRepository vendorRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepos userRepos;
    private final FlashSaleReporsitory flashSaleReporsitory;
    private final FlashSaleItemRepository flashSaleItemRepository;
    private final ProductReviewRepository productReviewRepository;

    private final R2Properties r2Properties;
    @Transactional
    public String addProduct(ProductRequestDTO dto,
                             CustomUserDetail userDetail) {

        User user = userDetail.getUser();

        String tenantId = TenantContext.getTenantId();

        if (!tenantId.equals(user.getTenantId())) {
            throw new RuntimeException("Invalid Tenant");
        }
        System.out.println(" VendorIdd "+user.getVendorId());
        System.out.println("Category Id is {}"+dto.getCategoryId());
        ProductCategory category = categoryRepository
                .findByIdAndVendorId(dto.getCategoryId(), user.getVendorId())
                .orElseThrow(() -> new RuntimeException("Category does not exist."));

        Vendor vendor = vendorRepos.findById(user.getVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor Not Found"));

        LocalDateTime now = LocalDateTime.now();

        Product product = new Product();
        product.setTenantId(tenantId);
        product.setVendorId(vendor.getId());
        product.setCategoryId(category.getId());
        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setSellingPrice(dto.getSellingPrice());
        product.setMrp(dto.getMrp());
        product.setStockQuantity(dto.getStockQuantity());
        product.setUnit(dto.getUnit());
        product.setProductImage(dto.getProductImage());
        product.setStatus(dto.getStatus());
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        product.setCreatedBy(userDetail.getId());
        product.setUpdatedBy(userDetail.getId());
        product.setFeatured(Boolean.TRUE.equals(dto.getFeatured()));

        Product savedProduct = productRepository.save(product);

        List<ProductSpecificationRequestDto> specifications = dto.getSpecifications();

        if (specifications != null && !specifications.isEmpty()) {

            Set<Long> specificationIds = new HashSet<>();
            List<ProductSpecificationValue> specificationValues = new ArrayList<>();

            for (ProductSpecificationRequestDto specificationDto : specifications) {

                if (!specificationIds.add(specificationDto.getCategorySpecificationId())) {
                    throw new RuntimeException("Duplicate specification found.");
                }

                CategorySpecification specification =
                        categorySpecificationRepository
                                .findByIdAndCategoryIdAndTenantId(
                                        specificationDto.getCategorySpecificationId(),
                                        category.getId(),
                                        tenantId);
                if(specification==null){
                    throw new RuntimeException("Invalid Value");
                }

                ProductSpecificationValue value = new ProductSpecificationValue();

                value.setTenantId(tenantId);
                value.setVendorId(user.getVendorId());

                value.setProduct(savedProduct);


                value.setCategorySpecification(specification);



                value.setValue(specificationDto.getValue());

                value.setCreatedAt(now);
                value.setUpdatedAt(now);
                value.setCreatedBy(userDetail.getId());
                value.setUpdatedBy(userDetail.getId());

                specificationValues.add(value);
            }

            productSpecificationValueRepository.saveAll(specificationValues);
        }

        return "Product Added Successfully";
    }
    public List<ProductResponseDTO> loadAllProductsAdmin(
            CustomUserDetail userDetail) {

        User user = userDetail.getUser();
        String tenantId = TenantContext.getTenantId();
        log.error("tenant form Thread"+tenantId);

        if (!tenantId.equalsIgnoreCase(user.getTenantId())) {
            throw new RuntimeException("Invalid Tenant");
        }

        List<ProductResponseDTO> products = productRepository.loadAllProducts(
                tenantId,
                user.getVendorId());

        for (ProductResponseDTO dto : products) {

            List<ProductSpecificationValue> values =
                    productSpecificationValueRepository
                            .findByProductIdAndTenantId(dto.getId(), tenantId);

            List<ProductSpecificationResponeDto> specs = values.stream()
                    .map(value -> {
                        ProductSpecificationResponeDto spec =
                                new ProductSpecificationResponeDto();

                        spec.setCategorySpecificationId(
                                value.getCategorySpecification().getId());

                        spec.setSpecificationName(
                                value.getCategorySpecification().getSpecificationName());

                        spec.setValue(value.getValue());

                        return spec;
                    })
                    .toList();

            dto.setSpecifications(specs);
        }

        return products;
    }

    @Transactional
    public String updateProduct(Long id,
                                ProductRequestDTO dto,
                                CustomUserDetail userDetail) {

        String tenantId = TenantContext.getTenantId();
        User user = userDetail.getUser();

        if (!tenantId.equalsIgnoreCase(user.getTenantId())) {
            throw new RuntimeException("Vendor does not exist.");
        }


        Product product = productRepository.findByIdAndTenantIdAndVendorId(
                id,
                tenantId,
                user.getVendorId());

        if (product == null) {
            throw new RuntimeException("Product does not exist.");
        }

        ProductCategory category = categoryRepository
                .findByIdAndVendorId(dto.getCategoryId(), user.getVendorId())
                .orElseThrow(() -> new RuntimeException("Category does not exist."));
        if(category.getId()!=dto.getCategoryId()){
            throw new RuntimeException("Category cannot be Changed ...");
        }

        if (dto.getMrp() != null &&
                dto.getSellingPrice().compareTo(dto.getMrp()) > 0) {
            throw new RuntimeException("Selling Price cannot be greater than MRP.");
        }

        product.setCategoryId(category.getId());
        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setSellingPrice(dto.getSellingPrice());
        product.setMrp(dto.getMrp());
        product.setStockQuantity(dto.getStockQuantity());
        product.setUnit(dto.getUnit());
        product.setStatus(dto.getStatus());
        product.setFeatured(Boolean.TRUE.equals(dto.getFeatured()));
//        System.out.print(product.setFeatured(dto.getFeatured());
        if (dto.getProductImage() != null &&
                !dto.getProductImage().isBlank()) {
            product.setProductImage(dto.getProductImage());
        }
        LocalDateTime now = LocalDateTime.now();
        product.setUpdatedAt(now);
        product.setUpdatedBy(userDetail.getId());


        List<CategorySpecification> categorySpecifications =
                categorySpecificationRepository.findByCategoryIdAndTenantId(
                        category.getId(),
                        tenantId);

        Map<Long, CategorySpecification> specificationMap =
                categorySpecifications.stream()
                        .collect(Collectors.toMap(
                                CategorySpecification::getId,
                                Function.identity()
                        ));

        for (ProductSpecificationRequestDto specificationDto : dto.getSpecifications()) {

            CategorySpecification categorySpecification =
                    specificationMap.get(specificationDto.getCategorySpecificationId());

            if (categorySpecification == null) {
                throw new RuntimeException(
                        "Invalid Specification Id : "
                                + specificationDto.getCategorySpecificationId());
            }

            ProductSpecificationValue productSpecificationValue =
                    productSpecificationValueRepository
                            .findByProductIdAndCategorySpecificationIdAndTenantId(
                                    product.getId(),
                                    specificationDto.getCategorySpecificationId(),
                                    tenantId);

            if (productSpecificationValue == null) {

                productSpecificationValue = new ProductSpecificationValue();

                productSpecificationValue.setTenantId(tenantId);
                productSpecificationValue.setVendorId(user.getVendorId());
                productSpecificationValue.setProduct(product);
                productSpecificationValue.setCategorySpecification(categorySpecification);
                productSpecificationValue.setCreatedAt(now);
                productSpecificationValue.setCreatedBy(userDetail.getId());
            }

            productSpecificationValue.setValue(specificationDto.getValue());
            productSpecificationValue.setUpdatedAt(now);
            productSpecificationValue.setUpdatedBy(userDetail.getId());

            productSpecificationValueRepository.save(productSpecificationValue);
        }

        productRepository.save(product);

        return "Product Updated Successfully.";
    }

    public String deleteProduct(CustomUserDetail userDetail, Long id) {
        String tenantId = TenantContext.getTenantId();
        User user = userDetail.getUser();

        if (!tenantId.equalsIgnoreCase(user.getTenantId())) {
            throw new RuntimeException("Vendor does not exist.");
        }

        Product product = productRepository
                .findByIdAndTenantIdAndVendorId(
                        id,
                        tenantId,
                        user.getVendorId());

        if (product == null) {
            throw new RuntimeException("Product does not exist.");
        }
        productRepository.delete(product);
        return "Product Deleted Successfully";
    }

    public ProductResponseDTO getProductById(Long id) {

        String tenantId = TenantContext.getTenantId();

        Product product = productRepository
                .findByIdAndTenantIdAndStatus(id, tenantId, ProductStatus.ACTIVE);

        if (product == null) {
            throw new RuntimeException("Product not found.");
        }

        ProductCategory productCategory = categoryRepository
                .findByIdAndTenantIdAndStatus(
                        product.getCategoryId(),
                        tenantId,CategoryStatus.ACTIVE

                );

        List<ProductSpecificationValue> productSpecificationValues =
                productSpecificationValueRepository
                        .findByProductIdAndTenantId(id, tenantId);

        List<ProductSpecificationResponeDto> specificationDtos =
                productSpecificationValues.stream()
                        .map(value -> ProductSpecificationResponeDto.builder()
                                .categorySpecificationId(value.getCategorySpecification().getId())
                                .value(value.getValue())
                                .build())
                        .toList();

        return ProductResponseDTO.builder()
                .id(product.getId())
                .categoryId(product.getCategoryId())
                .categoryName(productCategory != null ? productCategory.getCategoryName() : null)
                .productName(product.getProductName())
                .sellingPrice(product.getSellingPrice())
                .mrp(product.getMrp())
                .stockQuantity(product.getStockQuantity())
                .unit(product.getUnit())
                .productImage(product.getProductImage())
                .status(ProductStatus.ACTIVE)
                .description(product.getDescription())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .specifications(specificationDtos)
                .build();
    }


    public ProductResponseDTOs findByProductId(Long productId) {

        // =========================================================
        // 1. TENANT
        // =========================================================
        String tenantId = TenantContext.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Invalid Tenant");
        }

        // =========================================================
        // 2. VENDOR
        // =========================================================
        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor Does Not Exist")
                );

        // =========================================================
        // 3. PRODUCT
        // =========================================================
        Product product = productRepository
                .findByIdAndTenantIdAndStatus(
                        productId,
                        tenantId,
                        ProductStatus.ACTIVE
                );

        if (product == null) {
            throw new RuntimeException("Product Does Not Exist");
        }

        // =========================================================
        // 4. PRODUCT KI CATEGORY ID
        // Product table me already categoryId hai
        // =========================================================
        Long categoryId = product.getCategoryId();

        if (categoryId == null) {
            throw new RuntimeException(
                    "Category is not assigned to this product"
            );
        }

        // =========================================================
        // 5. PRODUCT CATEGORY
        // =========================================================
        ProductCategory category = categoryRepository
                .findByIdAndVendorIdAndTenantId(
                        categoryId,
                        vendor.getId(),
                        tenantId
                );

        if (category == null) {
            throw new RuntimeException(
                    "Product Category Does Not Exist"
            );
        }

        // =========================================================
        // 6. PRODUCT RESPONSE
        // =========================================================
        ProductResponseDTOs response = new ProductResponseDTOs();

        response.setProductId(product.getId());
        response.setName(product.getProductName());

        response.setBrand(vendor.getStoreName());
        response.setVendorId(vendor.getId());
        response.setBusinessName(vendor.getBussinessName());

        response.setImage(r2Properties.getPublicUrl()+"/"+product.getProductImage());
        response.setImages(null);

        response.setRating(4.4);
        response.setReviewCount(50);

        response.setPrice(product.getSellingPrice());
        response.setOldPrice(product.getMrp());

        // =========================================================
        // 7. DISCOUNT
        // =========================================================
        if (product.getSellingPrice() != null
                && product.getMrp() != null
                && product.getMrp().compareTo(BigDecimal.ZERO) > 0) {

            response.setDiscountPercent(
                    cartsService.findDiscount(
                            product.getSellingPrice(),
                            product.getMrp()
                    )
            );

        } else {
            response.setDiscountPercent(BigDecimal.ZERO);
        }

        // =========================================================
        // 8. STOCK LEVEL
        // =========================================================
        if (product.getStockQuantity() == null
                || product.getStockQuantity() <= 0) {

            response.setStockLevel("out_of_stock");

        } else if (product.getStockQuantity() <= 10) {

            response.setStockLevel("low_stock");

        } else {

            response.setStockLevel("in_stock");
        }

        response.setDeliveryEta("Delivery Within 0-1 Days");
        response.setDescription(product.getDescription());

        // =========================================================
        // 9. CATEGORY KE SAARE SPECIFICATIONS
        //
        // Example:
        // categoryId = 1 (Bricks)
        //
        // Brand
        // Grade
        // Size
        // Weight
        // =========================================================
        List<CategorySpecification> categorySpecifications =
                categorySpecificationRepository
                        .findByCategoryIdAndVendorIdAndTenantIdAndStatusOrderByDisplayOrderAsc(
                                category.getId(),
                                vendor.getId(),
                                tenantId,
                                CategoryStatus.ACTIVE
                        );

        List<ProductSpecificationResponeDto> specificationResponse =
                new ArrayList<>();

        // =========================================================
        // 10. HAR CATEGORY SPECIFICATION KI PRODUCT VALUE
        // =========================================================
        if (categorySpecifications != null
                && !categorySpecifications.isEmpty()) {

            for (CategorySpecification specification
                    : categorySpecifications) {

                if (specification == null
                        || specification.getId() == null) {
                    continue;
                }



                ProductSpecificationValue value =
                        productSpecificationValueRepository
                                .findByProductIdAndCategorySpecificationIdAndTenantId(
                                        product.getId(),
                                        specification.getId(),
                                        tenantId
                                );

                if (value == null) {
                    continue;
                }

                ProductSpecificationResponeDto specificationDto =
                        new ProductSpecificationResponeDto();

                specificationDto.setCategorySpecificationId(
                        specification.getId()
                );

                specificationDto.setSpecificationName(
                        specification.getSpecificationName()
                );

                specificationDto.setValue(
                        value.getValue()
                );

                specificationResponse.add(specificationDto);
            }
        }

        // =========================================================
        // 11. SET SPECIFICATIONS
        // =========================================================
        response.setProductSpecificationResponeDtos(
                specificationResponse
        );

        return response;
    }

    public List<RelatedProductDTO> getReleatedproducts(Long productId) {
        String tenantId=TenantContext.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Invalid Tenant");
        }

        // =========================================================
        // 2. VENDOR
        // =========================================================
        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor Does Not Exist")
                );
        Product product=productRepository.findByIdAndTenantIdAndStatus(productId,tenantId,ProductStatus.ACTIVE);
        Long categoryId=product.getCategoryId();
        List<Product> relatedProducts =
                productRepository
                        .findTop4ByTenantIdAndVendorIdAndCategoryIdAndStatusAndIdNotOrderByTotalSoldDesc(
                                tenantId,
                                vendor.getId(),
                               categoryId,
                                ProductStatus.ACTIVE,
                                product.getId()
                        );
        List<RelatedProductDTO> relatedProductDTOList=new ArrayList<>();
       for(Product product1:relatedProducts){
           RelatedProductDTO relatedProductDTO=new RelatedProductDTO();
           relatedProductDTO.setProductId(product1.getId());
           relatedProductDTO.setName(product1.getProductName());
           relatedProductDTO.setBrand(vendor.getStoreName());
           relatedProductDTO.setImage(r2Properties.getPublicUrl()+"/"+product1.getProductImage());
           relatedProductDTO.setPrice(product1.getSellingPrice());
           relatedProductDTO.setOldPrice(product1.getMrp());
           relatedProductDTO.setDiscountPercent(cartsService.findDiscount(product1.getSellingPrice(),product1.getMrp()));
           relatedProductDTO.setReviewCount(50);
           if(product1.getStockQuantity()>=10) {
               relatedProductDTO.setStockLevel("in_stock");

           }else if(product1.getStockQuantity()==0){
               relatedProductDTO.setStockLevel("out_of_stock");

           }
           else{
               relatedProductDTO.setStockLevel("low_stock");
           }
           relatedProductDTOList.add(relatedProductDTO);


       }
       return relatedProductDTOList;



    }

    public List<OrderResponseDto> getOrerdetail(CustomUserDetail customUserDetail) {
        String tenantId=TenantContext.getTenantId();
        if (customUserDetail==null){
            throw new RuntimeException("Please login");
        }
        if(!customUserDetail.getUser().getRole().equals(Roles.ADMIN)){
            throw new RuntimeException("Unauthorized To access");

        }
        HashMap<Long, HashMap<Long, String>> customerDetail=getAllCustomerWithOrder(tenantId);
        List<Order> orderList=orderRepository.findAllByTenantIdAndReturnStatus(tenantId,ReturnStatus.NONE);
        List<OrderResponseDto> orderResponseDto=new ArrayList<>();
        for(Order order:orderList){
            String customerName = "";

            if (customerDetail != null) {

                HashMap<Long, String> userDetail = customerDetail.get(order.getUserId());

                if (userDetail != null) {
                    customerName = userDetail.getOrDefault(order.getUserId(), "N/A");
                }
            }

            OrderResponseDto orderResponseDto1=new OrderResponseDto();
            List<OrderItemDTo> orderItemsDto=new ArrayList<>();
            orderResponseDto1.setOrderNo(order.getId());
            orderResponseDto1.setCustomer(customerName);
            orderResponseDto1.setOrderId(order.getOrderNumber());
            orderResponseDto1.setDate(String.valueOf(order.getCreatedAt()));
            orderResponseDto1.setStatus(String.valueOf(order.getOrderStatus()));
            orderResponseDto1.setTotal(order.getTotal());
            orderResponseDto1.setPaymentMethod(order.getPaymentMethod());
            orderResponseDto1.setPaymentStatus(order.getPaymentStatus());
            List<OrderItem> orderItems=orderItemRepository.findAllByOrderIdAndTenantId(order.getId(),tenantId);
            for(OrderItem orderItem:orderItems){
                OrderItemDTo orderItemDTo=new OrderItemDTo();
                orderItemDTo.setProductId(orderItem.getProductId());
                orderItemDTo.setName(orderItem.getProductName());
                orderItemDTo.setImage(r2Properties.getPublicUrl()+"/"+orderItem.getImageUrl());
                orderItemDTo.setPrice(orderItem.getSellingPrice());
                if(order.getOrderStatus()==OrderStatus.DELIVERED && order.getReturnStatus()==ReturnStatus.NONE){
                    orderItemDTo.setReview(true);

                }
                else{
                    orderItemDTo.setReview(false);
                }

                orderItemsDto.add(orderItemDTo);
            }
            orderResponseDto1.setOrderItemDToList(orderItemsDto);

            orderResponseDto.add(orderResponseDto1);

        }
        return orderResponseDto;

    }
    private HashMap<Long, HashMap<Long, String>> getAllCustomerWithOrder(String tenantId) {

        Set<Long> ids = new HashSet<>();

        List<Order> orders = orderRepository.findAllByTenantId(tenantId);

        for (Order order : orders) {
            ids.add(order.getUserId());
        }

        List<User> users = userRepos.findAllByIdIn(ids);

        HashMap<Long, HashMap<Long, String>> result = new HashMap<>();

        for (User user : users) {

            HashMap<Long, String> innerMap = new HashMap<>();
            innerMap.put(user.getId(), user.getFirstName() + " " + user.getLastName());

            result.put(user.getId(), innerMap);
        }
        log.error("All User WIth Order :: {}",result);

        return result;
    }

    @Transactional
    public FlashSaleRequestDto createFlashSale(CustomUserDetail userDetail,
                                               FlashSaleRequestDto request) {

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            throw new RuntimeException("Invalid tenant.");
        }

        if (userDetail == null || userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorized access.");
        }

        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RuntimeException("Vendor not found."));
        // Check overlapping flash sale


        if (request.getEndDateTime().isBefore(request.getStartDateTime())
                || request.getEndDateTime().isEqual(request.getStartDateTime())) {
            throw new RuntimeException("End date must be after start date.");
        }

        if (request.getDiscountType() == DiscountType.PERCENTAGE &&
                request.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("Percentage cannot exceed 100.");
        }
        List<FlashSale> conflictingFlashSales =
                flashSaleReporsitory.findConflictingFlashSales(
                        tenantId,
                        vendor.getId(),
                        request.getStartDateTime(),
                        request.getEndDateTime(),
                        FlashSaleStatus.ACTIVE
                );

        if (!conflictingFlashSales.isEmpty()) {

            log.error(
                    "Flash Sale duration conflict. Tenant: {}, Vendor: {}, Start: {}, End: {}",
                    tenantId,
                    vendor.getId(),
                    request.getStartDateTime(),
                    request.getEndDateTime()
            );

            throw new RuntimeException(
                    "Another Flash Sale already exists for the selected duration. Please choose different start and end date."
            );
        }
        FlashSale flashSale = FlashSale.builder()
                .tenantId(tenantId)
                .vendorId(vendor.getId())
                .saleName(request.getSaleName())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .maxDiscountCap(request.getMaxDiscountCap())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .status(request.getStatus())
                .createdBy(userDetail.getId())
                .updatedBy(userDetail.getId())
                .build();

        List<FlashSaleItem> items = new ArrayList<>();

        for (FlashSaleItemDto dto : request.getItems()) {

            Product product = productRepository
                    .findByIdAndTenantIdAndVendorId(
                            dto.getProductId(),
                            tenantId,
                            vendor.getId());
            if(product==null){
                throw new RuntimeException("Product not Found : "+dto.getProductId());
            }

            BigDecimal originalPrice1 = product.getMrp();
            BigDecimal originalPrice=product.getSellingPrice();
            BigDecimal salePrice;

            if (request.getDiscountType() == DiscountType.PERCENTAGE) {

                BigDecimal discount = originalPrice
                        .multiply(request.getDiscountValue())
                        .divide(BigDecimal.valueOf(100));

                if (request.getMaxDiscountCap() != null &&
                        discount.compareTo(request.getMaxDiscountCap()) > 0) {
                    discount = request.getMaxDiscountCap();
                }

                salePrice = originalPrice.subtract(discount);

            } else {

                salePrice = originalPrice.subtract(request.getDiscountValue());

            }

            if (salePrice.compareTo(BigDecimal.ZERO) < 0) {
                salePrice = BigDecimal.ZERO;
            }

            FlashSaleItem item = FlashSaleItem.builder()
                    .flashSale(flashSale)
                    .productId(product.getId())
                    .originalPrice(originalPrice1)
                    .salePrice(salePrice)
                    .build();

            items.add(item);
        }

        flashSale.setItems(items);

        flashSaleReporsitory.save(flashSale);

        return request;
    }

    @Transactional
    public List<FlashSaleResponseDto> getAllFlashSales(CustomUserDetail userDetail) {

        List<FlashSaleResponseDto> response = new ArrayList<>();

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            throw new RuntimeException("Invalid tenant.");
        }

        if (userDetail == null || userDetail.getRole() != Roles.ADMIN) {
            throw new RuntimeException("Unauthorized access.");
        }

        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RuntimeException("Vendor not found."));

        List<FlashSale> flashSales =
                flashSaleReporsitory.findAllByTenantIdAndVendorId(tenantId, vendor.getId());

        if (flashSales.isEmpty()) {
            return response;
        }

        for (FlashSale flashSale : flashSales) {

            FlashSaleResponseDto dto = new FlashSaleResponseDto();

            dto.setId(flashSale.getId());
            dto.setSaleName(flashSale.getSaleName());
            dto.setDescription(flashSale.getDescription());
            dto.setDiscountType(flashSale.getDiscountType());
            dto.setDiscountValue(flashSale.getDiscountValue());
            dto.setMaxDiscountCap(flashSale.getMaxDiscountCap());
            dto.setStartDateTime(flashSale.getStartDateTime());
            dto.setEndDateTime(flashSale.getEndDateTime());
            dto.setStatus(flashSale.getStatus());

            List<FlashSaleItemResponseDto> itemDtos = new ArrayList<>();

            for (FlashSaleItem flashSaleItem : flashSale.getItems()) {

                Product product = productRepository.findByIdAndTenantId(
                        flashSaleItem.getProductId(),
                        tenantId
                );

                if (product == null) {
                    continue;
                }

                BigDecimal originalPrice = product.getMrp();
                BigDecimal salePrice;

//                if (flashSale.getDiscountType() == DiscountType.PERCENTAGE) {
//
//                    BigDecimal discountAmount = originalPrice
//                            .multiply(flashSale.getDiscountValue())
//                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
//
//                    if (flashSale.getMaxDiscountCap() != null
//                            && discountAmount.compareTo(flashSale.getMaxDiscountCap()) > 0) {
//                        discountAmount = flashSale.getMaxDiscountCap();
//                    }
//
//                    salePrice = originalPrice.subtract(discountAmount);
//
//                } else {
//
//                    salePrice = originalPrice.subtract(flashSale.getDiscountValue());
//
//                }
//
//                if (salePrice.compareTo(BigDecimal.ZERO) < 0) {
//                    salePrice = BigDecimal.ZERO;
//                }

                FlashSaleItemResponseDto itemDto = new FlashSaleItemResponseDto();

                itemDto.setProductId(product.getId());
                itemDto.setProductName(product.getProductName());
                itemDto.setOriginalPrice(flashSaleItem.getOriginalPrice());
                itemDto.setSalePrice(flashSaleItem.getSalePrice());

                itemDtos.add(itemDto);
            }

            dto.setItems(itemDtos);

            response.add(dto);
        }

        return response;
    }



    @Transactional
    public String updateFlashSale(CustomUserDetail userDetail,
                                  Long flashSaleId,
                                  FlashSaleRequestDto request) {

        log.info("Updating flash sale. FlashSaleId : {}", flashSaleId);

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            log.error("Tenant id is null.");
            throw new RuntimeException("Invalid tenant.");
        }

        if (userDetail == null || userDetail.getRole() != Roles.ADMIN) {
            log.error("Unauthorized access.");
            throw new RuntimeException("Unauthorized access.");
        }

        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() -> {
                    log.error("Vendor not found. Tenant : {}", tenantId);
                    return new RuntimeException("Vendor not found.");
                });


        FlashSale flashSale = flashSaleReporsitory
                .findByTenantIdAndVendorIdAndId(tenantId, vendor.getId(), flashSaleId);

        if (flashSale == null) {
            log.error("Flash sale not found. Id : {}", flashSaleId);
            throw new RuntimeException("Flash Sale does not exist.");
        }

        if (!request.getEndDateTime().isAfter(request.getStartDateTime())) {
            throw new RuntimeException("End date must be after start date.");
        }

        if (request.getDiscountType() == DiscountType.PERCENTAGE
                && request.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("Percentage discount cannot exceed 100.");
        }
        List<FlashSale> conflictingFlashSales =
                flashSaleReporsitory.findConflictingFlashSalesForUpdate(
                        tenantId,
                        vendor.getId(),
                        request.getStartDateTime(),
                        request.getEndDateTime(),
                        FlashSaleStatus.ACTIVE,
                        flashSaleId
                );

        if (!conflictingFlashSales.isEmpty()) {

            log.error(
                    "Flash Sale duration conflict. Tenant: {}, Vendor: {}, Start: {}, End: {}",
                    tenantId,
                    vendor.getId(),
                    request.getStartDateTime(),
                    request.getEndDateTime()
            );

            throw new RuntimeException(
                    "Another Flash Sale already exists for the selected duration. Please choose different start and end date."
            );
        }
        // Update Master
        flashSale.setSaleName(request.getSaleName());
        flashSale.setDescription(request.getDescription());
        flashSale.setDiscountType(request.getDiscountType());
        flashSale.setDiscountValue(request.getDiscountValue());
        flashSale.setMaxDiscountCap(request.getMaxDiscountCap());
        flashSale.setStartDateTime(request.getStartDateTime());
        flashSale.setEndDateTime(request.getEndDateTime());
        flashSale.setStatus(request.getStatus());
        flashSale.setUpdatedBy(userDetail.getId());

        // Delete old items
        flashSaleItemRepository.deleteAllByFlashSaleId(flashSale.getId());
        flashSaleItemRepository.flush();

        // Clear persistence collection
        flashSale.getItems().clear();

        for (FlashSaleItemDto dto : request.getItems()) {

            Product product = productRepository.findByIdAndTenantIdAndVendorId(
                    dto.getProductId(),
                    tenantId,
                    vendor.getId());

            if (product == null) {
                throw new RuntimeException("Product not found : " + dto.getProductId());
            }

            BigDecimal originalPrice = product.getSellingPrice();
            BigDecimal originalPrice1 = product.getMrp();

            BigDecimal salePrice;

            if (request.getDiscountType() == DiscountType.PERCENTAGE) {

                BigDecimal discount = originalPrice
                        .multiply(request.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                if (request.getMaxDiscountCap() != null
                        && discount.compareTo(request.getMaxDiscountCap()) > 0) {
                    discount = request.getMaxDiscountCap();
                }

                salePrice = originalPrice.subtract(discount);

            } else {

                salePrice = originalPrice.subtract(request.getDiscountValue());

            }

            if (salePrice.compareTo(BigDecimal.ZERO) < 0) {
                salePrice = BigDecimal.ZERO;
            }

            FlashSaleItem item = FlashSaleItem.builder()
                    .flashSale(flashSale)
                    .productId(product.getId())
                    .originalPrice(originalPrice1)
                    .salePrice(salePrice)
                    .build();

            flashSale.getItems().add(item);
        }

        flashSaleReporsitory.saveAndFlush(flashSale);

        log.info("Flash sale updated successfully. FlashSaleId : {}", flashSaleId);

        return "Flash Sale Updated Successfully";
    }
    public String deleteFlashSale(CustomUserDetail userDetail,Long flashSaleId){
        log.info("Updating flash sale. FlashSaleId : {}", flashSaleId);

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            log.error("Tenant id is null.");
            throw new RuntimeException("Invalid tenant.");
        }

        if (userDetail == null || userDetail.getRole() != Roles.ADMIN) {
            log.error("Unauthorized access.");
            throw new RuntimeException("Unauthorized access.");
        }

        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() -> {
                    log.error("Vendor not found. Tenant : {}", tenantId);
                    return new RuntimeException("Vendor not found.");
                });

        FlashSale flashSale = flashSaleReporsitory
                .findByTenantIdAndVendorIdAndId(tenantId, vendor.getId(), flashSaleId);

        if (flashSale == null) {
            log.error("Flash sale not found. Id : {}", flashSaleId);
            throw new RuntimeException("Flash Sale does not exist.");
        }
        flashSaleReporsitory.deleteById(flashSaleId);
        return flashSale.getSaleName()+" deleted Successfully";

    }

    public FlashSaleDashBoardResponseDTO getFlashSaleProduct() {

        String tenantId = TenantContext.getTenantId();
        FlashSaleDashBoardResponseDTO flashSaleDashBoardResponseDTO=new FlashSaleDashBoardResponseDTO();

        if (tenantId == null) {
            log.error("Tenant id is null.");
            throw new RuntimeException("Invalid tenant.");
        }
        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() -> {
                    log.error("Vendor not found. Tenant : {}", tenantId);
                    return new RuntimeException("Vendor not found.");
                });
        LocalDateTime now = LocalDateTime.now();

        FlashSale activeFlashSale = flashSaleReporsitory
                .findByTenantIdAndStatusAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(
                        tenantId,
                        FlashSaleStatus.ACTIVE,
                        now,
                        now
                );

        if (activeFlashSale == null
                || activeFlashSale.getStartDateTime().isAfter(now)) {

            throw new RuntimeException(
                    "No Active Flash Sale"
            );
        }
        LocalDateTime start = activeFlashSale.getStartDateTime();
        LocalDateTime end = activeFlashSale.getEndDateTime();

        long startMillis = start.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        long endMillis = end.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        flashSaleDashBoardResponseDTO.setEndsAt(endMillis);
        flashSaleDashBoardResponseDTO.setStartedAt(startMillis);
        List<FlashSaleItem> flashSaleItems=activeFlashSale.getItems();
        List<ProductDTO> productDTOS=new ArrayList<>();
        for(FlashSaleItem flashSaleItem:flashSaleItems){
            Product product=productRepository.findByIdAndTenantId(flashSaleItem.getProductId(),tenantId);
            ProductDTO productDTO=new ProductDTO();
            productDTO.setProductId(String.valueOf(flashSaleItem.getProductId()));
            productDTO.setName(product.getProductName());
            productDTO.setBrand(vendor.getStoreName());
            productDTO.setVendorId(String.valueOf(vendor.getId()));
            productDTO.setVendorName(vendor.getFirstName());
            productDTO.setBusinessName(vendor.getBussinessName());
            productDTO.setImage(r2Properties.getPublicUrl()+"/"+product.getProductImage());
            productDTO.setImages(Collections.singletonList(r2Properties.getPublicUrl() + "/" + product.getProductImage()));
            productDTO.setDescription(product.getDescription());
            productDTO.setRating(5.0);
            productDTO.setReviewCount(115);
            productDTO.setOldPrice(flashSaleItem.getOriginalPrice());
            productDTO.setPrice(flashSaleItem.getSalePrice());
            productDTO.setDiscountPercent(cartsService.findDiscount(flashSaleItem.getSalePrice(),flashSaleItem.getOriginalPrice()));
            productDTO.setStockLevel(String.valueOf(product.getStockQuantity()));
            productDTO.setDeliveryEta("Next day");

            productDTO.setProductSpecificationResponeDtos(null);
            productDTOS.add(productDTO);
        }
        flashSaleDashBoardResponseDTO.setProducts(productDTOS);
        return flashSaleDashBoardResponseDTO;


    }
   @Transactional
    public String addreviewToProduct(CustomUserDetail userDetail,ReviewRequetDTO reviewRequetDTO,String orderNum) {
        String tenantId= TenantContext.getTenantId();
        if(tenantId==null){
            throw new RuntimeException("No tenant");
        }
        Optional<Vendor> vendor=vendorRepository.findByTenantId(tenantId);
        if(vendor.isEmpty()){
            throw new RuntimeException("Tenant does Not Exists");
        }
        if(userDetail==null){
            throw new RuntimeException("Please Login First");
        }
        String  orderId=orderNum;
        Order order=orderRepository.findByTenantIdAndOrderNumber(tenantId,orderId);
        if(order==null){
            throw new RuntimeException("Order does Not exist");
        }
        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new RuntimeException("You cannot review before order delivery.");
        }

        if (order.getReturnStatus() != ReturnStatus.NONE) {
            throw new RuntimeException("You cannot review this product because a return has been initiated.");
        }
        Long pid= Long.valueOf(reviewRequetDTO.getProductId());
        OrderItem existence=orderItemRepository.findByOrderIdAndProductIdAndTenantId(order.getId(),pid,tenantId);
        if(existence==null){
            throw new RuntimeException("You cnnot review this item..Purchase it to review");
        }
        if(existence.isReview()){
            throw new RuntimeException("Already reviewed");
        }
        existence.setReview(true);

        ProductReview productReview= ProductReview.builder()
                .productId(pid)
                .tenantId(tenantId)
                .vendorId(vendor.get().getId())
                .orderItemId(existence.getId())
                .userId(userDetail.getId())
                .rating(reviewRequetDTO.getRating())
                .reviewTitle("Item Review")
                .reviewText(reviewRequetDTO.getText())
                .status(ReviewStatus.PENDING)
                .build();
        productReviewRepository.save(productReview);
        orderItemRepository.save(existence);
        return "Thanks For Your Review";
    }
}
