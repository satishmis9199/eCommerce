package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.repository.VendorOnnBRepo;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.OnboardingService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/vendor")
@AllArgsConstructor
public class OnboardingController {
    private static final Logger logger =
            LoggerFactory.getLogger(OnboardingController.class);
    private final OnboardingService onboardingService;
    private final VendorOnnBRepo vendorOnnBRepo;


    @GetMapping("/s1/v1/getOnBoardDetail")
    public VendorOnboardingResponseDTO getOnBoardDetail(
            @AuthenticationPrincipal CustomUserDetail user) {

        return onboardingService.getOnboarding(user.getUser().getVendorId());
    }

    @PostMapping("/s1/v1/basic-info")
    public ResponseEntity<?> saveBasicInfo(
            Authentication authentication,
            @RequestBody BasicInfoDto dto) {

        Map<String, Object> response = new HashMap<>();

        try {

            CustomUserDetail user =
                    (CustomUserDetail) authentication.getPrincipal();

            String message = onboardingService.saveBasicDetail(
                    user.getUser().getVendorId(),
                    dto
            );

            response.put("success", true);
            response.put("message", message);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
    @PostMapping("/s1/v1/business-details")
    public ResponseEntity<?> SaveBussinessDetail(Authentication authentication,
                                      @RequestBody BusinessDetailsDTO dto){
        Map<String, Object> response = new HashMap<>();

        try {

            CustomUserDetail user =
                    (CustomUserDetail) authentication.getPrincipal();

            String message = onboardingService.saveBussinessDetail(
                    user.getUser().getVendorId(),
                    dto
            );

            response.put("success", true);
            response.put("message", message);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/s1/v1/address")
    public ResponseEntity<?> aveBankDetaiils(Authentication authentication,
                                                 @RequestBody BusinessAddressDTO dto){
        Map<String, Object> response = new HashMap<>();

        try {

            CustomUserDetail user =
                    (CustomUserDetail) authentication.getPrincipal();

            String message = onboardingService.saveBussienssddress(
                    user.getUser().getVendorId(),
                    dto
            );

            response.put("success", true);
            response.put("message", message);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/s1/v1/bank-details")
    public ResponseEntity<?> saveBankDetail(Authentication authentication,
                                            @RequestBody BankInfoDto dto){
        Map<String, Object> response = new HashMap<>();

        try {

            CustomUserDetail user =
                    (CustomUserDetail) authentication.getPrincipal();

            String message = onboardingService.saveBankDetail(
                    user.getUser().getVendorId(),
                    dto
            );

            response.put("success", true);
            response.put("message", message);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }

    }
    @PostMapping("/s1/v1/branding")
    public ResponseEntity<?> saveBankDetail(Authentication authentication,
                                            @RequestBody BrandingInfoDto dto){
        Map<String, Object> response = new HashMap<>();

        try {

            CustomUserDetail user =
                    (CustomUserDetail) authentication.getPrincipal();

            String message = onboardingService.saveBrandDetail(
                    user.getUser().getVendorId(),
                    dto
            );

            response.put("success", true);
            response.put("message", message);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }


    }
    @PostMapping("/s1/v1/submit")
    public ResponseEntity<?> submitApplication(Authentication authentication) {

        CustomUserDetail user =
                (CustomUserDetail) authentication.getPrincipal();

        try {

            SubmitApplicationResponseDTO response =
                    onboardingService.submitApplication(
                            user.getUser().getVendorId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            SubmitApplicationResponseDTO response =
                    SubmitApplicationResponseDTO.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build();

            return ResponseEntity.badRequest().body(response);
        }
    }
    @PostMapping("/s1/v1/super/admin/vendor/onboarding/decision")
    public ResponseEntity<?> makeDecision(
            @RequestBody OnBoardingDecisionDto request,
            @AuthenticationPrincipal CustomUserDetail userDetails) {

        try {
            logger.info("Application IDs  "+request.getApplicationId());

            User loggedInUser = userDetails.getUser();
            logger.info("Logged User"+loggedInUser.getFirstName());

            String message = onboardingService.makeDecisiion(request, loggedInUser);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", message
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()
                    )
            );
        }
    }

//    GET /vendor
    @GetMapping("/s1/v1/application-status")
    public ResponseEntity<VenddorOnBoardingApplicationStatus> getApplicationStaus(@AuthenticationPrincipal CustomUserDetail userDetail){
        VenddorOnBoardingApplicationStatus v2=new VenddorOnBoardingApplicationStatus();
        try{
            v2=onboardingService.getOnboardingStatus(userDetail);
            return ResponseEntity.ok(v2);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(v2);


        }
    }
    @PostMapping("/s1/onboarding/resubmit")
    public ResponseEntity<?> resubmitApplication(
            @RequestParam("applicationId") String applicationId,
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {

            logger.info("Application Id : {}", applicationId);

            onboardingService.initiateResubmit(userDetail, applicationId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "You can now edit and resubmit your application.",
                    "redirectUrl", "/vendor/s1/on/v1/onBoarding"
            ));

        } catch (Exception e) {

            logger.info("Resubmit failed", e);

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
    }


