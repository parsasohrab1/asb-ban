#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
مثال استفاده از Content Scraper
"""

from content_scraper import ContentScraper
import json

def example_basic_usage():
    """مثال استفاده پایه"""
    print("=" * 60)
    print("مثال 1: استفاده پایه")
    print("=" * 60)
    
    # ایجاد اسکرپر
    scraper = ContentScraper(output_dir="example_output")
    
    # اسکرپ یک صفحه خاص
    url = "https://example.com/article-about-horses"
    data = scraper.scrape_page(url)
    
    if data:
        print(f"\n✓ محتوا جمع‌آوری شد:")
        print(f"  عنوان: {data['title']}")
        print(f"  تعداد پاراگراف: {len(data.get('headings', []))}")
        print(f"  تعداد تصاویر: {len(data.get('images', []))}")
    else:
        print("\n❌ خطا در جمع‌آوری محتوا")


def example_custom_site():
    """مثال اسکرپ سایت سفارشی"""
    print("\n" + "=" * 60)
    print("مثال 2: اسکرپ سایت سفارشی")
    print("=" * 60)
    
    scraper = ContentScraper(output_dir="custom_output")
    
    # تعریف سایت سفارشی
    custom_site = {
        'name': 'سایت سفارشی',
        'base_url': 'https://example.com',
        'search_paths': ['/articles', '/blog'],
        'keywords': ['اسب', 'سوارکاری']
    }
    
    # اسکرپ سایت
    scraper.scrape_site(custom_site)
    
    # ذخیره نتایج
    if scraper.scraped_content:
        scraper.save_to_json("custom_content.json")
        print(f"\n✓ {len(scraper.scraped_content)} محتوا جمع‌آوری شد")


def example_filter_content():
    """مثال فیلتر کردن محتوا"""
    print("\n" + "=" * 60)
    print("مثال 3: فیلتر کردن محتوا")
    print("=" * 60)
    
    # خواندن محتوای جمع‌آوری شده
    with open('scraped_content/data/scraped_content.json', 'r', encoding='utf-8') as f:
        contents = json.load(f)
    
    # فیلتر بر اساس کلمات کلیدی
    keywords = ['نژاد', 'تربیت', 'بیماری']
    filtered = [
        c for c in contents
        if any(kw in c.get('title', '').lower() or kw in c.get('content', '').lower() 
               for kw in keywords)
    ]
    
    print(f"\n✓ تعداد محتواهای فیلتر شده: {len(filtered)}")
    for item in filtered[:5]:
        print(f"  - {item['title'][:50]}...")


def example_import_preparation():
    """مثال آماده‌سازی برای import"""
    print("\n" + "=" * 60)
    print("مثال 4: آماده‌سازی برای import")
    print("=" * 60)
    
    from validate_content import ContentValidator
    
    # خواندن محتوا
    with open('scraped_content/data/scraped_content.json', 'r', encoding='utf-8') as f:
        contents = json.load(f)
    
    # اعتبارسنجی
    validator = ContentValidator()
    valid_contents, invalid_contents = validator.validate_batch(contents)
    
    print(f"\n✓ محتواهای معتبر: {len(valid_contents)}")
    print(f"❌ محتواهای نامعتبر: {len(invalid_contents)}")
    
    # ذخیره محتواهای معتبر
    if valid_contents:
        with open('scraped_content/data/validated_content.json', 'w', encoding='utf-8') as f:
            json.dump(valid_contents, f, ensure_ascii=False, indent=2)
        print("✓ محتواهای معتبر ذخیره شدند")


if __name__ == "__main__":
    print("📚 مثال‌های استفاده از Content Scraper\n")
    
    # اجرای مثال‌ها (comment/uncomment کنید)
    # example_basic_usage()
    # example_custom_site()
    # example_filter_content()
    # example_import_preparation()
    
    print("\n💡 برای اجرای مثال‌ها، comment ها را بردارید")

