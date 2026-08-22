package com.e_commerce.eCommerce.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class ProductDocumentController {
    @GetMapping("/product-document")
    public String getProductImage(){
        return "ProductDocumenatation";
    }
}
