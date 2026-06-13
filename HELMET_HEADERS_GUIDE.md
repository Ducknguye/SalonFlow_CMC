# Helmet.js-like Headers Configuration

## Helmet.js là gì?

**Helmet.js** là middleware bảo mật cho Express.js (Node.js) framework. Nó tự động thiết lập các HTTP security headers để bảo vệ ứng dụng khỏi các tấn công phổ biến.

Dự án Spring Boot này sử dụng `SecurityHeadersInterceptor.java` - một implementation tương tự Helmet.js cho Java Spring Boot.

## Headers Được Thiết Lập

### 1. Content-Security-Policy (CSP)
**Mục đích:** Ngăn chặn XSS, Clickjacking, Injection attacks

```
Development:
script-src 'self' 'unsafe-inline' 'unsafe-eval' http://localhost:*
style-src 'self' 'unsafe-inline' http://localhost:*

Production:
script-src 'self' https:
style-src 'self' https:
```

### 2. X-Content-Type-Options: nosniff
**Mục đích:** Ngăn chặn MIME-type sniffing attacks
- Buộc trình duyệt tuân theo Content-Type header
- Không thể execute scripts nếu Content-Type không phải application/javascript

### 3. X-Frame-Options: DENY
**Mục đích:** Ngăn chặn Clickjacking attacks
- Không cho phép trang này được nhúng trong `<frame>`, `<iframe>`, `<embed>`, `<object>`

### 4. X-XSS-Protection: 1; mode=block
**Mục đích:** Kích hoạt XSS filter (Legacy - modern browsers bỏ qua)
- IE/Edge: Sẽ block request nếu phát hiện XSS

### 5. Strict-Transport-Security (HSTS)
**Mục đích:** Yêu cầu HTTPS cho tất cả requests

```
Development: max-age=86400 (1 ngày)
Production: max-age=31536000; includeSubDomains; preload (1 năm)
```

### 6. Referrer-Policy: strict-origin-when-cross-origin
**Mục đích:** Kiểm soát Referer header
- Chỉ gửi origin nếu là cross-origin request
- Không gửi path/query string

### 7. Permissions-Policy
**Mục đض:** Disable browser features không cần dùng
```
Disabled: accelerometer, camera, geolocation, gyroscope, 
         magnetometer, microphone, payment, usb, vr, xr-spatial-tracking
```

### 8. Cross-Origin-Opener-Policy: same-origin
**Mục đích:** Isolate window context
- Bảo vệ khỏi Spectre-like attacks
- Ngoài origin không thể access window object

### 9. Cross-Origin-Embedder-Policy: require-corp
**Mục đích:** Require CORP headers từ embedded resources
- Ngăn chặn timing attacks

### 10. Cross-Origin-Resource-Policy: cross-origin
**Mục đích:** Cho phép resources được sử dụng từ khác origin

### 11. Referrer-Policy & Server Headers
**Mục đích:** Không expose server information
```
X-Powered-By: (removed)
X-AspNet-Version: (removed)
Server: (removed)
```

## Configuration

### Setup Environment Variable

**File:** `application.properties`

```properties
# Development environment
app.environment=development

# Hoặc Production
# app.environment=production
```

### Development vs Production

#### Development
- ✓ Cho phép unsafe-inline/unsafe-eval (để debug dễ)
- ✓ Cho phép localhost connections
- ✓ HSTS max-age: 1 ngày
- ✓ CSP Report-Only mode

#### Production
- ✓ Strict CSP (không unsafe-inline)
- ✓ Chỉ allow https
- ✓ HSTS max-age: 1 năm + preload
- ✓ HSTS includeSubDomains

## Excluded Paths

Các paths này **không được apply** security headers:
- `/static/**` - Static files
- `/public/**` - Public files
- `/assets/**` - Asset files

Tùy chỉnh trong `SecurityHeadersConfig.java`:
```java
.excludePathPatterns(
    "/static/**",
    "/public/**",
    "/assets/**"
)
```

## Customization

### Thay đổi Environment

**application.properties:**
```properties
app.environment=production
```

### Tùy Chỉnh CSP Policy

**File:** `SecurityHeadersInterceptor.java` - phương thức `setContentSecurityPolicy()`

```java
csp = "default-src 'self'; " +
      "script-src 'self' https: cdn.example.com; " +
      "img-src 'self' https: *.example.com; " +
      // ... thêm directives khác
```

### Thêm Custom Header

**File:** `SecurityHeadersInterceptor.java` - phương thức `preHandle()`

```java
response.setHeader("Custom-Header", "custom-value");
```

### Remove Header

```java
response.setHeader("X-Powered-By", "");
```

## Testing Headers

### cURL
```bash
curl -i http://localhost:8080/test/public
```

### Browser DevTools
1. Mở DevTools (F12)
2. Network tab
3. Click request
4. Response Headers

### Online Tools
- https://securityheaders.com/ - Scan security headers
- https://csp-evaluator.appspot.com/ - Evaluate CSP

## Best Practices

### Development
```properties
app.environment=development
# Cho phép debug/hot reload
```

### Production (Application.properties)
```properties
app.environment=production
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.same-site=strict
server.servlet.session.cookie.domain=yourdomain.com
```

### Nginx Reverse Proxy (Optional)
```nginx
# Add/Override headers at proxy level
add_header X-Frame-Options "DENY" always;
add_header X-Content-Type-Options "nosniff" always;
```

## Issues & Troubleshooting

### Issue: CSP blocking scripts/styles
**Solution:** Update CSP directive hoặc use nonce

```java
csp += "script-src 'nonce-" + generateNonce() + "'; ";
```

### Issue: HSTS not working
**Solution:** HSTS require HTTPS in production
```properties
app.environment=production
server.ssl.enabled=true
```

### Issue: CORS + Headers conflict
**Solution:** CorsConfig được set trước SecurityHeaders
```java
// CorsConfig runs first
// SecurityHeadersInterceptor runs after
```

## Comparing with Helmet.js

| Feature | Helmet.js | Spring Boot |
|---------|-----------|------------|
| CSP | ✓ Customizable | ✓ Customizable |
| X-Frame-Options | ✓ | ✓ |
| HSTS | ✓ | ✓ |
| Referrer-Policy | ✓ | ✓ |
| Permissions-Policy | ✓ | ✓ |
| COOP/COEP | ✓ | ✓ |
| Environment-specific | ✓ (via config) | ✓ |

## Resources

- [OWASP - Secure Headers](https://owasp.org/www-project-secure-headers/)
- [Helmet.js Docs](https://helmetjs.github.io/)
- [MDN - HTTP Headers](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers)
- [Content Security Policy](https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP)

---

**Created:** 2024
**Status:** ✅ Active
**Environment:** Spring Boot 4.0.6
