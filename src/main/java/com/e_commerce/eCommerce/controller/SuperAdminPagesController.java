package com.e_commerce.eCommerce.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/s1")
public class SuperAdminPagesController {
    @GetMapping("/super/admin/v1/dashboard")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String getDahBoard() {
        return "SuperAdminDashBoard";
    }


}
