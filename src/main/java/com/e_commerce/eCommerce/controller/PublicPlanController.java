package com.e_commerce.eCommerce.controller;


import com.e_commerce.eCommerce.dto.response.PublicPlansResponseDTO;
import com.e_commerce.eCommerce.service.PlanService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/public/plans")
public class PublicPlanController {
    private final PlanService planService;
    @GetMapping
    public ResponseEntity<List<PublicPlansResponseDTO>> getAvailablePlans() {
        return ResponseEntity.ok(planService.getAvailablePlans());
    }
}

