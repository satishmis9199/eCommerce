package com.e_commerce.eCommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface SalesReportProjection {

    String getOrderNumber();

    LocalDateTime getOrderDate();

    Long getCustomerId();

    String getCustomerName();

    Long getProductId();

    String getProductName();

    String getBrandName();

    Integer getQuantity();

    BigDecimal getMrp();

    BigDecimal getUnitPrice();

    BigDecimal getLineTotal();

    String getPaymentMethod();

    String getPaymentStatus();

    String getOrderStatus();
}

