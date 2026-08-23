package com.e_commerce.eCommerce.entity;

import com.e_commerce.eCommerce.enums.TicketCategory;
import com.e_commerce.eCommerce.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_tickets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tenantId;

    private Long vendorId;

    private Long userId;

    @Column(unique = true, length = 40)
    private String ticketNumber;

    // Optional - set when the ticket is about a specific order
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketCategory category;

    @Column(nullable = false, length = 150)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    @Builder.Default
    private TicketStatus status = TicketStatus.OPEN;

    @Column(columnDefinition = "TEXT")
    private String resolutionNote;

    @Builder.Default
    private Boolean customerConfirmedResolved = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime resolvedAt;

    private LocalDateTime closedAt;
}