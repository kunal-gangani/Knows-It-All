# KnowItAll: P2P Skill Trading Platform

A revolutionary peer-to-peer (P2P) platform that enables users in semi-urban areas to trade skills using a **hybrid barter and token system**. Features GPS-based matchmaking, blockchain-inspired trust ledger, and micro-credentials for the gig economy.

## 🎯 Project Overview

**KnowItAll** monetizes time, creates digital skill records (micro-credentials), and facilitates hyper-local connectivity within 5km radius. Using a **blockchain-inspired ledger system**, all trades are tamper-proof and every user transaction is hashed, creating an immutable chain of trust.

### Key Features

| Feature | Description |
|---------|-------------|
| **Skill Radar** | GPS-based discovery of mentors within 5km |
| **Hybrid Trading** | Barter (1:1 skills) or Token-based (skill→points) |
| **Trust Ledger** | SHA-256 hashed transaction history (blockchain-inspired) |
| **Live Verification** | Video call integration for remote sessions |
| **QR Handshake** | In-person verification via QR code scan |
| **Skill Passport** | PDF certificate of learned/taught skills |
| **Token Escrow** | Safe token holding during active swaps |

---

## 📂 Project Structure

```
KnowItAll/
├── app/                                # Android Frontend (Kotlin)
│   ├── src/main/java/com/example/know_it_all/
│   │   ├── MainActivity.kt
│   │   ├── data/
│   │   │   ├── model/                 # Data classes (User, Skill, Swap, TrustLedger)
│   │   │   ├── local/                 # Room Database (DAO, Database, Converters)
│   │   │   ├── remote/                # Retrofit API Services
│   │   │   └── repository/            # Repository layer
│   │   ├── presentation/
│   │   │   ├── viewmodel/             # MVVM ViewModels
│   │   │   └── ui/
│   │   │       ├── screen/            # Composable screens
│   │   │       ├── navigation/        # Navigation graph
│   │   │       └── theme/
│   │   └── util/                      # Utilities (Hash, Token, Location)
│   └── build.gradle.kts
│
├── KnowItAll-Backend/                 # Spring Boot Backend (Java)
│   ├── src/main/java/com/example/knowitall/
│   │   ├── KnowItAllApplication.java
│   │   ├── config/                    # Spring configs (Security, CORS)
│   │   ├── controller/                # REST API endpoints
│   │   ├── service/                   # Business logic layer
│   │   ├── entity/                    # JPA entities
│   │   ├── repository/                # Spring Data JPA repositories
│   │   ├── dto/                       # Data Transfer Objects
│   │   └── security/                  # JWT authentication
│   ├── src/main/resources/
│   │   └── application.yml            # Configuration
│   ├── database/
│   │   ├── schema.sql                 # PostgreSQL schema
│   │   └── SCHEMA_DOCUMENTATION.md    # Database guide
│   └── pom.xml                        # Maven dependencies
│
└── README.md                          # This file
```

---

## 🚀 Getting Started

### Prerequisites

**Android Frontend:**
- Android Studio 2023.2+
- Kotlin 2.0.21+
- Android SDK 24+
- Gradle 9.0.1+

**Backend:**
- Java 17+
- Maven 3.8+
- PostgreSQL 12+ (or Supabase account)
- Spring Boot 3.2.0

---

## 📱 Android Frontend Setup

### Step 1: Clone and Open Project

```bash
# Open in Android Studio
cd c:\Users\KUNAL\AndroidStudioProjects\KnowItAll
# Open with Android Studio
```

### Step 2: Configure Backend URL

Edit [app/src/main/java/com/example/know_it_all/data/remote/RetrofitClient.kt](app/src/main/java/com/example/know_it_all/data/remote/RetrofitClient.kt):

```kotlin
private const val BASE_URL = "http://192.168.1.100:8080/api/v1/"  // Your backend URL
```

For **local dev**: Use `http://10.0.2.2:8080/api/v1/`

### Step 3: Build and Run

```bash
# In Android Studio Terminal
./gradlew build
./gradlew installDebug

# Or simply: Build > Make Project, then Run app
```

### Step 4: Grant Permissions

The app requires:
- **Location**: Fine and Coarse location for Skill Radar
- **Camera**: For QR code scanning
- **Internet**: For API communication

---

## 🖥️ Backend Setup

### Step 1: Clone Repository

```bash
cd c:\Users\KUNAL\KnowItAll-Backend
```

### Step 2: Configure Database

#### Option A: Local PostgreSQL

```bash
# Create database
createdb knowitall_db

# Run schema
psql knowitall_db -f database/schema.sql

# Update application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/knowitall_db
    username: postgres
    password: your_password
```

#### Option B: Supabase (Cloud)

1. Go to [supabase.com](https://supabase.com) and create project
2. Copy connection string
3. Go to SQL Editor → paste `database/schema.sql` → Execute
4. Update `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres
    username: postgres
    password: your_supabase_password
```

### Step 3: Generate JWT Secret

```bash
# Generate a 256-character random string for JWT
# Use an online generator or:
openssl rand -base64 256

# Add to application.yml
jwt:
  secret: your_generated_secret_here_minimum_32_characters
```

### Step 4: Build and Run

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run

# Or via IDE: Run KnowItAllApplication.java
```

Server starts at: `http://localhost:8080/api/v1`

---

## 📡 API Endpoints

### Authentication

```
POST   /auth/register      - Register new user
POST   /auth/login         - Login user
GET    /health             - Health check
```

### Users

```
GET    /users/profile      - Get current user profile
PUT    /users/location     - Update location (lat/long)
GET    /users/nearby       - Find nearby users (5km radius)
```

### Skills

```
POST   /skills             - Add new skill
GET    /skills/user/{id}   - Get user's skills
GET    /skills/search      - Search skills by name
```

### Swaps (Trades)

```
POST   /swap/request       - Request skill swap
GET    /swap/{id}          - Get swap details
PUT    /swap/{id}/accept   - Accept swap request
PUT    /swap/{id}/complete - Complete swap
POST   /swap/{id}/rating   - Rate completed swap
GET    /swap/user/active   - Get active swaps
GET    /swap/user/history  - Get swap history
```

### Trust Ledger

```
GET    /ledger/user/{id}        - Get user's transaction history
GET    /ledger/verify/{txId}    - Verify transaction integrity
GET    /ledger/trust-score/{id} - Calculate trust score
```

### Example Requests

**Register:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com", "password":"secure123", "name":"Alice"}'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com", "password":"secure123"}'
```

**Request Swap:**
```bash
curl -X POST "http://localhost:8080/api/v1/swap/request?mentorId=user1&learnerId=user2&mentorSkillId=1&swapType=BARTER" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🏗️ Architecture

### Android (Kotlin) - MVVM Pattern

```
View (Composable Screen)
        ↓
ViewModel (State Management)
        ↓
Repository (Data Access)
    ↙       ↘
LocalDB   Remote API
(Room)    (Retrofit)
    ↓         ↓
Entities   DTOs
```

**Data Flow:**
1. **UI** calls ViewModel
2. **ViewModel** calls Repository
3. **Repository** fetches from Room or Retrofit
4. Data updates via **StateFlow** → UI re-renders

### Backend (Java) - Layered Architecture

```
Controller (REST Endpoints)
        ↓
Service (Business Logic)
        ↓
Repository (Data Access)
        ↓
Entity (JPA Models)
        ↓
Database (PostgreSQL)
```

---

## 🔐 Security

### JWT Authentication

All protected endpoints require: `Authorization: Bearer <JWT_TOKEN>`

**Token Structure:**
```json
{
  "sub": "user-uuid",
  "email": "user@example.com",
  "iat": 1234567890,
  "exp": 1234654290
}
```

**Expiration:** 24 hours (configurable in `application.yml`)

### Trust Ledger Security

Every transaction is hashed using **SHA-256**:

```
Transaction 1: Hash = SHA256(Data1 + "GENESIS")
Transaction 2: Hash = SHA256(Data2 + Hash1)
Transaction 3: Hash = SHA256(Data3 + Hash2)
...

If Data2 is modified, Hash2 becomes invalid → Chain breaks → FRAUD DETECTED
```

---

## 💾 Database Schema

### Core Tables

| Table | Purpose |
|-------|---------|
| `users` | User profiles, location, token balance, trust score |
| `skills` | Skills offered/learned, category, proficiency level |
| `swaps` | Trade transactions (barter/token/hybrid) |
| `trust_ledger` | SHA-256 hashed transaction history (blockchain-inspired) |
| `token_escrow` | Hold tokens during active swaps |
| `rating_history` | User ratings after completed swaps |

### Key Relationships

```
User (1) ──→ (Many) Skill
User (1) ──→ (Many) Swap (as mentor or learner)
Swap (1) ──→ (Many) TrustLedger
User (1) ──→ (Many) RatingHistory
```

---

## 🎨 UI Screens

### Android App Screens

1. **Login/Register** - Authentication
2. **Radar View** - Map with nearby mentors (5km)
3. **Trade Center** - Active swaps and history
4. **Vault** - Token wallet and ledger
5. **Skill Profile** - Digital resume and badges

*More details in Phase 6 documentation*

---

## 🔄 Workflow Example: Skill Swap

### Scenario: Alice teaches Python to Bob

```
1. DISCOVERY
   └─ Bob opens Radar → Sees Alice nearby (3.5 km)
   
2. REQUEST
   └─ Bob requests swap: "Teach me Python"
   └─ Alice gets notification
   
3. NEGOTIATION
   └─ Alice accepts swap
   └─ Status: ACTIVE
   
4. SESSION
   └─ Alice & Bob meet/call
   └─ Alice teaches Python
   
5. COMPLETION
   └─ Both confirm completion
   └─ Status: COMPLETED
   
6. RATING
   └─ Bob rates Alice: 5 stars ⭐⭐⭐⭐⭐
   └─ Creates Trust Ledger entry with SHA-256 hash
   └─ Alice's trust score updated
   
7. TOKENS
   └─ If TOKEN swap: Tokens transferred from escrow
   └─ If BARTER: Just skill record updated
```

---

## 🚀 Deployment

### Android Release

```bash
# Generate release APK
./gradlew assembleRelease

# Or upload to Google Play Store
# Build → Generate Signed Bundle/APK
```

### Backend Deployment

**Option 1: On-premise (Linux Server)**
```bash
# Build JAR
mvn clean package -DskipTests

# Run
java -Dspring.profiles.active=prod -jar target/knowitall-backend-1.0.0.jar
```

**Option 2: Docker**
```dockerfile
FROM openjdk:17-slim
COPY target/knowitall-backend-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Option 3: Cloud Platforms**
- **Heroku:** `git push heroku main`
- **AWS EC2:** Upload JAR and run
- **Google Cloud Run:** Container deployment
- **Railway/Render:** Git auto-deploy

---

## 📊 Monitoring & Analytics

### Backend Logs

```bash
# View logs
tail -f nohup.out

# With specific level
grep "ERROR\|WARN" application.log
```

### Database Queries

Monitor slow queries:
```sql
SELECT query, calls, mean_time 
FROM pg_stat_statements 
ORDER BY mean_time DESC LIMIT 10;
```

### User Analytics

```sql
-- Total users
SELECT COUNT(*) FROM users;

-- Active swaps
SELECT COUNT(*) FROM swaps WHERE status = 'ACTIVE';

-- Trust score distribution
SELECT 
    FLOOR(trust_score / 10) * 10 AS score_range,
    COUNT(*) AS user_count
FROM users
GROUP BY score_range;
```

---

## 🐛 Troubleshooting

### Android Issues

**Problem:** App crashes on location request
```
Solution: Grant location permissions in Settings → Apps → KnowItAll → Permissions
```

**Problem:** Cannot connect to backend
```
Solution: Check RetrofitClient.kt BASE_URL matches your backend address
Use adb logcat to see network errors
```

### Backend Issues

**Problem:** Database connection refused
```
Solution: Verify PostgreSQL is running:
  Linux/Mac: pg_isready -h localhost
  Windows: Check Services tab for PostgreSQL
```

**Problem:** JWT token invalid
```
Solution: Ensure jwt.secret in application.yml is set and consistent
Check token expiration time in security config
```

---

## 📚 Additional Resources

- [Android Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [Spring Boot Guide](https://spring.io/guides/gs/spring-boot/)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
- [JWT Introduction](https://jwt.io/introduction)
- [SHA-256 Explanation](https://en.wikipedia.org/wiki/SHA-2)

---

## 🤝 Contributing

1. Create feature branch: `git checkout -b feature/new-feature`
2. Make changes and test
3. Create pull request
4. Code review and merge

---

## 📄 License

This project is proprietary. Do not distribute without permission.

---

## 📞 Support

- **Issues:** Open GitHub issue with detailed description
- **Questions:** Write to support@knowitall.app
- **Security:** Report to security@knowitall.app

---

## 🎓 Learning Path

1. **Start:** Read this README
2. **Android:** Explore `app/src/main/java/*/presentation/ui/screen/`
3. **Backend:** Check `KnowItAll-Backend/src/main/java/*/service/`
4. **Database:** Review `database/schema.sql`
5. **API:** Test endpoints with curl or Postman
6. **Deploy:** Follow deployment guides above

---

**Built with ❤️ for the gig economy and skill-sharing communities.**

*Last Updated: March 2, 2026*
