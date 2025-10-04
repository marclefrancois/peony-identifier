# Peony Identifier App Plan

## Project Status: 🔄 IN DEVELOPMENT v1.7.5

A Kotlin Multiplatform Compose app for identifying peonies across multiple fields, with enhanced branding, improved navigation flow, and larger typography for better accessibility. Features comprehensive field notes management system with CSV export functionality. Now with cloud-based data loading from Google Drive spreadsheets.

## Version 1.7.5 Status: 🔄 IN DEVELOPMENT

### 🎯 Version 1.7.5: Google Drive Data Integration - **PHASES 1-2.5 COMPLETE**

#### Overview
Migrate from bundled JSON files to cloud-based data loading via Google Drive spreadsheets, enabling real-time data updates without app redeployment.

#### Current Implementation Status
**✅ FUNCTIONAL:** App now loads all field data from Google Drive spreadsheets
- **8 field/parcel configurations** across 3 spreadsheets
- **10,300+ peony entries** fetched from cloud
- **Parallel fetching** for optimal performance
- **Automatic fallback** to bundled JSON on network failure
- **Configurable header rows** and column mappings per field
- **Standardized naming**: Field names from config, not CSV data

**Data Flow (Cache Disabled for Testing):**
1. App launch → FieldConfigLoader loads 8 configs from field-config.json
2. GoogleDriveDataSource fetches all 8 spreadsheet tabs in parallel
3. CsvParser parses each with custom headerRowIndex and column mapping
4. Field/parcel names injected from config (e.g., "1-PP", "3-Maison", "4-Haut")
5. All entries merged into single list (10,300+ entries)
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
- ✅ **8 Fields Configured**:
  - Field 1-PP: 908 entries | Field 1-GP: 4,467 entries
  - Field 2-PP: 995 entries | Field 2-Mil: TBD | Field 2-Par: TBD
  - Field 3-Maison: 1,330 entries | Field 3-Mil: 901 entries | Field 3-Par: 1,330 entries
  - Field 4-Haut: 369 entries
- ✅ **Total: 10,300+ peony entries** from Google Drive
- ✅ All spreadsheets publicly accessible and validated
- ✅ Cross-platform compilation successful (Android & iOS)

#### Pending: Phases 3, 4, and 5

##### Phase 3: Data Synchronization & Loading States (Optional)
- [ ] SyncManager for orchestrating data fetching
- [ ] Background sync on app launch with progress indicators
- [ ] Manual refresh capability in settings/home screen
- [ ] Loading states UI with sync status display
- [ ] Error handling UI for network failures

##### Phase 4: Configuration & Settings (Optional)
- [ ] Settings screen for spreadsheet URL management
- [ ] "Refresh Data Now" button with progress indicator
- [ ] "Clear Cache" option for troubleshooting
- [ ] Display last sync timestamp and data version
- [ ] Toggle for offline mode

##### Phase 5: Migration & Testing (Optional)
- [ ] Test with poor network conditions (slow 3G, packet loss)
- [ ] Test offline mode with expired cache
- [ ] Test data integrity after sync
- [ ] Cross-platform networking validation

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
- ✅ **Google Drive Integration**: 8 fields configured, 10,300+ entries loading from cloud
- ✅ **Network Stack**: Ktor HTTP client working on both platforms
- ✅ **Fallback Strategy**: Graceful degradation to bundled JSON verified
- 🔄 **Cache System**: Disabled for testing (ENABLE_REMOTE_CACHE = false)

### Next Steps
1. Test app with real Google Drive data loading
2. Enable cache system (set ENABLE_REMOTE_CACHE = true)
3. Implement Phase 3 (optional): Sync UI and loading states
4. Complete v1.7.5 and prepare for production deployment

---

For complete version history and detailed development timeline, see [history.md](./history.md)
