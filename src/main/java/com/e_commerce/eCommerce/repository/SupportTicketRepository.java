package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.SupportTicket;
import com.e_commerce.eCommerce.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {


    List<SupportTicket> findAllByTenantIdAndUserIdOrderByCreatedAtDesc(String tenantId, Long userId);

    SupportTicket findByTenantIdAndIdAndUserId(String tenantId, Long id, Long userId);

    // Vendor side - vendor sees every ticket raised within their tenant
    List<SupportTicket> findAllByTenantIdOrderByCreatedAtDesc(String tenantId);

    List<SupportTicket> findAllByTenantIdAndStatusOrderByCreatedAtDesc(String tenantId, TicketStatus status);

    SupportTicket findByTenantIdAndId(String tenantId, Long id);

    long countByTenantId(String tenantId);
}