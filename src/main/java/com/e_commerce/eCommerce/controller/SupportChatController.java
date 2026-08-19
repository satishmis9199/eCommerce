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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class SupportChatController {

    private final ChatClient supportChatClient;

    @PostMapping(value = "/support",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ChatResponseDto>> chat(
            @Valid @RequestBody ChatRequestDto request,
            @AuthenticationPrincipal CustomUserDetail userDetail) {

        try {
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
