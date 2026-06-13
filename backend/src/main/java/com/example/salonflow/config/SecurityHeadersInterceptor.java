package com.example.salonflow.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Helmet.js-like Security Headers Interceptor
 * 
 * Tương tự Helmet.js (Express.js), interceptor này thêm các HTTP security headers
 * để bảo vệ ứng dụng khỏi các tấn công phổ biến:
 * 
 * ✓ Content-Security-Policy - Ngăn chặn XSS, clickjacking
 * ✓ X-Content-Type-Options - Ngăn MIME-type sniffing
 * ✓ X-Frame-Options - Ngăn Clickjacking
 * ✓ X-XSS-Protection - Kích hoạt XSS filter
 * ✓ Strict-Transport-Security - Yêu cầu HTTPS
 * ✓ Referrer-Policy - Kiểm soát referrer info
 * ✓ Permissions-Policy - Kiểm soát browser features
 * ✓ Cross-Origin-Opener-Policy - Isolate window context
 * ✓ Cross-Origin-Embedder-Policy - Require CORP headers
 * ✓ Cross-Origin-Resource-Policy - Kiểm soát resource sharing
 */
@Slf4j
public class SecurityHeadersInterceptor implements HandlerInterceptor {

    /**
     * Environment - development hoặc production
     * Có thể được set từ application.properties
     */
    private String environment = "development";

    public SecurityHeadersInterceptor(String environment) {
        this.environment = environment;
    }

    public SecurityHeadersInterceptor() {
        this("development");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        
        // ==================== CSRF Protection ====================
        // X-CSRF-Token header name (được xử lý bởi Spring Security)
        
        // ==================== Content Security Policy ====================
        setContentSecurityPolicy(response);
        
        // ==================== X-Content-Type-Options ====================
        // Ngăn chặn MIME-type sniffing attacks
        response.setHeader("X-Content-Type-Options", "nosniff");
        
        // ==================== X-Frame-Options ====================
        // Ngăn chặn Clickjacking (UI Redressing) attacks
        response.setHeader("X-Frame-Options", "DENY");
        
        // ==================== X-XSS-Protection ====================
        // Kích hoạt XSS filter trên IE/Edge/older browsers
        // Modern browsers bỏ qua header này
        response.setHeader("X-XSS-Protection", "1; mode=block");
        
        // ==================== Strict-Transport-Security ====================
        // HSTS - Yêu cầu HTTPS cho tất cả requests (production only)
        if ("production".equalsIgnoreCase(environment)) {
            // maxAge: 1 năm (31536000 giây)
            response.setHeader("Strict-Transport-Security", 
                "max-age=31536000; includeSubDomains; preload");
        } else {
            // Development - 1 ngày
            response.setHeader("Strict-Transport-Security", 
                "max-age=86400; includeSubDomains");
        }
        
        // ==================== Referrer-Policy ====================
        // Kiểm soát những thông tin nào được gửi trong Referer header
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // ==================== Permissions-Policy ====================
        // Kiểm soát browser features (camera, microphone, v.v.)
        response.setHeader("Permissions-Policy",
            "accelerometer=(), " +
            "camera=(), " +
            "geolocation=(), " +
            "gyroscope=(), " +
            "magnetometer=(), " +
            "microphone=(), " +
            "payment=(), " +
            "usb=(), " +
            "vr=(), " +
            "xr-spatial-tracking=()"
        );
        
        // ==================== Cross-Origin-Opener-Policy ====================
        // Isolate window context - bảo vệ khỏi Spectre-like attacks
        response.setHeader("Cross-Origin-Opener-Policy", "same-origin");
        
        // ==================== Cross-Origin-Embedder-Policy ====================
        // Require CORP headers khi embed resources từ khác origin
        response.setHeader("Cross-Origin-Embedder-Policy", "require-corp");
        
        // ==================== Cross-Origin-Resource-Policy ====================
        // Kiểm soát request từ khác origin
        response.setHeader("Cross-Origin-Resource-Policy", "cross-origin");
        
        // ==================== Remove Server Header ====================
        // Không expose server version information
        response.setHeader("Server", "");
        response.setHeader("X-Powered-By", "");
        response.setHeader("X-AspNet-Version", "");
        response.setHeader("X-AspNetMvc-Version", "");
        
        // ==================== Logging ====================
        log.debug("Helmet-like security headers added to response for: {} {}", 
            request.getMethod(), request.getRequestURI());
        
        return true;
    }

    /**
     * Thiết lập Content-Security-Policy header
     * 
     * CSP ngăn chặn:
     * - Inline scripts/styles
     * - External scripts từ untrusted sources
     * - Unsafe eval()
     * - Data URLs
     */
    private void setContentSecurityPolicy(HttpServletResponse response) {
        
        String csp;
        
        if ("production".equalsIgnoreCase(environment)) {
            // Strict policy cho production
            csp = "default-src 'self'; " +
                  "script-src 'self' https:; " +
                  "style-src 'self' https:; " +
                  "img-src 'self' https: data:; " +
                  "font-src 'self' https: data:; " +
                  "connect-src 'self' https:; " +
                  "media-src 'self'; " +
                  "object-src 'none'; " +
                  "frame-ancestors 'none'; " +
                  "base-uri 'self'; " +
                  "form-action 'self'; " +
                  "upgrade-insecure-requests";
        } else {
            // Flexible policy cho development
            csp = "default-src 'self'; " +
                  "script-src 'self' 'unsafe-inline' 'unsafe-eval' http://localhost:*; " +
                  "style-src 'self' 'unsafe-inline' http://localhost:*; " +
                  "img-src 'self' data: https: http://localhost:*; " +
                  "font-src 'self' data: http://localhost:*; " +
                  "connect-src 'self' http://localhost:* ws://localhost:*; " +
                  "media-src 'self'; " +
                  "object-src 'none'; " +
                  "frame-ancestors 'self'; " +
                  "base-uri 'self'; " +
                  "form-action 'self'";
        }
        
        response.setHeader("Content-Security-Policy", csp);
        
        // CSP Report-Only (cho testing, không block requests)
        // response.setHeader("Content-Security-Policy-Report-Only", csp);
    }

    /**
     * Getter/Setter để có thể thay đổi environment
     */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getEnvironment() {
        return environment;
    }
}

