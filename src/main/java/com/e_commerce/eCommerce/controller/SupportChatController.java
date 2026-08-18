package com.e_commerce.eCommerce.controller;

import com.e_commerce.eCommerce.dto.ApiResponse;
import com.e_commerce.eCommerce.dto.ChatRequestDto;
import com.e_commerce.eCommerce.dto.ChatResponseDto;
import com.e_commerce.eCommerce.service.CustomUserDetail;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Customer-facing support chatbot, backed by Spring AI + Gemini.
 * Scope: product availability/price/stock questions and order-status
 * lookups for the CURRENT tenant only (see SupportChatClientConfig /
 * SupportAssistantTools).
 */
@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class SupportChatController {

    private final ChatClient supportChatClient;

    @PostMapping("/support")
    public ResponseEntity<ApiResponse<ChatResponseDto>> chat(
            @Valid @RequestBody ChatRequestDto request,
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {
            // Keep the same conversation memory going if the client already
            // has an id; otherwise start a fresh one (tie it to the logged-in
            // user when available, purely as a convenience default).
            String conversationId = (request.conversationId() != null && !request.conversationId().isBlank())
                    ? request.conversationId()
                    : (userDetail != null ? "user-" + userDetail.getId() + "-" + UUID.randomUUID()
                        : UUID.randomUUID().toString());

            String reply = supportChatClient.prompt()
                    .user(request.message())
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                    .call()
                    .content();

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "OK",
                    new ChatResponseDto(reply, conversationId)
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "Support chat is unavailable right now. Please try again shortly.")
            );
        }
    }
}
