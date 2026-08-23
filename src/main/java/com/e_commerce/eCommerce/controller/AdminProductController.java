package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.service.AdminProductService;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vendor")
public class AdminProductController {
    private final ProductService productService;
    private final AdminProductService adminProductService;
    private static final Logger logger = LoggerFactory.getLogger(AdminProductController.class);

    @PostMapping("/s11/v1/product")
    public ResponseEntity<?> addProduct(
            @RequestBody ProductRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        String message = productService.addProduct(dto, userDetail);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", message
        ));
    }

    @GetMapping("/s11/v1/product")
    public ResponseEntity<?> loadProductsForAdmin(
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        List<ProductResponseDTO> products =
                productService.loadAllProductsAdmin(userDetail);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Products fetched successfully.",
                "products", products
        ));
    }

    @PutMapping("/s11/v1/product/{editingProductId}")
    public ResponseEntity<?> editProduct(
            @PathVariable Long editingProductId,
            @RequestBody ProductRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        String message = productService.updateProduct(editingProductId, dto, userDetail);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", message
        ));
    }


    @PostMapping("/s11/v11/create-Category")
    public ResponseEntity<?> createCategory(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestBody CategoryRequestDTO categoryRequestDTO) {
        CategoryResponseDTO c1 = new CategoryResponseDTO();
        try {
            c1 = adminProductService.createCategory(userDetail, categoryRequestDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Category created Successfully",
                    "data", c1
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage(),
                    "data", c1
            ));
        }
    }

    @GetMapping("/s11/v11/create-Category")

    public ResponseEntity<?> getAllCategory(
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        List<CategoryResponseDTO> categories =
                adminProductService.getAllCategory(userDetail);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "categories", categories,
                "message", categories.isEmpty()
                        ? "No categories found."
                        : "Categories fetched successfully."
        ));
    }

    @PutMapping("/s11/v11/create-Category/{categoryId}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody CategoryRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetail userDetail) {
        try {

            adminProductService.updateCategory(categoryId, dto, userDetail);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Category updated successfully."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false,
                    "message", e.getMessage()));
        }
    }

    @PatchMapping("/s11/v11/create-Category/status")
    public ResponseEntity<?> updateStatus(@RequestBody UpdateCategoryDTO updateCategoryDTO, @AuthenticationPrincipal CustomUserDetail userDetail) {
        try {
            adminProductService.updateCategoryStatus(updateCategoryDTO, userDetail);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Status Upated Successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }

    }


    @DeleteMapping("/s11/v11/create-Category/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId, @AuthenticationPrincipal CustomUserDetail userDetail) {
        try {
            String message = adminProductService.deleteCategory(categoryId, userDetail);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", message
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }

    }

    @DeleteMapping("/s11/v1/product/{id}")
    public ResponseEntity<?> deleteProduct(@AuthenticationPrincipal CustomUserDetail userDetail, @PathVariable Long id) {
        try {
            String message = productService.deleteProduct(userDetail, id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", message

            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/s11/v1/category/move-and-delete")
    public ResponseEntity<?> moveAndDelete(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestBody MoveAndDeleteDto moveAndDeleteDto) {
        try {
            String message = adminProductService.moveAndDeleteCategory(userDetail, moveAndDeleteDto);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", message
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/s11/v1/products/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            ProductResponseDTO productResponseDTOS = productService.getProductById(id);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Product fetched Successfully",
                            productResponseDTOS
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(
                            false,
                            "Product not found",
                            null
                    )
            );
        }


    }

    @GetMapping(value = "/s2/v1/allorders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getAdminOrders(@AuthenticationPrincipal CustomUserDetail customUserDetail) {
        try {

            List<OrderResponseDto> orderDetail = productService.getOrerdetail(customUserDetail);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Product fetched Successfully",
                            orderDetail

                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(
                            false,
                            "Order not found",
                            null
                    )
            );
        }
    }


    @PostMapping(value = "/s11/v1/flash-sale", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<?>> createFlashSale(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestBody FlashSaleRequestDto flashSaleRequestDto) {
        try {
            productService.createFlashSale(userDetail, flashSaleRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Flash sale Created Successfully"

                            )
                    );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(),
                                    null
                            )
                    );
        }
    }

    @GetMapping(value = "/s11/v1/flash-sale")
    public ResponseEntity<ApiResponse<List<FlashSaleResponseDto>>> getFlashSaleData(@AuthenticationPrincipal CustomUserDetail userDetail) {
        try {
            List<FlashSaleResponseDto> flashSaleRequestDto1 = productService.getAllFlashSales(userDetail);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Flash sale fetched Successfully",
                                    flashSaleRequestDto1
                            )
                    );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(),
                                    null
                            )
                    );
        }
    }

    @PutMapping(value = "/s11/v1/flash-sale/{flashSaleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<?>> getFlashSaleById(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long flashSaleId, @RequestBody FlashSaleRequestDto flashSaleRequestDto) {
        try {
            String response =
                    productService.updateFlashSale(userDetail, flashSaleId, flashSaleRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    response
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

    @DeleteMapping(value = "/s11/v1/flash-sale/{flashSaleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<?>> getFlashSaleById(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long flashSaleId) {
        try {
            String response =
                    productService.deleteFlashSale(userDetail, flashSaleId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    response
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


}
