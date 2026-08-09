package com.e_commerce.eCommerce.config;

import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.repository.UserRepos;
import com.e_commerce.eCommerce.repository.VendorRepository;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class VendorDashboardFilter extends OncePerRequestFilter {
    private static final Logger logger= LoggerFactory.getLogger(VendorDashboardFilter.class);
    @Autowired
    private UserRepos userRepository;

    @Autowired
    private VendorRepository vendorRepository;

    private static final String DASHBOARD_URI = "/vendor/s1/v1/dashboard";
    private static final String ONBOARD_URI = "/vendor/s1/v1/dashboard";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        logger.info("Inside Dashboard filter");

        String uri = request.getRequestURI();

        // Dashboard nahi hai to kuch mat karo
        if (!DASHBOARD_URI.equals(uri) ) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof CustomUserDetail userDetail)) {

            filterChain.doFilter(request, response);
            return;
        }

        User user = userRepository.findById(userDetail.getId()).orElse(null);

        if (user == null) {

            response.sendRedirect("/api/vendor/v1/login");
            return;
        }

        if (user.getVendorId() == null) {

            response.sendRedirect("/api/vendor/v1/login");
            return;
        }

        Vendor vendor =
                vendorRepository.findById(user.getVendorId()).orElse(null);

        if (vendor == null) {

            response.sendRedirect("/api/vendor/v1/login");
            return;
        }

        switch (vendor.getStatus()) {

            case ACTIVE:
                filterChain.doFilter(request, response);
                return;

            case ONBOARDING:
                logger.info("Status is ONBOARDING");
                response.sendRedirect("/vendor/s1/on/v1/onBoarding");
                return;

            case SUSPENDED:
                response.sendRedirect("/v1/s1/suspend");


            case BLOCKED:
                response.sendRedirect("/vendor/s1/account-blocked");
                return;

            case REJECTED:
                response.sendRedirect("/vendor/s11/v1/application-status");
                return;

            case EXPIRED:
                response.sendRedirect("/vendor/s1/subscription-expired");
                return;

            default:
                response.sendRedirect("/vendor/s1/on/v1/onBoarding");
        }
    }
}