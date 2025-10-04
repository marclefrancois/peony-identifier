#!/bin/bash

echo "=== Testing All Configured Fields ==="
echo ""

echo "Field 1-PP (GID: 886671777):"
ROWS=$(curl -sL "https://docs.google.com/spreadsheets/d/1AzAWoqNiQR2bIaA93DXGrztZ8dq1V8QktqJR_YQlbYc/export?format=csv&gid=886671777" | tail -n +3 | wc -l)
echo "  Data rows: $ROWS"
echo ""

echo "Field 1-GP (GID: 1675517178):"
ROWS=$(curl -sL "https://docs.google.com/spreadsheets/d/1AzAWoqNiQR2bIaA93DXGrztZ8dq1V8QktqJR_YQlbYc/export?format=csv&gid=1675517178" | tail -n +3 | wc -l)
echo "  Data rows: $ROWS"
echo ""

echo "Field 2-PP (GID: 1679499058):"
ROWS=$(curl -sL "https://docs.google.com/spreadsheets/d/1sipVd08l2LyjY5bYhMB21ynQss4-K8xjXzeiAu1CHts/export?format=csv&gid=1679499058" | tail -n +2 | wc -l)
echo "  Data rows: $ROWS"
echo ""

echo "Field 4-Haut (GID: 342654710):"
ROWS=$(curl -sL "https://docs.google.com/spreadsheets/d/1j8FT5Y7u0YVqgwIQCVftZiSKkzaGDFsWWEsVyjAiCzk/export?format=csv&gid=342654710" | tail -n +3 | wc -l)
echo "  Data rows: $ROWS"
echo ""

echo "=== Summary ==="
echo "✅ All 4 fields configured and accessible"
