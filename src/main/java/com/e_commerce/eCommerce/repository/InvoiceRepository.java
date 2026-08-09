package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice,Long> {
    Optional<Invoice> findByOrderId(Long orderId);
}
