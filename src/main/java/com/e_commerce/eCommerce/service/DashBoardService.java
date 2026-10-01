package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.config.R2Properties;
import com.e_commerce.eCommerce.config.TenantContext;
import com.e_commerce.eCommerce.dto.MyProfileDTO;
import com.e_commerce.eCommerce.dto.VendorProfileDTO;
import com.e_commerce.eCommerce.dto.response.VendorDashboardDTO;
import com.e_commerce.eCommerce.entity.Plan;
import com.e_commerce.eCommerce.entity.User;
import com.e_commerce.eCommerce.entity.Vendor;
import com.e_commerce.eCommerce.entity.VendorBranding;
import com.e_commerce.eCommerce.exception.VendorRequestException;
import com.e_commerce.eCommerce.exception.vendorNotFoundException;
import com.e_commerce.eCommerce.repository.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service

@AllArgsConstructor
public class DashBoardService {
    private final UserRepos userRepos;
    private final VendorRepository vendorRepository;
    private final VendorBrandingRepository vendorBrandingRepository;
    private final R2Properties r2Properties;
    private final OrderRepository orderRepository;
    private final UserRepos userRepository;
    private final ProductRepository productRepository;
    private final  PlanRepository planRepository;


    public VendorProfileDTO loadDashBoardData(User user) {
        VendorProfileDTO vendorProfileDTO = new VendorProfileDTO();
        Optional<Vendor> vendor = vendorRepository.findById(user.getVendorId());
        VendorBranding vendorBranding = vendorBrandingRepository.findByVendorId(user.getVendorId());
        if (vendor.isEmpty()) {
            throw new RuntimeException("Vendor Not Found");
        }
        if (vendorBranding == null) {
            throw new RuntimeException("Branding data Not Available");
        }
        Vendor v2 = vendor.get();
        log.error(String.valueOf(v2.getId()));
        Optional<Plan> plan=planRepository.findById(v2.getPlanId());
        if(plan.isEmpty()){
            vendorProfileDTO.setSubscriptionPlan("Not Availble");
        }
        else{

            vendorProfileDTO.setSubscriptionPlan(plan.get().getName());
        }


        vendorProfileDTO.setVendorId(user.getVendorId());
        vendorProfileDTO.setTenantId(TenantContext.getTenantId());
        vendorProfileDTO.setFullName(user.getFirstName() + " " + user.getLastName());
        vendorProfileDTO.setEmailVerified(user.getEmailVerified());
        vendorProfileDTO.setEmail(user.getEmail());
        vendorProfileDTO.setMobile(user.getMobileNumber());
        vendorProfileDTO.setRole(String.valueOf(user.getRole()));
        vendorProfileDTO.setLastLogin(user.getLastLoginTime());
        vendorProfileDTO.setStatus(v2.getStatus());
        vendorProfileDTO.setBusinessName(v2.getBussinessName());
        vendorProfileDTO.setLogo(r2Properties.getPublicUrl() + "/" + vendorBranding.getLogoUrl());
        vendorProfileDTO.setStoreName(v2.getStoreName());
        return vendorProfileDTO;
    }

    public MyProfileDTO getPrrofileData(User userDetail) {
        MyProfileDTO vendorProfileDTO = new MyProfileDTO();
        Optional<Vendor> v1 = vendorRepository.findById(userDetail.getVendorId());
        if (!v1.isPresent()) {
            throw new RuntimeException("Not Present");
        }
        Vendor v2 = v1.get();

        vendorProfileDTO.setUserId(userDetail.getId());
        vendorProfileDTO.setFirstName(userDetail.getFirstName());
        vendorProfileDTO.setLastName(userDetail.getLastName());
        vendorProfileDTO.setEmail(userDetail.getEmail());
        vendorProfileDTO.setMobile(userDetail.getMobileNumber());
        vendorProfileDTO.setRole(String.valueOf(userDetail.getRole()));
        vendorProfileDTO.setStatus("ACTIVE");
        vendorProfileDTO.setProfileImage(r2Properties.getPublicUrl() + "/" + userDetail.getProfileImage());
        vendorProfileDTO.setMemberSince(userDetail.getCreatedAt());
        vendorProfileDTO.setLastLogin(userDetail.getLastLoginTime());
        return vendorProfileDTO;


    }
    public VendorDashboardDTO getDashBoardData(
            CustomUserDetail userDetail,
            String tenantId) {
        if (userDetail == null) {
            throw new vendorNotFoundException("User is Invalid");
        }
        if (tenantId == null || tenantId.isBlank()) {
            throw new VendorRequestException(
                    HttpStatus.UNAUTHORIZED,
                    "Tenant Does Not exist"
            );
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentMonthStart =
                now.withDayOfMonth(1)
                        .withHour(0)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);
        LocalDateTime previousMonthStart =
                currentMonthStart.minusMonths(1);
        LocalDateTime previousMonthEnd =
                previousMonthStart
                        .plusDays(now.getDayOfMonth() - 1L)
                        .withHour(now.getHour())
                        .withMinute(now.getMinute())
                        .withSecond(now.getSecond())
                        .withNano(now.getNano());
        BigDecimal totalRevenue =
                orderRepository.findTotalCountOfRevenue(tenantId);
        BigDecimal currentMonthRevenue =
                orderRepository.findRevenueBetween(
                        tenantId,
                        currentMonthStart,
                        now
                );
        BigDecimal previousMonthRevenue =
                orderRepository.findRevenueBetween(
                        tenantId,
                        previousMonthStart,
                        previousMonthEnd
                );
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }
        if (currentMonthRevenue == null) {
            currentMonthRevenue = BigDecimal.ZERO;
        }
        if (previousMonthRevenue == null) {
            previousMonthRevenue = BigDecimal.ZERO;
        }
        BigDecimal revenueChangePercentage = BigDecimal.ZERO;
        if (previousMonthRevenue.compareTo(BigDecimal.ZERO) != 0) {
            revenueChangePercentage =
                    currentMonthRevenue
                            .subtract(previousMonthRevenue)
                            .divide(
                                    previousMonthRevenue,
                                    4,
                                    RoundingMode.HALF_UP
                            )
                            .multiply(BigDecimal.valueOf(100));
        }
        Long totalOrders =
                orderRepository.totalOrders(tenantId);

        Long currentMonthOrders =
                orderRepository.totalOrdersBetween(
                        tenantId,
                        currentMonthStart,
                        now
                );
        Long previousMonthOrders =
                orderRepository.totalOrdersBetween(
                        tenantId,
                        previousMonthStart,
                        previousMonthEnd
                );
        if (totalOrders == null) {
            totalOrders = 0L;
        }
        if (currentMonthOrders == null) {
            currentMonthOrders = 0L;
        }
        if (previousMonthOrders == null) {
            previousMonthOrders = 0L;
        }
        BigDecimal ordersChangePercentage = BigDecimal.ZERO;
        if (previousMonthOrders != 0) {
            ordersChangePercentage =
                    BigDecimal.valueOf(currentMonthOrders - previousMonthOrders)
                            .divide(
                                    BigDecimal.valueOf(previousMonthOrders),
                                    4,
                                    RoundingMode.HALF_UP
                            )
                            .multiply(BigDecimal.valueOf(100));
        }
        Long totalCustomers =
                userRepository.totalCustomers(tenantId);
        Long currentMonthCustomers =
                userRepository.totalCustomersBetween(
                        tenantId,
                        currentMonthStart,
                        now
                );
        Long previousMonthCustomers =
                userRepository.totalCustomersBetween(
                        tenantId,
                        previousMonthStart,
                        previousMonthEnd
                );
        if (totalCustomers == null) {
            totalCustomers = 0L;
        }
        if (currentMonthCustomers == null) {
            currentMonthCustomers = 0L;
        }
        if (previousMonthCustomers == null) {
            previousMonthCustomers = 0L;
        }

        BigDecimal customersChangePercentage = BigDecimal.ZERO;
        if (previousMonthCustomers != 0) {
            customersChangePercentage =
                    BigDecimal.valueOf(
                                    currentMonthCustomers - previousMonthCustomers
                            )
                            .divide(
                                    BigDecimal.valueOf(previousMonthCustomers),
                                    4,
                                    RoundingMode.HALF_UP
                            )
                            .multiply(BigDecimal.valueOf(100));
        }
        Long pendingOrders =
                orderRepository.pendingOrders(tenantId);

        Long productsListed =
                productRepository.totalProducts(tenantId);

        Long lowStockProducts =
                productRepository.totalLowStockProducts(tenantId);


        if (pendingOrders == null) {
            pendingOrders = 0L;
        }

        if (productsListed == null) {
            productsListed = 0L;
        }

        if (lowStockProducts == null) {
            lowStockProducts = 0L;
        }
        return VendorDashboardDTO.builder()
                .totalRevenue(totalRevenue)
                .revenueChangePercentage(revenueChangePercentage)
                .totalOrders(totalOrders)
                .ordersChangePercentage(ordersChangePercentage)
                .totalCustomers(totalCustomers)
                .customersChangePercentage(customersChangePercentage)
                .pendingOrders(pendingOrders)
                .productsListed(productsListed)
                .lowStockProducts(lowStockProducts)
                .averageRating((int) 4.5)
                .returnsRefunds(0L)
                .returnsRefundsPercentage(BigDecimal.ZERO)
                .build();
    }
}
