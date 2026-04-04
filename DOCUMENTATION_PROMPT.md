# KnowItAll: Complete Documentation Prompt

## Copy the entire content below into your diagram tool (Miro, Lucidchart, Draw.io, PlantUML, etc.)

---

## INTRODUCTION

**System Name**: KnowItAll - Peer-to-Peer Skill Trading Platform

**Description**: 
KnowItAll is a revolutionary mobile platform that enables users in semi-urban areas to trade skills using a hybrid barter and token system. The platform leverages GPS-based matchmaking to connect mentors and learners within a 5km radius, implements a blockchain-inspired trust ledger system to maintain immutable transaction records, and provides digital micro-credentials through skill passports. The system supports both synchronous (video calls) and asynchronous (QR-based) verification methods to ensure genuine skill exchanges.

**Platform Type**: Android Mobile Application with Spring Boot Backend

**Target Users**: 
- Skill Mentors (experienced professionals wanting to teach)
- Skill Learners (individuals wanting to acquire new skills)
- Semi-urban communities

---

## PROBLEM STATEMENT

1. **Geographic Skill Gap**: Users in semi-urban areas lack access to skill development opportunities due to limited local expertise
2. **Trust and Verification**: Difficulty in verifying skill authenticity and establishing trust between strangers in peer-to-peer transactions
3. **Economic Barriers**: High cost of formal training; limited monetization of informal skills
4. **Skill Documentation**: Lack of digital proof/credentials for informally learned skills
5. **Discoverability**: No mechanism to discover skilled mentors within proximity
6. **Transaction Safety**: Risk of skill non-completion, token loss, or unfair exchanges in unregulated peer trades

---

## OBJECTIVES

1. **Hyper-local Connectivity**: Connect users within a 5km radius using GPS-based matchmaking (Skill Radar)
2. **Dual Trading System**: Enable both barter (1:1 skill exchange) and token-based (skill→points) trading models
3. **Immutable Trust System**: Implement SHA-256 hashed transaction history creating tamper-proof audit trails
4. **Skill Verification**: Provide multiple verification methods:
   - Video call integration for remote verification
   - QR code handshake for in-person verification
   - Endorsement system for community validation
5. **Digital Micro-credentials**: Generate PDF skill passports as proof of learned/taught skills
6. **Transaction Safety**: Implement token escrow system for secure token holding during active swaps
7. **Profile Trust Scoring**: Calculate and display trust scores based on transaction history

---

## CORE ENTITIES AND RELATIONSHIPS

### Entity 1: USER
**Attributes**:
- uid (Primary Key): Unique user identifier
- name: User's full name
- email: Email address
- profileImageUrl: Avatar/profile picture
- latitude, longitude: GPS coordinates
- skillTokenBalance: Current token balance (currency)
- trustScore: Calculated reputation score (0-100)
- profileVerified: Boolean flag for identity verification
- createdAt, updatedAt: Timestamps

**Relationships**:
- ONE User has MANY Skills (1:N)
- ONE User can be MANY Swaps as Mentor (1:N)
- ONE User can be MANY Swaps as Learner (1:N)
- ONE User has ONE TrustLedger (1:1)

---

### Entity 2: SKILL
**Attributes**:
- skillId (Primary Key): Auto-increment skill identifier
- userId (Foreign Key): References User
- skillName: Name of the skill
- description: Detailed skill description
- category: DIGITAL | PHYSICAL | HYBRID
- proficiencyLevel: BEGINNER | INTERMEDIATE | ADVANCED | EXPERT
- verificationStatus: Boolean (verified by mentor community)
- endorsements: Count of endorsements received
- createdAt: Timestamp

**Relationships**:
- MANY Skills belong to ONE User (N:1)
- ONE Skill can be MANY Swaps as mentorSkill (1:N)
- ONE Skill can be MANY Swaps as learnerSkill (1:N)

---

### Entity 3: SWAP
**Attributes**:
- swapId (Primary Key): Unique transaction identifier
- mentorId (Foreign Key): References User (skill provider)
- learnerId (Foreign Key): References User (skill receiver)
- mentorSkillId (Foreign Key): References Skill (offered skill)
- learnerSkillId (Foreign Key, Optional): References Skill (barter skill)
- swapType: BARTER | TOKEN | HYBRID
- tokenAmount: Number of tokens exchanged (if applicable)
- status: REQUESTED | ACTIVE | COMPLETED | CANCELLED | DISPUTED
- verificationMethod: VIDEO_CALL | QR_CODE | BOTH
- completedAt: Timestamp (null if incomplete)
- createdAt: Timestamp

**Relationships**:
- MANY Swaps involve ONE User as Mentor (N:1)
- MANY Swaps involve ONE User as Learner (N:1)
- MANY Swaps reference ONE Skill as Mentor Skill (N:1)
- MANY Swaps reference ONE Skill as Learner Skill (optional, N:1)
- ONE Swap has ONE TrustLedger entry (1:1)

---

### Entity 4: TRUST LEDGER
**Attributes**:
- ledgerId (Primary Key): Unique ledger entry identifier
- swapId (Foreign Key): References Swap
- mentorId (Foreign Key): References User
- learnerId (Foreign Key): References User
- transactionHash: SHA-256 hash of transaction
- previousHash: SHA-256 hash of previous transaction (blockchain pattern)
- timestamp: Transaction timestamp
- rating: Mentor rating by learner (1-5 stars)
- feedback: Text feedback from learner
- status: COMPLETED | DISPUTED | RESOLVED

**Relationships**:
- ONE TrustLedger entry references ONE Swap
- ONE TrustLedger entry involves User as Mentor
- ONE TrustLedger entry involves User as Learner
- Creates immutable chain: LedgerEntry → previousHash → prior LedgerEntry

---

## PRIMARY USE FLOWS

### Flow 1: User Registration & Profile Setup
1. User downloads app and initiates registration
2. System authenticates via email/phone
3. User enters location (latitude/longitude)
4. User uploads profile image
5. System creates User record in database
6. User can add skills to their profile

### Flow 2: Discover Skills (Skill Radar)
1. Learner opens app (current location auto-detected via GPS)
2. System queries Skill database for skills within 5km radius
3. Filters by: category, proficiency level, availability
4. Returns sorted list of mentors with distance
5. Learner can view mentor profile, reviews, skills
6. Learner can initiate swap request

### Flow 3: Initiate Barter Swap
1. Learner selects mentor and desired skill
2. Learner selects their own skill to offer (if barter)
3. System creates Swap record with status=REQUESTED
4. Mentor receives notification
5. Mentor reviews request (learner profile, learner's skill offering)
6. Mentor approves/rejects swap

### Flow 4: Initiate Token Swap
1. Learner selects mentor and skill
2. System displays token cost
3. Learner confirms token deduction
4. System creates Swap record with status=REQUESTED, tokenAmount set
5. System holds tokens in escrow (reduces from learner balance)
6. Mentor receives notification and approves/rejects

### Flow 5: Execute Verified Swap
1. Swap is approved (status=ACTIVE)
2. Mentor and learner choose verification method:
   - **Video Call**: System initiates video call, both parties confirm completion
   - **QR Code**: Mentor generates QR code, learner scans at in-person meeting
   - **Both**: Sequential video + in-person verification
3. Upon verification, status changes to COMPLETED
4. System creates TrustLedger entry with hashed transaction
5. Learner provides rating and feedback
6. Tokens released from escrow (if applicable)
7. Both users' trustScores updated
8. Skill Passport generated for learner

### Flow 6: View Trust Ledger
1. User accesses their transaction history
2. System displays list of all completed swaps (as mentor and learner)
3. For each swap: rating, feedback, timestamp, transaction hash
4. Learners can view chain of hashes (blockchain-like immutability proof)

---

## SYSTEM ARCHITECTURE LAYERS

### Presentation Layer (Mobile UI - Android)
- MainActivity: App entry point
- Authentication screens: Login/Register
- Skill Radar screen: GPS-based discovery
- Mentor/Learner profiles
- Swap request/approval screens
- Video call interface
- QR code scanner/generator
- Skill Passport viewer

### Business Logic Layer (ViewModels)
- AuthViewModel: Authentication logic
- RadarViewModel: Skill discovery and filtering
- TradeViewModel: Swap management and status updates
- UserProfileViewModel: User data management

### Data Access Layer (Repository Pattern)
- UserRepository: User CRUD operations
- SkillRepository: Skill management
- SwapRepository: Swap lifecycle management
- LedgerRepository: Trust ledger queries and hashing

### Local Persistence (Room Database)
- UserDao: Query/insert users
- SkillDao: Query/insert skills
- SwapDao: Query/insert/update swaps
- TrustLedgerDao: Query/insert ledger entries
- RoomConverters: Convert enums to database-compatible formats

### Remote Data Layer (Retrofit API)
- RetrofitClient: HTTP client configuration
- UserAPI: Remote user operations
- SkillAPI: Skill synchronization
- SwapAPI: Swap operations
- LedgerAPI: Ledger synchronization
- LocationAPI: GPS and proximity queries

### Utility Services
- HashUtil: SHA-256 hashing for trust ledger
- LocationUtil: GPS calculations and proximity filtering (5km radius)
- QRCodeGenerator: Generate QR codes for verification
- SkillPassportGenerator: Generate PDF certificates
- TokenManager: Manage token escrow and balance
- VideoCallManager: Integrate video call APIs (e.g., Firebase, Twilio)

### Backend (Spring Boot)
- Authentication service (JWT tokens)
- RESTful API endpoints for all CRUD operations
- Business logic services
- Location-based query optimizations (PostgreSQL PostGIS for geo queries)
- Token escrow management
- TrustLedger hashing and validation

---

## KEY SYSTEM CHARACTERISTICS

1. **Blockchain-Inspired Trust**: SHA-256 hashing creates immutable transaction history
2. **GPS-Based Proximity**: 5km radius matching using Haversine formula
3. **Hybrid Currency**: Both barter exchange and token-based system
4. **Multi-Verification**: Video + QR code providing dual authenticity
5. **Skill Categorization**: DIGITAL (online), PHYSICAL (in-person), HYBRID (both)
6. **Reputation System**: Star ratings and trust scores drive platform credibility
7. **Escrow Protection**: Tokens held safely during active swaps
8. **Micro-credentials**: PDF skill passports as proof of competency

---

## DATA FLOW OVERVIEW

**Data Sources**: 
- Mobile App (user input, GPS location)
- Backend API (persistent storage, business rules)
- Local Database (offline caching)

**Data Destinations**:
- Room Database (local cache)
- Spring Boot Backend (cloud persistence)
- PDF Generator (skill passports)
- Video/QR Services (external integrations)

**Key Data Transformations**:
- Location coordinates → GPS proximity calculations
- Skill records → Searchable index with filtering
- Swap approval → Creation of hashed ledger entry
- Ledger entries → Trust score calculation

---

## REQUEST DIAGRAMS TO GENERATE

Please generate the following diagrams:

### 1. ENTITY RELATIONSHIP DIAGRAM (ERD)
**Scope**: Complete database schema
**Entities**: User, Skill, Swap, TrustLedger
**Relationships**: All 1:1, 1:N, N:N relationships with cardinality indicators
**Attributes**: Show all key attributes for each entity
**Keys**: Highlight primary keys and foreign keys

### 2. SEQUENCE DIAGRAM: Initiate and Complete a Token-Based Swap
**Actors**: Learner, Mobile App, Backend API, Database
**Sequence**:
1. Learner selects mentor and skill
2. Learner confirms token payment
3. Mobile app sends swap request to backend
4. Backend creates Swap record (status=REQUESTED)
5. Backend holds tokens in escrow
6. Mentor notification sent
7. Mentor approves swap (status=ACTIVE)
8. Video call initiated and completed
9. Both parties confirm verification
10. Backend creates TrustLedger entry with hash
11. Tokens released from escrow
12. TrustScores updated
13. Skill Passport generated

### 3. DATA FLOW DIAGRAM (DFD) - Level 0 (Context Diagram)
**External Entities**: 
- Mentors
- Learners
- Video Call Service
- QR Code Scanner
- Skill Passport Generator

**Main Process**: KnowItAll P2P Trading Platform
**Data Flows**: 
- Skill offers/requests
- Verification requests
- Transaction records
- Trust scores
- Location data

### 4. DFD - Level 1 (Main Processes)
**Processes**:
1. User Management (Authenticate, Create Profile, Update Location)
2. Skill Discovery (Search Skills, Filter by Proximity/Category, Display Mentors)
3. Swap Management (Create Swap, Verify Swap, Update Status, Release Escrow)
4. Trust Ledger (Hash Transaction, Store Ledger Entry, Calculate Trust Score)
5. Skill Passport Generation (Create PDF, Store Certificate)

**Data Stores**:
- User Database
- Skill Database
- Swap Database
- TrustLedger Database
- Token Escrow Store

### 5. ACTIVITY DIAGRAM: Complete User Journey (Learner Perspective)
**Start**: User opens app
**Activities** (in sequence):
1. Authenticate/Login
2. Grant location permission
3. View Skill Radar (nearby mentors)
4. Select skill and mentor
5. Initiate swap request
6. Wait for mentor approval
7. Choose verification method (Video or QR)
8. Complete verification
9. Rate mentor and provide feedback
10. Receive skill passport
**End**: Transaction complete

### 6. ACTIVITY DIAGRAM: Mentor Workflow
**Start**: Mentor opens notification
**Activities**:
1. Review learner profile
2. Review learner's offered skill (if barter)
3. Approve/Reject swap
4. Prepare for verification
5. Initiate video call or setup QR code
6. Conduct verification with learner
7. Confirm skill transfer
8. View learner's rating and feedback
9. Check updated trust score
**End**: Transaction complete

### 7. USE CASE DIAGRAM
**System**: KnowItAll Platform

**Actors**:
- Learner
- Mentor
- System (auto-processes)
- External Services (Video Call, QR Scanner)

**Use Cases**:
1. **User Management**
   - Register Account
   - Login
   - Complete Profile
   - Update Location
   - View Trust Score

2. **Skill Management**
   - Add Skill
   - Remove Skill
   - Edit Skill Details
   - Update Proficiency Level
   - Get Skill Verified

3. **Discovery**
   - Search Skills by Category
   - Filter by Proficiency Level
   - View Mentors within 5km
   - View Mentor Profile
   - View Mentor Reviews

4. **Trading**
   - Request Barter Swap
   - Request Token Swap
   - View Pending Swaps
   - Approve/Reject Swap
   - Cancel Swap
   - Review Swap History

5. **Verification**
   - Initiate Video Call
   - Scan QR Code
   - Confirm Skill Completion
   - Provide Rating & Feedback

6. **Trust & Credentials**
   - View Transaction History
   - View Transaction Hash Chain
   - Download Skill Passport
   - Check Trust Score
   - View Endorsements

### 8. SYSTEM ARCHITECTURE DIAGRAM
**Layers**:
- **Presentation Layer**: Android UI (Composables, Activities)
- **Business Logic Layer**: ViewModels, UseCases
- **Repository Layer**: Abstraction over data sources
- **Data Layer**: 
  - Local: Room Database (DAO layer)
  - Remote: Retrofit API client
- **External Services**: Video Call API, GPS Service, QR Library, PDF Generator
- **Backend Services**: Spring Boot microservices
- **Database**: PostgreSQL with PostGIS for geo-queries

### 9. STATE DIAGRAM: Swap Lifecycle
**States**:
1. **REQUESTED**: Initial request sent by learner
2. **ACTIVE**: Mentor approved, skill exchange in progress
3. **COMPLETED**: Verification done, transaction recorded
4. **CANCELLED**: Either party cancelled
5. **DISPUTED**: Disagreement on completion

**Transitions**:
- REQUESTED → ACTIVE (mentor approval)
- REQUESTED → CANCELLED (learner or mentor cancellation)
- ACTIVE → COMPLETED (verification done)
- ACTIVE → DISPUTED (party files dispute)
- DISPUTED → COMPLETED (dispute resolved)
- DISPUTED → CANCELLED (refund + cancellation)

### 10. DEPLOYMENT DIAGRAM
**Components**:
- User's Mobile Device (Android app)
- Backend Server (Spring Boot API)
- Database Server (PostgreSQL)
- External Services (Video Call, QR library, GPS)
- Cloud Storage (PDF passports)

**Connections**: 
- Mobile app ↔ Backend (REST API)
- Backend ↔ Database (JDBC)
- Backend ↔ External Services (API calls)

### 11. CLASS DIAGRAM (Domain Model)
**Classes**:
- User (attributes and methods for profile management)
- Skill (attributes for skill definition)
- Swap (states and transitions)
- TrustLedger (hashing and validation methods)
- SkillPassport (PDF generation)
- LocationMatcher (proximity filtering)
- TokenEscrow (token holding logic)

### 12. COMPONENT DIAGRAM
**Components**:
- Authentication Component
- Location Service Component
- Skill Discovery Component
- Swap Management Component
- Trust Ledger Component
- Notification Component
- External Integration Component (Video, QR, PDF)

---

## ADDITIONAL DETAILS FOR DIAGRAM GENERATION

### Color Coding Suggestions:
- **Users Entities**: Blue
- **Transactions (Swaps)**: Green
- **Trust/Security (TrustLedger)**: Red
- **External Services**: Orange
- **Data Stores**: Purple

### Frequency and Volume Indicators:
- **High Frequency**: Skill searches, location updates (multiple per session)
- **Medium Frequency**: Skill creation, swap requests
- **Low Frequency**: Profile updates, trust score changes

### Performance Considerations (for diagrams):
- Caching layer for frequently accessed skills
- Indexed geo-queries for 5km proximity
- Hash verification on critical transactions
- Token escrow timeouts (48 hours)

### Security Flows (note in diagrams):
- JWT authentication for API calls
- SHA-256 hashing for immutability
- Token escrow for transaction safety
- Verification gates (video + QR) for authenticity

---

## END OF PROMPT

**How to Use This Prompt**:
1. Copy the entire content above
2. Paste into your chosen diagram tool:
   - **Miro**: Create shapes and connections based on entities and flows
   - **Lucidchart**: Use templates for ERD, DFD, Sequence diagrams
   - **Draw.io**: Build each diagram using provided specifications
   - **PlantUML/Mermaid**: Use text-based syntax for automatic layout
   - **Creately/Smartdraw**: Use collaborative features
3. Generate one diagram at a time following "REQUEST DIAGRAMS TO GENERATE" section
4. Reference the Core Entities, Relationships, and Flows sections for accurate representation

