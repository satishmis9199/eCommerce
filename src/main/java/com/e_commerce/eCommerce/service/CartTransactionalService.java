package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.entity.*;
import com.e_commerce.eCommerce.repository.CartItemRepository;
import com.e_commerce.eCommerce.repository.CartRepository;
import com.e_commerce.eCommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartTransactionalService {

    private final CartRepository cartRepository;
    private final PercentageService cartsService;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public String addToCartInternal(
            User user,
            AddToCartRequestDto dto,
            String tenantId,
            Vendor vendor) {

        final int ACTIVE = 1;

        Product product =
                productRepository.findByIdAndTenantIdAndVendorId(
                        dto.getProductId(),
                        tenantId,
                        vendor.getId()
                );

        if (product == null) {
            throw new RuntimeException("No Product Exists");
        }

        if (dto.getQuantity() <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than 0"
            );
        }

        if (dto.getQuantity() > product.getStockQuantity()) {
            throw new RuntimeException(
                    "Only " + product.getStockQuantity()
                            + " quantity available"
            );
        }

        Cart cart =
                cartRepository
                        .findByTenantIdAndVendorIdAndUserIdAndRowState(
                                tenantId,
                                vendor.getId(),
                                user.getId(),
                                ACTIVE
                        );

        if (cart == null) {

            cart = Cart.builder()
                    .userId(user.getId())
                    .vendorId(vendor.getId())
                    .tenantId(tenantId)
                    .rowState(ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            cart = cartRepository.save(cart);
            cartRepository.flush();
        }

        Optional<CartItem> existing =
                cartItemRepository
                        .findByCartIdAndProductIdAndRowStateAndTenantId(
                                cart.getId(),
                                product.getId(),
                                ACTIVE,
                                tenantId
                        );

        if (existing.isPresent()) {

            CartItem item = existing.get();

            int newQuantity =
                    item.getQuantity() + dto.getQuantity();

            if (newQuantity > product.getStockQuantity()) {
                throw new RuntimeException(
                        "Only " + product.getStockQuantity()
                                + " quantity available"
                );
            }

            item.setQuantity(newQuantity);
            item.setPrice(product.getSellingPrice());

            item.setTotalPrice(
                    cartsService.findTotalTotalPrice(
                            newQuantity,
                            product.getSellingPrice()
                    )
            );

            item.setUpdatedAt(LocalDateTime.now());

            cartItemRepository.save(item);

            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);

            return "Cart quantity updated";
        }

        CartItem item = CartItem.builder()
                .cartId(cart.getId())
                .userId(user.getId())
                .tenantId(tenantId)
                .productId(product.getId())
                .quantity(dto.getQuantity())
                .price(product.getSellingPrice())
                .totalPrice(
                        cartsService.findTotalTotalPrice(
                                dto.getQuantity(),
                                product.getSellingPrice()
                        )
                )
                .rowState(ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        cartItemRepository.save(item);

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return "Added Into cart";
    }
}