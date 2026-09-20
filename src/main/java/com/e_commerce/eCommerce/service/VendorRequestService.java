package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.constants.GlobalConstants;
import com.e_commerce.eCommerce.dto.request.VendorRequestCreateRequest;
import com.e_commerce.eCommerce.dto.request.VendorRequestTrackingResponse;
import com.e_commerce.eCommerce.dto.response.VendorRequestStatusResponse;
import com.e_commerce.eCommerce.entity.VendorRequest;
import com.e_commerce.eCommerce.entity.VendorRequestTracking;
import com.e_commerce.eCommerce.enums.VendorRequestStatus;
import com.e_commerce.eCommerce.exception.VendorRequestException;
import com.e_commerce.eCommerce.repository.VendorRequestRepository;
import com.e_commerce.eCommerce.repository.VendorRequestTrackingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendorRequestService {
    private final VendorRequestTrackingRepository vendorRequestTrackingRepository;

    private static final List<VendorRequestStatus> OPEN =
            List.of(VendorRequestStatus.PENDING, VendorRequestStatus.CONTACTED);
    private static final int MAX_REQUESTS_PER_IP_PER_HOUR = 2;

    // same rules as the public page
    private static final Pattern MOBILE = Pattern.compile("^(?:\\+?91|0)?([6-9]\\d{9})$");
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");

    private final VendorRequestRepository vendorRequestRepository;

    @Transactional
    public String submit(VendorRequestCreateRequest req, String ipAddress) {

        if (req == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        String businessName =
                required(req.getBusinessName(), "Business name", 120);

        String ownerName =
                required(req.getOwnerName(), "Owner name", 100);

        String phone =
                mobile(req.getPhone());

        String email =
                email(req.getEmail());

        String city =
                required(req.getCity(), "City", 80);

        String state =
                required(req.getState(), "State", 80);

        String category =
                required(req.getBusinessCategory(), "Business category", 60);

        String message =
                optional(req.getMessage(), 500);

        if (ipAddress != null &&
                vendorRequestRepository.countByIpAddressAndCreatedAtAfter(
                        ipAddress,
                        LocalDateTime.now().minusHours(1)
                ) >= MAX_REQUESTS_PER_IP_PER_HOUR) {

            throw new VendorRequestException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Too many requests. Please try again after 1 Hour."
            );
        }

        if (vendorRequestRepository.existsByEmailIgnoreCaseAndStatusIn(
                email, OPEN
        ) || vendorRequestRepository.existsByPhoneAndStatusIn(
                phone, OPEN
        )) {

            throw new VendorRequestException(
                    HttpStatus.CONFLICT,
                    "A request with this email or mobile number is already under review."
            );
        }

        VendorRequest entity = new VendorRequest();

        entity.setBusinessName(businessName);
        entity.setOwnerName(ownerName);
        entity.setPhone(phone);
        entity.setEmail(email);
        entity.setCity(city);
        entity.setState(state);
        entity.setBusinessCategory(category);
        entity.setMessage(message);
        entity.setStatus(VendorRequestStatus.PENDING);
        entity.setIpAddress(ipAddress);

        VendorRequest saved =
                vendorRequestRepository.save(entity);

        String requestCode =
                String.format("REQ%06d", saved.getId());

        saved.setRequestCode(requestCode);

        vendorRequestRepository.save(saved);

        saveTracking(
                saved,
                VendorRequestStatus.PENDING,
                null,
                "SYSTEM"
        );

        return requestCode;
    }

    @Transactional
    public void saveTracking(
            VendorRequest vendorRequest,
            VendorRequestStatus status,
            String remarks,
            String changedBy
    ) {

        VendorRequestTracking tracking =
                VendorRequestTracking.builder()
                        .vendorRequest(vendorRequest)
                        .status(status)
                        .message(
                                GlobalConstants
                                        .getVendorRequestStatusMessage(status)
                        )
                        .remarks(remarks)
                        .changedBy(changedBy)
                        .build();

        vendorRequestTrackingRepository.save(tracking);
    }

    private String required(String value, String label, int max) {
        String v = clean(value);
        if (v == null) {
            throw new IllegalArgumentException(label + " is required");
        }
        if (v.length() > max) {
            throw new IllegalArgumentException(label + " must be at most " + max + " characters");
        }
        return v;
    }

    private String optional(String value, int max) {
        String v = clean(value);
        if (v != null && v.length() > max) {
            throw new IllegalArgumentException("Message must be at most " + max + " characters");
        }
        return v;
    }

    private String mobile(String value) {
        String v = clean(value);
        if (v == null) {
            throw new IllegalArgumentException("Mobile number is required");
        }
        Matcher m = MOBILE.matcher(v.replaceAll("[\\s-]", ""));
        if (!m.matches()) {
            throw new IllegalArgumentException("Enter a valid 10-digit Indian mobile number");
        }
        return m.group(1); // 10 digits, without +91 / 0
    }

    private String email(String value) {
        String v = clean(value);
        if (v == null) {
            throw new IllegalArgumentException("Email is required");
        }
        if (v.length() > 150 || !EMAIL.matcher(v).matches()) {
            throw new IllegalArgumentException("Enter a valid email address");
        }
        return v.toLowerCase();
    }
    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String v = value.trim().replaceAll("\\s+", " ");
        return v.isEmpty() ? null : v;
    }

    @Transactional(readOnly = true)
    public VendorRequestStatusResponse getStatus(String requestCode) {

        if (requestCode == null || requestCode.isBlank()) {
            throw new VendorRequestException(
                    HttpStatus.BAD_REQUEST,
                    "Request code is required"
            );
        }

        VendorRequest request =
                vendorRequestRepository.findByRequestCode(
                        requestCode.trim().toUpperCase()
                );

        if (request == null) {
            throw new VendorRequestException(
                    HttpStatus.NOT_FOUND,
                    "Request not found"
            );
        }

        List<VendorRequestTrackingResponse> tracking =
                vendorRequestTrackingRepository
                        .findByVendorRequestIdOrderByCreatedAtAsc(request.getId())
                        .stream()
                        .map(item -> VendorRequestTrackingResponse.builder()
                                .status(item.getStatus())
                                .message(item.getMessage())
                                .createdAt(item.getCreatedAt())
                                .build())
                        .toList();

        return VendorRequestStatusResponse.builder()
                .success(true)
                .requestCode(request.getRequestCode())
                .status(request.getStatus())
                .message(
                        GlobalConstants.getVendorRequestStatusMessage(
                                request.getStatus()
                        )
                )
                .tracking(tracking)
                .build();
    }
}