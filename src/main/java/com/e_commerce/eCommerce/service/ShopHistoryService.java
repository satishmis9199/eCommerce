package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.dto.response.ShopHistoryResponse;
import com.e_commerce.eCommerce.exception.VendorRequestException;
import com.e_commerce.eCommerce.exception.vendorNotFoundException;
import com.e_commerce.eCommerce.repository.ShopStatusHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ShopHistoryService {
    private final ShopStatusHistoryRepository shopStatusHistoryRepository;
    public List<ShopHistoryResponse> getHistory(CustomUserDetail userDetail, String tenantid) {
        if (userDetail == null) {
            throw new VendorRequestException(HttpStatus.UNAUTHORIZED,
                    "User Not found");
        }
            if (tenantid == null) {
                throw new vendorNotFoundException(
                        "Tenant Not found");
            }
            List<ShopHistoryResponse> shop = shopStatusHistoryRepository.findAllWithTenant(tenantid);
            return shop;
        }
    }

