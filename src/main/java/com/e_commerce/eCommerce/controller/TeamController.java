package com.e_commerce.eCommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class TeamController {
    @GetMapping("/team/info")
    public String team(){
        return "team";
    }
}

