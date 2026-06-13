package com.example.salonflow.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Helmet.js-like Security Headers Configuration
 * 
 * Cấu hình HTTP security headers tương tự Helmet.js middleware
 * Bảo vệ ứng dụng khỏi các tấn công phổ biến như:
 * - MIME-type sniffing
 * - Clickjacking
 * - XSS (Cross-Site Scripting)
 * - CSRF (Cross-Site Request Forgery)
 * - Spectre/Meltdown attacks
 */
@Configuration
public class SecurityHeadersConfig {

    /**
     * Environment: development hoặc production
     * Được đọc từ application.properties: app.environment
     */
    @Value("${app.environment:development}")
    private String environment;

    /**
     * Cấu hình interceptor để thêm security headers
     */
    @Bean
    public WebMvcConfigurer securityHeadersConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                SecurityHeadersInterceptor interceptor = new SecurityHeadersInterceptor(environment);
                registry.addInterceptor(interceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns(
                        "/static/**",
                        "/public/**",
                        "/assets/**"
                    );
            }
        };
    }
}

