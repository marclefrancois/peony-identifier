# Peony Identifier App Plan

## Project Status: 🔄 IN DEVELOPMENT v1.7.5

A Kotlin Multiplatform Compose app for identifying peonies across multiple fields, with enhanced branding, improved navigation flow, and larger typography for better accessibility. Features comprehensive field notes management system with CSV export functionality. Now with cloud-based data loading from Google Drive spreadsheets (16 field/parcel configurations across 5 fields).

## Version 1.7.5 Status: 🔄 IN DEVELOPMENT

### 🎯 Version 1.7.5: Google Drive Data Integration - **PHASES 1-2.5 COMPLETE & TESTED**

#### Overview
Migrate from bundled JSON files to cloud-based data loading via Google Drive spreadsheets, enabling real-time data updates without app redeployment.

#### Current Implementation Status
**✅ FUNCTIONAL & TESTED:** App successfully loads all field data from Google Drive spreadsheets
- **16 field/parcel configurations** across 5 fields (3 Google Drive spreadsheets)
- **All peony entries** fetched from cloud in real-time
- **Parallel fetching** for optimal performance
- **Automatic fallback** to bundled JSON on network failure
- **Configurable header rows** and column mappings per field
- **Standardized naming**: Field names from config, not CSV data
- **✅ VERIFIED:** Tested in production app on both Android and iOS

**Data Flow (Cache Disabled for Testing):**
1. App launch → FieldConfigLoader loads 16 configs from field-config.json
2. GoogleDriveDataSource fetches all 16 spreadsheet tabs in parallel
3. CsvParser parses each with custom headerRowIndex and column mapping
4. Field/parcel names injected from config (e.g., "1-PP", "3-Maison", "4-Itoh", "5-Blanches")
5. All entries merged into single list
6. If fetch fails → falls back to bundled JSON files
7. Data served to FieldRepository → ViewModels → UI (no changes required)

#### Completed: Phases 1, 2, and 2.5 ✅

##### Phase 1: Network Infrastructure & Configuration System ✅
- ✅ Ktor Client 2.3.12 with platform-specific engines (OkHttp/Darwin)
- ✅ FieldConfig model with flexible column mapping per field
- ✅ FieldConfigLoader for configuration management
- ✅ GoogleDriveService with public spreadsheet CSV export support
- ✅ CsvParser with dynamic column mapping and validation
- ✅ NetworkResult sealed class for type-safe error handling
- ✅ HttpClientFactory with expect/actual pattern (30s connect, 60s read/write timeouts)

##### Phase 2: Data Loading Architecture Refactor ✅
- ✅ RemoteDataSource interface with GoogleDriveDataSource implementation
- ✅ Enhanced DataCacheManager with offline-first caching strategy
- ✅ CacheMetadata with 24-hour expiration policy
- ✅ File-based cache storage (field-data-cache.json, field-data-metadata.json)
- ✅ Multi-level fallback: Cache → Remote → Expired Cache → Bundled JSON
- ✅ Zero breaking changes to FieldRepository interface

##### Phase 2.5: Production Configuration & Enhancements ✅
- ✅ **Header Row Support**: Configurable headerRowIndex per field (supports description rows)
- ✅ **Field/Parcel Name Standardization**: Names from config, format "{fieldId}-{parcelId}"
- ✅ **Cache Control Flag**: ENABLE_REMOTE_CACHE (default: false for testing)
- ✅ **16 Field/Parcel Configurations**:
  - **Field 1** (2 parcels): PP, GP
  - **Field 2** (3 parcels): PP, Mil, Par
  - **Field 3** (3 parcels): Maison, Mil, Par
  - **Field 4** (4 parcels): Haut, Bas, Itoh, Herbacées
  - **Field 5** (4 parcels): Hâtives, Blanches, Blush, Variées
- ✅ All spreadsheets publicly accessible and validated
- ✅ Cross-platform compilation successful (Android & iOS)
- ✅ **Production Testing Complete**: App successfully loads and displays all cloud data

#### Completed: Loading & Refresh Implementation ✅
- ✅ Loading splash screen with Material Design (dark green theme)
- ✅ Blocking UI until data loads from Google Drive
- ✅ Manual refresh button in PeonyIdentifierScreen
- ✅ Cache system implemented (disabled for testing, will enable in v2.0)

#### Completed: Phase 3 - Google Authentication Implementation ✅
##### Phase 3: Google Authentication & Authorization
- ✅ Implement Google Sign-In for Android (Google Identity Services)
- ✅ Implement Google Sign-In for iOS (Google Sign-In SDK)
- ✅ Store user authentication state (AuthRepository & AuthViewModel)
- ✅ Add Google Drive OAuth2 scopes for private spreadsheet access
- ✅ Add sign-in/sign-out UI before loading screen
- ✅ Cross-platform authentication validation
- ✅ CocoaPods integration with GoogleSignIn 7.1.0
- ✅ iOS URL scheme configuration for OAuth callbacks
- 🔄 Update GoogleDriveService to use authenticated requests (Phase 4)
- 🔄 Handle token refresh and expiration (Phase 4)
- 🔄 Test with private Google Drive spreadsheets (Phase 4)

##### Phase 4: Future Enhancements (v2.0+)
- [ ] Enable cache system (ENABLE_REMOTE_CACHE = true)
- [ ] Test cache persistence and 24-hour expiration
- [ ] Test with poor network conditions (slow 3G, packet loss)
- [ ] Settings screen for spreadsheet URL management
- [ ] Display last sync timestamp and data version

## Technical Stack (v1.7.5 Additions)

### New Dependencies
- **Ktor Client 2.3.12**: Cross-platform HTTP client
  - `ktor-client-core`, `ktor-client-okhttp` (Android), `ktor-client-darwin` (iOS)
  - `ktor-client-content-negotiation`, `ktor-serialization-kotlinx-json`
  - `ktor-client-logging` for debugging

### Architecture Additions
```
data/
├── config/
│   ├── FieldConfig.kt           # Per-field configuration model
│   └── FieldConfigLoader.kt     # Configuration loader
├── remote/
│   ├── GoogleDriveService.kt    # Interface for remote data
│   ├── GoogleDriveServiceImpl.kt # Ktor implementation
│   ├── GoogleDriveDataSource.kt # Data source orchestration
│   ├── RemoteDataSource.kt      # Data source interface
│   ├── CsvParser.kt             # CSV parser with column mapping
│   ├── NetworkResult.kt         # Result sealed class
│   └── HttpClientFactory.kt     # Platform-specific HTTP client
└── cache/
    ├── DataCacheManager.kt      # Enhanced with remote support
    ├── CacheMetadata.kt         # Cache expiration logic
    └── CachedData.kt            # Data + metadata wrapper
```

## Future Enhancements (Version 1.8+)

### Planned Features
- [ ] **Speech-to-Text**: Platform-specific voice dictation for field notes (from v1.7.0 Phase 5)
- [ ] **Cloud Note Sync**: Synchronize field notes across devices
- [ ] **Photo Integration**: Add photos of actual plants in the field
- [ ] **GPS Integration**: Location-based field navigation
- [ ] **Advanced Search Filters**: Filter by field, year, size, or other attributes
- [ ] **Testing Expansion**: Unit tests for CSV parsing, fuzzy matching, integration tests
- [ ] **Accessibility**: Content descriptions and improved focus handling
- [ ] **Performance**: Advanced caching strategies for very large datasets

## Deployment Status

### Version History
- ✅ **v1.0-1.6.0**: See [history.md](./history.md) for complete details
- ✅ **v1.7.0**: Enhanced field management & note-taking system
- 🔄 **v1.7.5**: Google Drive data integration (Phases 1-2.5 complete)

### Current Build Status (v1.7.5)
- ✅ **Android**: Compilation successful, all features working
- ✅ **iOS**: Compilation successful, all features working
- ✅ **Google Drive Integration**: 16 field/parcel configurations across 5 fields loading from cloud
- ✅ **Network Stack**: Ktor HTTP client working on both platforms
- ✅ **Fallback Strategy**: Graceful degradation to bundled JSON verified
- ✅ **Production Testing**: App tested with real Google Drive data loading - working perfectly
- ✅ **Loading Splash Screen**: Material Design loading screen with dark green theme
- ✅ **Refresh Functionality**: Manual refresh button in position selection screen
- 🔄 **Cache System**: Disabled for testing (ENABLE_REMOTE_CACHE = false, will enable in v2.0)
- 🔄 **Google Authentication**: Not yet implemented (Phase 3)

### Next Steps
1. ~~Test app with real Google Drive data loading~~ ✅ **COMPLETE**
2. ~~Implement loading splash screen and refresh functionality~~ ✅ **COMPLETE**
3. Implement Phase 3: Google Authentication & Authorization for private spreadsheet access
4. Complete v1.7.5 and prepare for v2.0 (cache system and production deployment)

---

For complete version history and detailed development timeline, see [history.md](./history.md)
