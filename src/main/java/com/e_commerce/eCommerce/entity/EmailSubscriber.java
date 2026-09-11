package com.e_commerce.eCommerce.entity;

import com.e_commerce.eCommerce.enums.ReminderType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "email_subscribers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_tenant_email",
                        columnNames = {"tenant_id", "email"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailSubscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean subscribed;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime lastReminderSentAt;

    /** MISS_YOU ya SPECIAL_OFFER — konsa template last bheja gaya tha */
    @Enumerated(EnumType.STRING)
    private ReminderType lastReminderType;
}