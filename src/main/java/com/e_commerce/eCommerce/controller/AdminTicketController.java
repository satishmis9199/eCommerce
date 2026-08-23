package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.*;

import com.e_commerce.eCommerce.dto.request.TicketMessageRequestDto;
import com.e_commerce.eCommerce.dto.request.TicketStatusUpdateDto;
import com.e_commerce.eCommerce.dto.response.TicketDetailResponseDto;
import com.e_commerce.eCommerce.dto.response.TicketSummaryResponseDto;
import com.e_commerce.eCommerce.enums.TicketStatus;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import com.e_commerce.eCommerce.service.SupportTicketService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/vendor/s2/v1/tickets")
@Slf4j
@AllArgsConstructor
public class AdminTicketController {
    private final SupportTicketService supportTicketService;
    @GetMapping
    public ResponseEntity<ApiResponse<List<TicketSummaryResponseDto>>> listTickets(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @RequestParam(required = false) TicketStatus status) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(true, "OK",
                    supportTicketService.listVendorTickets(userDetail, status)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketDetailResponseDto>> getTicket(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(true, "OK",
                    supportTicketService.getTicketForVendor(userDetail, id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<ApiResponse<TicketDetailResponseDto>> reply(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id,
            @Valid @RequestBody TicketMessageRequestDto dto) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(true, "Reply sent.",
                    supportTicketService.addVendorMessage(userDetail, id, dto)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TicketDetailResponseDto>> updateStatus(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id,
            @Valid @RequestBody TicketStatusUpdateDto dto) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(true, "Status updated.",
                    supportTicketService.updateStatus(userDetail, id, dto)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}