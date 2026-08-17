package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.VendorDetailsResponseDto;
import com.e_commerce.eCommerce.dto.VendorRequestDto;
import com.e_commerce.eCommerce.dto.VendorResponseDto;
import com.e_commerce.eCommerce.service.VendorService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/s1/")
public class VendorController {
    private static final Logger logger = LoggerFactory.getLogger(VendorController.class);

    @Autowired
    private VendorService vendorService;

    @PostMapping("v1/super/admin/createVendors")
    public ResponseEntity<ApiResponse> createVendor(
            @RequestBody VendorRequestDto vendorRequestDto, HttpServletRequest request) {

        try {

            String requests = request.getServerName();
            vendorService.createVendor(vendorRequestDto, requests);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(
                            true,
                            "Vendor created successfully."
                    ));

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while Vendor Saving :: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(
                            false,
                            e.getMessage()
                    ));
        }
    }

    @GetMapping("/v1/super/admin/getAllVendor")
    public ResponseEntity<List<VendorResponseDto>> getAllVendors() {
        try {
            List<VendorResponseDto> vendorResponseDtoList = vendorService.getAllVendors();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(vendorResponseDtoList);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/v1/super/admin/vendor/{id}")
    public ResponseEntity<VendorDetailsResponseDto> getVendorDetails(
            @PathVariable Long id) {


        VendorDetailsResponseDto response = vendorService.getVendorDetails(id);

        return ResponseEntity.ok(response);
    }


}