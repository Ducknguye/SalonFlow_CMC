#!/bin/bash
# Helmet Headers Testing Script

echo "======================================"
echo "Helmet Headers Testing"
echo "======================================"
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

SERVER="http://localhost:8080"
ENDPOINT="/test/public"

echo -e "${BLUE}Testing headers from: ${SERVER}${ENDPOINT}${NC}\n"

# Fetch headers
curl -i -s "$SERVER$ENDPOINT" | head -30

echo ""
echo -e "${YELLOW}Headers Checklist:${NC}"
echo ""

# Test each header
test_header() {
    local header=$1
    local description=$2
    local response=$(curl -i -s "$SERVER$ENDPOINT" | grep -i "^$header:")
    
    if [ -z "$response" ]; then
        echo -e "${YELLOW}✗ $header${NC} - $description"
    else
        echo -e "${GREEN}✓ $header${NC}"
        echo "  $response"
    fi
    echo ""
}

# Test all Helmet headers
test_header "Content-Security-Policy" "Ngăn XSS & Injection"
test_header "X-Content-Type-Options" "Ngăn MIME-type sniffing"
test_header "X-Frame-Options" "Ngăn Clickjacking"
test_header "X-XSS-Protection" "XSS filter (legacy)"
test_header "Strict-Transport-Security" "Yêu cầu HTTPS"
test_header "Referrer-Policy" "Kiểm soát Referer"
test_header "Permissions-Policy" "Disable browser features"
test_header "Cross-Origin-Opener-Policy" "Isolate window context"
test_header "Cross-Origin-Embedder-Policy" "Require CORP headers"
test_header "Cross-Origin-Resource-Policy" "Cross-origin resource sharing"
test_header "X-Powered-By" "Server info (should be empty)"

echo ""
echo -e "${BLUE}======================================"
echo "Full Response Headers:"
echo "======================================${NC}\n"

curl -i -s "$SERVER$ENDPOINT" | grep "^[A-Za-z-]*:" | sort

echo ""
echo -e "${GREEN}Done!${NC}"
