package com.e_commerce.eCommerce.dto;

import com.e_commerce.eCommerce.entity.OnboardingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitApplicationResponseDTO {

    private boolean success;

    private String message;

    private String applicationId;

    private OnboardingStatus status;

    private LocalDateTime submittedAt;


}