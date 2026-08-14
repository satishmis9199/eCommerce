package com.e_commerce.eCommerce.controller;


import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.request.PasswordResetDto;
import com.e_commerce.eCommerce.dto.request.PasswordResetRequestDto;
import com.e_commerce.eCommerce.dto.response.TokenVerificationResponse;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.repository.UserRepos;
import com.e_commerce.eCommerce.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@AllArgsConstructor
@RestController

@RequestMapping("/api/u1/v1/auth/password-reset")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final UserRepos userRepository;
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<Void>> requestReset(@Valid @RequestBody PasswordResetRequestDto dto, HttpServletRequest request) {
        String tenantId= TenantContext.getTenantId();
      User email=  userRepository.findByEmailAndTenantId(dto.getEmail(),tenantId);
      if(email!=null){
          passwordResetService.initiatePasswordSetup(email,request);
      }


         return ResponseEntity.status(HttpStatus.CREATED)
                 .body(new ApiResponse<>(
                         true,
                         "If that email is registered, a reset link has been sent.",
                         null
                 ));

    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<TokenVerificationResponse>> verify(@RequestParam String token) {
        TokenVerificationResponse result = passwordResetService.verifyToken(token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "verify.",
                        result
                ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> reset(@Valid @RequestBody PasswordResetDto dto) {
        passwordResetService.resetPassword(dto.getToken(), dto.getNewPassword());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Password set successfully. You can now log in.",
                        null
                ));
    }
}
