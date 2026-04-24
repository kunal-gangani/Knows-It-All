# 🌟 KnowItAll: Complete Project Overview

**Project Name:** KnowItAll  
**Type:** Peer-to-Peer Skill Trading Platform  
**Current Status:** Phase 1 & 2 Complete, Phase 3 Ready for Implementation  
**Last Updated:** April 2026  
**Target Platform:** Android (API 24+) with Spring Boot Backend  

---

## 📑 Table of Contents

1. [Project Overview](#project-overview)
2. [Mission & Vision](#mission--vision)
3. [Key Innovation](#key-innovation)
4. [System Architecture](#system-architecture)
5. [Technology Stack](#technology-stack)
6. [Data Model & Entities](#data-model--entities)
7. [Implementation Status](#implementation-status)
8. [Completed Features](#completed-features-phase-1)
9. [In-Progress Features](#in-progress-features-phase-2)
10. [Planned Features](#planned-features-phase-3)
11. [Project Structure](#project-structure)
12. [Build & Deployment](#build--deployment)
13. [Development Guidelines](#development-guidelines)

---

## 🎯 Project Overview

**KnowItAll** is a revolutionary peer-to-peer (P2P) platform designed to empower individuals in semi-urban areas by turning their skills into currency. The platform facilitates hyper-local skill exchanges within a **5km radius** using a unique **Hybrid Barter & Token System**.

### Problem Statement
In many communities, valuable skills go untapped because there is no formal marketplace for them. Traditional learning requires significant monetary investment, limiting access for many individuals.

### Solution
KnowItAll creates a marketplace where:
- People can trade skills directly (Barter)
- People can earn platform currency (SkillTokens) by teaching
- People can spend tokens to learn from others
- Trust and reputation are built through verified transactions

### Target Users
- **Skill Mentors:** Experienced professionals wanting to teach and earn tokens
- **Skill Learners:** Individuals wanting to acquire new skills
- **Communities:** Local hyper-local networks within 5km radius

---

## 🌟 Mission & Vision

### Mission
Empower individuals through skill-based commerce by:
- **Monetizing Time:** Allow users to "earn" by teaching others
- **Democratizing Learning:** Make education accessible without traditional monetary barriers
- **Building Trust:** Use technology to create verifiable, immutable records of skill proficiency

### Vision
Create a global network of skill-trading communities where every person's knowledge is valued and every learning journey is supported by trustworthy peers.

---

## 💎 Key Innovation: Hybrid Barter & Token System

The platform solves the "double coincidence of wants" problem using a dual-currency approach:

### 1. **Direct Barter (1:1)**
- **Use Case:** You have what I want, and I have what you want
- **Process:** Direct peer-to-peer skill exchange
- **Advantage:** No intermediary needed, immediate value exchange

### 2. **SkillTokens (Platform Currency)**
- **Earning:** Get paid in tokens when you teach someone
- **Spending:** Use tokens to "buy" lessons from any mentor on the platform
- **Escrow System:** Tokens held safely during active swaps, released only upon successful verification
- **Advantage:** Solves scenarios where mentor/learner skills don't align

### 3. **Hybrid Trades**
- **Combination:** Mix of barter and token exchange
- **Example:** "I'll teach you Python (worth 50 tokens) + you teach me Guitar"

---

## 🏗 System Architecture

### High-Level Component Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Android Client App                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Presentation Layer (Jetpack Compose UI)              │   │
│  │ ├─ Auth Screens (Login/Register)                     │   │
│  │ ├─ Skill Radar (GPS-based Discovery)                 │   │
│  │ ├─ Trade Center (Swap Management)                    │   │
│  │ ├─ The Vault (Token & Ledger)                        │   │
│  │ └─ Skill Profile (User Profile & Skills)             │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Business Logic Layer (ViewModels)                    │   │
│  │ ├─ AuthViewModel                                     │   │
│  │ ├─ RadarViewModel                                    │   │
│  │ ├─ TradeViewModel                                    │   │
│  │ ├─ SkillViewModel                                    │   │
│  │ └─ LedgerViewModel                                   │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Data Access Layer                                    │   │
│  │ ├─ Remote API (Retrofit)                             │   │
│  │ ├─ Local Cache (Room Database)                       │   │
│  │ └─ Repository (Data Orchestration)                   │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                 ↕ HTTPS/REST (JSON)
┌─────────────────────────────────────────────────────────────┐
│              Spring Boot Backend (API v1)                    │
│              http://10.0.2.2:8080/api/v1/                   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ REST Controllers                                     │   │
│  │ ├─ UserController (Authentication)                  │   │
│  │ ├─ SkillController (Skill Management)                │   │
│  │ ├─ SwapController (Trade Management)                 │   │
│  │ └─ LedgerController (Trust Records)                  │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Business Logic & Services                            │   │
│  │ ├─ Authentication (JWT Bearer Tokens)                │   │
│  │ ├─ Trust Ledger (SHA-256 Hashing)                    │   │
│  │ ├─ Token Escrow Management                           │   │
│  │ └─ Skill Verification                                │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Data Persistence Layer                               │   │
│  │ ├─ Users Table                                       │   │
│  │ ├─ Skills Table                                      │   │
│  │ ├─ Swaps (Trades) Table                              │   │
│  │ └─ TrustLedger Table                                 │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Database (PostgreSQL/MySQL)                          │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### User Flow Diagram

```
1. DISCOVER (Skill Radar)
   ├─ Open Skill Radar
   ├─ See map of mentors/learners within 5km
   └─ Filter by skill category

2. CONNECT & REQUEST (Skill Trade)
   ├─ Find someone teaching what you want
   ├─ Send Swap Request (Barter or Token)
   └─ Wait for acceptance

3. HANDSHAKE (Verification)
   ├─ Session happens (In-person or Remote)
   ├─ Verify completion:
   │  ├─ In-Person: Scan QR codes
   │  └─ Remote: Video call confirmation
   └─ Complete verification

4. FINALIZE (Trust & Review)
   ├─ Record transaction in Trust Ledger
   ├─ Both parties rate each other (1-5 stars)
   ├─ Write feedback/review
   └─ Update global Trust Score
```

---

## 💻 Technology Stack

### Frontend (Android)

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Language** | Kotlin | 1.9+ | Modern Android development |
| **UI Framework** | Jetpack Compose | Latest | Modern declarative UI |
| **Min SDK** | Android | API 24 | Broad device support |
| **Target SDK** | Android | API 35 | Latest Android features |
| **Compilation** | Java | JVM 17 | Java compatibility |
| **Navigation** | Compose Navigation | Latest | Screen navigation |
| **State Mgmt** | ViewModel + Compose | Latest | Lifecycle-aware state |
| **Location** | Fused Location API | Latest | GPS-based radar |
| **ML/AI** | TensorFlow Lite | Latest | On-device inference (GPU) |

### Networking & Data

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **HTTP Client** | OkHttp | HTTP requests, interceptors, logging |
| **REST Framework** | Retrofit 2 | Type-safe API calls |
| **Serialization** | Gson | JSON serialization/deserialization |
| **Local Database** | Room (SQLite) | Offline-first caching |
| **Code Generation** | KSP | Room entity processing |

### Backend (Spring Boot)

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **Framework** | Spring Boot 3 | REST API development |
| **Language** | Java 17 | Backend logic |
| **Authentication** | JWT + Spring Security | Secure token-based auth |
| **Database** | PostgreSQL/MySQL | Data persistence |
| **API Format** | JSON/REST | Client-server communication |

### Security & Utilities

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **Hashing** | SHA-256 | Trust ledger integrity |
| **PDF Generation** | iText 7 | Skill Passport export |
| **QR Codes** | ZXing | In-person verification |
| **Image Loading** | Coil | Network image caching |

---

## 📊 Data Model & Entities

### Entity Relationship Diagram

```
┌──────────────┐           ┌──────────────┐
│    USER      │ 1────────→ │   SKILL      │
├──────────────┤           ├──────────────┤
│ uid (PK)     │ N         │ skillId (PK) │
│ name         │           │ userId (FK)  │
│ email        │           │ skillName    │
│ profileImage │           │ category     │
│ latitude     │           │ proficiency  │
│ longitude    │           │ status       │
│ tokenBalance │           │ endorsements │
│ trustScore   │           │ createdAt    │
│ profileVerif │           └──────────────┘
│ createdAt    │               ↓ ↑
│ updatedAt    │               │ │
└──────────────┘               │ │
        ↑                        │ │
        │                        │ │
    ┌───┴────────────────────────┴─┐
    │         SWAP                 │
    ├──────────────────────────────┤
    │ swapId (PK)                  │
    │ mentorId (FK) ───────────────→ USER
    │ learnerId (FK) ──────────────→ USER
    │ mentorSkillId (FK) ─────────→ SKILL
    │ learnerSkillId (FK, opt) ───→ SKILL
    │ swapType                     │
    │ tokenAmount                  │
    │ status                       │
    │ verificationMethod           │
    │ completedAt                  │
    │ createdAt                    │
    └──────────────┬───────────────┘
                   │ 1:1
                   ↓
        ┌──────────────────────────┐
        │   TRUST_LEDGER           │
        ├──────────────────────────┤
        │ ledgerId (PK)            │
        │ swapId (FK)              │
        │ mentorId (FK)            │
        │ learnerId (FK)           │
        │ transactionHash (SHA256) │
        │ previousHash (chain)     │
        │ timestamp                │
        │ rating (1-5)             │
        │ feedback                 │
        │ status                   │
        └──────────────────────────┘
```

### Core Entities

#### **USER**
```kotlin
data class User(
    val uid: String,              // Unique user identifier (Primary Key)
    val name: String,             // Full name
    val email: String,            // Email address
    val profileImageUrl: String,  // Avatar/profile image URL
    val latitude: Double,         // GPS latitude for location-based discovery
    val longitude: Double,        // GPS longitude for location-based discovery
    val skillTokenBalance: Int,   // Available SkillTokens
    val trustScore: Float,        // Reputation score (0-100)
    val profileVerified: Boolean, // Identity verification status
    val createdAt: Long,          // Registration timestamp (milliseconds)
    val updatedAt: Long           // Last profile update timestamp
)
```

#### **SKILL**
```kotlin
enum class SkillCategory {
    DIGITAL,     // Programming, digital marketing, etc.
    PHYSICAL,    // Carpentry, cooking, fitness, etc.
    HYBRID       // Mixed or multi-discipline skills
}

enum class ProficiencyLevel {
    BEGINNER,      // Basic knowledge
    INTERMEDIATE,  // Working proficiency
    ADVANCED,      // Expert-level ability
    EXPERT         // Master/authority level
}

data class Skill(
    val skillId: String,                    // Unique skill identifier
    val userId: String,                     // User who offers this skill (FK)
    val skillName: String,                  // e.g., "Python", "Carpentry"
    val description: String,                // Detailed skill description
    val category: SkillCategory,            // Classification
    val proficiencyLevel: ProficiencyLevel, // User's level
    val verificationStatus: Boolean,        // Community verified
    val endorsements: Int,                  // Number of endorsements
    val createdAt: Long                     // When skill was added
)
```

#### **SWAP** (Trade Transaction)
```kotlin
enum class SwapStatus {
    REQUESTED,     // Awaiting mentor response
    ACTIVE,        // Session in progress
    COMPLETED,     // Session successfully finished
    CANCELLED,     // Cancelled by either party
    DISPUTED       // Under dispute/resolution
}

enum class SwapType {
    BARTER,        // Pure 1:1 skill exchange
    TOKEN,         // SkillToken payment
    HYBRID         // Mix of barter + tokens
}

data class Swap(
    val swapId: String,                 // Unique transaction ID
    val mentorId: String,               // Teacher (FK to User)
    val learnerId: String,              // Student (FK to User)
    val mentorSkillId: String,          // Skill being taught (FK)
    val learnerSkillId: String?,        // Skill in return (nullable)
    val swapType: SwapType,             // Type of exchange
    val tokenAmount: Int,               // Tokens involved (if any)
    val status: SwapStatus,             // Current state
    val verificationMethod: String,     // VIDEO_CALL | QR_CODE | BOTH
    val completedAt: Long?,             // Completion timestamp
    val createdAt: Long                 // Creation timestamp
)
```

#### **TRUST_LEDGER** (Blockchain-Inspired Record)
```kotlin
data class TrustLedger(
    val ledgerId: String,              // Unique ledger entry ID
    val swapId: String,                // Related transaction (FK)
    val mentorId: String,              // Mentor participant
    val learnerId: String,             // Learner participant
    val transactionHash: String,       // SHA-256 hash of this entry
    val previousHash: String,          // Hash chain link (immutability)
    val timestamp: Long,               // Transaction time
    val rating: Int,                   // Rating given (1-5 stars)
    val feedback: String,              // Review/feedback text
    val status: String                 // COMPLETED | DISPUTED | RESOLVED
)
```

---

## ✅ Implementation Status

### Overall Progress
- **Phase 1 (Foundation):** 100% ✅ COMPLETE
- **Phase 2 (UI Implementation):** 60% 🔄 IN PROGRESS
- **Phase 3 (Testing & Polish):** 0% ⏳ PLANNED

---

## ✅ Completed Features (Phase 1)

### Authentication System ✅
- **LoginScreen.kt** - Full form validation, password visibility toggle, error handling
- **RegisterScreen.kt** - Email/password validation, password match verification, real-time feedback
- **AuthViewModel** - Login/register flow with proper error handling using `fold()` pattern
- **UserRepository** - API integration returning `AuthData(token, userId)`
- **SessionManager** - Secure token + userId persistence to SharedPreferences
- **Token Management** - Automatic Bearer token injection in API headers

**Key Achievement:** Users can register/login, tokens are saved and reused for authenticated API calls

```kotlin
// Token automatically added to requests
sessionManager.saveToken(authData.token)
sessionManager.saveUserInfo(authData.userId, name, email)
```

### Data Models ✅
- ✅ User entity with location tracking
- ✅ Skill entity with category and proficiency levels
- ✅ Swap transaction model with comprehensive status enums
- ✅ TrustLedger entity with blockchain-inspired hash chain
- ✅ DTO layer for API communication
- ✅ ApiResponse wrapper for standardized responses
- ✅ All necessary enum classes

### Networking Infrastructure ✅
- ✅ Retrofit client with OkHttp configuration
- ✅ Request/response logging in DEBUG builds
- ✅ Base URL configured for emulator (10.0.2.2:8080)
- ✅ API service interfaces for all endpoints:
  - UserService (login, register, profile)
  - SkillService (CRUD operations)
  - SwapService (trade management)
  - LedgerService (trust records)

### Database Setup ✅
- ✅ Room SQLite integration
- ✅ DAOs for all entities
- ✅ Database versioning support
- ✅ Offline-first caching capabilities

### Build System ✅
- ✅ Gradle with Kotlin DSL (build.gradle.kts)
- ✅ KSP for Room code generation
- ✅ Dependency management via libs.versions.toml
- ✅ ProGuard configuration for release builds
- ✅ Proper minification setup

### Project Structure ✅
- ✅ MVC/MVVM architecture established
- ✅ Clear separation of concerns (presentation/business/data)
- ✅ Navigation graph configured
- ✅ Dependency injection foundation

---

## 🔄 In-Progress Features (Phase 2)

### RadarScreen (Skill Discovery) - 60% Complete
**Status:** Core structure complete, API integration needed

**Completed:**
- ✅ Scaffold with top/bottom bars
- ✅ Location permission request setup
- ✅ Map display integration framework
- ✅ Navigation wired to NavGraph

**In Progress:**
- 🔄 Load nearby users from API (`GET /api/v1/users/nearby`)
- 🔄 Display mentor list with distance
- 🔄 Filter by skill category/proficiency
- 🔄 Mentor profile preview on tap
- 🔄 "Send Swap Request" functionality

**What's Needed:**
```kotlin
// In RadarViewModel:
fun loadNearbyUsers(token: String, lat: Double, lon: Double)
```

### TradeScreen (Swap Management) - 50% Complete
**Status:** UI scaffolding done, business logic needed

**Completed:**
- ✅ Screen navigation structure
- ✅ Bottom/Top app bars

**In Progress:**
- 🔄 Load active swaps from API
- 🔄 Display swap cards (mentor, skill, status)
- 🔄 Swap status progression visualization
- 🔄 Verification button (QR/Video)
- 🔄 Rating interface after completion

**What's Needed:**
```kotlin
// In TradeViewModel:
fun loadActiveSwaps(token: String, userId: String)
fun requestVerification(swapId: String, method: String)
fun rateSwap(swapId: String, rating: Int, feedback: String)
```

### VaultScreen (Tokens & Ledger) - 70% Complete
**Status:** UI mostly complete, API calls and password generation need fixes

**Completed:**
- ✅ Token balance card display
- ✅ Transaction history list structure
- ✅ LedgerEntryItem card component
- ✅ Basic transaction filtering

**In Progress:**
- 🔄 Load ledger entries from API
- 🔄 Display transaction chain (with hash validation)
- 🔄 Fix Skill Passport PDF generation

**Known Issue:**
- Passport generation expects User object, needs refactoring to use userId
- Function: `generateSkillPassport()` needs to be rewritten

**What's Needed:**
```kotlin
// In LedgerViewModel:
fun generateSkillPassport(userId: String): File  // PDF export
```

### SkillProfileScreen - 40% Complete
**Status:** Basic scaffolding only, core functionality needed

**Completed:**
- ✅ Screen structure with Scaffold
- ✅ Navigation integration

**In Progress:**
- 🔄 Load user profile from API
- 🔄 Edit profile form (name, email)
- 🔄 Add new skill form
- 🔄 Display user's skills list
- 🔄 Delete skill functionality
- 🔄 Skill Passport generation

**What's Needed:**
```kotlin
// In SkillViewModel:
fun loadUserProfile(token: String, userId: String)
fun updateProfile(token: String, name: String, email: String)
fun addSkill(token: String, skill: Skill)
fun removeSkill(token: String, skillId: String)
```

### BottomNavigation - 100% Complete ✅
- ✅ BottomNavigationBar with 4 nav items
- ✅ Navigation logic wired to NavGraph

---

## ⏳ Planned Features (Phase 3)

### Testing Suite
- [ ] Unit tests for ViewModels
- [ ] Integration tests for Repository layer
- [ ] UI tests for Composables
- [ ] API mock testing

### Advanced Features
- [ ] **Video Call Integration** - Real-time verification
- [ ] **QR Code Verification** - In-person handshakes
- [ ] **Notification System** - Swap requests, status updates
- [ ] **Chat Messaging** - Direct communication between users
- [ ] **Review System** - Community ratings and endorsements
- [ ] **Payment Gateway** (Optional) - Real currency option

### Performance & Polish
- [ ] Image compression and caching
- [ ] Pagination for lists (Radar, Trade history)
- [ ] Offline functionality improvements
- [ ] Error recovery mechanisms
- [ ] Loading state animations
- [ ] Empty state UI

### Security Enhancements
- [ ] Biometric authentication
- [ ] End-to-end encryption for messages
- [ ] Enhanced profile verification
- [ ] Fraud detection system

### Analytics & Monitoring
- [ ] User behavior tracking
- [ ] Transaction success rate monitoring
- [ ] Performance metrics
- [ ] Crash reporting

---

## 📁 Project Structure

### Directory Layout

```
d:\Projects\KnowItAll/
│
├── README.md                          # Project overview
├── PROJECT_SUMMARY.md                 # Detailed summary
├── PROJECT_REVIEW.md                  # Progress review
├── PROJECT_RULES.md                   # Development guidelines
├── model_capabilities.yaml             # Model selection guidance
├── GSD-STYLE.md                       # Coding style guide
├── DOCUMENTATION_PROMPT.md            # Documentation template
├── AUTH_TESTING_GUIDE.md              # Auth testing procedures
│
├── app/                               # Android app module
│   ├── build.gradle.kts               # App build configuration
│   ├── proguard-rules.pro             # Minification rules
│   │
│   ├── src/main/
│   │   ├── AndroidManifest.xml        # App manifest
│   │   │
│   │   ├── java/com/example/know_it_all/
│   │   │   ├── KnowItAllApplication.kt # App initialization
│   │   │   ├── MainActivity.kt         # Entry point
│   │   │   │
│   │   │   ├── data/                  # Data layer
│   │   │   │   ├── local/             # Room database
│   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   └── DAOs/
│   │   │   │   │
│   │   │   │   ├── remote/            # API calls
│   │   │   │   │   ├── RetrofitClient.kt
│   │   │   │   │   └── Services/
│   │   │   │   │       ├── UserService.kt
│   │   │   │   │       ├── SkillService.kt
│   │   │   │   │       ├── SwapService.kt
│   │   │   │   │       └── LedgerService.kt
│   │   │   │   │
│   │   │   │   ├── model/             # Data entities
│   │   │   │   │   ├── User.kt
│   │   │   │   │   ├── Skill.kt
│   │   │   │   │   ├── Swap.kt
│   │   │   │   │   └── TrustLedger.kt
│   │   │   │   │
│   │   │   │   └── repository/        # Data orchestration
│   │   │   │       ├── UserRepository.kt
│   │   │   │       ├── SkillRepository.kt
│   │   │   │       ├── SwapRepository.kt
│   │   │   │       └── LedgerRepository.kt
│   │   │   │
│   │   │   ├── presentation/          # UI layer
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screen/
│   │   │   │   │   │   ├── auth/
│   │   │   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   │   │   ├── RegisterScreen.kt
│   │   │   │   │   │   │   └── SplashScreen.kt
│   │   │   │   │   │   │
│   │   │   │   │   │   ├── main/
│   │   │   │   │   │   │   ├── RadarScreenEnhanced.kt
│   │   │   │   │   │   │   ├── TradeScreenEnhanced.kt
│   │   │   │   │   │   │   ├── VaultScreenEnhanced.kt
│   │   │   │   │   │   │   ├── SkillProfileScreenEnhanced.kt
│   │   │   │   │   │   │   └── BottomNavigationBar.kt
│   │   │   │   │   │   │
│   │   │   │   │   │   └── components/
│   │   │   │   │   │       ├── LedgerEntryItem.kt
│   │   │   │   │   │       ├── MentorCard.kt
│   │   │   │   │   │       ├── SwapCard.kt
│   │   │   │   │   │       └── TokenBalanceCard.kt
│   │   │   │   │   │
│   │   │   │   │   ├── state/
│   │   │   │   │   │   ├── AuthUiState.kt
│   │   │   │   │   │   ├── RadarUiState.kt
│   │   │   │   │   │   ├── TradeUiState.kt
│   │   │   │   │   │   ├── VaultUiState.kt
│   │   │   │   │   │   └── SkillUiState.kt
│   │   │   │   │   │
│   │   │   │   │   ├── viewmodel/
│   │   │   │   │   │   ├── AuthViewModel.kt
│   │   │   │   │   │   ├── RadarViewModel.kt
│   │   │   │   │   │   ├── TradeViewModel.kt
│   │   │   │   │   │   ├── LedgerViewModel.kt
│   │   │   │   │   │   └── SkillViewModel.kt
│   │   │   │   │   │
│   │   │   │   │   └── theme/
│   │   │   │   │       ├── Theme.kt
│   │   │   │   │       └── Color.kt
│   │   │   │   │
│   │   │   │   └── navigation/
│   │   │   │       └── NavGraph.kt
│   │   │   │
│   │   │   └── util/
│   │   │       ├── SessionManager.kt
│   │   │       ├── QRCodeGenerator.kt
│   │   │       ├── PDFGenerator.kt
│   │   │       ├── Constants.kt
│   │   │       └── Extensions.kt
│   │   │
│   │   └── res/
│   │       ├── values/
│   │       ├── drawable/
│   │       └── ...
│   │
│   ├── src/test/                     # Unit tests
│   ├── src/androidTest/              # Android tests
│   └── build/                        # Build outputs (generated)
│
├── adapters/                         # Model-specific enhancements
│   ├── CLAUDE.md                     # Claude model recommendations
│   ├── GEMINI.md                     # Gemini model recommendations
│   └── GPT_OSS.md                    # GPT/OSS recommendations
│
├── docs/                             # Documentation
│   ├── model-selection-playbook.md   # Model selection guide
│   ├── runbook.md                    # Operational guide
│   └── token-optimization-guide.md   # Token usage optimization
│
├── gradle/                           # Gradle configuration
│   ├── libs.versions.toml            # Dependency versions
│   └── wrapper/
│
├── scripts/                          # Utility scripts
│   ├── search_repo.ps1
│   ├── search_repo.sh
│   ├── validate-*.ps1/sh             # Validation scripts
│   └── setup_search.ps1/sh
│
├── UML/                              # Architecture diagrams
│   └── (Mermaid diagrams)
│
├── build.gradle.kts                  # Root build file
├── settings.gradle.kts               # Gradle settings
├── gradlew & gradlew.bat            # Gradle wrapper
├── gradle.properties                 # Gradle properties
└── local.properties                  # Local configuration

```

### Key Directories Explained

**app/src/main/java/.../**
- **data/** - Data layer (Repositories, Services, Models, Local DB)
- **presentation/** - UI layer (Screens, ViewModels, Components)
- **util/** - Utility functions and helpers

**gradle/**
- **libs.versions.toml** - Centralized dependency version management

**adapters/**
- Optional model-specific enhancements
- References back to PROJECT_RULES.md for canonical rules

**docs/**
- Operational documentation
- Model selection guidance
- Performance optimization guides

---

## 🚀 Build & Deployment

### Prerequisites
```
✓ Android Studio (Jellyfish or later)
✓ Android SDK 24+ (API Level 24+)
✓ Kotlin 1.9+
✓ Java 17+
✓ Gradle 8.x+
✓ Emulator or Android device
✓ Spring Boot backend (running on port 8080)
```

### Build Commands

**Clean build:**
```bash
./gradlew clean
```

**Assemble debug APK:**
```bash
./gradlew assembleDebug
```

**Install to emulator/device:**
```bash
./gradlew installDebug
```

**Run app on emulator:**
```bash
./gradlew installDebug
adb shell am start -n com.example.know_it_all/.MainActivity
```

**Build release APK:**
```bash
./gradlew assembleRelease
```

**View connected devices:**
```bash
adb devices
```

### Emulator Configuration
```
Base URL: http://10.0.2.2:8080/api/v1
Min API Level: 24
Target API Level: 35
JVM Target: 17
```

### Backend API
```
Base URL: http://10.0.2.2:8080 (for emulator)
                or http://localhost:8080 (for device)

API Version: v1
Authentication: Bearer Token (JWT)
Content-Type: application/json
```

---

## 📋 Development Guidelines

### Code Style
- Follow [GSD-STYLE.md](GSD-STYLE.md) for coding conventions
- Use Kotlin best practices
- Maintain separation of concerns (MVVM architecture)
- Use `fold()` pattern for error handling with Result types

### Git Workflow
```
Branch naming: feature/FEATURE_NAME or fix/BUG_NAME
Commit format: type(scope): description
  ├─ feat(phase-1): Add authentication
  ├─ fix(radar): Fix location permission
  ├─ docs(readme): Update setup instructions
  └─ test(auth): Add login flow tests
```

### Error Handling
- Always use `fold()` or `try-catch` for API calls
- Display user-friendly error messages
- Log errors for debugging
- Handle loading states explicitly

### Testing Approach
- Test authentication flows end-to-end
- Test each API endpoint integration
- Test UI state changes
- Test offline scenarios

### Phase Completion Checklist
- [ ] All screens functional
- [ ] API integration complete
- [ ] Error handling implemented
- [ ] Loading states showing
- [ ] Tested on emulator
- [ ] Documentation updated
- [ ] All changes committed with proper messages

### Next Immediate Steps (Priority Order)

1. **Verify Build**
   ```bash
   ./gradlew clean assembleDebug
   ```

2. **Test Authentication Flow**
   - Launch emulator
   - Register new user
   - Verify token saved to SharedPreferences
   - Check API calls include Bearer token

3. **Complete RadarScreen**
   - Implement `loadNearbyUsers()` in RadarViewModel
   - Display mentors on map/list
   - Test API integration

4. **Complete TradeScreen**
   - Implement `loadActiveSwaps()` in TradeViewModel
   - Display swap cards with status
   - Implement verification flow

5. **Fix VaultScreen**
   - Refactor `generateSkillPassport()` to use userId
   - Test PDF generation

---

## 📊 Key Metrics

### Code Organization
- **Total Files:** 50+ (screens, viewmodels, repositories, services)
- **Main Screens:** 5 (Auth, Radar, Trade, Vault, Profile)
- **ViewModels:** 5 (Auth, Radar, Trade, Ledger, Skill)
- **Data Entities:** 4 (User, Skill, Swap, TrustLedger)
- **API Services:** 4 (User, Skill, Swap, Ledger)

### Build Configuration
- **Min SDK:** 24
- **Target SDK:** 35
- **Java Compatibility:** 17
- **Kotlin Version:** 1.9+

### Architecture
- **Pattern:** MVVM
- **UI Framework:** Jetpack Compose
- **Database:** Room (SQLite)
- **Networking:** Retrofit + OkHttp
- **Auth:** JWT Bearer Tokens

---

## 🎓 Learning Resources

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Android ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)

---

## 📞 Support & Contact

**Project Status:** Active Development  
**Current Phase:** Phase 2 (UI Implementation)  
**Backend Status:** Available at http://10.0.2.2:8080/api/v1  
**Last Updated:** April 2026

---

**Document Generated:** April 2026  
**Version:** 1.0  
**Format:** Markdown
