package com.e_commerce.eCommerce.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDto {

    @NotBlank
    @Email
    private String to;

    private List<@Email String> cc;

    private List<@Email String> bcc;

    @NotBlank
    private String subject;

    private String text;

    private String html;

    @Builder.Default
    private boolean htmlEnabled = false;

    private String templateName;

    private Map<String, Object> templateVariables;
}