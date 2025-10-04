#!/bin/bash

echo "Testing Google Drive CSV fetch for Field 4..."
echo ""
echo "Spreadsheet ID: 1j8FT5Y7u0YVqgwIQCVftZiSKkzaGDFsWWEsVyjAiCzk"
echo "Sheet GID: 342654710"
echo ""

URL="https://docs.google.com/spreadsheets/d/1j8FT5Y7u0YVqgwIQCVftZiSKkzaGDFsWWEsVyjAiCzk/export?format=csv&gid=342654710"

echo "Fetching from: $URL"
echo ""
echo "First 10 lines of CSV:"
echo "----------------------------------------"

curl -s "$URL" | head -10

echo ""
echo "----------------------------------------"
echo ""
echo "Row count (excluding header):"
curl -s "$URL" | tail -n +2 | wc -l

echo ""
echo "Test complete!"
