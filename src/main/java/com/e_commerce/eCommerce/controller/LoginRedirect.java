package com.e_commerce.eCommerce.controller;


import com.e_commerce.eCommerce.config.TenantContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

public class LoginRedirect {
    @GetMapping("/super/admin")
    public String superAdminLogin(){
        String tenant= TenantContext.getTenantId();

        return "super-admin";
    }

    @GetMapping("/super/admin/tenant-not-found")
    public String tenantNotFound() {


        return "tenant-not-found";
    }
}
