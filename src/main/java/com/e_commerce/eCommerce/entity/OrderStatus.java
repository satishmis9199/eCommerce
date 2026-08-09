package com.e_commerce.eCommerce.entity;

public enum OrderStatus {

    PENDING(1),

    PAYMENT_PENDING(2),

    PLACED(3),

    CONFIRMED(4),

    PROCESSING(5),

    PACKED(6),

    SHIPPED(7),

    OUT_FOR_DELIVERY(8),

    DELIVERED(9),

    CANCELLED(10);



    private final int sequence;

    OrderStatus(int sequence) {
        this.sequence = sequence;
    }

    public int getSequence() {
        return sequence;
    }
}