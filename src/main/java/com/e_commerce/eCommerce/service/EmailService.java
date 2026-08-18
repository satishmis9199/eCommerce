package com.e_commerce.eCommerce.service;

import com.e_commerce.eCommerce.dto.request.EmailRequestDto;
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

import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    @Value("${app.mail.from:no-reply@example.com}")
    private String defaultFrom;


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

    public static class EmailSendException extends RuntimeException {

        public EmailSendException(
                String message,
                Throwable cause
        ) {
            super(message, cause);
        }
    }
}