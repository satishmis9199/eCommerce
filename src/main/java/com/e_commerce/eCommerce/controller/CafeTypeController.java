package com.e_commerce.eCommerce.controller;

import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/")
public class CafeTypeController {
    @GetMapping("u1/v1/home")
    public String returnPage(){
        return "index";
    }
}
