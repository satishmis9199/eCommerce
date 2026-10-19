package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.CustomAnnotation.RequiresFeature;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class UserProduct {
    @GetMapping("/access-denied")
    public String getAccessDeniedPage() {
        return "Access-denied";
    }

    @RequiresFeature("MEDIA_GALLERY")
    @GetMapping("/store-gallery")
    public String getGallery() {
        return "store-gallery";


    }
}
