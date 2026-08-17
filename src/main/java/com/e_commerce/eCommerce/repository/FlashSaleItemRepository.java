package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.FlashSaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlashSaleItemRepository extends JpaRepository<FlashSaleItem, Long> {
    void deleteAllByFlashSaleId(Long id);
}
