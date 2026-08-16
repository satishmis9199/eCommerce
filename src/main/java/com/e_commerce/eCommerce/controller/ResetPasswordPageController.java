package com.e_commerce.eCommerce.controller;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ResetPasswordPageController {

    @GetMapping("/reset-password")
    public String resetPasswordPage() {
        return "reset";
    }
    @GetMapping("/know/about-developer")
    public String about(){
        return "about-developer";
    }
}

