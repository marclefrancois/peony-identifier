# Peony Identifier App Plan

## Project Status: ✅ Core MVP Complete

A Kotlin Multiplatform Compose app for identifying peonies across 4 fields, with hierarchical selection and detailed peony information display.

### Completed Features
- ✅ Clean architecture with Repository/UseCase/ViewModel pattern
- ✅ Koin dependency injection setup
- ✅ Kotlin Multiplatform targeting Android and iOS
- ✅ JSON data loading from resources (field data and peony database)
- ✅ Null-safe JSON parsing with custom serializers
- ✅ Complete force unwrapping elimination for safety
- ✅ Cascading dropdown selection (Field → Parcel → Row → Position)
- ✅ Fuzzy string matching for peony identification
- ✅ Material3 UI with responsive layout
- ✅ Error handling and loading states
- ✅ Cross-platform compilation validation
- ✅ **Optimized UI Layout**: Space-efficient design with proper OS control respect
- ✅ **Compact Selection Controls**: 2x2 grid layout at top saves 70% more space for content

## Current App Layout ✅ Recently Redesigned
- **Top Section**: Compact selection controls in 2x2 grid (Field/Parcel, Row/Position)
  - Uses WindowInsets padding to respect system bars (status bar, navigation bar)  
  - Modern Material3 Surface design with smaller typography
  - Cascading logic: Field → Parcel → Row → Position selection
- **Main Section**: Maximized peony details display area (75% of screen space)
  - Variety name card (no redundant position info)
  - Exact match peony details with cultivar, originator, date, group, description
  - Fuzzy match suggestions when exact match not found
  - Proper scrolling for long descriptions

## Data Structure

### Field JSONs (4 files)
- `Champ1PP.json`, `Champ2PP.json`, etc. (located in `data/` folder)
- Contains flat array structure with field hierarchy
- Example structure:
```json
[
  {
    "champ": "1",
    "parcelle": "1-PP", 
    "rang": "1",
    "trou": "1",
    "variete": "? blanche double",
    "annee_plantation": null,
    "taille": "p",
    "etiquette": null,
    "vente": null
  }
]
```

### Peony Database
- `peony-database.json` (located in `data/` folder)
- Array structure with peony details
- Example structure:
```json
[
  {
    "id": 1979,
    "cultivar": "A la Mode",
    "originator": "Klehm, R.G.",
    "date": "1981",
    "group": "Lactiflora",
    "description": "Single lactiflora, pure white large flower...",
    "image": "https://americanpeonysociety.org/wp-content/uploads/2019/04/Peony-A-la-Mode.jpg",
    "url": "https://americanpeonysociety.org/cultivars/peony-registry/a-la-mode/"
  }
]
```

## User Flow
1. Select field (champ) → populates parcel spinner
2. Select parcel (parcelle) → populates row spinner  
3. Select row (rang) → populates position spinner
4. Select position (trou) → shows peony variety name + fuzzy matches against peony database by cultivar
5. Display matched peony details with image from URL in main display area

## Architecture Implementation ✅ Complete
- ✅ Clean Repository pattern with interfaces and implementations
- ✅ Use cases for business logic (fuzzy matching, field selection)
- ✅ ViewModel with reactive state management using StateFlow
- ✅ Koin DI modules for all dependencies
- ✅ Custom JSON serializers for robust parsing (handles mixed Boolean/String types)
- ✅ Null-safe field handling with "Unknown" fallbacks
- ✅ Material3 UI components with proper error states
- ✅ Cross-platform resource loading via Compose Resources

## Recent Design Improvements ✅ Just Completed
- ✅ **Space Optimization**: Moved selection controls to top, giving 70% more space to peony details
- ✅ **OS Control Respect**: Added proper WindowInsets padding for system bars
- ✅ **Compact Grid Layout**: 2x2 dropdown arrangement instead of vertical stack
- ✅ **Redundancy Removal**: Eliminated duplicate field position info from variety card
- ✅ **Modern Material3**: Surface design with proper color theming and typography hierarchy

## Todo List for Version 1.0

### High Priority (Core Functionality)
- [ ] **Platform Testing**: Test redesigned UI on both Android and iOS devices
  - Verify new compact 2x2 dropdown grid works correctly
  - Test peony lookup and display with maximized space
  - Validate WindowInsets padding on different devices
  - Check UI layout on different screen sizes and orientations

### Medium Priority (Polish & UX)
- [ ] **Image Loading**: Add async image loading from peony URLs with caching
  - Implement proper loading states for images in peony cards
  - Add fallback for broken/missing images
  - Consider image caching strategy for offline viewing

### Low Priority (Future Enhancements)
- [ ] **Network Error Handling**: Improve error handling for connectivity issues
- [ ] **Performance**: Optimize JSON loading with background threading
- [ ] **Testing**: Add unit tests for fuzzy matching algorithm
- [ ] **Testing**: Add integration tests for repository implementations
- [ ] **Accessibility**: Add content descriptions and proper focus handling

## Current Technical Stack
- **Language**: Kotlin 2.1.21
- **UI**: Compose Multiplatform 1.8.1 with Material3
- **Architecture**: Clean Architecture (Repository/UseCase/ViewModel)
- **DI**: Koin
- **Serialization**: kotlinx.serialization with custom serializers
- **State Management**: StateFlow/Compose State
- **Platforms**: Android (SDK 24-35), iOS (via Kotlin/Native)