package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.EmailSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface EmailSubscriberRepository extends JpaRepository<EmailSubscriber,Long> {
    Optional<EmailSubscriber> findByTenantIdAndEmail(String tenantId, String email);

   

    List<EmailSubscriber> findByTenantIdAndSubscribedTrue(String tenantId);

    @Query("""
        SELECT DISTINCT e.tenantId
        FROM EmailSubscriber e
        WHERE e.subscribed = true
    """)
    List<String> findAllTenantIdsWithActiveSubscribers();
}
