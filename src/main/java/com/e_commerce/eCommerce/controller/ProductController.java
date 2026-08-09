package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.core.Response;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/u1/v1/")
public class ProductController {
    private final ProductService productService;
    @GetMapping("products/{productId}")
    public ResponseEntity<ApiResponse<ProductResponseDTOs>> getProductByProductId(@PathVariable Long productId){
        try{
         ProductResponseDTOs productResponseDTOs=productService.findByProductId(productId);
         return ResponseEntity.ok(
                 new ApiResponse<>(
                         true,
                         "Product Detail fetched",
                         productResponseDTOs
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
    @GetMapping("products/{productId}/related")
    public ResponseEntity<ApiResponse<List<RelatedProductDTO>>> getRelatedProduct(@PathVariable Long productId){
        try{
            List<RelatedProductDTO> relatedProductDTOList=productService.getReleatedproducts(productId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Related Products Fetched",
                                    relatedProductDTOList
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
    @GetMapping("/products/flash-sale")
    public ResponseEntity<ApiResponse<FlashSaleDashBoardResponseDTO>> getFlashProduct(){
        try{
            FlashSaleDashBoardResponseDTO flashSaleDashBoardResponseDTO=productService.getFlashSaleProduct();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "Related Products Fetched",
                                    flashSaleDashBoardResponseDTO
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
    @PostMapping("/orders/{orderId}/items/review")
    public ResponseEntity<ApiResponse<?>> postReview(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestBody ReviewRequetDTO reviewRequetDTO,@PathVariable String orderId){
        try{
            String message=productService.addreviewToProduct(userDetail,reviewRequetDTO,orderId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    message,
                                    null
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

}
