package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.dto.request.EmailRequestDto;
import com.e_commerce.eCommerce.enums.ReminderType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    @Value("${app.mail.from:no-reply@example.com}")
    private String defaultFrom;

    // --- Reminder email tuning — templates niche resolveReminderTemplate() me set hain ---
    private static final String MISS_YOU_TEMPLATE = "email/miss-you-reminder";
    private static final String SPECIAL_OFFER_TEMPLATE = "email/special-offer-reminder";
    private static final String SPECIAL_OFFER_DISCOUNT_CODE = "WELCOME15";
    private static final int SPECIAL_OFFER_DISCOUNT_PERCENTAGE = 15;


    public void sendEmail(EmailRequestDto request) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    "UTF-8"
            );

            helper.setFrom(defaultFrom);
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            if (request.getCc() != null && !request.getCc().isEmpty()) {
                helper.setCc(request.getCc().toArray(new String[0]));
            }

            if (request.getBcc() != null && !request.getBcc().isEmpty()) {
                helper.setBcc(request.getBcc().toArray(new String[0]));
            }

            String body = resolveBody(request);
            boolean html = request.getTemplateName() != null
                    && !request.getTemplateName().isBlank()
                    || request.isHtmlEnabled();

            helper.setText(body, html);

            mailSender.send(message);
        } catch (MessagingException | MailException ex) {

            log.error(
                    "Failed to send email to {}: {}",
                    request.getTo(),
                    ex.getMessage(),
                    ex
            );

            throw new EmailSendException(
                    "Failed to send email to " + request.getTo(),
                    ex
            );
        }
    }

    @Async
    public void sendEmailAsync(EmailRequestDto request) {

        try {

            sendEmail(request);

        } catch (EmailSendException ex) {

            log.error(
                    "Async email send failed for {}: {}",
                    request.getTo(),
                    ex.getMessage(),
                    ex
            );
        }
    }


    private String resolveBody(EmailRequestDto request) {

        if (request.getTemplateName() != null
                && !request.getTemplateName().isBlank()) {

            Context context = new Context();

            Map<String, Object> variables =
                    request.getTemplateVariables();

            if (variables != null && !variables.isEmpty()) {
                context.setVariables(variables);
            }

            return templateEngine.process(
                    request.getTemplateName(),
                    context
            );
        }

        if (request.isHtmlEnabled()) {

            if (request.getHtml() == null
                    || request.getHtml().isBlank()) {

                throw new IllegalArgumentException(
                        "HTML email body cannot be empty when htmlEnabled=true"
                );
            }

            return request.getHtml();
        }


        if (request.getText() == null
                || request.getText().isBlank()) {

            throw new IllegalArgumentException(
                    "Email text body cannot be empty"
            );
        }

        return request.getText();
    }

    public void sendReminderEmail(String email, String tenantName, ReminderType typeToSend,String subdomain) {

        if (typeToSend == null) {
            log.warn("sendReminderEmail called with null ReminderType for {}, skipping", email);
            return;
        }
        String storeUrl= "https://"+subdomain;
        Map<String, Object> variables = new HashMap<>();
        variables.put("tenantName", tenantName);
        variables.put("email", email);
        variables.put("storeUrl",storeUrl);

        String subject;
        String templateName;

        if (typeToSend == ReminderType.SPECIAL_OFFER) {

            subject = tenantName + " — We miss you! Here's " + SPECIAL_OFFER_DISCOUNT_PERCENTAGE + "% off, just for you";
            templateName = SPECIAL_OFFER_TEMPLATE;
            variables.put("discountCode", SPECIAL_OFFER_DISCOUNT_CODE);
            variables.put("discountPercentage", SPECIAL_OFFER_DISCOUNT_PERCENTAGE);

        } else {

            subject = "It's been a while — see what's new at " + tenantName;
            templateName = MISS_YOU_TEMPLATE;
        }

        EmailRequestDto request = EmailRequestDto.builder()
                .to(email)
                .subject(subject)
                .templateName(templateName)
                .templateVariables(variables)
                .build();

        sendEmailAsync(request);
    }

    public static class EmailSendException extends RuntimeException {

        public EmailSendException(
                String message,
                Throwable cause
        ) {
            super(message, cause);
        }
    }
}