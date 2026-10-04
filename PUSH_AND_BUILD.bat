@echo off
setlocal
title HOPE AI V2.0 - Push and Build

where git >nul 2>nul
if errorlevel 1 (
  echo Git is not installed. Install Git for Windows first.
  pause
  exit /b 1
)

if not exist settings.gradle.kts (
  echo Run this file from the extracted HOPE-AI-Android project folder.
  pause
  exit /b 1
)

if not exist app\google-services.json (
  echo Firebase config is missing at app\google-services.json.
  pause
  exit /b 1
)

set NEW_REPO=0
if not exist .git (
  set NEW_REPO=1
  git init
  git branch -M main
  git remote add origin https://github.com/yashpawar9274/HOPE-AI-Android.git
)

git add .
git commit -m "Build HOPE AI V2 Android app"

if "%NEW_REPO%"=="1" (
  git fetch origin main
  git merge origin/main --allow-unrelated-histories -s ours -m "Merge existing repository history"
) else (
  git pull origin main --rebase
  if errorlevel 1 (
    echo Pull failed. Resolve any Git conflict before pushing.
    pause
    exit /b 1
  )
)

git push -u origin main
if errorlevel 1 (
  echo Push failed. Sign in to GitHub when Git asks for authentication.
  pause
  exit /b 1
)

echo.
echo Source pushed successfully.
echo Open GitHub Actions and download HOPE-AI-V2-debug-apk after the build finishes.
pause
