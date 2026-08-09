package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository< ProductImage,Long> {
}
