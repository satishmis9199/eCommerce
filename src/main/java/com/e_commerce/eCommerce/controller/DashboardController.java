package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.DashBoardService;
import com.e_commerce.eCommerce.service.VendorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vendor")
public class DashboardController {
    private final DashBoardService dashBoardService;
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private final VendorService vendorService;

    public DashboardController(DashBoardService dashBoardService, VendorService vendorService) {
        this.dashBoardService = dashBoardService;
        this.vendorService = vendorService;
    }

    @GetMapping("/s1/v1/load/dashBoard")
    public ResponseEntity<?> getDashBoardData(@AuthenticationPrincipal CustomUserDetail user1) {

        VendorDashboardResponseDTO response = new VendorDashboardResponseDTO();

        try {
            User user = user1.getUser();
            VendorProfileDTO vendorProfileDTO = dashBoardService.loadDashBoardData(user);
            response.setSuccess(true);
            response.setMessage("Vendor Profile has been successfully loaded.");
            response.setVendorProfile(vendorProfileDTO);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            logger.info("Error while loading dashboard", e);

            response.setSuccess(false);
            response.setMessage("Error while loading dashboard data: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/s1/v1/view/profile")
    public ResponseEntity<MyProfileResponseDTO> getProfileData(
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        MyProfileResponseDTO response = new MyProfileResponseDTO();

        try {
            User user = userDetail.getUser();


            MyProfileDTO profile = dashBoardService.getPrrofileData(user);

            response.setSuccess(true);
            response.setMessage("My profile loaded successfully.");
            response.setProfile(profile);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            logger.info("Error while loading profile", e);

            response.setSuccess(false);
            response.setMessage("Error while fetching profile details.");

            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/v1/vendor/editProfile")
    public ResponseEntity<?> editProfile(@RequestBody VendorEditResponse vendorEditResponse, @AuthenticationPrincipal CustomUserDetail userDetail) {
        try {
            User user = userDetail.getUser();
            String message = vendorService.editProfile(vendorEditResponse, user);
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", message
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Errir While Profile Edit"
            ));
        }
    }

    @PostMapping("/s2/v1/change-password")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordChangeDto pas, @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {

            String message = vendorService.changeCurrentUserPassword(pas, userDetail);
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", message
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));

        }
    }

    @GetMapping(value = "/customer", produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CustomerListResponseDTO>>> getAllCustomerInforFornAdmin(@AuthenticationPrincipal CustomUserDetail userDetail) {
        try {
            List<CustomerListResponseDTO> customerDetailDTos = vendorService.findAllCustomer(userDetail);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    "All Customer Of Required Orgg",
                                    customerDetailDTos
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
