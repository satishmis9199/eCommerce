package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.EmailSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface EmailSubscriberRepository extends JpaRepository<EmailSubscriber,Long> {
    Optional<EmailSubscriber> findByTenantIdAndEmail(String tenantId, String email);
}
