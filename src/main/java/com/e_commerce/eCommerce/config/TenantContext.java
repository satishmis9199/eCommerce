package com.e_commerce.eCommerce.config;

import com.e_commerce.eCommerce.entity.Vendor;

public final class TenantContext {

    private static final ThreadLocal<Vendor> CURRENT_TENANT = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_TENANT_ID = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setTenant(Vendor vendor) {
        CURRENT_TENANT.set(vendor);

        if (vendor != null) {
            CURRENT_TENANT_ID.set(vendor.getTenantId());
        }
    }

    public static void setTenantId(String tenantId) {
        CURRENT_TENANT_ID.set(tenantId);
    }

    public static Vendor getTenant() {
        return CURRENT_TENANT.get();
    }

    public static String getTenantId() {

        // First preference: explicit tenant id
        if (CURRENT_TENANT_ID.get() != null) {
            return CURRENT_TENANT_ID.get();
        }

        // Fallback: vendor object
        Vendor vendor = CURRENT_TENANT.get();
        return vendor != null ? vendor.getTenantId() : null;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
        CURRENT_TENANT_ID.remove();
    }
}