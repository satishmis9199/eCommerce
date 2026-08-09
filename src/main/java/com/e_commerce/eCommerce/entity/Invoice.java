package com.e_commerce.eCommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "invoices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_invoice_number",
                        columnNames = "invoice_number"
                ),
                @UniqueConstraint(
                        name = "uk_invoice_order",
                        columnNames = "order_id"
                )
        },
        indexes = {
                @Index(name = "idx_invoice_tenant", columnList = "tenant_id"),
                @Index(name = "idx_invoice_vendor", columnList = "vendor_id"),
                @Index(name = "idx_invoice_order", columnList = "order_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "invoice_number",
            nullable = false,
            unique = true,
            length = 50
    )
    private String invoiceNumber;

    @Column(
            name = "order_id",
            nullable = false,
            unique = true
    )
    private Long orderId;

    @Column(
            name = "tenant_id",
            nullable = false,
            length = 100
    )
    private String tenantId;

    @Column(
            name = "vendor_id",
            nullable = false
    )
    private Long vendorId;

    /*
     * Cloudflare R2 object key.
     *
     * Example:
     * invoices/TENANT001/2026/INV-2026-000001.pdf
     *
     * Initially null while invoice is GENERATING.
     */
    @Column(
            name = "pdf_key",
            length = 500
    )
    private String pdfKey;

    /*
     * Optional public/permanent URL.
     * If R2 is private, this can remain null.
     */
    @Column(
            name = "pdf_url",
            length = 1000
    )
    private String pdfUrl;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private InvoiceStatus status;

    @Column(
            name = "generated_at"
    )
    private LocalDateTime generatedAt;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;

        if (status == null) {
            status = InvoiceStatus.GENERATING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}