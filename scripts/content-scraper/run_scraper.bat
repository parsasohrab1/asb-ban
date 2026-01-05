@echo off
REM اسکریپت اجرای جمع‌آوری محتوا برای Windows

echo 🚀 شروع جمع‌آوری محتوا...
echo.

REM بررسی Python
python --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Python یافت نشد!
    pause
    exit /b 1
)

REM بررسی وابستگی‌ها
echo 📦 بررسی وابستگی‌ها...
pip install -r requirements.txt --quiet

REM اجرای اسکرپر
echo.
echo 🔍 شروع اسکرپ...
python content_scraper.py

REM اعتبارسنجی محتوا
if exist "scraped_content\data\scraped_content.json" (
    echo.
    echo ✅ اعتبارسنجی محتوا...
    python validate_content.py
)

echo.
echo ✅ تمام!
pause

