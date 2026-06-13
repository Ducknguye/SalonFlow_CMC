# NestJS demo: throttler + helmet + CSRF

This demo implements `@nestjs/throttler` with limit 100 requests per minute per IP, `helmet()` middleware for security headers, cookie-based CSRF using `csurf`, and an e2e test that sends 101 requests to verify a `429` on the 101st.

Quick run:

```bash
cd nestjs-demo
npm install
npm test
```
