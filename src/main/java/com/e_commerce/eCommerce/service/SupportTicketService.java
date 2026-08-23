package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.*;

import com.e_commerce.eCommerce.dto.request.ConfirmResolutionRequestDto;
import com.e_commerce.eCommerce.dto.request.CreateTicketRequestDto;
import com.e_commerce.eCommerce.dto.request.TicketMessageRequestDto;
import com.e_commerce.eCommerce.dto.request.TicketStatusUpdateDto;
import com.e_commerce.eCommerce.dto.response.TicketDetailResponseDto;
import com.e_commerce.eCommerce.dto.response.TicketMessageResponseDto;
import com.e_commerce.eCommerce.dto.response.TicketSummaryResponseDto;
import com.e_commerce.eCommerce.entity.*;
import com.e_commerce.eCommerce.enums.SenderType;
import com.e_commerce.eCommerce.enums.TicketStatus;
import com.e_commerce.eCommerce.repository.OrderRepository;
import com.e_commerce.eCommerce.repository.SupportTicketRepository;
import com.e_commerce.eCommerce.repository.TicketMessageRepository;
import com.e_commerce.eCommerce.repository.VendorRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final TicketMessageRepository ticketMessageRepository;
    private final VendorRepository vendorRepository;
    private final OrderRepository orderRepository;
    @Transactional
    public TicketDetailResponseDto createTicket(CustomUserDetail userDetail, CreateTicketRequestDto dto) {
        String tenantId = requireTenant();
        requireRole(userDetail, Roles.USER);
        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RuntimeException("Store not found."));
        if (dto.getOrderNumber() != null && !dto.getOrderNumber().isBlank()) {
            Order order = orderRepository.findByTenantIdAndOrderNumberAndUserId(
                    tenantId, dto.getOrderNumber().trim(), userDetail.getId());
            if (order == null) {
                throw new RuntimeException("Order not found for this account.");
            }
        }
        SupportTicket ticket = SupportTicket.builder()
                .tenantId(tenantId)
                .vendorId(vendor.getId())
                .userId(userDetail.getId())
                .ticketNumber(generateTicketNumber(tenantId))
                .orderNumber(dto.getOrderNumber())
                .category(dto.getCategory())
                .subject(dto.getSubject().trim())
                .description(dto.getDescription().trim())
                .status(TicketStatus.OPEN)
                .customerConfirmedResolved(false)
                .build();
        ticket = supportTicketRepository.save(ticket);
        saveMessage(tenantId, ticket.getId(), SenderType.CUSTOMER, userDetail.getId(), ticket.getDescription());
        return toDetailDto(ticket);
    }

    public List<TicketSummaryResponseDto> listMyTickets(CustomUserDetail userDetail) {
        String tenantId = requireTenant();
        requireRole(userDetail, Roles.USER);

        return supportTicketRepository.findAllByTenantIdAndUserIdOrderByCreatedAtDesc(tenantId, userDetail.getId())
                .stream().map(this::toSummaryDto).toList();
    }

    public TicketDetailResponseDto getTicketForCustomer(CustomUserDetail userDetail, Long ticketId) {
        String tenantId = requireTenant();
        requireRole(userDetail, Roles.USER);

        SupportTicket ticket = supportTicketRepository.findByTenantIdAndIdAndUserId(tenantId, ticketId, userDetail.getId());
        if (ticket == null) {
            throw new RuntimeException("Ticket not found.");
        }
        return toDetailDto(ticket);
    }

    @Transactional
    public TicketDetailResponseDto addCustomerMessage(CustomUserDetail userDetail, Long ticketId, TicketMessageRequestDto dto) {
        String tenantId = requireTenant();
        requireRole(userDetail, Roles.USER);

        SupportTicket ticket = supportTicketRepository.findByTenantIdAndIdAndUserId(tenantId, ticketId, userDetail.getId());
        if (ticket == null) {
            throw new RuntimeException("Ticket not found.");
        }
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new RuntimeException("This ticket is closed. Please raise a new ticket.");
        }
        saveMessage(tenantId, ticket.getId(), SenderType.CUSTOMER, userDetail.getId(), dto.getMessage().trim());
        if (ticket.getStatus() == TicketStatus.WAITING_FOR_CUSTOMER || ticket.getStatus() == TicketStatus.RESOLVED) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            ticket.setCustomerConfirmedResolved(false);
            supportTicketRepository.save(ticket);
        }

        return toDetailDto(ticket);
    }

    @Transactional
    public TicketDetailResponseDto confirmResolution(CustomUserDetail userDetail, Long ticketId, ConfirmResolutionRequestDto dto) {
        String tenantId = requireTenant();
        requireRole(userDetail, Roles.USER);

        SupportTicket ticket = supportTicketRepository.findByTenantIdAndIdAndUserId(tenantId, ticketId, userDetail.getId());
        if (ticket == null) {
            throw new RuntimeException("Ticket not found.");
        }
        if (ticket.getStatus() != TicketStatus.RESOLVED) {
            throw new RuntimeException("This ticket has no pending resolution to confirm.");
        }
        if (Boolean.TRUE.equals(dto.getConfirmed())) {
            ticket.setCustomerConfirmedResolved(true);
            ticket.setStatus(TicketStatus.CLOSED);
            ticket.setClosedAt(LocalDateTime.now());
            saveMessage(tenantId, ticket.getId(), SenderType.SYSTEM, null,
                    "Customer confirmed the issue is resolved. Ticket closed.");
        } else {
            ticket.setCustomerConfirmedResolved(false);
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            String note = (dto.getNote() != null && !dto.getNote().isBlank())
                    ? "Customer said the issue is NOT resolved: " + dto.getNote().trim()
                    : "Customer said the issue is NOT resolved.";
            saveMessage(tenantId, ticket.getId(), SenderType.SYSTEM, null, note);
        }

        supportTicketRepository.save(ticket);
        return toDetailDto(ticket);
    }
    public List<TicketSummaryResponseDto> listVendorTickets(CustomUserDetail userDetail, TicketStatus statusFilter) {
        String tenantId = requireTenant();
        requireRole(userDetail, Roles.ADMIN);

        List<SupportTicket> tickets = (statusFilter != null)
                ? supportTicketRepository.findAllByTenantIdAndStatusOrderByCreatedAtDesc(tenantId, statusFilter)
                : supportTicketRepository.findAllByTenantIdOrderByCreatedAtDesc(tenantId);

        return tickets.stream().map(this::toSummaryDto).toList();
    }

    public TicketDetailResponseDto getTicketForVendor(CustomUserDetail userDetail, Long ticketId) {
        String tenantId = requireTenant();
        requireRole(userDetail, Roles.ADMIN);

        SupportTicket ticket = supportTicketRepository.findByTenantIdAndId(tenantId, ticketId);
        if (ticket == null) {
            throw new RuntimeException("Ticket not found.");
        }
        return toDetailDto(ticket);
    }

    @Transactional
    public TicketDetailResponseDto addVendorMessage(CustomUserDetail userDetail, Long ticketId, TicketMessageRequestDto dto) {
        String tenantId = requireTenant();
        requireRole(userDetail, Roles.ADMIN);

        SupportTicket ticket = supportTicketRepository.findByTenantIdAndId(tenantId, ticketId);
        if (ticket == null) {
            throw new RuntimeException("Ticket not found.");
        }
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new RuntimeException("This ticket is closed.");
        }

        saveMessage(tenantId, ticket.getId(), SenderType.VENDOR, userDetail.getId(), dto.getMessage().trim());

        if (ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            supportTicketRepository.save(ticket);
        }

        return toDetailDto(ticket);
    }

    @Transactional
    public TicketDetailResponseDto updateStatus(CustomUserDetail userDetail, Long ticketId, TicketStatusUpdateDto dto) {
        String tenantId = requireTenant();
        requireRole(userDetail, Roles.ADMIN);

        SupportTicket ticket = supportTicketRepository.findByTenantIdAndId(tenantId, ticketId);
        if (ticket == null) {
            throw new RuntimeException("Ticket not found.");
        }
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new RuntimeException("This ticket is already closed.");
        }

        TicketStatus newStatus = dto.getStatus();

        if (newStatus == TicketStatus.RESOLVED) {
            if (dto.getResolutionNote() == null || dto.getResolutionNote().isBlank()) {
                throw new RuntimeException("resolutionNote is required when resolving a ticket.");
            }
            ticket.setResolutionNote(dto.getResolutionNote().trim());
            ticket.setResolvedAt(LocalDateTime.now());
            ticket.setCustomerConfirmedResolved(false);
            saveMessage(tenantId, ticket.getId(), SenderType.VENDOR, userDetail.getId(),
                    "Marked as resolved: " + ticket.getResolutionNote());

        } else if (newStatus == TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
            saveMessage(tenantId, ticket.getId(), SenderType.SYSTEM, 0L, "Ticket closed by vendor.");

        } else {
            saveMessage(tenantId, ticket.getId(), SenderType.SYSTEM, null,
                    "Status changed to " + newStatus.name() + ".");
        }

        ticket.setStatus(newStatus);
        supportTicketRepository.save(ticket);
        return toDetailDto(ticket);
    }

    private void saveMessage(String tenantId, Long ticketId, SenderType senderType, Long senderId, String message) {
        TicketMessage msg = TicketMessage.builder()
                .tenantId(tenantId)
                .ticketId(ticketId)
                .senderType(senderType)
                .senderId(senderId)
                .message(message)
                .build();
        ticketMessageRepository.save(msg);
    }

    private String generateTicketNumber(String tenantId) {
        long nextSeq = supportTicketRepository.countByTenantId(tenantId) + 1;
        return "TCK-" + String.format("%06d", nextSeq);
    }

    private String requireTenant() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Invalid Tenant Id.");
        }
        return tenantId;
    }

    private void requireRole(CustomUserDetail userDetail, Roles expected) {
        if (userDetail == null || userDetail.getRole() != expected) {
            throw new RuntimeException("Unauthorized Access.");
        }
    }

    private TicketSummaryResponseDto toSummaryDto(SupportTicket t) {
        return new TicketSummaryResponseDto(
                t.getId(), t.getTicketNumber(), t.getCategory(), t.getSubject(),
                t.getStatus(), t.getOrderNumber(), t.getCreatedAt(), t.getUpdatedAt()
        );
    }

    private TicketDetailResponseDto toDetailDto(SupportTicket t) {
        List<TicketMessageResponseDto> messages = ticketMessageRepository
                .findAllByTenantIdAndTicketIdOrderByCreatedAtAsc(t.getTenantId(), t.getId())
                .stream()
                .map(m -> new TicketMessageResponseDto(m.getId(), m.getSenderType(), m.getSenderId(), m.getMessage(), m.getCreatedAt()))
                .toList();

        return new TicketDetailResponseDto(
                t.getId(), t.getTicketNumber(), t.getCategory(), t.getSubject(), t.getDescription(),
                t.getStatus(), t.getOrderNumber(), t.getResolutionNote(), t.getCustomerConfirmedResolved(),
                t.getCreatedAt(), t.getUpdatedAt(), t.getResolvedAt(), t.getClosedAt(), messages
        );
    }
}