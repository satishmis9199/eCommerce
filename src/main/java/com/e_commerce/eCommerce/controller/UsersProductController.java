package com.e_commerce.eCommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/s4")
public class UsersProductController {
    @GetMapping("/v1/t1/view-product")
    public String viewProductForSell() {
        return "employee";
    }

    @GetMapping("/v1/tenantnot-found")
    public String getTenantNotFo8nd() {
        return "tenant-not-found";
    }
}
