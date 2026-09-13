package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.dto.request.*;
import com.e_commerce.eCommerce.dto.response.VenodorBusinessProfile;
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
                    "message", "Error While Profile Edit"
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/settings/business-profile")
    public ResponseEntity<ApiResponse<VenodorBusinessProfile>> getVendorBussinesProfile(
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {


            VenodorBusinessProfile venodorBusinessProfile =
                    vendorService.loadVendorBusinessProfile(userDetail);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,

                            "Business Data Loaded",
                            venodorBusinessProfile

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
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/settings/business-profile")
    public ResponseEntity<ApiResponse<VenodorBusinessProfile>> SaveVendorBussinesProfile(
            @AuthenticationPrincipal CustomUserDetail userDetail,@RequestBody VenodorBusinessProfile venodorBusinessProfiles) {

        try {


            VenodorBusinessProfile venodorBusinessProfile =
                    vendorService.updatevendorBusinessProfile(userDetail, venodorBusinessProfiles);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,

                            "Business Data Loaded",
                            venodorBusinessProfile

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


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/settings/address")
    public ResponseEntity<ApiResponse<VendorBusinessAddressDTO>> loadAddress(
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {


            VendorBusinessAddressDTO venodorBusinessProfile=vendorService.loadVendorAddress(userDetail);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,

                            "Address Data Loaded",
                            venodorBusinessProfile

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


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/settings/address")
    public ResponseEntity<ApiResponse<VendorBusinessAddressDTO>> editVendorAddress(
            @AuthenticationPrincipal CustomUserDetail userDetail,@RequestBody VendorBusinessAddressDTO vendorBusinessAddressDTO) {

        try {


            VendorBusinessAddressDTO venodorBusinessProfile=vendorService.editVendorAddress(userDetail,vendorBusinessAddressDTO);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,

                            "Vendor Address Successfully loaded and Edited",
                            venodorBusinessProfile

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


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/settings/branding")
    public ResponseEntity<ApiResponse<VendorBrandingRequestDTO>> loadBrandingDetail(
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {


            VendorBrandingRequestDTO vendorBrandingRequestDTO=vendorService.loadBrandingDetails(userDetail);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,

                            "Branded Data Loaded Successfully",
                            vendorBrandingRequestDTO

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


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/settings/branding")
    public ResponseEntity<ApiResponse<VendorBrandingRequestDTO>> editBranding(
            @AuthenticationPrincipal CustomUserDetail userDetail,@RequestBody VendorBrandingRequestDTO vendorBrandingRequestDTO) {

        try {


            VendorBrandingRequestDTO vendorBrandingRequestDTO1=vendorService.editBranding(userDetail,vendorBrandingRequestDTO);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,

                            "Branding has been Successfully Loaded",
                            vendorBrandingRequestDTO1

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



    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/settings/contact")
    public ResponseEntity<ApiResponse<VendorContactSocialRequestDTO>> saveContactInfo(
            @AuthenticationPrincipal CustomUserDetail userDetail,@RequestBody VendorContactSocialRequestDTO vendorContactSocialRequestDTO) {

        try {

            VendorContactSocialRequestDTO vendorContactSocialRequestDTO1=vendorService.editContactDetails(userDetail,vendorContactSocialRequestDTO);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,

                            "Contact  has been edited Successfully",
                            vendorContactSocialRequestDTO1

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


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/settings/contact")
    public ResponseEntity<ApiResponse<VendorContactSocialRequestDTO>> loadContact(
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {

            VendorContactSocialRequestDTO vendorContactSocialRequestDTO1=vendorService.loadContactInfo(userDetail);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,

                            "Contact  has been edited Successfully",
                            vendorContactSocialRequestDTO1

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



    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/settings/bank")
    public ResponseEntity<ApiResponse<BankAccountRequestDto>> loadBankDdata(
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {

            BankAccountRequestDto bankAccountRequestDto=vendorService.loadBankVendorData(userDetail);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,

                            "Contact  has been edited Successfully",
                            bankAccountRequestDto

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
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/settings/bank")
    public ResponseEntity<ApiResponse<BankAccountRequestDto>> editBankData(
            @AuthenticationPrincipal CustomUserDetail userDetail,@RequestBody BankAccountRequestDto bankAccountRequestDto1 ) {

        try {

            BankAccountRequestDto bankAccountRequestDto=vendorService.updateBankVendorData(userDetail,bankAccountRequestDto1);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,

                            "Bank detail has been edited Successfully",
                            bankAccountRequestDto

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


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/settings/invoice")
    public ResponseEntity<ApiResponse<InvoiceSettingsRequestDto>> editInvoiceSettings(
            @AuthenticationPrincipal CustomUserDetail userDetail,@RequestBody InvoiceSettingsRequestDto invoiceSettingsRequestDto ) {

        try {

            InvoiceSettingsRequestDto invoiceSettingsRequestDto1=vendorService.editInvoiceData(userDetail,invoiceSettingsRequestDto);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,

                            "Invoice settings updated Successfully",
                            invoiceSettingsRequestDto1

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


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/settings/invoice")
    public ResponseEntity<ApiResponse<InvoiceSettingsRequestDto>> getInvoiceSettings(
            @AuthenticationPrincipal CustomUserDetail userDetail ) {

        try {

            InvoiceSettingsRequestDto invoiceSettingsRequestDto1=vendorService.getInvoiceData(userDetail);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,

                            " Invoice Settings fecthed Successfully",
                            invoiceSettingsRequestDto1

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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/settings/store")
    public ResponseEntity<ApiResponse<StoreSettingsRequestDto>> getStoreSettings(
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {

            StoreSettingsRequestDto storeSettingsRequestDto =
                    vendorService.getStoreSettings(userDetail);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Store Settings fetched Successfully",
                            storeSettingsRequestDto
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


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/settings/store")
    public ResponseEntity<ApiResponse<StoreSettingsRequestDto>> editStoreSettings(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @RequestBody StoreSettingsRequestDto storeSettingsRequestDto) {

        try {

            StoreSettingsRequestDto storeSettingsRequestDto1 =
                    vendorService.editStoreSettings(
                            userDetail,
                            storeSettingsRequestDto
                    );

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Store Settings updated Successfully",
                            storeSettingsRequestDto1
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
