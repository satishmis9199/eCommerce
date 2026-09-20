package com.e_commerce.eCommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BecomeVendorRegisterController {
    @GetMapping("/api/u1/v1/register-vendor")
    public String getVendorRegister(){
        return "become-a-vendor";
    }
}
