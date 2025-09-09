# Peony Identifier

> **Version 1.7.0** - A Kotlin Multiplatform Compose application for identifying peonies across agricultural fields with comprehensive field notes management, CSV export functionality, and professional tile-based navigation. Features enhanced note-taking capabilities and dual variety tracking system.

## 🌿 Project Overview

The Peony Identifier is a production-ready cross-platform application that enables identification of peony varieties across multiple fields using hierarchical field selection and fuzzy string matching. Built with Compose Multiplatform, it features a professional NavHost navigation system, type-safe routing, and optimized performance for both Android and iOS platforms.

## 🚀 Key Features

- **Tile-Based Navigation**: Professional home screen with three main functions (Search, Identify, Field Notes)
- **Hierarchical Field Selection**: Cascading dropdowns (Field → Parcel → Row → Position) with intelligent auto-selection
- **Universal Peony Search**: Real-time search across all field locations with autocomplete and fuzzy matching
- **Comprehensive Field Notes**: Note-taking system with status tracking (Normal, Dead, Blocked) and timestamp management
- **Enhanced CSV Export**: Dual variety tracking with "In our notes" and "Confirmed in the field" columns
- **Quick Action Buttons**: One-tap marking for dead plants and blocked positions directly from peony details
- **Auto-Save Notes**: Real-time note editing with debounced auto-save functionality
- **Cross-Platform File Sharing**: Native sharing for CSV exports (Android Share Intent, iOS Activity Controller)
- **Fuzzy String Matching**: Intelligent peony variety identification with exact/approximate matches
- **Professional Navigation**: Type-safe NavHost with state preservation and cross-platform gesture support
- **Enhanced Design System v1.7.0**: Botanical theming with Material3 cards and consistent spacing
- **Cross-Platform Images**: Async loading with Coil (Android) and Kamel (iOS)
- **Offline-First**: JSON-based data loading with local field notes persistence
- **State Persistence**: All selections and notes remain preserved across navigation

## 🏗️ Architecture

### Clean Architecture Pattern
```
├── presentation/          # UI Layer (Compose, ViewModels, Themes, Navigation)
├── domain/               # Business Logic (Use Cases, Interfaces, Field Notes)
├── data/                # Data Layer (Repositories, Models, JSON, Local Storage)
└── platform/            # Platform-specific implementations (File Sharing, Time)
```

### Key Architectural Decisions
- **Repository/UseCase/ViewModel** pattern for clean separation of concerns
- **Navigation Compose** with type-safe serializable routes and tile-based home screen
- **Koin** for dependency injection (avoid `object:` singletons for testability)  
- **StateFlow** for reactive state management with field notes auto-save
- **Local JSON Storage** for field notes with thread-safe Mutex protection
- **expect/actual** declarations for platform-specific file sharing implementations
- **Material3** with custom botanical design system and card-based UI

## 🛠️ Development Setup

### Prerequisites
- **Kotlin** 2.2.0 (Latest with K2 compiler)
- **Android Studio** with Compose Multiplatform plugin
- **Xcode** 15+ (for iOS development)
- **JDK** 17+

### Build Commands
```bash
# Build all platforms
./gradlew build

# Android-specific builds
./gradlew composeApp:assembleDebug
./gradlew composeApp:assembleRelease

# iOS framework builds
./gradlew composeApp:linkDebugFrameworkIosX64
./gradlew composeApp:linkDebugFrameworkIosArm64
./gradlew composeApp:linkDebugFrameworkIosSimulatorArm64

# Run tests
./gradlew test
./gradlew composeApp:testDebugUnitTest

# Clean build
./gradlew clean
```

## 📁 Project Structure

```
composeApp/
├── commonMain/
│   ├── kotlin/com/pivoinescapano/identifier/
│   │   ├── data/                    # JSON models, repositories, field notes storage
│   │   ├── domain/                  # Use cases, business logic, field notes management
│   │   ├── presentation/
│   │   │   ├── screen/             # Main UI screens (Home, FieldNotes, etc.)
│   │   │   ├── component/          # Reusable UI components
│   │   │   ├── theme/              # Design system (v1.7.0)
│   │   │   ├── viewmodel/          # State management with field notes
│   │   │   ├── state/              # UI state definitions
│   │   │   └── navigation/         # Type-safe routing
│   │   └── platform/               # Cross-platform abstractions (File sharing, Time)
│   └── composeResources/
│       └── files/data/             # JSON datasets (3 field files + peony database)
├── androidMain/                    # Android-specific code (Coil, file sharing)
├── iosMain/                       # iOS-specific code (Kamel, UIKit integration)
└── commonTest/                    # Shared test code
iosApp/                            # iOS app entry point with SwiftUI wrapper
```

## 🎨 Design System v1.7.0

### Botanical Color Palette
```kotlin
// Primary botanical green theme
val PrimaryGreen = Color(0xFF2E7D32)      # Rich botanical green
val PrimaryLight = Color(0xFF66BB6A)      # Light sage green
val PrimaryContainer = Color(0xFFE8F5E8)  # Light green container

// Semantic colors
val ExactMatch = Color(0xFF2E7D32)        # Green for exact matches
val FuzzyMatch = Color(0xFFED6C02)        # Orange for suggestions
val Error = Color(0xFFD32F2F)             # Red for errors
```

### Typography Hierarchy
- **Display**: 36sp/32sp for major headings
- **Headline**: 28sp/24sp/20sp for section headers
- **Body**: 16sp/14sp/12sp for content with optimized line heights
- **Label**: 14sp/12sp/11sp for UI elements
- **Specialized**: Caption/Overline for micro-content

### Spacing System (Golden Ratio Based)
```kotlin
val XXS = 2.dp, XS = 4.dp, S = 8.dp, M = 12.dp
val L = 16.dp, XL = 24.dp, XXL = 32.dp, XXXL = 48.dp
```

## 🔧 Key Technologies

| Component | Technology | Version |
|-----------|------------|----------|
| **UI Framework** | Compose Multiplatform | 1.8.2 |
| **Navigation** | Navigation Compose | 2.9.0-beta03 |
| **Language** | Kotlin | 2.2.0 |
| **DI** | Koin | 4.1.0 |
| **Serialization** | kotlinx.serialization | 1.9.0 |
| **Image Loading** | Coil (Android) / Kamel (iOS) | 2.7.0 / 0.9.5 |
| **Architecture** | Clean Architecture + NavHost | - |
| **State Management** | StateFlow + NavHost State Preservation | - |

## 📱 Platform-Specific Features

### Android
- **Coil** image loading with caching
- **Portrait orientation** lock
- **Hardware back button** support
- **Material3** theming integration

### iOS
- **Kamel** async image loading
- **System gesture navigation** with state preservation
- **UIKit integration** via expect/actual pattern
- **NavHost transition** animations with proper directional support

## 🧪 Testing Strategy

### Current Test Coverage
- **Unit Tests**: Repository and use case logic
- **Platform Tests**: Android-specific functionality
- **Integration Tests**: JSON parsing and data loading

### Running Tests
```bash
# All tests
./gradlew test

# Android unit tests
./gradlew composeApp:testDebugUnitTest

# Specific test class
./gradlew test --tests "*FuzzyMatchingTest*"
```

## 📊 Performance Optimizations

- **Background JSON Loading**: 5.1MB peony database loaded on background thread
- **Thread-Safe Caching**: Mutex-based cache prevents race conditions
- **Lazy State Management**: StateFlow with proper lifecycle handling
- **Image Optimization**: Platform-specific caching strategies
- **Memory Management**: Proper Compose state handling

## 🔄 Navigation Flow (v1.7.0)

### Home Screen Navigation
1. **Launch**: App opens to tile-based home screen with three main functions
2. **Function Selection**: Choose Search, Identify, or Field Notes via Material3 cards
3. **Context Preservation**: Each tile maintains its own navigation stack and state

### Field Identification Flow  
1. **Home → Identify**: Tap "Identify" tile → navigate to field selection
2. **Field Selection**: Select field/parcel → position selection with visual indicators
3. **Detail & Notes**: View peony info with enhanced note-taking and quick action buttons
4. **Auto-Save**: Notes automatically saved with timestamp and status tracking

### Universal Search Flow
1. **Home → Search**: Tap "Search" tile → navigate to search interface  
2. **Real-time Search**: Type variety name with autocomplete and fuzzy matching
3. **Location Results**: View all field locations containing searched variety
4. **Direct Navigation**: Tap location → navigate to peony detail with preserved context

### Field Notes Management Flow
1. **Home → Field Notes**: Tap "Field Notes" tile → navigate to notes list
2. **Notes Overview**: View all positions with notes, sorted by field/parcel/row/position
3. **Export & Management**: Enhanced CSV export with dual variety columns
4. **Detail Editing**: Tap note → edit with auto-save and status management

## 🔄 Data Flow

1. **Navigation Parameters**: Type-safe route objects pass data between screens
2. **Data Retrieval**: Repository loads field entry from cached JSON
3. **Fuzzy Matching**: Use case performs string matching against peony database
4. **State Update**: ViewModel updates UI state with results
5. **UI Rendering**: Compose renders updated state with NavHost animations

## 🚢 Deployment

### Android
- **Target SDK**: 35
- **Min SDK**: 24
- **Build Type**: APK/AAB
- **Signing**: Debug/Release configurations

### iOS
- **Target**: iOS 14+
- **Architecture**: arm64, x86_64, arm64-simulator
- **Framework**: XCFramework generation
- **Integration**: SwiftUI wrapper in iosApp/

## 🔍 Debugging & Development

### Common Issues
1. **Build Failures**: Clean project and invalidate caches
2. **iOS Simulator**: Ensure correct target architecture
3. **Image Loading**: Check INTERNET permission (Android)
4. **Navigation**: Verify BackHandler implementations

### Development Tips
- Use `./gradlew --no-daemon` for memory-constrained builds
- Test NavHost navigation and gesture support on physical iOS devices
- Monitor JSON loading performance with profiler
- Validate design system changes across both platforms

## 📚 Additional Resources

- **[CLAUDE.md](./CLAUDE.md)**: AI assistant project guidance
- **[plan.md](./plan.md)**: Detailed project roadmap and feature status
- **[Kotlin Multiplatform Docs](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)**
- **[Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)**

## 🤝 Contributing

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/amazing-feature`
3. **Follow** the existing architecture patterns and design system
4. **Test** on both Android and iOS platforms
5. **Commit** with descriptive messages following existing patterns
6. **Create** a Pull Request with detailed description

### Code Style Guidelines
- Follow **Clean Architecture** principles
- Use **Koin** for dependency injection
- Maintain **cross-platform compatibility**
- Follow **Material3** design system guidelines
- Write **testable code** with proper abstractions

---

**Current Status**: ✅ Production Ready v1.7.0 with Comprehensive Field Notes Management, Enhanced CSV Export, and Tile-Based Navigation