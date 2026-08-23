package com.e_commerce.eCommerce.repository;

import com.e_commerce.eCommerce.entity.TicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketMessageRepository extends JpaRepository<TicketMessage, Long> {

    List<TicketMessage> findAllByTenantIdAndTicketIdOrderByCreatedAtAsc(String tenantId, Long ticketId);
}