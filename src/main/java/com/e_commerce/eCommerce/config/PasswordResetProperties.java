package com.e_commerce.eCommerce.config;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;
@Configuration
@ConfigurationProperties(prefix = "app.password-reset")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PasswordResetProperties {
    private String frontendBaseUrl;
    private Duration tokenExpiry = Duration.ofHours(24);
    private int tokenByteLength = 32;
}