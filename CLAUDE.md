# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Kotlin Multiplatform Compose application targeting Android and iOS platforms. The project uses Compose Multiplatform for shared UI and follows the standard KMP project structure.

## Build Commands

- **Build for all platforms**: `./gradlew build`
- **Build Android**: `./gradlew composeApp:assembleDebug`
- **Build iOS framework**: `./gradlew composeApp:linkDebugFrameworkIosX64` (or iosArm64/iosSimulatorArm64)
- **Run tests**: `./gradlew test`
- **Run Android tests**: `./gradlew composeApp:testDebugUnitTest`
- **Clean build**: `./gradlew clean`

## Architecture

### Project Structure
- `/composeApp/` - Shared Compose Multiplatform code
  - `commonMain/` - Platform-agnostic code (UI, business logic)
  - `androidMain/` - Android-specific implementations
  - `iosMain/` - iOS-specific implementations
  - `commonTest/` - Shared test code
- `/iosApp/` - iOS app entry point with SwiftUI wrapper

### App Architecture
- **Repository/UseCase/ViewModel pattern**: Follow clean architecture principles
  - Repositories for data access (JSON loading, peony database queries)
  - Use cases for business logic (fuzzy matching, peony selection)
  - ViewModels for UI state management
- **Dependency Injection**: Always use Koin for DI - avoid `object:`-based singletons as they are hard to test
- **Testability**: Structure code to support unit testing with proper dependency injection

### Key Patterns
- **Expected/Actual declarations**: Platform-specific implementations use `expect/actual` pattern (see `Platform.kt`)
- **Compose UI**: All UI is built with Compose Multiplatform using Material3
- **Resource management**: Resources are managed through `composeResources/` and accessed via generated `Res` class
- **Package structure**: All Kotlin code is under `com.pivoinescapano.identifier`

### Platform Implementations
- Android platform detection in `Platform.android.kt` uses `Build.VERSION.SDK_INT`
- iOS platform detection in `Platform.ios.kt` uses `UIDevice.currentDevice`
- Main entry points: `MainActivity.kt` (Android), `MainViewController.kt` (iOS)

## Dependencies

The project uses Gradle version catalogs (`gradle/libs.versions.toml`) for dependency management:
- Kotlin 2.1.21
- Compose Multiplatform 1.8.1
- Android target SDK 35, min SDK 24
- AndroidX Lifecycle for ViewModel and runtime composition

## Testing

- Uses kotlin-test for common tests
- JUnit for platform-specific Android tests
- Test files should be placed in appropriate test source sets (`commonTest`, `androidTest`, etc.)
- Only use adb to test android result when asked specifficaly

## Coding Guidelines

- **Safety**: 
  - Never use force unwrapping