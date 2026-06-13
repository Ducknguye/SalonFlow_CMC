# Helmet Headers Testing Script (PowerShell - Windows)

$SERVER = "http://localhost:8080"
$ENDPOINT = "/test/public"

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Helmet Headers Testing" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Testing headers from: $SERVER$ENDPOINT" -ForegroundColor Blue
Write-Host ""

# Fetch headers
try {
    $response = Invoke-WebRequest -Uri "$SERVER$ENDPOINT" -Method Get -ErrorAction SilentlyContinue
    
    Write-Host "Status Code: $($response.StatusCode)" -ForegroundColor Green
    Write-Host ""
    
    Write-Host "Response Headers:" -ForegroundColor Yellow
    Write-Host ""
    
    # Display all headers
    $response.Headers.GetEnumerator() | ForEach-Object {
        Write-Host "$($_.Key): $($_.Value)" -ForegroundColor Green
    }
    
    Write-Host ""
    Write-Host "===== HELMET HEADERS VERIFICATION =====" -ForegroundColor Cyan
    Write-Host ""
    
    # Check specific headers
    $headers = @(
        @{ Name = "Content-Security-Policy"; Desc = "Ngăn XSS & Injection" },
        @{ Name = "X-Content-Type-Options"; Desc = "Ngăn MIME-type sniffing" },
        @{ Name = "X-Frame-Options"; Desc = "Ngăn Clickjacking" },
        @{ Name = "X-XSS-Protection"; Desc = "XSS filter (legacy)" },
        @{ Name = "Strict-Transport-Security"; Desc = "Yêu cầu HTTPS" },
        @{ Name = "Referrer-Policy"; Desc = "Kiểm soát Referer" },
        @{ Name = "Permissions-Policy"; Desc = "Disable browser features" },
        @{ Name = "Cross-Origin-Opener-Policy"; Desc = "Isolate window context" },
        @{ Name = "Cross-Origin-Embedder-Policy"; Desc = "Require CORP headers" },
        @{ Name = "Cross-Origin-Resource-Policy"; Desc = "Cross-origin resource sharing" }
    )
    
    foreach ($header in $headers) {
        $value = $response.Headers[$header.Name]
        if ($value) {
            Write-Host "✓ $($header.Name)" -ForegroundColor Green
            Write-Host "  Description: $($header.Desc)"
            Write-Host "  Value: $value"
        } else {
            Write-Host "✗ $($header.Name)" -ForegroundColor Yellow
            Write-Host "  Description: $($header.Desc)"
            Write-Host "  Status: NOT FOUND"
        }
        Write-Host ""
    }
    
    # Check if Server header is removed
    $serverHeader = $response.Headers["Server"]
    if ($serverHeader) {
        Write-Host "✗ Server header should be empty" -ForegroundColor Yellow
        Write-Host "  Current value: $serverHeader" -ForegroundColor Yellow
    } else {
        Write-Host "✓ Server header is properly hidden" -ForegroundColor Green
    }
    
} catch {
    Write-Host "Error connecting to server: $_" -ForegroundColor Red
    Write-Host "Make sure the server is running at $SERVER" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "====== Testing Complete! ======" -ForegroundColor Cyan
