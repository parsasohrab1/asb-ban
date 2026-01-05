#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
اسکریپت اعتبارسنجی و پاکسازی محتوای جمع‌آوری شده
"""

import json
import re
from pathlib import Path
from typing import List, Dict, Tuple

class ContentValidator:
    def __init__(self):
        self.min_title_length = 10
        self.min_content_length = 200
        self.max_content_length = 50000
        self.required_keywords = ['اسب', 'سوارکاری', 'مسابقات', 'نژاد', 'بیماری', 'تغذیه']
    
    def validate_title(self, title: str) -> bool:
        """اعتبارسنجی عنوان"""
        if not title or len(title) < self.min_title_length:
            return False
        
        # بررسی وجود کلمات کلیدی مرتبط
        title_lower = title.lower()
        if not any(keyword in title_lower for keyword in self.required_keywords):
            return False
        
        return True
    
    def validate_content(self, content: str) -> bool:
        """اعتبارسنجی محتوا"""
        if not content:
            return False
        
        if len(content) < self.min_content_length:
            return False
        
        if len(content) > self.max_content_length:
            return False
        
        # بررسی وجود کلمات کلیدی
        content_lower = content.lower()
        keyword_count = sum(1 for keyword in self.required_keywords if keyword in content_lower)
        
        if keyword_count < 2:  # حداقل 2 کلمه کلیدی
            return False
        
        return True
    
    def clean_content(self, content: Dict) -> Dict:
        """پاکسازی و اعتبارسنجی محتوا"""
        # حذف HTML tags باقی‌مانده
        content['content'] = re.sub(r'<[^>]+>', '', content.get('content', ''))
        content['excerpt'] = re.sub(r'<[^>]+>', '', content.get('excerpt', ''))
        
        # حذف فاصله‌های اضافی
        content['content'] = re.sub(r'\s+', ' ', content['content']).strip()
        content['excerpt'] = re.sub(r'\s+', ' ', content['excerpt']).strip()
        
        # محدود کردن طول excerpt
        if len(content['excerpt']) > 300:
            content['excerpt'] = content['excerpt'][:297] + '...'
        
        return content
    
    def validate(self, content: Dict) -> Tuple[bool, List[str]]:
        """اعتبارسنجی کامل محتوا"""
        errors = []
        
        if not self.validate_title(content.get('title', '')):
            errors.append('عنوان نامعتبر یا کوتاه است')
        
        if not self.validate_content(content.get('content', '')):
            errors.append('محتوا نامعتبر یا کوتاه است')
        
        if not content.get('slug'):
            errors.append('Slug وجود ندارد')
        
        if not content.get('url'):
            errors.append('URL وجود ندارد')
        
        return len(errors) == 0, errors
    
    def validate_batch(self, contents: List[Dict]) -> Tuple[List[Dict], List[Dict]]:
        """اعتبارسنجی دسته‌ای محتواها"""
        valid_contents = []
        invalid_contents = []
        
        for content in contents:
            # پاکسازی
            content = self.clean_content(content)
            
            # اعتبارسنجی
            is_valid, errors = self.validate(content)
            
            if is_valid:
                valid_contents.append(content)
            else:
                content['validation_errors'] = errors
                invalid_contents.append(content)
        
        return valid_contents, invalid_contents


def main():
    json_file = Path('scraped_content/data/scraped_content.json')
    
    if not json_file.exists():
        print(f"❌ فایل {json_file} یافت نشد!")
        return
    
    # خواندن محتوا
    with open(json_file, 'r', encoding='utf-8') as f:
        contents = json.load(f)
    
    print(f"📊 تعداد کل محتواها: {len(contents)}")
    
    # اعتبارسنجی
    validator = ContentValidator()
    valid_contents, invalid_contents = validator.validate_batch(contents)
    
    print(f"\n✅ محتواهای معتبر: {len(valid_contents)}")
    print(f"❌ محتواهای نامعتبر: {len(invalid_contents)}")
    
    # ذخیره محتواهای معتبر
    if valid_contents:
        output_file = json_file.parent / 'scraped_content_validated.json'
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(valid_contents, f, ensure_ascii=False, indent=2)
        print(f"\n✓ محتواهای معتبر در {output_file} ذخیره شدند")
    
    # نمایش خطاها
    if invalid_contents:
        print("\n⚠️  محتواهای نامعتبر:")
        for content in invalid_contents[:5]:  # نمایش 5 مورد اول
            print(f"  - {content.get('title', 'بدون عنوان')[:50]}")
            print(f"    خطاها: {', '.join(content.get('validation_errors', []))}")


if __name__ == "__main__":
    main()

