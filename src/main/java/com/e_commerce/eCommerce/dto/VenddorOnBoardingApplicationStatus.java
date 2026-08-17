package com.e_commerce.eCommerce.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VenddorOnBoardingApplicationStatus {

    private boolean success;

    private String applicationId;

    private String status;

    private String storeName;

    private String businessName;

    private LocalDateTime submittedAt;

    private LocalDateTime reviewedAt;

    private String remarks;

    private boolean resubmit;


}