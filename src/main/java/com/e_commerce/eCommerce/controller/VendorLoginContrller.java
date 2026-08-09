package com.e_commerce.eCommerce.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/api/vendor/v1")
public class VendorLoginContrller {
    private static  final Logger logger= LoggerFactory.getLogger(VendorController.class);
    @GetMapping("/login")
    public String getVendorLoginPage(HttpServletRequest request){


        return "vendorLogin";
    }
}
