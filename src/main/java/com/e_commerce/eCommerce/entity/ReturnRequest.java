//package com.e_commerce.eCommerce.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
////We are not ading this Feature for Now
//@Entity
//@Table(name = "return_request")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class ReturnRequest {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    // Multi Tenant
//    @Column(nullable = false, length = 100)
//    private String tenantId;
//
//    @Column(nullable = false)
//    private Long vendorId;
//
//    @Column(nullable = false)
//    private Long userId;
//
//    // Order Details
//    @Column(nullable = false)
//    private Long orderId;
//
////    current we return On Order Basis will implete,emt further
////    private Long orderItemId;
//
//    @Column(nullable = false, unique = true, length = 50)
//    private String returnId;
//
//    // Return Status
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private ReturnStatus returnStatus;
//
//    // Return Details
//    @Column(nullable = false, length = 255)
//    private String returnReason;
//
//    @Lob
//    private String customerRemark;
//
//    @Lob
//    private String adminRemark;
//
//    @Lob
//    private String rejectionReason;
//
//    // Refund
//    @Column(precision = 12, scale = 2)
//    private BigDecimal refundAmount;
//
//    // Dates
//    private LocalDateTime requestedAt;
//
//    private LocalDateTime approvedAt;
//
//    private LocalDateTime rejectedAt;
//
//    private LocalDateTime pickupDate;
//
//    private LocalDateTime refundDate;
//
//    private LocalDateTime completedAt;
//
//    // Audit
//    private LocalDateTime createdAt;
//
//    private LocalDateTime updatedAt;
//}