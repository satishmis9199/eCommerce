package com.e_commerce.eCommerce.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

@RequestMapping("/vendor")
@PreAuthorize("hasRole('ADMIN')")
public class VendorPagesController {
    @GetMapping("/s1/v1/dashboard")
    public String vendorDashBoard(){
        return "VendorDashBoard";
    }
    @GetMapping("/s1/on/v1/onBoarding")
    public String getOnBoarding() {
        return "onboarding";
    }
    @GetMapping("s11/v1/application-status")
    public String getApplicationStatus(){
        return "applicationStatus";
    }
    @GetMapping("/do/policy")
    public String openPolicy(){
        return "vendorPoliciesAdmin";
    }
}

