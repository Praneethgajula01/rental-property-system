# Rental Property System - Complete Project Guide

## 📋 Project Overview

This is a **Spring Boot REST API** for managing rental properties. Users can:
- Add properties to the system
- View all properties
- View only available properties
- Book/reserve a property (mark as unavailable)

**Tech Stack:**
- Backend: Spring Boot 3.5.6 (Java 17)
- Database: MySQL 8 (Docker) / H2 (local dev)
- Build Tool: Maven
- Container: Docker + Docker Compose

---

## 🚀 Quick Start (5 minutes)

### Option 1: Run with Docker (Recommended)

**Prerequisites:** Docker and Docker Compose installed

```powershell
cd c:\project\rental
docker-compose up --build
```

**What happens:**
1. Maven builds the Spring Boot JAR
2. Docker builds the application image
3. MySQL container starts and initializes
4. Spring app connects to MySQL
5. API available at `http://localhost:8080`

**Expected output:**
```
rental-app  | Started RentalApplication in X.XXX seconds
mysql-db    | ready for connections
```

### Option 2: Run Locally (Development)

**Prerequisites:** Java 17+, Maven, MySQL (optional)

```powershell
cd c:\project\rental

# Build the project
.\mvnw.cmd clean package -DskipTests

# Run the application (uses H2 in-memory database)
.\mvnw.cmd spring-boot:run
```

**API available at:** `http://localhost:8080`

---

## 📁 Project Structure

```
rental/
├── src/
│   ├── main/
│   │   ├── java/com/example/rental/
│   │   │   ├── RentalApplication.java          # Main entry point
│   │   │   ├── controller/
│   │   │   │   └── PropertyController.java     # REST endpoints
│   │   │   ├── service/
│   │   │   │   └── PropertyService.java        # Business logic
│   │   │   ├── model/
│   │   │   │   └── Property.java               # JPA entity
│   │   │   └── repo/
│   │   │       └── PropertyRepository.java     # Database access
│   │   └── resources/
│   │       ├── application.properties          # H2 config (local dev)
│   │       └── application-mysql.properties    # MySQL config (Docker)
│   └── test/
│       └── java/com/example/rental/
│           └── RentalApplicationTests.java     # Tests
├── pom.xml                                     # Maven dependencies
├── Dockerfile                                  # Container image definition
├── docker-compose.yml                          # Multi-container orchestration
└── mvnw, mvnw.cmd                             # Maven wrapper scripts
```

---

## 🔌 API Endpoints

### 1. Add a Property
```http
POST /properties
Content-Type: application/json

{
  "name": "Beach House",
  "location": "Miami, FL",
  "price": 500.00,
  "available": true
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Beach House",
  "location": "Miami, FL",
  "price": 500.00,
  "available": true
}
```

**PowerShell Example:**
```powershell
$body = @{
    name = "Beach House"
    location = "Miami, FL"
    price = 500
    available = $true
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/properties" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
```

### 2. Get All Properties
```http
GET /properties
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Beach House",
    "location": "Miami, FL",
    "price": 500.00,
    "available": true
  },
  {
    "id": 2,
    "name": "Mountain Cabin",
    "location": "Denver, CO",
    "price": 300.00,
    "available": false
  }
]
```

**PowerShell Example:**
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/properties" | ConvertFrom-Json
```

### 3. Get Available Properties Only
```http
GET /properties/available
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Beach House",
    "location": "Miami, FL",
    "price": 500.00,
    "available": true
  }
]
```

**PowerShell Example:**
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/properties/available" | ConvertFrom-Json
```

### 4. Book a Property
```http
POST /properties/{id}/book
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Beach House",
  "location": "Miami, FL",
  "price": 500.00,
  "available": false
}
```

**Error Response (404 Not Found):**
```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Property not found"
}
```

**PowerShell Example:**
```powershell
# Book property with ID 1
Invoke-WebRequest -Uri "http://localhost:8080/properties/1/book" `
  -Method POST
```

---

## 🐳 Docker Deployment Details

### docker-compose.yml Structure

**MySQL Service:**
- Image: mysql:8
- Container: mysql-db
- Port: 3306 (mapped from host)
- Root password: root
- Database: rentaldb
- Volume: db_data (persistent storage)
- Health check: mysqladmin ping (verifies DB is ready)

**Spring Boot App Service:**
- Build: From Dockerfile
- Container: rental-app
- Port: 8080 (REST API)
- Profile: mysql (activates MySQL config)
- Depends on: db (waits for MySQL health check)
- Environment variables: Database URL, credentials, JPA settings

### Dockerfile Breakdown

```dockerfile
FROM openjdk:17-jdk-slim              # Base image (Java 17)
WORKDIR /app                          # Set working directory
COPY target/*jar* app.jar             # Copy built JAR from target/
EXPOSE 8080                           # Expose port 8080
ENTRYPOINT ["java", "-jar", "app.jar"] # Run the JAR
```

---

## 🔄 Complete Workflow Example

### Step 1: Start the System
```powershell
cd c:\project\rental
docker-compose up --build
```

Wait for output:
```
rental-app  | Started RentalApplication in 5.234 seconds
mysql-db    | ready for connections
```

### Step 2: Add Properties
```powershell
# Add Beach House
$body1 = @{name="Beach House"; location="Miami"; price=500; available=$true} | ConvertTo-Json
Invoke-WebRequest -Uri "http://localhost:8080/properties" -Method POST -ContentType "application/json" -Body $body1

# Add Mountain Cabin
$body2 = @{name="Mountain Cabin"; location="Denver"; price=300; available=$true} | ConvertTo-Json
Invoke-WebRequest -Uri "http://localhost:8080/properties" -Method POST -ContentType "application/json" -Body $body2

# Add City Apartment
$body3 = @{name="City Apartment"; location="NYC"; price=800; available=$true} | ConvertTo-Json
Invoke-WebRequest -Uri "http://localhost:8080/properties" -Method POST -ContentType "application/json" -Body $body3
```

### Step 3: View All Properties
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/properties" | ConvertFrom-Json | Format-Table
```

Output:
```
id name               location   price available
-- ----               --------   ----- ---------
1  Beach House        Miami        500 True
2  Mountain Cabin     Denver       300 True
3  City Apartment     NYC          800 True
```

### Step 4: View Available Properties
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/properties/available" | ConvertFrom-Json | Format-Table
```

### Step 5: Book Properties
```powershell
# Book property 1
Invoke-WebRequest -Uri "http://localhost:8080/properties/1/book" -Method POST

# Book property 2
Invoke-WebRequest -Uri "http://localhost:8080/properties/2/book" -Method POST
```

### Step 6: Check Available Properties Again
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/properties/available" | ConvertFrom-Json | Format-Table
```

Output (only City Apartment remains available):
```
id name           location price available
-- ----           -------- ----- ---------
3  City Apartment NYC        800 True
```

---

## 🔍 Understanding the Code

### Property Entity (Model)
**File:** `src/main/java/com/example/rental/model/Property.java`

Represents a rental property with:
- `id`: Unique identifier (auto-generated)
- `name`: Property name
- `location`: Location/address
- `price`: Daily rental price
- `available`: Boolean flag (true = can be booked)

### PropertyRepository (Data Access)
**File:** `src/main/java/com/example/rental/repo/PropertyRepository.java`

Provides database queries:
- `findAll()`: Get all properties
- `findById(id)`: Get property by ID
- `findByAvailableTrue()`: Custom query for available properties
- `save(property)`: Save/update property

### PropertyService (Business Logic)
**File:** `src/main/java/com/example/rental/service/PropertyService.java`

Core business methods:
- `add(Property)`: Add new property
- `all()`: List all properties
- `available()`: List available properties
- `book(id)`: Book a property (mark unavailable)
  - Throws 404 if property not found
  - Updates `available` flag to false

### PropertyController (REST Endpoints)
**File:** `src/main/java/com/example/rental/controller/PropertyController.java`

Exposes HTTP endpoints:
- `@PostMapping` → `add()`
- `@GetMapping` → `all()`
- `@GetMapping("/available")` → `available()`
- `@PostMapping("/{id}/book")` → `book(id)`

---

## 📊 Database Schema

**properties table (auto-created):**

```sql
CREATE TABLE properties (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255),
  location VARCHAR(255),
  price DECIMAL(10,2),
  available BOOLEAN
);
```

**Sample Data (after running examples):**

| id | name | location | price | available |
|----|------|----------|-------|-----------|
| 1 | Beach House | Miami | 500.00 | false |
| 2 | Mountain Cabin | Denver | 300.00 | false |
| 3 | City Apartment | NYC | 800.00 | true |

---

## 🛠️ Development Tips

### Run Tests
```powershell
.\mvnw.cmd test
```

### View Logs
```powershell
# If running with docker-compose
docker-compose logs -f rental-app

# Watch MySQL logs
docker-compose logs -f mysql-db
```

### Stop Docker Containers
```powershell
docker-compose down

# Remove volumes (delete database)
docker-compose down -v
```

### Run Locally with H2 (In-Memory DB)
```powershell
# Data is lost on restart
.\mvnw.cmd spring-boot:run

# H2 console available at http://localhost:8080/h2-console
```

### View MySQL Data Directly
```powershell
# Access MySQL container
docker exec -it mysql-db mysql -u root -p
# Enter password: root

# In MySQL shell
use rentaldb;
select * from properties;
```

---

## 🐛 Troubleshooting

### Issue: "Address already in use" port 8080
```powershell
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID with actual process ID)
taskkill /PID <PID> /F
```

### Issue: MySQL container not starting
```powershell
# Check logs
docker-compose logs mysql-db

# Restart services
docker-compose restart
```

### Issue: "Property not found" when booking
- Verify the ID exists: `GET /properties`
- Property may already be booked (available=false)
- Try booking a different property

### Issue: H2 database "file may be locked"
```powershell
# Delete H2 database files and restart
Remove-Item -Path "testdb.*" -Force
```

---

## 📦 Project Dependencies

**Core:**
- spring-boot-starter-data-jpa (database access)
- spring-boot-starter-web (REST API)

**Databases:**
- h2 (in-memory, local development)
- mysql-connector-j (MySQL driver)

**Documentation:**
- springdoc-openapi-starter-webmvc-ui (Swagger/OpenAPI)

**Testing:**
- spring-boot-starter-test

---

## ✅ Checklist: Getting Started

- [ ] Docker & Docker Compose installed
- [ ] Navigate to `c:\project\rental`
- [ ] Run `docker-compose up --build`
- [ ] Wait for "Started RentalApplication" message
- [ ] Test API endpoints (use examples above)
- [ ] Try the complete workflow
- [ ] View database: `docker exec -it mysql-db mysql -u root -p`
- [ ] Stop: `docker-compose down`

---

## 🎯 Next Steps (Optional Enhancements)

1. **Add Validation:**
   - @NotBlank on String fields
   - @Positive on price
   - @NotNull on required fields

2. **Add Tests:**
   - Unit tests for PropertyService
   - Integration tests for PropertyController
   - Test database operations

3. **Add Security:**
   - JWT authentication
   - Role-based access control
   - API key validation

4. **Add Features:**
   - User authentication & profiles
   - Booking history/reservations
   - Payment integration
   - Notifications

5. **Add Monitoring:**
   - Spring Boot Actuator
   - Prometheus metrics
   - ELK stack (Elasticsearch, Logstash, Kibana)

---

**Ready to go!** 🚀 Start with: `docker-compose up --build`
