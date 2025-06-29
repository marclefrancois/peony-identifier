# Peony Identifier

> **Version 1.6.0** - A Kotlin Multiplatform Compose application for identifying peonies across agricultural fields with professional NavHost navigation, type-safe routing, and comprehensive search functionality. Now powered by Kotlin 2.2.0 with enhanced K2 compiler performance and improved user experience.

## 🌿 Project Overview

The Peony Identifier is a production-ready cross-platform application that enables identification of peony varieties across multiple fields using hierarchical field selection and fuzzy string matching. Built with Compose Multiplatform, it features a professional NavHost navigation system, type-safe routing, and optimized performance for both Android and iOS platforms.

## 🚀 Key Features

- **Hierarchical Field Selection**: Cascading dropdowns (Field → Parcel → Row → Position) with intelligent auto-selection
- **Universal Peony Search**: Real-time search across all field locations with autocomplete and fuzzy matching
- **Fuzzy String Matching**: Intelligent peony variety identification with exact/approximate matches
- **Professional Navigation**: Type-safe NavHost with state preservation and cross-platform gesture support
- **Enhanced Design System v1.6.0**: Botanical theming with modern Kotlin 2.2.0 architecture and iOS polish
- **Cross-Platform Images**: Async loading with Coil (Android) and Kamel (iOS)
- **Offline-First**: JSON-based data loading with background threading and caching
- **Accessibility**: WCAG AA compliant design with proper contrast ratios
- **State Persistence**: Selected positions and search terms remain preserved across navigation
- **iOS Safe Area**: Perfect safe area handling with dynamic padding for modern iOS devices

## 🏗️ Architecture

### Clean Architecture Pattern
```
├── presentation/          # UI Layer (Compose, ViewModels, Themes)
├── domain/               # Business Logic (Use Cases, Interfaces)
├── data/                # Data Layer (Repositories, Models, JSON)
└── platform/            # Platform-specific implementations
```

### Key Architectural Decisions
- **Repository/UseCase/ViewModel** pattern for clean separation of concerns
- **Navigation Compose** with type-safe serializable routes
- **Koin** for dependency injection (avoid `object:` singletons for testability)
- **StateFlow** for reactive state management with NavHost state preservation
- **expect/actual** declarations for platform-specific implementations
- **Material3** with custom botanical design system

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
│   │   ├── data/                    # JSON models, repositories
│   │   ├── domain/                  # Use cases, business logic
│   │   ├── presentation/
│   │   │   ├── screen/             # Main UI screens
│   │   │   ├── component/          # Reusable UI components
│   │   │   ├── theme/              # Design system (v1.2)
│   │   │   ├── viewmodel/          # State management
│   │   │   └── state/              # UI state definitions
│   │   └── platform/               # Cross-platform abstractions
│   └── composeResources/
│       └── files/data/             # JSON datasets
├── androidMain/                    # Android-specific code
├── iosMain/                       # iOS-specific code
└── commonTest/                    # Shared test code
iosApp/                            # iOS app entry point
```

## 🎨 Design System v1.2

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

## 🔄 Navigation Flow

### Traditional Field Selection
1. **Field Selection**: Auto-selected first field/parcel or user manual selection via FieldSelectionScreen
2. **Position Selection**: Navigate to PeonyIdentifierScreen for row → position selection with visual selection indicators
3. **Detail Navigation**: Navigate to PeonyDetailScreen for peony information
4. **State Preservation**: All selections (field/parcel/position) remembered across navigation
5. **Gesture Support**: iOS swipe and Android back button with proper animations and safe area handling

### Search Flow (v1.6.0)
1. **Search Access**: Tap floating search button from field selection screen
2. **Real-time Search**: Type variety name with autocomplete suggestions and fuzzy matching
3. **Location Results**: View all field locations containing the searched variety
4. **Direct Navigation**: Tap location to navigate directly to peony detail with preserved search context
5. **Search Persistence**: Return to search results with preserved query and state

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

**Current Status**: ✅ Production Ready v1.6.0 with Comprehensive Search Feature, Enhanced UX, and Cross-Navigation State Preservation