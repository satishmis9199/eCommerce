package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.CartLockManager;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.controller.CartController;
import com.e_commerce.eCommerce.dto.*;
import com.e_commerce.eCommerce.entity.*;
import com.e_commerce.eCommerce.event.OrderDeliveredEvent;
import com.e_commerce.eCommerce.event.OrderTrackingEvent;
import com.e_commerce.eCommerce.repository.*;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Service
@AllArgsConstructor

public class CartsService {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final VendorRepository vendorRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final OrderAddressRepository orderAddressRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductSalesAsyncService productSalesAsyncService;
    private final OrderTrackingrepository orderTrackingrepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CartLockManager cartLockManager;
    private final CartTransactionalService cartTransactionalService;



    private final String keySecret="yg04Jq5QC2yDIvBMWCslo1VC";

    private final String keyId="rzp_test_TFRosJx8RugKLp";
    @Transactional
    public String addToCart(
            CustomUserDetail userDetail,
            AddToCartRequestDto dto) {

        User user = userDetail.getUser();

        String tenantId = TenantContext.getTenantId();

        Vendor vendor = vendorRepository
                .findByTenantId(tenantId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor Does Not Exist"));

        ReentrantLock lock = cartLockManager.getLock(
                tenantId,
                vendor.getId(),
                user.getId()
        );

        lock.lock();

        try {

            return cartTransactionalService.addToCartInternal(
                    user,
                    dto,
                    tenantId,
                    vendor
            );

        } finally {

            lock.unlock();
        }
    }

    public CartResponseDTO loadCartItems(CustomUserDetail userDetail) {

        CartResponseDTO cartResponseDTO = new CartResponseDTO();
        List<CartItemResponseDTO> items = new ArrayList<>();

        String tenantId = TenantContext.getTenantId();

        if (userDetail == null) {
            throw new RuntimeException("Invalid User");
        }

        User user = userDetail.getUser();

        Optional<Vendor> optionalVendor = vendorRepository.findByTenantId(tenantId);

        if (optionalVendor.isEmpty()) {
            throw new RuntimeException("Invalid Vendor OR Vendor does not exist");
        }

        Vendor vendor = optionalVendor.get();

        Cart cart = cartRepository.findByVendorIdAndUserId(vendor.getId(), user.getId());

        if (cart == null) {
            cartResponseDTO.setItems(new ArrayList<>());
            cartResponseDTO.setCartCount(0);
            cartResponseDTO.setPricing(new CartPricingDTO());
            return cartResponseDTO;
        }

        List<CartItem> cartItems =
                cartItemRepository.findByCartIdAndUserId(cart.getId(), user.getId());

        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal mrpTotal = BigDecimal.ZERO;

        int cartCount = 0;

        for (CartItem cartItem : cartItems) {

            Product product = productRepository.findByIdAndTenantIdAndVendorId(
                    cartItem.getProductId(),
                    tenantId,
                    vendor.getId()
            );

            CartItemResponseDTO item = CartItemResponseDTO.builder()
                    .itemId(cartItem.getId())
                    .productId(product.getId())
                    .name(product.getProductName())
                    .image(product.getProductImage())
                    .businessName(vendor.getBussinessName())
                    .brand(vendor.getStoreName())
                    .unitPrice(cartItem.getPrice())
                    .qty(cartItem.getQuantity())
                    .lineTotal(findLineTotal(cartItem.getPrice(), cartItem.getQuantity()))
                    .build();

            items.add(item);

            // Selling Price Total
            subTotal = subTotal.add(
                    cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );

            // MRP Total
            mrpTotal = mrpTotal.add(
                    product.getMrp().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );

            // Unique products count
            cartCount++;
        }

        CartPricingDTO pricing = CartPricingDTO.builder()
                .subtotal(mrpTotal)
                .discount(subTotal.subtract(mrpTotal))
                .discountLabel(findDiscount(subTotal, mrpTotal) + " %OFF")
                .shipping(BigDecimal.ZERO)
                .shippingLabel("Free")
                .total(subTotal)
                .build();

        cartResponseDTO.setItems(items);
        cartResponseDTO.setPricing(pricing);
        cartResponseDTO.setCartCount(cartCount);

        return cartResponseDTO;
    }

    public BigDecimal findDiscount(BigDecimal subTotal, BigDecimal mrpTotal) {

        if (mrpTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return mrpTotal
                .subtract(subTotal)
                .multiply(BigDecimal.valueOf(100))
                .divide(mrpTotal, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal findLineTotal(BigDecimal sellingPrice, Integer quantity) {
        return sellingPrice.multiply(BigDecimal.valueOf(quantity));
    }


    public BigDecimal findTotalTotalPrice(int quantity, BigDecimal sellingPrice) {
        return sellingPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Transactional
    public AddressResponseDTO saveAdress(CustomUserDetail userDetail,
                                         AddressRequestDTO dto) {

        if (userDetail == null) {
            throw new RuntimeException("Please login first.");
        }

        User user = userDetail.getUser();

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Invalid tenant.");
        }

        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RuntimeException("Vendor does not exist."));

//         Optional: Limit addresses per user
        long count = addressRepository.countByUserIdAndTenantIdAndRowState(
                user.getId(), tenantId, 1);
        if (count >= 10) {
            throw new RuntimeException("Maximum 10 addresses are allowed.");
        }

        // Only one default address
        if (Boolean.TRUE.equals(dto.getIsDefault())) {

            List<Address> defaultAddresses =
                    addressRepository.findByUserIdAndTenantIdAndIsDefaultAndRowState(
                            user.getId(),
                            tenantId,
                            true,
                            1
                    );

            for (Address address : defaultAddresses) {
                address.setIsDefault(false);
            }

            addressRepository.saveAll(defaultAddresses);
        }

        Address address = new Address();

        address.setUserId(user.getId());
        address.setTenantId(tenantId);

        address.setLabel(dto.getLabel().trim());
        address.setFullName(dto.getFullName().trim());
        address.setMobileNumber(dto.getMobileNumber().trim());
        address.setAlternateMobile(dto.getAlternateMobile());
        address.setAddressLine1(dto.getAddressLine1().trim());
        address.setAddressLine2(dto.getAddressLine2());
        address.setLandmark(dto.getLandmark());
        address.setCity(dto.getCity().trim());
        address.setState(dto.getState().trim());
        address.setPostalCode(dto.getPostalCode().trim());
        address.setCountry(dto.getCountry().trim());
        address.setAddressType(dto.getAddressType());
        System.out.println(dto.getAddressType());

        address.setIsDefault(Boolean.TRUE.equals(dto.getIsDefault()));

        address.setRowState(1);

        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());

        Address savedAddress = addressRepository.save(address);

        return AddressResponseDTO.builder()
                .id(savedAddress.getId())
                .userId(savedAddress.getUserId())
                .tenantId(savedAddress.getTenantId())
                .label(savedAddress.getLabel())
                .fullName(savedAddress.getFullName())
                .mobileNumber(savedAddress.getMobileNumber())
                .alternateMobile(savedAddress.getAlternateMobile())
                .addressLine1(savedAddress.getAddressLine1())
                .addressLine2(savedAddress.getAddressLine2())
                .landmark(savedAddress.getLandmark())
                .city(savedAddress.getCity())
                .state(savedAddress.getState())
                .postalCode(savedAddress.getPostalCode())
                .country(savedAddress.getCountry())
                .addressType(savedAddress.getAddressType())
                .isDefault(savedAddress.getIsDefault())
                .rowState(savedAddress.getRowState())
                .createdAt(savedAddress.getCreatedAt())
                .updatedAt(savedAddress.getUpdatedAt())
                .build();
    }

    public List<AddressResponseDTO> getAllAdress(CustomUserDetail userDetail) {

        if (userDetail == null) {
            throw new RuntimeException("Please login first.");
        }

        User user = userDetail.getUser();

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Invalid tenant.");
        }

        vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RuntimeException("Vendor does not exist."));

        List<Address> addresses = addressRepository
                .findByUserIdAndTenantIdAndRowState(
                        user.getId(),
                        tenantId,
                        1
                );

        return addresses.stream()
                .map(address -> AddressResponseDTO.builder()
                        .id(address.getId())
                        .userId(address.getUserId())
                        .tenantId(address.getTenantId())
                        .label(address.getLabel())
                        .fullName(address.getFullName())
                        .mobileNumber(address.getMobileNumber())
                        .alternateMobile(address.getAlternateMobile())
                        .addressLine1(address.getAddressLine1())
                        .addressLine2(address.getAddressLine2())
                        .landmark(address.getLandmark())
                        .city(address.getCity())
                        .state(address.getState())
                        .postalCode(address.getPostalCode())
                        .country(address.getCountry())
                        .addressType(address.getAddressType())
                        .isDefault(address.getIsDefault())
                        .rowState(address.getRowState())
                        .createdAt(address.getCreatedAt())
                        .updatedAt(address.getUpdatedAt())
                        .build())
                .toList();
    }
    @Transactional
    public CheckoutResponseDTO placeOrder(CustomUserDetail userDetail,
                                          CheckoutRequestDTO dto) {
        List<OrderItem> orderItems=new ArrayList<>();
        if (userDetail == null) {
            throw new RuntimeException("Please login first.");
        }

        User user = userDetail.getUser();
        String tenantId = TenantContext.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Invalid Tenant");
        }
        Vendor vendor = vendorRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        Address address = addressRepository
                .findByIdAndUserIdAndTenantIdAndRowState(
                        dto.getAddressId(),
                        user.getId(),
                        tenantId,
                        1
                )
                .orElseThrow(() -> new RuntimeException("Address not found"));
        Cart cart = cartRepository
                .findByTenantIdAndVendorIdAndUserIdAndRowState(
                        tenantId,
                        vendor.getId(),
                        user.getId(),
                        1
                );

        if (cart == null) {
            throw new RuntimeException("Cart is empty");
        }
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        int canceled=cancelPendingOrder(user,tenantId);
        logger.error("--Previous Order with Pending has been cancelled and New Ordder hs been Created ---- with Count --- "+canceled);
        Order order = new Order();

        order.setTenantId(tenantId);
        order.setVendorId(vendor.getId());
        order.setUserId(user.getId());

        order.setPaymentMethod(dto.getPaymentMethod());

        if (dto.getPaymentMethod() == PaymentMethod.COD) {

            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setOrderStatus(OrderStatus.PLACED);

        } else {

            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setOrderStatus(OrderStatus.PAYMENT_PENDING);
        }

        order = orderRepository.save(order);
        OrderAddress orderAddress = new OrderAddress();

        orderAddress.setOrderId(order.getId());

        orderAddress.setUserId(user.getId());

        orderAddress.setTenantId(tenantId);

        orderAddress.setFullName(address.getFullName());

        orderAddress.setMobileNumber(address.getMobileNumber());

        orderAddress.setAlternateMobile(address.getAlternateMobile());

        orderAddress.setAddressLine1(address.getAddressLine1());

        orderAddress.setAddressLine2(address.getAddressLine2());

        orderAddress.setLandmark(address.getLandmark());

        orderAddress.setCity(address.getCity());

        orderAddress.setState(address.getState());

        orderAddress.setPostalCode(address.getPostalCode());

        orderAddress.setCountry(address.getCountry());

        orderAddress.setAddressType(address.getAddressType());

        OrderAddress savedAddress =
                orderAddressRepository.save(orderAddress);

        order.setOrderAddressId(savedAddress.getId());
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {

            Product product =
                    productRepository.findByIdAndTenantIdAndVendorId(
                            cartItem.getProductId(),
                            tenantId,
                            vendor.getId()
                    );

            if (product == null) {
                throw new RuntimeException("Product not found");
            }

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException(
                        product.getProductName() + " is out of stock"
                );
            }

            BigDecimal lineTotal =
                    product.getSellingPrice()
                            .multiply(
                                    BigDecimal.valueOf(cartItem.getQuantity())
                            );

            subtotal = subtotal.add(lineTotal);

            OrderItem item = new OrderItem();

            item.setOrderId(order.getId());
            item.setTenantId(tenantId);

            item.setProductId(product.getId());

            item.setProductName(product.getProductName());

            item.setQuantity(cartItem.getQuantity());

            item.setMrp(product.getMrp());

            item.setSellingPrice(product.getSellingPrice());

            item.setUnitPrice(product.getSellingPrice());

            item.setImageUrl(product.getProductImage());

            item.setLineTotal(lineTotal);
            orderItems.add(item);

            orderItemRepository.save(item);
        }
        productSalesAsyncService.updateSoldCount(orderItems,tenantId);
        String businessName = Optional.ofNullable(vendor.getBussinessName())
                .orElse("SHOP")
                .replaceAll("[^A-Za-z]", "")
                .toUpperCase();

        String prefix = businessName.isBlank()
                ? "SHOP"
                : businessName.substring(0, Math.min(4, businessName.length()));

        order.setOrderNumber(
                "ORD-" + prefix + "-" + order.getId()
        );
        order.setSubtotal(subtotal);

        order.setDiscount(BigDecimal.ZERO);

        order.setShipping(BigDecimal.ZERO);

        order.setTotal(subtotal);
        order.setReturnStatus(ReturnStatus.NONE);

        order.setTotalItems(cartItems.size());

        order.setTotalQuantity(
                cartItems.stream()
                        .mapToInt(CartItem::getQuantity)
                        .sum()
        );

        Order savedOrder =
                orderRepository.save(order);


        if (dto.getPaymentMethod() == PaymentMethod.ONLINE) {

            try {

                RazorpayClient razorpayClient =
                        new RazorpayClient(keyId, keySecret);

                JSONObject orderRequest = new JSONObject();

                orderRequest.put(
                        "amount",
                        savedOrder.getTotal()
                                .multiply(BigDecimal.valueOf(100))
                                .longValue()
                );

                orderRequest.put("currency", "INR");

                orderRequest.put("receipt", savedOrder.getOrderNumber());

                com.razorpay.Order razorpayOrder =
                        razorpayClient.orders.create(orderRequest);


                savedOrder.setPaymentReferenceId(
                        razorpayOrder.get("id").toString()
                );

                orderRepository.save(savedOrder);

                return CheckoutResponseDTO.builder()
                        .orderId(savedOrder.getId())
                        .orderNumber(savedOrder.getOrderNumber())
                        .paymentMethod(savedOrder.getPaymentMethod())
                        .paymentStatus(savedOrder.getPaymentStatus())
                        .orderStatus(savedOrder.getOrderStatus())
                        .razorpay(
                                RazorpayResponseDTO.builder()
                                        .key(keyId)
                                        .orderId(
                                                razorpayOrder.get("id").toString()
                                        )
                                        .amount(
                                                Long.parseLong(
                                                        razorpayOrder.get("amount").toString()
                                                )
                                        )
                                        .currency(
                                                razorpayOrder.get("currency").toString()
                                        )
                                        .internalOrderId(savedOrder.getId())
                                        .orderNumber(savedOrder.getOrderNumber())
                                        .build()
                        )
                        .build();

            } catch (Exception e) {
                throw new RuntimeException("Unable to create Razorpay Order", e);
            }
        }
        for (CartItem cartItem : cartItems) {

            Product product = productRepository.findByIdAndTenantIdAndVendorId(
                    cartItem.getProductId(),
                    tenantId,
                    vendor.getId()
            );

            if (product == null) {
                throw new RuntimeException("Product not found");
            }

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException(
                        product.getProductName() + " is out of stock"
                );
            }
            product.setStockQuantity(
                    product.getStockQuantity() - cartItem.getQuantity()
            );

            productRepository.save(product);
        }

        cartItemRepository.deleteAll(cartItems);

        cartRepository.delete(cart);
        eventPublisher.publishEvent(
                new OrderTrackingEvent(order.getId(),tenantId,vendor.getId())
        );

        return CheckoutResponseDTO.builder()
                .orderId(savedOrder.getId())
                .orderNumber(savedOrder.getOrderNumber())
                .paymentMethod(savedOrder.getPaymentMethod())
                .paymentStatus(savedOrder.getPaymentStatus())
                .orderStatus(savedOrder.getOrderStatus())
                .build();
    }

    private int cancelPendingOrder(User user, String tenantId) {
        List<Order> order=orderRepository.findByTenantIdAndUserIdAndOrderStatus(tenantId,user.getId(),OrderStatus.PAYMENT_PENDING);
        List<Order> orderList=new ArrayList<>();
        int count=0;
        if(order.size()>0){
            for(Order orders:order){
                orders.setOrderStatus(OrderStatus.CANCELLED);
                orders.setPaymentStatus(PaymentStatus.FAILED);
                orders.setUpdatedAt(LocalDateTime.now());
                orderList.add(orders);
                count++;

            }
        }
        orderRepository.saveAll(orderList);
return count;
    }


    @Transactional
    public void verifyOrderPayment(VerifyPaymentRequestDto dto) {
        logger.error("Order Id "+dto.getOrderId());
        String status="";

        String tenantId = TenantContext.getTenantId();

        Order order = orderRepository.findByTenantIdAndOrderNumber(
                tenantId,
                dto.getOrderId()
        );
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            return;
        }

        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        try {

            JSONObject attributes = new JSONObject();

            attributes.put("razorpay_order_id", dto.getRazorpayOrderId());
            attributes.put("razorpay_payment_id", dto.getRazorpayPaymentId());
            attributes.put("razorpay_signature", dto.getRazorpaySignature());

            Utils.verifyPaymentSignature(attributes, keySecret);

        } catch (RazorpayException e) {
            throw new RuntimeException("Invalid payment signature");
        }
        try {
            RazorpayClient razorpayClient = new RazorpayClient( keyId,keySecret);
            Payment payment =
                    razorpayClient.payments.fetch(
                            dto.getRazorpayPaymentId()
                    );

             status = payment.get("status");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(status.equalsIgnoreCase("captured")){
            order.setPaymentReferenceId(dto.getRazorpayPaymentId());
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setOrderStatus(OrderStatus.PLACED);

//        }else if(status.equalsIgnoreCase("authorized")){
//            order.setPaymentReferenceId(dto.getRazorpayPaymentId());
//            order.setPaymentStatus(PaymentStatus.PENDING);
//            order.setOrderStatus(OrderStatus.PAYMENT_PENDING);
        }else if(status.equalsIgnoreCase("refunded")){

            order.setPaymentStatus(PaymentStatus.REFUNDED);

            order.setOrderStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            throw new RuntimeException(
                    "Refund initiated. Amount will be credited within 2-3 business days."
            );
        }else if(status.equalsIgnoreCase("failed")){
            order.setPaymentReferenceId(dto.getRazorpayPaymentId());
            order.setPaymentStatus(PaymentStatus.FAILED);
            order.setOrderStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            throw new RuntimeException("Payment  failed");

        }
        //for Now We will not Consider Authorized;;;;
        else{
            throw new RuntimeException("Payment not completed.");
        }



        orderRepository.save(order);

        List<OrderItem> orderItems =
                orderItemRepository.findByOrderId(order.getId());

        for (OrderItem item : orderItems) {

            Product product =
                    productRepository.findByIdAndTenantIdAndVendorId(
                            item.getProductId(),
                            tenantId,
                            order.getVendorId()
                    );

            if (product == null) {
                throw new RuntimeException("Product not found");
            }

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException(
                        product.getProductName() + " is out of stock"
                );
            }

            product.setStockQuantity(
                    product.getStockQuantity() - item.getQuantity()
            );

            productRepository.save(product);
        }

        Cart cart = cartRepository.findByTenantIdAndVendorIdAndUserIdAndRowState(
                tenantId,
                order.getVendorId(),
                order.getUserId(),
                1
        );

        if (cart != null) {

            List<CartItem> cartItems =
                    cartItemRepository.findByCartId(cart.getId());

            cartItemRepository.deleteAll(cartItems);

            cartRepository.delete(cart);
        }
    }

    @Transactional
    public String removeCartItems(Long itemId, CustomUserDetail userDetail) {

        if (userDetail == null) {
            throw new RuntimeException("Please login first");
        }

        String tenantId = TenantContext.getTenantId();

        if (tenantId == null || tenantId.isBlank()) {
            throw new RuntimeException("Invalid Tenant");
        }

        Long userId = userDetail.getUser().getId();

        logger.info("Removing cart item: {}, user: {}, tenant: {}",
                itemId, userId, tenantId);

        int deleted = cartItemRepository
                .deleteByIdAndUserId(itemId, userId);

        if (deleted > 0) {
            return "Item deleted successfully";
        }

        return "Item not available";
    }
}
