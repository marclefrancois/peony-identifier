# Peony Identifier

> **Version 1.2** - A Kotlin Multiplatform Compose application for identifying peonies across agricultural fields with advanced design system and native iOS-style navigation.

## 🌿 Project Overview

The Peony Identifier is a production-ready cross-platform application that enables identification of peony varieties across 4 different fields using hierarchical field selection and fuzzy string matching. Built with Compose Multiplatform, it features a comprehensive design system, fluid gesture navigation, and optimized performance for both Android and iOS platforms.

## 🚀 Key Features

- **Hierarchical Field Selection**: Cascading dropdowns (Field → Parcel → Row → Position)
- **Fuzzy String Matching**: Intelligent peony variety identification with exact/approximate matches
- **Advanced Navigation**: Native iOS-style swipe gestures with velocity-based detection
- **Enhanced Design System v1.2**: Botanical theming with golden ratio-based spacing
- **Cross-Platform Images**: Async loading with Coil (Android) and Kamel (iOS)
- **Offline-First**: JSON-based data loading with background threading and caching
- **Accessibility**: WCAG AA compliant design with proper contrast ratios

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
- **Koin** for dependency injection (avoid `object:` singletons for testability)
- **StateFlow** for reactive state management
- **expect/actual** declarations for platform-specific implementations
- **Material3** with custom botanical design system

## 🛠️ Development Setup

### Prerequisites
- **Kotlin** 2.1.21
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
| **UI Framework** | Compose Multiplatform | 1.8.1 |
| **Language** | Kotlin | 2.1.21 |
| **DI** | Koin | 4.1.0 |
| **Serialization** | kotlinx.serialization | 1.8.0 |
| **Image Loading** | Coil (Android) / Kamel (iOS) | 2.7.0 / 0.9.5 |
| **Architecture** | Clean Architecture | - |
| **State Management** | StateFlow + Compose State | - |

## 📱 Platform-Specific Features

### Android
- **Coil** image loading with caching
- **Portrait orientation** lock
- **Hardware back button** support
- **Material3** theming integration

### iOS
- **Kamel** async image loading
- **Native swipe gestures** with velocity tracking
- **UIKit integration** via expect/actual pattern
- **iOS-style navigation** animations

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

## 🔄 Data Flow

1. **Field Selection**: User selects field → parcel → row → position
2. **Data Retrieval**: Repository loads field entry from cached JSON
3. **Fuzzy Matching**: Use case performs string matching against peony database
4. **State Update**: ViewModel updates UI state with results
5. **UI Rendering**: Compose renders updated state with animations

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
- Test gesture navigation on physical iOS devices
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

**Current Status**: ✅ Production Ready v1.2 with Enhanced Design System