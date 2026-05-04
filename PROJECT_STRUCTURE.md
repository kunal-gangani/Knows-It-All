# KnowItAll Project Structure

## Overview
KnowItAll is an Android application built with Kotlin and Jetpack Compose. It implements a skill-sharing and trading platform with authentication, skill management, and user interactions.

---

## Complete Project File Structure

### Root Directory Files
```
KnowItAll/
├── .gitignore                           # Git ignore rules
├── ARCHITECTURE_ANALYSIS.md             # Architecture documentation
├── AUTH_TESTING_GUIDE.md                # Authentication testing guidelines
├── build.gradle.kts                     # Root Gradle build configuration
├── build_check.log                      # Build check log
├── build_output.log                     # Build output log
├── DOCUMENTATION_PROMPT.md              # Documentation prompt template
├── gradle.properties                    # Gradle properties configuration
├── gradlew                              # Gradle wrapper (Linux/Mac)
├── gradlew.bat                          # Gradle wrapper (Windows)
├── GSD-STYLE.md                         # GSD styling guidelines
├── local.properties                     # Local properties (environment-specific)
├── model_capabilities.yaml              # Model capabilities configuration
├── PROJECT_COMPLETE_OVERVIEW.md         # Complete project overview
├── PROJECT_REVIEW.md                    # Project review document
├── PROJECT_RULES.md                     # Project rules and standards
├── PROJECT_SUMMARY.md                   # Project summary
├── QUICK_TEST.ps1                       # Quick test PowerShell script
├── README.md                            # Project README
├── settings.gradle.kts                  # Gradle settings configuration
├── SPRINT_IMPLEMENTATION_GUIDE.md       # Sprint implementation guidelines
└── Unit                                 # (file without extension)
```

---

## Directory Structure

### 📁 `.agent/` Directory
Configuration files for agent customization
```
.agent/
└── (Agent configuration files)
```

### 📁 `.gemini/` Directory
Gemini AI model integration files
```
.gemini/
└── (Gemini integration files)
```

### 📁 `.git/` Directory
Git version control system directory
```
.git/
└── (Git version control metadata)
```

### 📁 `.github/` Directory
GitHub workflows and CI/CD configuration
```
.github/
└── java-upgrade/
    ├── .gitignore
    └── hooks/
```

### 📁 `.gradle/` Directory
Gradle build system cache and metadata
```
.gradle/
└── (Gradle cache files)
```

### 📁 `.gsd/` Directory
GSD (General Skill Development) configuration
```
.gsd/
└── (GSD configuration files)
```

### 📁 `.idea/` Directory
IntelliJ IDEA project configuration
```
.idea/
├── .gitignore
├── AndroidProjectSystem.xml
├── caches/                              # IDE cache files
├── gradle.xml                           # Gradle configuration
├── markdown.xml                         # Markdown settings
├── migrations.xml                       # Migration history
├── misc.xml                             # Miscellaneous settings
├── runConfigurations.xml                # Run configurations
├── studiobot.xml                        # StudioBot settings
├── vcs.xml                              # Version control settings
└── workspace.xml                        # Workspace configuration
```

### 📁 `.kotlin/` Directory
Kotlin compiler configuration
```
.kotlin/
└── (Kotlin build metadata)
```

### 📁 `.sixth/` Directory
Sixth sense development tools configuration
```
.sixth/
└── (Development tools configuration)
```

### 📁 `.vscode/` Directory
Visual Studio Code configuration
```
.vscode/
└── settings.json                        # VS Code settings
```

### 📁 `adapters/` Directory
Adapter implementations for different LLM models
```
adapters/
├── CLAUDE.md                            # Claude API adapter documentation
├── GEMINI.md                            # Gemini API adapter documentation
└── GPT_OSS.md                           # GPT Open Source adapter documentation
```

### 📁 `app/` Directory
Main Android application module
```
app/
├── .gitignore                           # App-specific git ignore
├── build.gradle.kts                     # App Gradle build configuration
├── proguard-rules.pro                   # ProGuard obfuscation rules
├── build/                               # Build output directory
│   ├── generated/
│   │   ├── ap_generated_sources/        # Annotation processor generated sources
│   │   ├── ksp/                         # Kotlin Symbol Processing generated files
│   │   └── res/                         # Generated resources
│   ├── intermediates/                   # Build intermediates
│   │   ├── aar_metadata_check/
│   │   ├── annotation_processor_list/
│   │   ├── apk/
│   │   ├── apk_ide_redirect_file/
│   │   ├── app_metadata/
│   │   ├── assets/
│   │   ├── compatible_screen_manifest/
│   │   ├── compile_and_runtime_not_namespaced_r_class_jar/
│   │   ├── compressed_assets/
│   │   ├── data_binding_layout_info_type_merge/
│   │   ├── data_binding_layout_info_type_package/
│   │   ├── desugar_graph/
│   │   ├── dex/
│   │   ├── dex_archive_input_jar_hashes/
│   │   ├── dex_number_of_buckets_file/
│   │   ├── duplicate_classes_check/
│   │   ├── external_file_lib_dex_archives/
│   │   ├── external_libs_dex_archive/
│   │   └── (other build intermediates)
│   ├── kotlin/
│   ├── kspCaches/
│   │   └── debug/
│   ├── outputs/
│   └── tmp/
├── src/
│   ├── androidTest/                     # Android instrumented tests
│   │   └── java/com/example/know_it_all/
│   │       └── ExampleInstrumentedTest.kt
│   ├── main/                            # Main application source code
│   │   ├── AndroidManifest.xml          # Android application manifest
│   │   ├── java/
│   │   │   └── com/example/know_it_all/
│   │   │       ├── KnowItAllApplication.kt      # Application entry point
│   │   │       ├── MainActivity.kt              # Main activity
│   │   │       ├── data/
│   │   │       ├── presentation/
│   │   │       ├── ui/
│   │   │       └── util/
│   │   └── res/                         # Android resources
│   │       ├── drawable/
│   │       ├── mipmap-*/
│   │       ├── values/
│   │       └── xml/
│   └── test/                            # Unit tests
│       └── java/com/example/know_it_all/
│           ├── ExampleUnitTest.kt
│           ├── data/
│           └── presentation/
```

---

## Detailed Source Code Structure

### Main Application Source: `app/src/main/java/com/example/know_it_all/`

#### 📄 Root Application Files
```
├── KnowItAllApplication.kt              # Application configuration and initialization
└── MainActivity.kt                      # Main activity entry point
```

---

### 📁 `data/` Directory - Data Layer

#### Database Layer: `data/local/`
```
data/local/
├── converters/
│   └── RoomConverters.kt                # Type converters for Room database
├── dao/
│   ├── SkillDao.kt                      # Data access object for Skills
│   ├── SwapDao.kt                       # Data access object for Swaps
│   ├── TrustLedgerDao.kt                # Data access object for Trust Ledger
│   └── UserDao.kt                       # Data access object for Users
└── db/
    └── KnowItAllDatabase.kt             # Room database configuration and creation
```

#### Model/DTO Layer: `data/model/`
```
data/model/
├── ApiResponse.kt                       # API response wrapper class
├── Skill.kt                             # Skill entity model
├── Swap.kt                              # Swap/Trade entity model
├── TrustLedger.kt                       # Trust ledger entity model
├── User.kt                              # User entity model
└── dto/
    ├── LedgerDTO.kt                     # Ledger data transfer object
    ├── SkillDTO.kt                      # Skill data transfer object
    ├── SwapDTO.kt                       # Swap data transfer object
    └── UserDTO.kt                       # User data transfer object
```

#### Remote/API Layer: `data/remote/`
```
data/remote/
├── api/
│   ├── LedgerService.kt                 # Ledger API service interface
│   ├── SkillService.kt                  # Skill API service interface
│   ├── SwapService.kt                   # Swap API service interface
│   └── UserService.kt                   # User API service interface
├── MockDataSource.kt                    # Mock data source for testing
└── RetrofitClient.kt                    # Retrofit HTTP client configuration
```

#### Repository Layer: `data/repository/`
```
data/repository/
├── LedgerRepository.kt                  # Repository for Trust Ledger operations
├── SkillRepository.kt                   # Repository for Skill operations
├── SwapRepository.kt                    # Repository for Swap/Trade operations
└── UserRepository.kt                    # Repository for User operations
```

---

### 📁 `presentation/` Directory - Presentation Layer

#### ViewModels: `presentation/viewmodel/`
```
presentation/viewmodel/
├── AuthViewModel.kt                     # Authentication view model
├── LedgerViewModel.kt                   # Trust Ledger view model
├── RadarViewModel.kt                    # Radar/Discovery view model
├── SkillViewModel.kt                    # Skill management view model
├── TradeViewModel.kt                    # Trading/Swap view model
└── ViewModelFactory.kt                  # Factory for creating view models
```

#### UI Screens: `presentation/ui/`
```
presentation/ui/
├── app/
│   └── KnowItAllApp.kt                  # Main app composable
├── components/
│   ├── BottomNavigationBar.kt           # Bottom navigation bar component
│   └── CommonComponents.kt              # Shared composable components
├── navigation/
│   ├── BottomNavItem.kt                 # Navigation item definition
│   ├── NavGraph.kt                      # Navigation graph definition
│   └── Navigation.kt                    # Navigation setup
└── screen/
    ├── auth/
    │   ├── LoginScreen.kt               # Login screen UI
    │   ├── RegisterScreen.kt            # Registration screen UI
    │   └── SplashScreen.kt              # Splash screen UI
    └── main/
        ├── RadarScreenEnhanced.kt       # Radar/Discovery screen
        ├── SkillProfileScreenEnhanced.kt # Skill profile screen
        ├── TradeScreenEnhanced.kt       # Trade/Swap screen
        └── VaultScreenEnhanced.kt       # Vault/Portfolio screen
```

---

### 📁 `ui/` Directory - UI Theme & Styling

#### Theme Configuration: `ui/theme/`
```
ui/theme/
├── Color.kt                             # Color palette definitions
├── Theme.kt                             # Material Design theme configuration
└── Type.kt                              # Typography configuration
```

---

### 📁 `util/` Directory - Utility Classes

#### Utility Files
```
util/
├── HashUtil.kt                          # Hashing utility functions
├── LocationService.kt                   # Location services
├── LocationUtil.kt                      # Location utility functions
├── QRCodeGenerator.kt                   # QR code generation
├── SessionManager.kt                    # Session management
├── SkillPassportGenerator.kt            # Skill passport generation
└── VideoCallManager.kt                  # Video call management
```

---

## Test Structure

### Unit Tests: `app/src/test/java/com/example/know_it_all/`
```
test/
├── data/
│   └── repository/
│       └── UserRepositoryTest.kt        # User repository unit tests
└── presentation/
    └── viewmodel/
        └── AuthViewModelTest.kt         # Authentication view model tests
```

### Instrumented Tests: `app/src/androidTest/java/com/example/know_it_all/`
```
androidTest/
└── ExampleInstrumentedTest.kt           # Android instrumented test example
```

---

## Resources Structure: `app/src/main/res/`

### Drawable Resources
```
res/drawable/
├── ic_launcher_background.xml           # App launcher background
└── ic_launcher_foreground.xml           # App launcher foreground
```

### Mipmap Resources (App Icons - Multiple Densities)
```
res/mipmap-anydpi-v26/                  # Vector drawable icons (API 26+)
res/mipmap-hdpi/                        # High-density icons (240 DPI)
res/mipmap-mdpi/                        # Medium-density icons (160 DPI)
res/mipmap-xhdpi/                       # Extra high-density icons (320 DPI)
res/mipmap-xxhdpi/                      # 2x high-density icons (480 DPI)
res/mipmap-xxxhdpi/                     # 3x high-density icons (640 DPI)
```

### Value Resources
```
res/values/
├── colors.xml                           # Color resource definitions
├── strings.xml                          # String resource definitions
└── themes.xml                           # Theme resource definitions
```

### XML Configuration
```
res/xml/
├── backup_rules.xml                     # Backup rules configuration
├── data_extraction_rules.xml            # Data extraction rules
├── file_paths.xml                       # File path configuration
└── network_security_config.xml          # Network security configuration
```

---

## Build Configuration Files

### Gradle Configuration
```
gradle/
├── libs.versions.toml                   # Dependency versions catalog
└── wrapper/
    └── gradle-wrapper.properties        # Gradle wrapper configuration
```

### Build Configuration
```
build.gradle.kts                         # Root project build configuration
app/build.gradle.kts                     # App module build configuration
settings.gradle.kts                      # Gradle settings
gradle.properties                        # Gradle global properties
local.properties                         # Local machine properties (git-ignored)
```

---

## Documentation Files

### Project Documentation
```
docs/
├── model-selection-playbook.md          # Guide for model selection strategies
├── Project_Structure.docx               # Project structure document (Word format)
├── runbook.md                           # Operational runbook
└── token-optimization-guide.md          # Token optimization guide
```

### Root Documentation
```
├── ARCHITECTURE_ANALYSIS.md             # Architecture analysis document
├── AUTH_TESTING_GUIDE.md                # Authentication testing guidelines
├── DOCUMENTATION_PROMPT.md              # Documentation generation prompt
├── GSD-STYLE.md                         # GSD style guidelines
├── PROJECT_COMPLETE_OVERVIEW.md         # Complete project overview
├── PROJECT_REVIEW.md                    # Project review
├── PROJECT_RULES.md                     # Project standards and rules
├── PROJECT_SUMMARY.md                   # Executive project summary
├── README.md                            # Main project README
└── SPRINT_IMPLEMENTATION_GUIDE.md       # Sprint implementation guidelines
```

---

## Scripts Directory

### Validation and Search Scripts
```
scripts/
├── search_repo.ps1                      # PowerShell script to search repository
├── search_repo.sh                       # Bash script to search repository
├── setup_search.ps1                     # PowerShell script for search setup
├── setup_search.sh                      # Bash script for search setup
├── validate-all.ps1                     # PowerShell validation script (all checks)
├── validate-all.sh                      # Bash validation script (all checks)
├── validate-skills.ps1                  # PowerShell script for skill validation
├── validate-skills.sh                   # Bash script for skill validation
├── validate-templates.ps1               # PowerShell script for template validation
├── validate-templates.sh                # Bash script for template validation
├── validate-workflows.ps1               # PowerShell script for workflow validation
└── validate-workflows.sh                # Bash script for workflow validation
```

---

## GSD Template Directory

### GSD Framework Configuration
```
gsd-template/
├── CHANGELOG.md                         # Version changelog
├── GSD-STYLE.md                         # GSD style guidelines
├── LICENSE                              # License file
├── model_capabilities.yaml              # Model capabilities configuration
├── PROJECT_RULES.md                     # Project rules
├── README.md                            # GSD template README
├── VERSION                              # Version file
├── .agent/                              # Agent configuration
├── .gemini/                             # Gemini integration
├── .gsd/                                # GSD configuration
├── adapters/
│   ├── CLAUDE.md                        # Claude adapter
│   ├── GEMINI.md                        # Gemini adapter
│   └── GPT_OSS.md                       # GPT OSS adapter
├── docs/
│   ├── model-selection-playbook.md
│   ├── runbook.md
│   └── token-optimization-guide.md
└── scripts/
    ├── search_repo.ps1
    ├── search_repo.sh
    ├── setup_search.ps1
    ├── setup_search.sh
    ├── validate-all.ps1
    ├── validate-all.sh
    ├── validate-skills.ps1
    ├── validate-skills.sh
    ├── validate-templates.ps1
    ├── validate-templates.sh
    ├── validate-workflows.ps1
    └── validate-workflows.sh
```

---

## UML & Design Documentation

### UML Diagrams and Design Files
```
UML/
├── Activity.png                         # Activity diagram image
├── Activity_Diagram_KIA.docx            # Activity diagram document
├── Class.png                            # Class diagram image
├── Class_Diagrams_KIA.docx              # Class diagram document
├── Data_Dictionary_Knows_It_All.docx    # Data dictionary documentation
├── ERD.jpeg                             # Entity Relationship Diagram
├── Gemini_Generated_Image_a5z1hca5z1hca5z1.png  # AI-generated design image
├── KnowsItAll.pptx                      # Project presentation
├── Report.docx                          # Design report
├── UseCase.png                          # Use case diagram image
└── Use_Case_KIA.docx                    # Use case diagram document
```

---

## Special Test Script

```
QUICK_TEST.ps1                           # Quick test execution script (PowerShell)
```

---

## Configuration & Environment

```
local.properties                         # Local environment configuration (git-ignored)
gradle.properties                        # Gradle properties
model_capabilities.yaml                  # AI model capabilities configuration
```

---

## Logs

```
build_check.log                          # Build verification log
build_output.log                         # Build output log
```

---

## Summary

### Project Statistics
- **Total Directories**: 50+
- **Total Source Files**: 60+
  - Kotlin Source Files: 40+
  - Resource Files: 20+
- **Test Files**: 4+
- **Documentation Files**: 15+
- **Configuration Files**: 20+
- **Total Files**: 100+

### Key Components
1. **Authentication Module**: Login, Register, Splash screens
2. **Skill Management**: Skill CRUD operations with skill profiles
3. **Trading/Swapping System**: Trade screen for skill exchanges
4. **Trust Ledger**: Track trust and reputation
5. **Radar/Discovery**: Find and discover skills and users
6. **Vault/Portfolio**: User portfolio management
7. **Database**: Room-based local persistence
8. **API Integration**: Retrofit-based remote API calls
9. **UI Framework**: Jetpack Compose for modern UI
10. **Testing**: Unit and instrumented tests

### Technology Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Database**: Room (SQLite)
- **Networking**: Retrofit + OkHttp
- **Architecture**: MVVM (Model-View-ViewModel)
- **Build System**: Gradle (Kotlin DSL)
- **Testing**: JUnit, Android Test Framework

