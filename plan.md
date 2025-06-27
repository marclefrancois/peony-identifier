# Peony Identifier App Plan

## Overview
A Kotlin Multiplatform Compose app for identifying peonies across 4 fields, with hierarchical selection and detailed peony information display.

## Single Screen Layout
- **Top Section**: Large peony details display (name, image, description, breeder, year, bloom time)
- **Bottom Section**: Compact selection area with 4 cascading spinners (Field → Parcel → Row → Position)
  - Thumb-friendly layout optimized for one-handed use
  - Minimal vertical space to maximize peony detail area

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

## Technical Implementation
- Compose dropdowns for cascading selection in bottom compact area
- JSON assets loading from `data/` folder (move to composeResources)
- URL image loading with async/caching for main display
- Fuzzy string matching between field `variete` and database `cultivar`
- Reactive UI updates based on selections
- State management for spinner selections and peony details
- Responsive layout with bottom selection area and expanded detail view

## Components to Build
1. **Data Models**: FieldEntry, PeonyInfo classes matching JSON structure
2. **JSON Parsing**: Utilities to load field arrays and peony database array
3. **UI Components**: 
   - Compact bottom selection area with cascading spinners (champ/parcelle/rang/trou)
   - Large peony detail display with cultivar, image, description, originator, date, group
   - Responsive layout manager
4. **Fuzzy Matching**: String similarity algorithm matching `variete` to `cultivar`
5. **State Management**: ViewModel or state handling for selections and peony details
6. **Image Loading**: Async image loading from URLs with caching