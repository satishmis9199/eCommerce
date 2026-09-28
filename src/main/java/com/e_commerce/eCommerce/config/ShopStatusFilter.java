package com.e_commerce.eCommerce.config;

import com.e_commerce.eCommerce.service.VendorService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ShopStatusFilter extends OncePerRequestFilter {

    @Autowired
    private VendorService vendorService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (uri.equals("/api/shop-closed")) {
            String tenantId = TenantContext.getTenantId();

            if (vendorService.isShopOpen(tenantId)) {
                response.sendRedirect("/api/u1/v1/home");
                return;
            }

            filterChain.doFilter(request, response);
            return;
        }

        boolean shouldCheckShopStatus =
                uri.startsWith("/u1/v1/checkout")
                        || uri.startsWith("/u1/v1/orders")
                        || uri.startsWith("/home1.html")
                        || uri.startsWith("/home.html");

        if (!shouldCheckShopStatus) {
            filterChain.doFilter(request, response);
            return;
        }

        String tenantId = TenantContext.getTenantId();

        if (!vendorService.isShopOpen(tenantId)) {
            response.sendRedirect("/api/shop-closed");
            return;
        }

        filterChain.doFilter(request, response);
    }
}