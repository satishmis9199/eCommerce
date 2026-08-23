package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.request.ConfirmResolutionRequestDto;
import com.e_commerce.eCommerce.dto.request.CreateTicketRequestDto;
import com.e_commerce.eCommerce.dto.request.TicketMessageRequestDto;
import com.e_commerce.eCommerce.dto.response.TicketDetailResponseDto;
import com.e_commerce.eCommerce.dto.response.TicketSummaryResponseDto;
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
@RequestMapping("/api/u1/v1/tickets")
@Slf4j
@AllArgsConstructor
public class TicketController {

    private final SupportTicketService supportTicketService;

    @PostMapping
    public ResponseEntity<ApiResponse<TicketDetailResponseDto>> createTicket(@AuthenticationPrincipal CustomUserDetail userDetail, @Valid @RequestBody CreateTicketRequestDto dto) {
        try {
            TicketDetailResponseDto ticket = supportTicketService.createTicket(userDetail, dto);
            return ResponseEntity.status(
                    HttpStatus.CREATED).body(
                            new ApiResponse<>(
                                    true,
                                    "Ticket created successfully.",
                                    ticket));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(),
                                    null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TicketSummaryResponseDto>>> myTickets(@AuthenticationPrincipal CustomUserDetail userDetail) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "OK",
                    supportTicketService.listMyTickets(userDetail)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            false,
                            e.getMessage(),
                            null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketDetailResponseDto>> getTicket(@AuthenticationPrincipal CustomUserDetail userDetail, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "OK",
                            supportTicketService.getTicketForCustomer(userDetail, id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<ApiResponse<TicketDetailResponseDto>> addMessage(@AuthenticationPrincipal CustomUserDetail userDetail, @PathVariable Long id, @Valid @RequestBody TicketMessageRequestDto dto) {
        try {
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Message sent.",
                            supportTicketService.addCustomerMessage(userDetail, id, dto)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/{id}/confirm-resolution")
    public ResponseEntity<ApiResponse<TicketDetailResponseDto>> confirmResolution(@AuthenticationPrincipal CustomUserDetail userDetail, @PathVariable Long id, @Valid @RequestBody ConfirmResolutionRequestDto dto) {
        try {
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true, "OK",
                            supportTicketService.confirmResolution(userDetail, id, dto)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}