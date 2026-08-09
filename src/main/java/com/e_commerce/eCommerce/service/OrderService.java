package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.R2Properties;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.entity.*;
import com.e_commerce.eCommerce.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final VendorRepository vendorRepository;
    private final R2Properties r2Properties;
    private final OrderTrackingrepository orderTrackingrepository;
    private final ReturnTrackingRepository returnTrackingRepository;
    public OrderResponseDto findOrderDetails(String orderId, CustomUserDetail userDetail) {
        String tenantId= TenantContext.getTenantId();
        if(tenantId==null){
            throw new RuntimeException("No tenant");
        }
        Optional<Vendor> vendor=vendorRepository.findByTenantId(tenantId);
        if(vendor.isEmpty()){
            throw new RuntimeException("Tenant does Not Exists");
        }
        if(userDetail==null){
            throw new RuntimeException("Please Login First");
        }
        Order order=orderRepository.findByTenantIdAndOrderNumber(tenantId,orderId);
        if(order==null){
            throw new RuntimeException("Order Not Found");
        }

        OrderResponseDto orderResponseDto=new OrderResponseDto();
         List<OrderItemDTo> orderItemDToList=new ArrayList<>();
        List<OrderItem> orderItem=orderItemRepository.findByOrderId(order.getId());
        BigDecimal total= BigDecimal.valueOf(0);
        for(OrderItem item:orderItem){
            OrderItemDTo itemDTo=new OrderItemDTo();
            itemDTo.setName(item.getProductName());
            itemDTo.setProductId(item.getProductId());
            itemDTo.setImage(r2Properties.getPublicUrl()+"/"+item.getImageUrl());

            itemDTo.setPrice(item.getSellingPrice());
            if (item.getLineTotal() != null) {
                total = total.add(item.getLineTotal());
            }
            orderItemDToList.add(itemDTo);

        }

        orderResponseDto.setOrderItemDToList(orderItemDToList);
        orderResponseDto.setOrderId(order.getOrderNumber());
        orderResponseDto.setDate(String.valueOf(order.getCreatedAt()));

        orderResponseDto.setStatus(String.valueOf(order.getOrderStatus()));
        orderResponseDto.setTotal(total);
        orderResponseDto.setPaymentStatus(order.getPaymentStatus());
        orderResponseDto.setPaymentMethod(order.getPaymentMethod());
        return orderResponseDto;


    }

    public List<OrderResponseDto> findByOrder(CustomUserDetail userDetail) {
        String tenantId= TenantContext.getTenantId();
        List<OrderResponseDto> orderResponseDtos=new ArrayList<>();
        if(tenantId==null){
            throw new RuntimeException("No tenant");
        }
        Optional<Vendor> vendor=vendorRepository.findByTenantId(tenantId);
        if(vendor.isEmpty()){
            throw new RuntimeException("Tenant does Not Exists");
        }
        if(userDetail==null){
            throw new RuntimeException("Please Login First");
        }
        List<Order> order1=orderRepository.findAllByTenantIdAndUserId(tenantId,userDetail.getId());
        if(order1==null){
            throw new RuntimeException("Order Not Found");
        }
        for(Order order:order1){

            OrderResponseDto orderResponseDto=new OrderResponseDto();
            List<OrderItemDTo> orderItemDToList=new ArrayList<>();
            List<OrderItem> orderItem=orderItemRepository.findByOrderId(order.getId());
            BigDecimal total= BigDecimal.valueOf(0);
            for(OrderItem item:orderItem){
                OrderItemDTo itemDTo=new OrderItemDTo();
                itemDTo.setName(item.getProductName());
                itemDTo.setProductId(item.getProductId());
                itemDTo.setReview(true);
                itemDTo.setImage(r2Properties.getPublicUrl()+"/"+item.getImageUrl());

                itemDTo.setPrice(item.getSellingPrice());
                if (item.getLineTotal() != null) {
                    total = total.add(item.getLineTotal());
                }
                orderItemDToList.add(itemDTo);

            }

            orderResponseDto.setOrderItemDToList(orderItemDToList);
            orderResponseDto.setOrderNo(order.getId());
            orderResponseDto.setOrderId(order.getOrderNumber());
            orderResponseDto.setDate(String.valueOf(order.getCreatedAt()));

            if(order.getOrderStatus()==OrderStatus.DELIVERED && order.getReturnStatus()!=ReturnStatus.NONE){
                orderResponseDto.setStatus(String.valueOf(order.getReturnStatus()));
            }else{
                orderResponseDto.setStatus(String.valueOf(order.getOrderStatus()));
            }

            orderResponseDto.setTotal(total);
            orderResponseDto.setPaymentStatus(order.getPaymentStatus());
            orderResponseDto.setPaymentMethod(order.getPaymentMethod());
            orderResponseDtos.add(orderResponseDto);



        }
        return orderResponseDtos;
    }

    public OrderTrackingResponseDto getTracking(CustomUserDetail userDetail, String orderId) {

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            throw new RuntimeException("No tenant");
        }

        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenantId);
        if (vendor.isEmpty()) {
            throw new RuntimeException("Tenant does Not Exists");
        }

        if (userDetail == null) {
            throw new RuntimeException("Please Login First");
        }

        Order order = orderRepository.findByTenantIdAndOrderNumber(tenantId, orderId);
        if (order == null) {
            throw new RuntimeException("Order Not Found");
        }

        OrderTrackingResponseDto response = new OrderTrackingResponseDto();

        response.setOrderId(orderId);
        response.setReturnStatus(String.valueOf(order.getReturnStatus()));
        if(order.getOrderStatus()==OrderStatus.DELIVERED &&  order.getReturnStatus()==ReturnStatus.NONE){
            response.setCurrentStatus(order.getOrderStatus().name().toLowerCase());
        }
        response.setCurrentStatus(order.getOrderStatus().name().toLowerCase());
        response.setCourierName(vendor.get().getBussinessName());
        response.setTrackingNumber(orderId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate estimatedDelivery = calculateEstimatedDelivery(order.getCreatedAt());
        log.error("estimated Delivery is {}",estimatedDelivery);
        log.error("Estimated Delivery Format {}",estimatedDelivery.format(formatter));
        response.setEstimatedDelivery(estimatedDelivery.format(formatter));

        List<OrderTracking> trackingList =
                orderTrackingrepository.findByTenantIdAndOrderId(tenantId, order.getId());
        List<ReturnTracking> returnTrackings=returnTrackingRepository.findByTenantIdAndOrderId(tenantId, order.getId());

        HashMap<OrderStatus, OrderTracking> trackingMap = new HashMap<>();
        HashMap<ReturnStatus,ReturnTracking> rMap=new HashMap<>();

        for (OrderTracking tracking : trackingList) {
            trackingMap.put(tracking.getStatus(), tracking);
        }
        for(ReturnTracking returnTracking:returnTrackings){
            rMap.put(returnTracking.getStatus(),returnTracking);
        }

        List<OrderStatus> timeline = new ArrayList<>();

        timeline.add(OrderStatus.PLACED);
        timeline.add(OrderStatus.CONFIRMED);
        timeline.add(OrderStatus.SHIPPED);
        timeline.add(OrderStatus.OUT_FOR_DELIVERY);
        timeline.add(OrderStatus.DELIVERED);



        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            timeline.add(OrderStatus.CANCELLED);
        }

        List<OrderTrackingHistoryDto> history = new ArrayList<>();

        for (OrderStatus status : timeline) {

            OrderTrackingHistoryDto dto = new OrderTrackingHistoryDto();

            dto.setStatus(status.name().toLowerCase());
            dto.setLabel(status.name().replace("_", " ").toLowerCase());

            OrderTracking tracking = trackingMap.get(status);

            if (tracking != null) {

                dto.setCompleted(true);
                dto.setDescription(tracking.getRemarks());
                dto.setTimestamp(String.valueOf(tracking.getCreatedAt()));

            } else {

                dto.setCompleted(false);
                dto.setDescription(null);
                dto.setTimestamp(null);

            }

            history.add(dto);
        }

        List<ReturnStatus> timelines = new ArrayList<>();

        timelines.add(ReturnStatus.RETURN_REQUESTED);
        timelines.add(ReturnStatus.APPROVED);
        timelines.add(ReturnStatus.PICKUP_SCHEDULED);
        timelines.add(ReturnStatus.REFUND_INITIATED);
        timelines.add(ReturnStatus.COMPLETED);
        if (order.getReturnStatus()==ReturnStatus.REJECTED) {
            timelines.add(ReturnStatus.REJECTED);
        }else if(order.getReturnStatus()==ReturnStatus.RETURNRD_CANCELLED){
            timelines.add(ReturnStatus.RETURNRD_CANCELLED);
        }
        List<ReturnTrackingHistory> returnTrackingHistories=new ArrayList<>();
        for (ReturnStatus status : timelines) {

            ReturnTrackingHistory dto = new ReturnTrackingHistory();

            dto.setStatus(status.name().toLowerCase());
            dto.setLabel(status.name().replace("_", " ").toLowerCase());

            ReturnTracking tracking = rMap.get(status);

            if (tracking != null) {

                dto.setCompleted(true);
                dto.setDescription(tracking.getRemarks());
                dto.setTimestamp(String.valueOf(tracking.getCreatedAt()));

            } else {

                dto.setCompleted(false);
                dto.setDescription(null);
                dto.setTimestamp(null);

            }

            returnTrackingHistories.add(dto);
        }


        response.setHistory(history);
        response.setReturnHistory(returnTrackingHistories);

        return response;
    }
    private LocalDate calculateEstimatedDelivery(LocalDateTime orderCreatedAt) {
        log.error("Inside calculateEstimatedDelivery {}",orderCreatedAt);
        LocalTime cutoffTime = LocalTime.of(14, 0);

        if (orderCreatedAt.toLocalTime().isBefore(cutoffTime)) {
            return orderCreatedAt.toLocalDate();
        } else {
            return orderCreatedAt.toLocalDate().plusDays(1);
        }
    }
    @Transactional
    public String cancelOrderByUser(CustomUserDetail userDetail,String reason,String orderId){
        String tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            throw new RuntimeException("No tenant");
        }

        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenantId);
        if (vendor.isEmpty()) {
            throw new RuntimeException("Tenant does Not Exists");
        }

        if (userDetail == null) {
            throw new RuntimeException("Please Login First");
        }

        Order order = orderRepository.findByTenantIdAndOrderNumberAndUserId(tenantId, orderId,userDetail.getId());
        if (order == null) {
            throw new RuntimeException("Order Not Found");
        }
        Set<OrderStatus> allowedStatuses = Set.of(
                OrderStatus.PLACED,
                OrderStatus.CONFIRMED,
                OrderStatus.SHIPPED
        );

        if (!allowedStatuses.contains(order.getOrderStatus())) {
            throw new RuntimeException("Order cannot be cancelled at this stage.");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
//        order.setRemarks(reason);
        order.setUpdatedAt(LocalDateTime.now());
        OrderTracking tracking = OrderTracking.builder()
                .tenantId(tenantId)
                .vendorId(vendor.get().getId())
                .orderId(order.getId())
                .previousStatus(order.getOrderStatus())
                .status(OrderStatus.CANCELLED)
                .remarks(reason)
                .changedBy(userDetail.getId())
                .changedByType("CUSTOMER")
                .build();

        orderTrackingrepository.save(tracking);
        orderRepository.save(order);
        return "Order cancelled Successfully";

    }

    @Transactional
    public String returnOrder(CustomUserDetail userDetail,String reason,String orderId){
        String tenantId = TenantContext.getTenantId();

        if (tenantId == null) {
            throw new RuntimeException("No tenant");
        }

        Optional<Vendor> vendor = vendorRepository.findByTenantId(tenantId);
        if (vendor.isEmpty()) {
            throw new RuntimeException("Tenant does Not Exists");
        }

        if (userDetail == null) {
            throw new RuntimeException("Please Login First");
        }

        Order order = orderRepository.findByTenantIdAndOrderNumberAndUserId(tenantId, orderId,userDetail.getId());
        if (order == null) {
            throw new RuntimeException("Order Not Found");
        }
        Set<OrderStatus> allowedStatuses = Set.of(
                OrderStatus.PLACED,
                OrderStatus.CONFIRMED,
                OrderStatus.SHIPPED
        );

        if (allowedStatuses.contains(order.getOrderStatus()) && order.getReturnStatus()==ReturnStatus.NONE ) {
            throw new RuntimeException("Order cannot be Returned at this stage.");
        }
        order.setReturnStatus(ReturnStatus.RETURN_REQUESTED);
        order.setUpdatedAt(LocalDateTime.now());
        ReturnTracking returnTracking=ReturnTracking.builder()
                .tenantId(tenantId)
                .vendorId(vendor.get().getId())
                .orderId(order.getId())

                .previousStatus(String.valueOf(order.getOrderStatus()))
                .location("")
                .remarks(reason)
                .changedBy(userDetail.getId())
                .changedByType(userDetail.getUsername())
                .status(ReturnStatus.RETURN_REQUESTED)
                .build();
        returnTrackingRepository.save(returnTracking);
        orderRepository.save(order);


        return "Return Intiated Successfully";
    }


    }
