package com.e_commerce.eCommerce.entity;

import com.e_commerce.eCommerce.enums.SenderType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_messages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tenantId;

    private Long ticketId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private SenderType senderType;

    // userId of the customer/vendor-user who sent it; null for SYSTEM messages
    private Long senderId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @CreationTimestamp
    private LocalDateTime createdAt;
}