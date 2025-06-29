#!/bin/bash

# Peony Identifier - Version Increment Script
# Increments build numbers for both Android and iOS platforms

set -e

# File paths
ANDROID_BUILD_FILE="composeApp/build.gradle.kts"
IOS_PLIST_FILE="iosApp/iosApp/Info.plist"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔧 Peony Identifier Version Increment${NC}"
echo "=================================="

# Check if files exist
if [ ! -f "$ANDROID_BUILD_FILE" ]; then
    echo -e "${RED}❌ Error: Android build file not found: $ANDROID_BUILD_FILE${NC}"
    exit 1
fi

if [ ! -f "$IOS_PLIST_FILE" ]; then
    echo -e "${RED}❌ Error: iOS Info.plist not found: $IOS_PLIST_FILE${NC}"
    exit 1
fi

# Extract current Android version code
CURRENT_ANDROID_VERSION=$(grep -o 'versionCode = [0-9]*' "$ANDROID_BUILD_FILE" | grep -o '[0-9]*')
if [ -z "$CURRENT_ANDROID_VERSION" ]; then
    echo -e "${RED}❌ Error: Could not find versionCode in $ANDROID_BUILD_FILE${NC}"
    exit 1
fi

# Extract current iOS bundle version
CURRENT_IOS_VERSION=$(grep -A 1 'CFBundleVersion' "$IOS_PLIST_FILE" | grep -o '[0-9]*' | head -1)
if [ -z "$CURRENT_IOS_VERSION" ]; then
    echo -e "${RED}❌ Error: Could not find CFBundleVersion in $IOS_PLIST_FILE${NC}"
    exit 1
fi

# Verify versions match
if [ "$CURRENT_ANDROID_VERSION" != "$CURRENT_IOS_VERSION" ]; then
    echo -e "${RED}❌ Warning: Version mismatch - Android: $CURRENT_ANDROID_VERSION, iOS: $CURRENT_IOS_VERSION${NC}"
    echo "Using Android version as reference..."
fi

# Calculate new version
NEW_VERSION=$((CURRENT_ANDROID_VERSION + 1))

echo -e "${BLUE}📱 Current Versions:${NC}"
echo "   Android versionCode: $CURRENT_ANDROID_VERSION"
echo "   iOS CFBundleVersion: $CURRENT_IOS_VERSION"
echo ""
echo -e "${BLUE}🔄 Incrementing to: $NEW_VERSION${NC}"
echo ""

# Update Android build.gradle.kts
echo "📝 Updating Android version..."
sed -i.bak "s/versionCode = $CURRENT_ANDROID_VERSION/versionCode = $NEW_VERSION/" "$ANDROID_BUILD_FILE"

# Update iOS Info.plist
echo "📝 Updating iOS version..."
sed -i.bak "s/<string>$CURRENT_IOS_VERSION<\/string>/<string>$NEW_VERSION<\/string>/" "$IOS_PLIST_FILE"

# Verify updates
NEW_ANDROID_VERSION=$(grep -o 'versionCode = [0-9]*' "$ANDROID_BUILD_FILE" | grep -o '[0-9]*')
NEW_IOS_VERSION=$(grep -A 1 'CFBundleVersion' "$IOS_PLIST_FILE" | grep -o '[0-9]*' | head -1)

if [ "$NEW_ANDROID_VERSION" = "$NEW_VERSION" ] && [ "$NEW_IOS_VERSION" = "$NEW_VERSION" ]; then
    echo -e "${GREEN}✅ Successfully updated versions to $NEW_VERSION${NC}"
    echo ""
    echo -e "${BLUE}📋 Updated Files:${NC}"
    echo "   $ANDROID_BUILD_FILE"
    echo "   $IOS_PLIST_FILE"
    echo ""
    echo -e "${BLUE}🗑️  Backup files created:${NC}"
    echo "   $ANDROID_BUILD_FILE.bak"
    echo "   $IOS_PLIST_FILE.bak"
    echo ""
    echo -e "${GREEN}🎉 Version increment complete!${NC}"
else
    echo -e "${RED}❌ Error: Version update failed${NC}"
    echo "Expected: $NEW_VERSION"
    echo "Android got: $NEW_ANDROID_VERSION"
    echo "iOS got: $NEW_IOS_VERSION"
    exit 1
fi