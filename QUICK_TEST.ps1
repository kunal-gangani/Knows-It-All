#!/usr/bin/env powershell
# KnowItAll: Quick Build & Test Commands

# ===== BUILD COMMANDS =====

# Clean build
Write-Host "🧹 Clean build..." -ForegroundColor Cyan
./gradlew clean

# Build debug APK
Write-Host "🔨 Building debug APK..." -ForegroundColor Cyan
./gradlew assembleDebug

# Install to emulator
Write-Host "📱 Installing to emulator..." -ForegroundColor Cyan
./gradlew installDebug

# ===== VERIFICATION COMMANDS =====

# Check if app is installed
Write-Host "✓ Checking if app is installed..." -ForegroundColor Green
adb shell pm list packages | Select-String \"know_it_all\"

# Launch app
Write-Host \"🚀 Launching KnowItAll app...\" -ForegroundColor Green
adb shell am start -n com.example.know_it_all/.MainActivity

# Monitor logs
Write-Host \"📋 Monitoring app logs (press Ctrl+C to stop)...\" -ForegroundColor Green
adb logcat | Select-String \"KnowItAll|SessionManager|Retrofit\"

# Clear app data
Write-Host \"🧹 Clearing app data...\" -ForegroundColor Yellow
adb shell pm clear com.example.know_it_all

# ===== BACKEND TESTING =====

# Test if backend is running
Write-Host \"🔍 Testing backend connectivity...\" -ForegroundColor Cyan
Invoke-WebRequest -Uri \"http://localhost:8080/api/v1/health\" -ErrorAction SilentlyContinue | Select-Object StatusCode

# ===== QUICK RUN =====

Write-Host \"\" 
Write-Host \"🎯 QUICK START:\" -ForegroundColor Yellow
Write-Host \"1. Make sure Android Emulator is running\"
Write-Host \"2. Make sure Spring Boot backend is running on port 8080\"
Write-Host \"3. Run: ./gradlew installDebug && adb shell am start -n com.example.know_it_all/.MainActivity\"
Write-Host \"4. Test registration with: test@knowitall.com / TestPass123\"
Write-Host \"\"
