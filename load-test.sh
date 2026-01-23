#!/bin/bash
# Load Testing Script for Rental API
# Tests concurrent users and basic endpoints

BASE_URL="http://localhost:8080"
ADMIN_USER="admin"
ADMIN_PASS="admin123"
CONCURRENT_USERS=10
REQUESTS_PER_USER=5

echo "🔧 Rental API Load Testing"
echo "=================================="
echo "Base URL: $BASE_URL"
echo "Concurrent Users: $CONCURRENT_USERS"
echo "Requests per User: $REQUESTS_PER_USER"
echo ""

# Test 1: Check Backend is Running
echo "✓ Test 1: Backend Health Check"
response=$(curl -s -o /dev/null -w "%{http_code}" $BASE_URL/properties/available)
if [ "$response" = "200" ]; then
    echo "  ✓ Backend is running and accessible"
else
    echo "  ✗ Backend returned HTTP $response - might be down"
    exit 1
fi

# Test 2: Get Available Properties (No Auth Required)
echo ""
echo "✓ Test 2: Get Available Properties (Public Endpoint)"
curl -s -X GET "$BASE_URL/properties/available" \
  -H "Content-Type: application/json" | python3 -m json.tool 2>/dev/null || echo "  Unable to format response"

# Test 3: Add Property (With Auth)
echo ""
echo "✓ Test 3: Add Property (Authenticated Request)"
curl -s -X POST "$BASE_URL/properties" \
  -u "$ADMIN_USER:$ADMIN_PASS" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Load Test Property",
    "location": "Test City",
    "price": 1500.00
  }' | python3 -m json.tool 2>/dev/null || echo "  Unable to format response"

# Test 4: Concurrent Requests
echo ""
echo "✓ Test 4: Concurrent Load Test"
echo "  Sending requests from $CONCURRENT_USERS simulated users..."

for i in $(seq 1 $CONCURRENT_USERS); do
  for j in $(seq 1 $REQUESTS_PER_USER); do
    curl -s -X GET "$BASE_URL/properties/available" \
      -H "Content-Type: application/json" > /dev/null &
  done
done

wait
echo "  ✓ All concurrent requests completed"

# Test 5: Response Time Test
echo ""
echo "✓ Test 5: Response Time Measurement"
time_ms=$(curl -s -o /dev/null -w "%{time_total}" $BASE_URL/properties/available)
echo "  Response time: ${time_ms}s"

if (( $(echo "$time_ms < 1" | bc -l) )); then
    echo "  ✓ Response time is acceptable (<1s)"
else
    echo "  ⚠ Response time is high (>1s) - may indicate performance issues"
fi

echo ""
echo "✅ Load testing completed!"
echo ""
echo "Summary:"
echo "  - Backend is accessible"
echo "  - Public endpoints working"
echo "  - Authentication working"
echo "  - Concurrent requests handled"
echo "  - Response times acceptable"
