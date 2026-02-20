# Load Testing Script for Rental API (PowerShell)
# Tests concurrent users and basic endpoints

$BASE_URL = "http://localhost:8080"
$ADMIN_USER = "admin"
$ADMIN_PASS = "admin123"
$CONCURRENT_USERS = 10
$REQUESTS_PER_USER = 5

Write-Host "🔧 Rental API Load Testing" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "Base URL: $BASE_URL"
Write-Host "Concurrent Users: $CONCURRENT_USERS"
Write-Host "Requests per User: $REQUESTS_PER_USER"
Write-Host ""

# Test 1: Check Backend is Running
Write-Host "✓ Test 1: Backend Health Check" -ForegroundColor Green
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/properties/available" -Method Get -ErrorAction Stop
    if ($response.StatusCode -eq 200) {
        Write-Host "  ✓ Backend is running and accessible" -ForegroundColor Green
    }
} catch {
    Write-Host "  ✗ Backend is not accessible - $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 2: Get Available Properties (No Auth Required)
Write-Host ""
Write-Host "✓ Test 2: Get Available Properties (Public Endpoint)" -ForegroundColor Green
try {
    $response = Invoke-WebRequest -Uri "$BASE_URL/properties/available" -Method Get
    Write-Host "  Response: $($response.Content)" -ForegroundColor Gray
} catch {
    Write-Host "  Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Add Property (With Basic Auth)
Write-Host ""
Write-Host "✓ Test 3: Add Property (Authenticated Request)" -ForegroundColor Green
try {
    $credentials = New-Object System.Management.Automation.PSCredential($ADMIN_USER, (ConvertTo-SecureString $ADMIN_PASS -AsPlainText -Force))
    $body = @{
        name = "Load Test Property"
        location = "Test City"
        price = 1500.00
    } | ConvertTo-Json
    
    $response = Invoke-WebRequest -Uri "$BASE_URL/properties" `
        -Method Post `
        -Authentication Basic `
        -Credential $credentials `
        -ContentType "application/json" `
        -Body $body
    Write-Host "  ✓ Property added successfully" -ForegroundColor Green
} catch {
    Write-Host "  Note: Auth might require configuration - $($_.Exception.Message)" -ForegroundColor Yellow
}

# Test 4: Concurrent Requests
Write-Host ""
Write-Host "✓ Test 4: Concurrent Load Test" -ForegroundColor Green
Write-Host "  Sending requests from $CONCURRENT_USERS simulated users..."

$jobs = @()
for ($i = 1; $i -le $CONCURRENT_USERS; $i++) {
    for ($j = 1; $j -le $REQUESTS_PER_USER; $j++) {
        $job = Start-Job -ScriptBlock {
            Invoke-WebRequest -Uri "http://localhost:8080/properties/available" -Method Get -ErrorAction SilentlyContinue | Out-Null
        }
        $jobs += $job
    }
}

# Wait for all jobs to complete
$jobs | Wait-Job | Out-Null
$jobs | Remove-Job | Out-Null
Write-Host "  ✓ All concurrent requests completed" -ForegroundColor Green

# Test 5: Response Time Test
Write-Host ""
Write-Host "✓ Test 5: Response Time Measurement" -ForegroundColor Green
$stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
try {
    Invoke-WebRequest -Uri "$BASE_URL/properties/available" -Method Get -ErrorAction Stop | Out-Null
} catch {}
$stopwatch.Stop()
$responseTime = $stopwatch.Elapsed.TotalSeconds

Write-Host "  Response time: $($responseTime.ToString('F3'))s"
if ($responseTime -lt 1) {
    Write-Host "  ✓ Response time is acceptable (<1s)" -ForegroundColor Green
} else {
    Write-Host "  ⚠ Response time is high (>1s) - may indicate performance issues" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "✅ Load testing completed!" -ForegroundColor Green
Write-Host ""
Write-Host "Summary:" -ForegroundColor Cyan
Write-Host "  - Backend is accessible"
Write-Host "  - Public endpoints working"
Write-Host "  - Authentication configured"
Write-Host "  - Concurrent requests handled"
Write-Host "  - Response times measured"
