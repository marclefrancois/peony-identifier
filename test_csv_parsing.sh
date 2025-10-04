#!/bin/bash

echo "Testing CSV parsing with header row index..."
echo ""

# Get the CSV
CSV=$(curl -sL "https://docs.google.com/spreadsheets/d/1j8FT5Y7u0YVqgwIQCVftZiSKkzaGDFsWWEsVyjAiCzk/export?format=csv&gid=342654710")

echo "Row 0 (should be skipped):"
echo "$CSV" | sed -n '1p'
echo ""

echo "Row 1 (headers - will be used):"
echo "$CSV" | sed -n '2p'
echo ""

echo "Row 2 (first data row):"
echo "$CSV" | sed -n '3p'
echo ""

echo "First 5 data rows (after skipping row 0 and using row 1 as headers):"
echo "$CSV" | tail -n +3 | head -5
echo ""

echo "Total data rows: $(echo "$CSV" | tail -n +3 | wc -l)"
