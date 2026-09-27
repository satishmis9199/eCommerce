package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.CustomerDetailDTo;
import com.e_commerce.eCommerce.dto.request.BrandrequestDTO;
import com.e_commerce.eCommerce.dto.request.BransStatusToggle;
import com.e_commerce.eCommerce.dto.response.BrandResponseDTO;
import com.e_commerce.eCommerce.service.BrandService;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.json.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/vendor/s11/v1/brand")
public class BrandController {
    private final BrandService brandService;
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createBrand(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestBody BrandrequestDTO brandrequestDTO){
        String tenantId= TenantContext.getTenantId();
        BrandrequestDTO brandrequestDTO1=brandService.saveBrand(userDetail,brandrequestDTO,tenantId);
        return  ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Brand Has been Created Successfully",
                                brandrequestDTO1
                        )
                );
    }
    @GetMapping
    public ResponseEntity<ApiResponse<?>> loadBrand(@AuthenticationPrincipal CustomUserDetail userDetail){
        String tenantId=TenantContext.getTenantId();
        List<BrandResponseDTO> brandResponseDTOList=brandService.loadbrands(userDetail,tenantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Brands Loaded Successffuly",
                                brandResponseDTOList
                        )
                );
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> editBrands(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id,
            @RequestBody BrandrequestDTO brandrequestDTO) {

        String tenantId = TenantContext.getTenantId();

        BrandrequestDTO updatedBrand =
                brandService.editBrand(userDetail, tenantId, id, brandrequestDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        true,
                        "Brand edited successfully",
                        updatedBrand
                ));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteBrand(@AuthenticationPrincipal CustomUserDetail userDetail,@PathVariable Long id){
        String tenantId =TenantContext.getTenantId();
       String message= brandService.deleteBrand(userDetail,id,tenantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                message,
                                null

                        )
                );
    }
    @PatchMapping("/status")
    public ResponseEntity<ApiResponse<?>> statusToggle(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestBody BransStatusToggle bransStatusToggle){
        String tenantid=TenantContext.getTenantId();
        String message =brandService.toggleStatus(userDetail,bransStatusToggle,tenantid);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                message
                        )
                );
    }
}
