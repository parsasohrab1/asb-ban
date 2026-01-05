#!/bin/bash
# اسکریپت اجرای جمع‌آوری محتوا

echo "🚀 شروع جمع‌آوری محتوا..."
echo ""

# بررسی Python
if ! command -v python3 &> /dev/null; then
    echo "❌ Python 3 یافت نشد!"
    exit 1
fi

# بررسی وابستگی‌ها
echo "📦 بررسی وابستگی‌ها..."
pip install -r requirements.txt --quiet

# اجرای اسکرپر
echo ""
echo "🔍 شروع اسکرپ..."
python3 content_scraper.py

# اعتبارسنجی محتوا
if [ -f "scraped_content/data/scraped_content.json" ]; then
    echo ""
    echo "✅ اعتبارسنجی محتوا..."
    python3 validate_content.py
fi

echo ""
echo "✅ تمام!"

