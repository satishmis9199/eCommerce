package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.entity.AddToCartRequestDto;
import com.e_commerce.eCommerce.service.CartsService;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor

@RequestMapping("/user")
public class CartController {

    private final CartsService cartService;


    @PostMapping("/u1/v1/cartItems")
    public ResponseEntity<ApiResponse<?>> addToCart(@RequestBody AddToCartRequestDto addToCartRequestDto, @AuthenticationPrincipal CustomUserDetail userDetail) {
        try {
            String message = cartService.addToCart(userDetail, addToCartRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponse<>(
                            true,
                            message
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse<>(
                            false,
                            e.getMessage()
                    )
            );
        }

    }

    @GetMapping("/u1/v1/cartItems")
    public ResponseEntity<ApiResponse<CartResponseDTO>> loadCartItems(@AuthenticationPrincipal CustomUserDetail userDetail) {
        try {
            CartResponseDTO cartResponseDTO = cartService.loadCartItems(userDetail);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponse<>(
                            true,
                            "Cart Items Loaded SuccessFully",
                            cartResponseDTO
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse<>(
                            false,
                            "error While Loading ",
                            null
                    )
            );
        }
    }

    @PostMapping("/u1/v1/address")
    public ResponseEntity<ApiResponse<AddressResponseDTO>> saveAdress(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestBody AddressRequestDTO addressRequestDTO) {
        try {
            AddressResponseDTO addressResponseDTO = cartService.saveAdress(userDetail, addressRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Adresss Saved SuccessFully",
                                    addressResponseDTO

                            )
                    );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage()
                            )
                    );
        }
    }


    @GetMapping("/u1/v1/address")
    public ResponseEntity<ApiResponse<List<AddressResponseDTO>>> getAddress(@AuthenticationPrincipal CustomUserDetail userDetail) {
        try {
            List<AddressResponseDTO> addressResponseDTO = cartService.getAllAdress(userDetail);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Adresss Fetched SuccessFully",
                                    addressResponseDTO


                            )
                    );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage()
                            )
                    );
        }
    }

    @PostMapping("/u1/v1/checkout")
    public ResponseEntity<ApiResponse<CheckoutResponseDTO>> doCheckOut(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestBody CheckoutRequestDTO checkoutRequestDTO) {

        try {
            CheckoutResponseDTO checkoutResponseDTO = cartService.placeOrder(userDetail, checkoutRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Order Placed Successfully"
                                    , checkoutResponseDTO

                            )
                    );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage()
                            )
                    );
        }
    }

    @PostMapping("/u1/v1/payments/verify")
    public ResponseEntity<ApiResponse<?>> verifyPayment(@RequestBody VerifyPaymentRequestDto verifyPaymentRequestDto) {
        try {
            cartService.verifyOrderPayment(verifyPaymentRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponse<>(
                            true,
                            "Payment is Success"
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse<>(
                            true,
                            e.getMessage()
                    )
            );
        }

    }


    @DeleteMapping("/u1/v1/cart/items/{itemId}")
    public ResponseEntity<ApiResponse<?>> removeCartItems(@PathVariable Long itemId, @AuthenticationPrincipal CustomUserDetail userDetail) {
        try {
            String message = cartService.removeCartItems(itemId, userDetail);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponse<>(
                            true,
                            message
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse<>(
                            true,
                            e.getMessage()
                    )
            );
        }

    }
}
