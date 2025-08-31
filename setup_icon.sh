#!/bin/bash

# Saitama App Icon Helper Script
# This script helps you set up the app icons properly

echo "🥊 Saitama App Icon Setup Helper 🥊"
echo "=================================="
echo ""
echo "This script will guide you through setting up the Saitama app icon."
echo ""

# Check if the saitama_original.png exists
if [ ! -f "app/src/main/res/drawable/saitama_original.png" ]; then
    echo "❌ Error: saitama_original.png not found in drawable folder!"
    echo "Please ensure the Saitama image is in the correct location."
    exit 1
fi

echo "✅ Saitama image found!"
echo ""
echo "To complete the icon setup, you have 3 options:"
echo ""
echo "1. Use Android Studio's Image Asset tool (Recommended)"
echo "   - Right-click 'app' folder → New → Image Asset"
echo "   - Set foreground to saitama_original.png"
echo "   - Set background color to #FFD700"
echo ""
echo "2. Use an online tool like icon.kitchen"
echo "   - Visit https://icon.kitchen/"
echo "   - Upload the Saitama image"
echo "   - Set background to #FFD700"
echo "   - Download and extract to mipmap folders"
echo ""
echo "3. Manual creation with image editor"
echo "   - Create icons at these sizes:"
echo "     • mipmap-hdpi: 72x72px"
echo "     • mipmap-mdpi: 48x48px"
echo "     • mipmap-xhdpi: 96x96px"
echo "     • mipmap-xxhdpi: 144x144px"
echo "     • mipmap-xxxhdpi: 192x192px"
echo ""
echo "After creating the icons, rebuild your project!"
echo ""
echo "Need help? Check ICON_SETUP_GUIDE.md for detailed instructions."