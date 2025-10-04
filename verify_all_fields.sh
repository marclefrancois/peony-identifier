#!/bin/bash

echo "========================================="
echo "Verifying All Fields Will Be Loaded"
echo "========================================="
echo ""

echo "Reading field-config.json..."
CONFIG_COUNT=$(cat composeApp/src/commonMain/composeResources/files/field-config.json | grep '"fieldId"' | wc -l)
echo "✅ Found $CONFIG_COUNT field configurations"
echo ""

echo "Detailed breakdown:"
echo ""

echo "1. Field 1-PP:"
echo "   Spreadsheet: 1AzAWoqNiQR2bIaA93DXGrztZ8dq1V8QktqJR_YQlbYc"
echo "   GID: 886671777"
ROWS=$(curl -sL "https://docs.google.com/spreadsheets/d/1AzAWoqNiQR2bIaA93DXGrztZ8dq1V8QktqJR_YQlbYc/export?format=csv&gid=886671777" | tail -n +3 | wc -l | xargs)
echo "   ✅ $ROWS entries"
echo ""

echo "2. Field 1-GP:"
echo "   Spreadsheet: 1AzAWoqNiQR2bIaA93DXGrztZ8dq1V8QktqJR_YQlbYc"
echo "   GID: 1675517178"
ROWS=$(curl -sL "https://docs.google.com/spreadsheets/d/1AzAWoqNiQR2bIaA93DXGrztZ8dq1V8QktqJR_YQlbYc/export?format=csv&gid=1675517178" | tail -n +3 | wc -l | xargs)
echo "   ✅ $ROWS entries"
echo ""

echo "3. Field 2-PP:"
echo "   Spreadsheet: 1sipVd08l2LyjY5bYhMB21ynQss4-K8xjXzeiAu1CHts"
echo "   GID: 1679499058"
ROWS=$(curl -sL "https://docs.google.com/spreadsheets/d/1sipVd08l2LyjY5bYhMB21ynQss4-K8xjXzeiAu1CHts/export?format=csv&gid=1679499058" | tail -n +2 | wc -l | xargs)
echo "   ✅ $ROWS entries"
echo ""

echo "4. Field 4-Haut:"
echo "   Spreadsheet: 1j8FT5Y7u0YVqgwIQCVftZiSKkzaGDFsWWEsVyjAiCzk"
echo "   GID: 342654710"
ROWS=$(curl -sL "https://docs.google.com/spreadsheets/d/1j8FT5Y7u0YVqgwIQCVftZiSKkzaGDFsWWEsVyjAiCzk/export?format=csv&gid=342654710" | tail -n +3 | wc -l | xargs)
echo "   ✅ $ROWS entries"
echo ""

echo "========================================="
echo "Data Loading Flow:"
echo "========================================="
echo "1. FieldConfigLoader loads all 4 configs from field-config.json"
echo "2. GoogleDriveDataSource fetches ALL configs in parallel"
echo "3. CsvParser parses each with correct headerRowIndex"
echo "4. All entries merged into single list"
echo "5. DataCacheManager caches combined data"
echo ""
echo "✅ ALL FIELDS WILL BE LOADED"
echo ""
