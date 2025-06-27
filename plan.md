# Peony Identifier App Plan

## Overview
A Kotlin Multiplatform Compose app for identifying peonies across 4 fields, with hierarchical selection and detailed peony information display.

## Single Screen Layout
- 4 cascading spinners: Field → Parcel → Row → Position
- Peony details display below spinners
- Image loaded from URL with name, description, breeder, year, bloom time

## Data Structure

### Field JSONs (4 files)
- `field1.json`, `field2.json`, `field3.json`, `field4.json`
- Contains hierarchy: parcels → rows → positions → peony names
- Example structure:
```json
{
  "field": "Field 1",
  "parcels": [
    {
      "name": "Parcel A",
      "rows": [
        {
          "number": 1,
          "positions": [
            {"position": 1, "peony_name": "Sarah Bernhardt"},
            {"position": 2, "peony_name": "Festiva Maxima"}
          ]
        }
      ]
    }
  ]
}
```

### Peony Database
- `peony_database.json`
- Key-value pairs: peony name → full details + image URL
- Example structure:
```json
{
  "Sarah Bernhardt": {
    "name": "Sarah Bernhardt",
    "picture_url": "https://example.com/sarah-bernhardt.jpg",
    "description": "Double pink peony with large, fragrant blooms",
    "breeder": "Lemoine",
    "year": 1906,
    "bloom_time": "Late spring"
  }
}
```

## User Flow
1. Select field → populates parcel spinner
2. Select parcel → populates row spinner  
3. Select row → populates position spinner
4. Select position → shows peony name + loads details from database
5. Display peony info with image from URL

## Technical Implementation
- Compose dropdowns for cascading selection
- JSON assets loading from composeResources
- URL image loading with async/caching
- Reactive UI updates based on selections
- State management for spinner selections and peony details

## Components to Build
1. **Data Models**: Field, Parcel, Row, Position, Peony classes
2. **JSON Parsing**: Utilities to load and parse field and database JSON files
3. **UI Components**: Cascading spinners, peony detail card, image loading
4. **State Management**: ViewModel or state handling for selections and data
5. **Image Loading**: Async image loading from URLs with caching