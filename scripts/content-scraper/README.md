# اسکریپت جمع‌آوری محتوای مرتبط با اسب

این اسکریپت محتوا، تصاویر و متن‌های SEO شده را از سایت‌های فارسی مرتبط با اسب جمع‌آوری می‌کند.

## 📋 ویژگی‌ها

- ✅ جمع‌آوری محتوا از سایت‌های فارسی
- ✅ دانلود و بهینه‌سازی تصاویر
- ✅ استخراج Meta Tags برای SEO
- ✅ ایجاد Slug از عنوان
- ✅ ذخیره در فرمت JSON و SQL
- ✅ رعایت robots.txt
- ✅ تاخیر بین درخواست‌ها برای رعایت اخلاقی
- ✅ پشتیبانی از Selenium برای سایت‌های JavaScript-heavy

## 🚀 نصب

### پیش‌نیازها

```bash
# Python 3.8 یا بالاتر
python --version

# نصب وابستگی‌ها
pip install -r requirements.txt
```

### برای استفاده از Selenium (اختیاری)

```bash
# نصب ChromeDriver
# Windows: دانلود از https://chromedriver.chromium.org/
# Linux: sudo apt-get install chromium-chromedriver
# Mac: brew install chromedriver
```

## 📖 استفاده

### استفاده پایه

```bash
cd scripts/content-scraper
python content_scraper.py
```

### استفاده پیشرفته با Selenium

```bash
python advanced_scraper.py
```

### تنظیمات

فایل `sites_config.json` را ویرایش کنید تا سایت‌های مورد نظر را اضافه کنید:

```json
{
  "sites": [
    {
      "name": "نام سایت",
      "base_url": "https://example.com",
      "search_paths": ["/articles", "/blog"],
      "keywords": ["اسب", "سوارکاری"],
      "use_selenium": false
    }
  ]
}
```

## 📁 ساختار خروجی

```
scraped_content/
├── data/
│   ├── scraped_content.json    # داده‌ها در فرمت JSON
│   └── scraped_content.sql      # داده‌ها برای import به دیتابیس
└── images/
    ├── image1.jpg
    ├── image2.png
    └── ...
```

## 📊 فرمت داده خروجی

### JSON Format

```json
{
  "id": "abc123",
  "url": "https://example.com/article",
  "slug": "article-title",
  "title": "عنوان مقاله",
  "meta_description": "توضیحات SEO",
  "meta_keywords": "اسب, سوارکاری",
  "content": "متن کامل مقاله...",
  "excerpt": "خلاصه مقاله...",
  "headings": [
    {"level": 1, "text": "عنوان اصلی"},
    {"level": 2, "text": "زیرعنوان"}
  ],
  "images": [
    {
      "path": "images/image1.jpg",
      "alt": "توضیحات تصویر",
      "title": "عنوان تصویر"
    }
  ],
  "scraped_at": "2024-01-15T10:30:00",
  "source": "example.com"
}
```

### SQL Format

```sql
INSERT INTO blog_posts (
    title, slug, excerpt, content, featured_image,
    meta_description, meta_keywords, author_id, category_id,
    is_published, published_at, created_at
) VALUES (
    'عنوان مقاله',
    'article-slug',
    'خلاصه...',
    'متن کامل...',
    'images/image1.jpg',
    'توضیحات SEO',
    'کلمات کلیدی',
    1,
    1,
    true,
    NOW(),
    NOW()
);
```

## ⚙️ تنظیمات پیشرفته

### تغییر تاخیر بین درخواست‌ها

در `content_scraper.py`:

```python
time.sleep(3)  # تغییر به مقدار دلخواه (ثانیه)
```

### تغییر کیفیت تصاویر

در `content_scraper.py`:

```python
img.save(image_path, optimize=True, quality=85)  # تغییر quality
```

### محدود کردن تعداد مقالات

در `sites_config.json`:

```json
{
  "settings": {
    "max_articles_per_site": 50  # تغییر به تعداد دلخواه
  }
}
```

## ⚠️ نکات مهم

1. **رعایت قوانین**: همیشه robots.txt را بررسی کنید
2. **تاخیر**: بین درخواست‌ها تاخیر بگذارید تا سرور overload نشود
3. **محدودیت**: تعداد درخواست‌ها را محدود کنید
4. **قانونی**: فقط از سایت‌هایی که اجازه می‌دهند محتوا جمع‌آوری کنید
5. **حقوق نشر**: محتواهای جمع‌آوری شده را با رعایت حقوق نشر استفاده کنید

## 🔧 عیب‌یابی

### خطای Connection

```bash
# بررسی اتصال اینترنت
ping google.com

# بررسی فایروال
```

### خطای ChromeDriver

```bash
# نصب ChromeDriver
# یا استفاده از content_scraper.py بدون Selenium
```

### خطای Encoding

```python
# در content_scraper.py
response.encoding = 'utf-8'  # یا 'windows-1256' برای برخی سایت‌ها
```

## 📝 مثال استفاده در کد

```python
from content_scraper import ContentScraper

# ایجاد اسکرپر
scraper = ContentScraper(output_dir="my_content")

# اسکرپ یک صفحه خاص
data = scraper.scrape_page("https://example.com/article")

# ذخیره نتایج
scraper.save_to_json("my_data.json")
scraper.save_to_sql("my_data.sql")
```

## 🎯 استفاده در پروژه

پس از جمع‌آوری محتوا، می‌توانید آن‌ها را به دیتابیس import کنید:

```bash
# Import به PostgreSQL
psql -U postgres -d asb_ban -f scraped_content/data/scraped_content.sql
```

یا از API استفاده کنید:

```python
# در backend/src/database/seed.ts
# می‌توانید فایل JSON را بخوانید و به دیتابیس اضافه کنید
```

---

**تاریخ ایجاد**: ۱۴۰۳/۱۲/۱۵  
**نسخه**: 1.0

