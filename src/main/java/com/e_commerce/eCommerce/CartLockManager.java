package com.e_commerce.eCommerce;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class CartLockManager {

    private final ConcurrentHashMap<String, ReentrantLock> locks =
            new ConcurrentHashMap<>();

    public ReentrantLock getLock(String tenantId, Long vendorId, Long userId) {

        String key = tenantId + "_" + vendorId + "_" + userId;

        return locks.computeIfAbsent(
                key,
                k -> new ReentrantLock()
        );
    }
}