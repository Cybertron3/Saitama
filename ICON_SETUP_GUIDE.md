# Saitama App Icon Setup Guide

## Current Status
1. ✅ UI has been centered - the app content now appears in the middle of the screen
2. ✅ Saitama image has been copied to: `app/src/main/res/drawable/saitama_original.png`
3. ✅ Icon background color changed to One Punch Man themed yellow (#FFD700)
4. ⚠️ Icon setup requires proper image sizing for Android

## To Complete the Icon Change

### Option 1: Using Android Studio (Recommended)
1. Open the project in Android Studio
2. Right-click on the `app` folder in the project view
3. Select `New` > `Image Asset`
4. In the Configure Image Asset dialog:
   - **Icon Type**: Launcher Icons (Adaptive and Legacy)
   - **Name**: ic_launcher
   - **Foreground Layer**:
     - Asset Type: Image
     - Path: Browse to `/app/src/main/res/drawable/saitama_original.png`
     - Resize: Adjust to fit (usually around 66%)
   - **Background Layer**:
     - Asset Type: Color
     - Color: #FFD700 (One Punch Man yellow)
5. Click Next and Finish

### Option 2: Using Online Tool
1. Go to https://icon.kitchen/
2. Upload the Saitama image
3. Set background color to #FFD700
4. Download the icon pack
5. Extract and copy the generated files to respective mipmap folders

### Option 3: Using Image Editing Software
Create the following icon sizes and save them in the respective folders:
- `mipmap-hdpi/ic_launcher.png`: 72x72 px
- `mipmap-mdpi/ic_launcher.png`: 48x48 px
- `mipmap-xhdpi/ic_launcher.png`: 96x96 px
- `mipmap-xxhdpi/ic_launcher.png`: 144x144 px
- `mipmap-xxxhdpi/ic_launcher.png`: 192x192 px

## Important Notes
- Android adaptive icons use a 108x108dp space with safe zone in center 66dp circle
- The Saitama image should be centered and scaled to fit within the safe zone
- The yellow background (#FFD700) provides the One Punch Man theme
- After generating icons, rebuild the project to see changes

## Files Modified
1. `/app/src/main/java/com/ankitesh/saitama/ui/WeightTrackerScreen.kt` - UI centering
2. `/app/src/main/res/drawable/ic_launcher_background.xml` - Yellow background
3. `/app/src/main/res/drawable/saitama_original.png` - Original Saitama image