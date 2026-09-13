
        package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.response.WishlistResponseDto;
import com.e_commerce.eCommerce.entity.Product;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.entity.Wishlist;
import com.e_commerce.eCommerce.repository.ProductRepository;
import com.e_commerce.eCommerce.repository.UserRepos;
import com.e_commerce.eCommerce.repository.WiShlistRepositorye;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistService {

    private final WiShlistRepositorye wiShlistRepositorye;
    private final UserRepos userRepos;
    private final ProductRepository productRepository;

    public WishlistResponseDto addToWishlist(
            Long productId,
            CustomUserDetail userDetail) {

        if (userDetail == null || userDetail.getId() == null) {
            throw new RuntimeException("Please login again");
        }

        if (productId == null) {
            throw new RuntimeException("Product ID is required");
        }

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Tenant information not found");
        }

        Long userId = userDetail.getId();

        User user = userRepos.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getTenantId() == null
                || !tenantId.equals(product.getTenantId())) {

            throw new RuntimeException(
                    "Product does not belong to current tenant");
        }

        if (wiShlistRepositorye
                .existsByUserIdAndProductIdAndTenantId(
                        userId,
                        productId,
                        tenantId)) {

            throw new RuntimeException(
                    "Product already in wishlist");
        }

        Wishlist wishlist = new Wishlist();

        wishlist.setUser(user);
        wishlist.setProduct(product);
        wishlist.setTenantId(tenantId);

        Wishlist savedWishlist =
                wiShlistRepositorye.save(wishlist);

        return mapToResponse(savedWishlist);
    }

    public void removeFromWishlist(
            Long productId,
            CustomUserDetail userDetail) {

        if (userDetail == null || userDetail.getId() == null) {
            throw new RuntimeException("Please login again");
        }

        if (productId == null) {
            throw new RuntimeException("Product ID is required");
        }

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Tenant information not found");
        }

        Long userId = userDetail.getId();

        Wishlist wishlist = wiShlistRepositorye
                .findByUserIdAndProductIdAndTenantId(
                        userId,
                        productId,
                        tenantId);

        wiShlistRepositorye.delete(wishlist);
    }

    @Transactional(readOnly = true)
    public List<WishlistResponseDto> getWishlist(
            CustomUserDetail userDetail) {

        if (userDetail == null || userDetail.getId() == null) {
            throw new RuntimeException("Please login again");
        }

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Tenant information not found");
        }

        Long userId = userDetail.getId();

        List<Wishlist> wishlists =
                wiShlistRepositorye
                        .findAllByUserIdAndTenantIdOrderByCreatedAtDesc(
                                userId,
                                tenantId);

        return wishlists.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isWishlisted(
            Long productId,
            CustomUserDetail userDetail) {

        if (userDetail == null || userDetail.getId() == null) {
            throw new RuntimeException("Please login again");
        }

        if (productId == null) {
            throw new RuntimeException("Product ID is required");
        }

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Tenant information not found");
        }

        return wiShlistRepositorye
                .existsByUserIdAndProductIdAndTenantId(
                        userDetail.getId(),
                        productId,
                        tenantId);
    }

    private WishlistResponseDto mapToResponse(
            Wishlist wishlist) {

        WishlistResponseDto response =
                new WishlistResponseDto();

        response.setId(wishlist.getId());

        if (wishlist.getProduct() != null) {

            Product product = wishlist.getProduct();

            response.setProductId(product.getId());
            response.setProductName(product.getProductName());
            response.setProductImage(product.getProductImage());
            response.setPrice(product.getSellingPrice());
        }

        response.setCreatedAt(wishlist.getCreatedAt());

        return response;
    }
}
