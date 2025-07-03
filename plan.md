# Peony Identifier App Plan

## Project Status: 🚧 Planning v1.7.0

A Kotlin Multiplatform Compose app for identifying peonies across multiple fields, with enhanced branding, improved navigation flow, and larger typography for better accessibility. Now powered by Kotlin 2.2.0 with enhanced performance, latest framework features, and improved user experience.

## Version 1.7.0 Status: 📋 PLANNED

### 🎯 Version 1.7.0: Enhanced Field Management & Note-Taking - **IN PLANNING**

#### Major Changes: Restructured Navigation Flow

- [ ] **New Home Screen Architecture**: Tile-based landing page with 3 main functions
  - [ ] 🔍 **Search Tile**: Navigate to universal peony search (existing v1.6.0 feature)
  - [ ] 📍 **Identify Tile**: Navigate to field selection → peony identification flow
  - [ ] 📝 **Field Notes Tile**: Navigate to comprehensive field notes management
  - [ ] Material3 card-based design with icons and descriptive text
  - [ ] Clean separation of app's three core functions

- ✅ **Enhanced Field Selection UX**: Streamlined navigation interaction
  - ✅ **Clickable Selection Summary**: Replace continue button with tappable selection summary
    - ✅ Selection summary becomes interactive element that triggers navigation
    - ✅ Visual feedback on tap (selected state styling with enhanced colors)
    - ✅ Maintains current selection state display functionality
    - ✅ Improves single-tap workflow by reducing UI elements

- [ ] **Enhanced Peony Details with Field Notes**: In-field documentation system
  - [ ] **Quick Action Buttons**: 
    - [ ] ❌ "Mark as Dead" - single tap to flag deceased peonies
    - [ ] 🚫 "Position Blocked" - mark positions as inaccessible/blocked
  - [ ] **Custom Notes Field**: 
    - [ ] Multi-line text input with dictation support
    - [ ] Real-time save to local storage
    - [ ] Character limit with counter (500 chars recommended)
  - [ ] **Notes Persistence**: 
    - [ ] JSON-based local storage using platform file system
    - [ ] Structure: `{champ, parcelle, rang, trou, notes, isDead, isBlocked, timestamp}`

- [ ] **Field Notes Management Screen**: Comprehensive notes overview
  - [ ] **Unified List Design**: Reuse position list screen components
  - [ ] **Comprehensive Location Cards**: Show full hierarchy (Field → Parcel → Row → Position)
  - [ ] **Smart Filtering**: Display only positions with associated notes
  - [ ] **Multi-Level Sorting**: Primary by field, secondary by parcel, tertiary by row, quaternary by position
  - [ ] **Note Preview**: Truncated note text with full view on tap

- [ ] **Export & Management Features**: Data portability and maintenance
  - [ ] **CSV Export**: 
    - [ ] Headers: Field, Parcel, Row, Position, Variety, Notes, Status, Date
    - [ ] Platform-specific file sharing (Android: Share Intent, iOS: Activity Controller)
  - [ ] **Clear All Notes**: 
    - [ ] Confirmation dialog with destructive action styling
    - [ ] Option to export before clearing
    - [ ] Complete data reset functionality

## Current App Features ✅ Complete

### Core Functionality
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

### UI/UX Features
- ✅ **Optimized UI Layout**: Space-efficient design with proper OS control respect
- ✅ **Compact Selection Controls**: 2x2 grid layout at top saves 70% more space for content
- ✅ **Async Image Loading**: Platform-specific image loading with caching (Android) and placeholders (iOS)
- ✅ **Complete Information Display**: Always show field entry data even without peony matches
- ✅ **Cross-Platform Deployment**: Both Android and iOS builds working successfully
- ✅ **Professional Navigation System**: Type-safe NavHost with state preservation
- ✅ **Enhanced Design System v1.2**: Complete visual refinement with botanical theming
- ✅ **Universal Peony Search v1.6.0**: Comprehensive search feature with autocomplete and state preservation

### Navigation Architecture (v1.7.0)
- **Enhanced Navigation System**: Tile-based home screen with professional NavHost routing
  - Navigation Compose with serializable route objects for compile-time safety
  - Five-screen architecture: Home → FieldSelection/Search/FieldNotes → PeonyIdentifier → PeonyDetail
  - Consistent 300ms horizontal slide animations for all transitions
  - State preservation across navigation (field/parcel/search/notes selections remembered)
  - Cross-platform BackHandler support with iOS gesture navigation integration
  - Separated search and identification flows with dedicated entry points

## User Flow (v1.7.0)

### New Home Screen Flow
1. Launch app → navigate to tile-based home screen
2. Choose primary function: Search, Identify, or Field Notes
3. Navigate to selected workflow with preserved context

### Enhanced Identification Flow
1. **Home Screen**: Tap "Identify" tile → navigate to field selection
2. **Field Selection**: Select field (champ) → populates parcel spinner
3. **Position Selection**: Select parcel/row/position → shows peony variety details
4. **Enhanced Details**: View peony info + add field notes with quick actions
5. **Note Taking**: Mark status (dead/blocked) or add custom notes with dictation

### Existing Search Flow (Preserved from v1.6.0)
1. **Home Screen**: Tap "Search" tile → navigate to search interface
2. **Search Interface**: Type peony variety name → see real-time autocomplete
3. **Results**: View all field locations containing that variety
4. **Navigation**: Tap location → navigate to peony detail with enhanced note-taking

### New Field Notes Management Flow
1. **Home Screen**: Tap "Field Notes" tile → navigate to notes list
2. **Notes List**: View all positions with associated notes, sorted by location
3. **Management**: Export notes to CSV or clear all with confirmation
4. **Detail View**: Tap note → view/edit full note with location context

## Data Structure (Enhanced for v1.7.0)

### Field Notes Structure (New)
- `field-notes.json` (local storage)
- Array structure with field notes
- Example structure:
```json
[
  {
    "champ": "1",
    "parcelle": "1-PP",
    "rang": "1",
    "trou": "5",
    "notes": "Peony showing signs of disease on lower leaves",
    "isDead": false,
    "isBlocked": false,
    "timestamp": 1704067200000,
    "variety": "A la Mode"
  }
]
```

### Field JSONs (3 files)
- `Champ1PP.json`, `Champ1GP.json`, `Champ2PP.json` (located in `composeResources/files/`)
- Contains flat array structure with field hierarchy
- **Champ1PP.json**: Field 1, Parcel PP (original dataset)
- **Champ1GP.json**: Field 1, Parcel GP (4,467 entries)
- **Champ2PP.json**: Field 2, Parcel PP (994 entries)
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

## Architecture Implementation (v1.7.0 Enhancements)

### Existing Architecture ✅ Complete
- ✅ Clean Repository pattern with interfaces and implementations
- ✅ Use cases for business logic (fuzzy matching, field selection, search)
- ✅ ViewModel with reactive state management using StateFlow
- ✅ Koin DI modules for all dependencies
- ✅ Custom JSON serializers for robust parsing (handles mixed Boolean/String types)
- ✅ Null-safe field handling with "Unknown" fallbacks
- ✅ Material3 UI components with proper error states
- ✅ Cross-platform resource loading via Compose Resources

### New Architecture Components (v1.7.0)
- [ ] **FieldNotesRepository**: Local JSON persistence with CRUD operations
- [ ] **FieldNotesManager**: Business logic for note management and export
- [ ] **Field Notes Use Cases**: CreateNote, UpdateNote, DeleteNote, ExportNotes
- [ ] **FieldNotesViewModel**: State management for notes list and editing
- [ ] **Platform-Specific Implementations**: Speech recognition and file sharing
- [ ] **Enhanced Data Models**: FieldNote data class with timestamp and status flags

## Technical Stack (v1.7.0)
- **Language**: Kotlin 2.2.0 (Latest stable with K2 compiler)
- **UI**: Compose Multiplatform 1.8.2 with Material3
- **Navigation**: Navigation Compose 2.9.0-beta03 with type-safe routing
- **Architecture**: Clean Architecture (Repository/UseCase/ViewModel)
- **DI**: Koin 4.1.0
- **Serialization**: kotlinx.serialization 1.9.0 with custom serializers
- **State Management**: StateFlow/Compose State with NavHost state preservation
- **Image Loading**: Coil 2.7.0 (Android), Kamel 0.9.5 (iOS) with expect/actual pattern
- **Platforms**: Android (SDK 24-35), iOS (via Kotlin/Native)
- **Build System**: Gradle with Kotlin DSL, Xcode integration
- **New Dependencies (v1.7.0)**:
  - **Speech Recognition**: Platform-specific dictation APIs
  - **File Management**: Enhanced platform file I/O for notes storage
  - **CSV Generation**: Custom CSV writer for export functionality

## Future Enhancements (Version 1.8+)
- [ ] **Additional Field Data**: Integration of remaining field data files (Champ3, Champ4, etc.)
- [ ] **Advanced Note Features**: Photo attachments, voice notes, GPS coordinates
- [ ] **Search Filters**: Filter by field, year, size, or other peony attributes
- [ ] **Cloud Sync**: Synchronize field notes across devices
- [ ] **Network Features**: Sync with remote peony database updates
- [ ] **Offline Favorites**: Save favorite peonies for quick access
- [ ] **Photo Integration**: Add photos of actual plants in the field
- [ ] **GPS Integration**: Location-based field navigation
- [ ] **Testing Expansion**: Unit tests for fuzzy matching and integration tests for repositories
- [ ] **UI Testing**: Automated tests for gesture navigation system
- [ ] **Accessibility**: Content descriptions and improved focus handling
- [ ] **Navigation Polish**: Haptic feedback for gesture interactions
- [ ] **Animation Enhancement**: Parallax effects and sophisticated transitions
- [ ] **Performance**: Advanced caching strategies for very large datasets

## Deployment Status
- ✅ **Version 1.6.0**: Production ready with comprehensive search feature
- 📋 **Version 1.7.0**: In planning phase - Enhanced field management & note-taking

### Current Production Status (v1.6.0)
- ✅ **Android**: APK builds successfully, portrait locked, image loading functional, gesture navigation working
- ✅ **iOS**: Framework builds cleanly, async image loading functional, native swipe gestures implemented
- ✅ **Cross-Platform**: All shared business logic and UI working across both platforms
- ✅ **Navigation**: Professional NavHost system with type-safe routing deployed and tested on both platforms
- ✅ **Design System v1.2**: Botanical theme deployed, enhanced typography and spacing active
- ✅ **Field Data v1.2.1**: Comprehensive multi-field coverage with 5,461+ additional entries
- ✅ **App Branding v1.3.0**: Professional icons and splash screens deployed on both platforms
- ✅ **Enhanced UX v1.3.0**: Improved navigation flow with field selection screen and larger typography
- ✅ **Professional Navigation v1.4.0**: Type-safe NavHost with state preservation and cross-platform gesture support
- ✅ **Kotlin 2.2.0 Upgrade v1.4.1**: Latest Kotlin with K2 compiler performance and enhanced framework versions
- ✅ **Enhanced UX v1.5.0**: Position state persistence, iOS safe area polish, and auto-selection improvements
- ✅ **Search Feature v1.6.0**: Comprehensive peony search with fuzzy matching, autocomplete, and state preservation

### Planned for v1.7.0
- [ ] **Home Screen Architecture**: Tile-based navigation with separated functions
- [ ] **Field Notes System**: Comprehensive note-taking with dictation support
- [ ] **Data Export**: CSV export functionality with platform-specific sharing
- [ ] **Enhanced Peony Details**: Quick action buttons and custom notes field

---

For complete version history and detailed development timeline, see [history.md](./history.md)