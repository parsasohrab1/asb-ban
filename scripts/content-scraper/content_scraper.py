#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
اسکریپت جمع‌آوری محتوای مرتبط با اسب از سایت‌های فارسی
این اسکریپت محتوا، تصاویر و متن‌های SEO شده را از سایت‌های فارسی جمع‌آوری می‌کند
"""

import os
import re
import json
import time
import requests
from urllib.parse import urljoin, urlparse
from datetime import datetime
from pathlib import Path
from typing import List, Dict, Optional
from bs4 import BeautifulSoup
import hashlib
from PIL import Image
import io

class ContentScraper:
    def __init__(self, output_dir: str = "scraped_content"):
        self.output_dir = Path(output_dir)
        self.images_dir = self.output_dir / "images"
        self.data_dir = self.output_dir / "data"
        
        # ایجاد پوشه‌ها
        self.images_dir.mkdir(parents=True, exist_ok=True)
        self.data_dir.mkdir(parents=True, exist_ok=True)
        
        # User-Agent برای جلوگیری از بلاک شدن
        self.headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
            'Accept-Language': 'fa-IR,fa;q=0.9,en-US;q=0.8,en;q=0.7',
            'Accept-Encoding': 'gzip, deflate, br',
            'Connection': 'keep-alive',
        }
        
        self.session = requests.Session()
        self.session.headers.update(self.headers)
        
        # لیست سایت‌های فارسی مرتبط با اسب
        self.target_sites = [
            {
                'name': 'اسب ایران',
                'base_url': 'https://www.asbiran.com',
                'search_paths': ['/articles', '/blog', '/news'],
                'keywords': ['اسب', 'سوارکاری', 'مسابقات اسب']
            },
            {
                'name': 'فدراسیون سوارکاری',
                'base_url': 'https://www.iranequestrian.com',
                'search_paths': ['/news', '/articles'],
                'keywords': ['اسب', 'سوارکاری', 'مسابقات']
            },
            # می‌توانید سایت‌های بیشتری اضافه کنید
        ]
        
        self.scraped_urls = set()
        self.scraped_content = []
        
    def check_robots_txt(self, base_url: str) -> bool:
        """بررسی robots.txt برای رعایت قوانین"""
        try:
            robots_url = urljoin(base_url, '/robots.txt')
            response = self.session.get(robots_url, timeout=10)
            if response.status_code == 200:
                # بررسی ساده - در production باید کامل‌تر باشد
                return True
        except:
            pass
        return True  # در صورت عدم دسترسی، ادامه می‌دهیم
    
    def clean_text(self, text: str) -> str:
        """پاکسازی و نرمال‌سازی متن فارسی"""
        if not text:
            return ""
        
        # حذف فاصله‌های اضافی
        text = re.sub(r'\s+', ' ', text)
        # حذف کاراکترهای خاص
        text = re.sub(r'[^\u0600-\u06FF\u0750-\u077F\u08A0-\u08FF\uFB50-\uFDFF\uFE70-\uFEFFa-zA-Z0-9\s.,!?;:()\-]', '', text)
        # نرمال‌سازی فاصله‌ها
        text = text.strip()
        return text
    
    def extract_meta_tags(self, soup: BeautifulSoup) -> Dict:
        """استخراج Meta Tags برای SEO"""
        meta_data = {
            'title': '',
            'description': '',
            'keywords': '',
            'og_title': '',
            'og_description': '',
            'og_image': '',
        }
        
        # Title
        title_tag = soup.find('title')
        if title_tag:
            meta_data['title'] = self.clean_text(title_tag.get_text())
        
        # Meta Description
        meta_desc = soup.find('meta', attrs={'name': 'description'})
        if meta_desc:
            meta_data['description'] = self.clean_text(meta_desc.get('content', ''))
        
        # Meta Keywords
        meta_keywords = soup.find('meta', attrs={'name': 'keywords'})
        if meta_keywords:
            meta_data['keywords'] = self.clean_text(meta_keywords.get('content', ''))
        
        # Open Graph
        og_title = soup.find('meta', attrs={'property': 'og:title'})
        if og_title:
            meta_data['og_title'] = self.clean_text(og_title.get('content', ''))
        
        og_desc = soup.find('meta', attrs={'property': 'og:description'})
        if og_desc:
            meta_data['og_description'] = self.clean_text(og_desc.get('content', ''))
        
        og_image = soup.find('meta', attrs={'property': 'og:image'})
        if og_image:
            meta_data['og_image'] = og_image.get('content', '')
        
        return meta_data
    
    def extract_content(self, soup: BeautifulSoup) -> Dict:
        """استخراج محتوای اصلی صفحه"""
        content = {
            'headings': [],
            'paragraphs': [],
            'images': [],
            'links': [],
        }
        
        # استخراج headings (H1-H6)
        for i in range(1, 7):
            headings = soup.find_all(f'h{i}')
            for heading in headings:
                text = self.clean_text(heading.get_text())
                if text:
                    content['headings'].append({
                        'level': i,
                        'text': text
                    })
        
        # استخراج paragraphs
        paragraphs = soup.find_all('p')
        for p in paragraphs:
            text = self.clean_text(p.get_text())
            if text and len(text) > 20:  # فقط پاراگراف‌های با محتوا
                content['paragraphs'].append(text)
        
        # استخراج تصاویر
        images = soup.find_all('img')
        for img in images:
            src = img.get('src') or img.get('data-src') or img.get('data-lazy-src')
            if src:
                alt = img.get('alt', '')
                content['images'].append({
                    'url': src,
                    'alt': self.clean_text(alt),
                    'title': self.clean_text(img.get('title', ''))
                })
        
        # استخراج لینک‌ها
        links = soup.find_all('a', href=True)
        for link in links:
            href = link.get('href')
            text = self.clean_text(link.get_text())
            if href and text:
                content['links'].append({
                    'url': href,
                    'text': text
                })
        
        return content
    
    def download_image(self, image_url: str, base_url: str) -> Optional[str]:
        """دانلود و ذخیره تصویر"""
        try:
            # تبدیل URL نسبی به مطلق
            if not image_url.startswith('http'):
                image_url = urljoin(base_url, image_url)
            
            # بررسی اینکه قبلاً دانلود نشده باشد
            url_hash = hashlib.md5(image_url.encode()).hexdigest()
            image_ext = Path(urlparse(image_url).path).suffix or '.jpg'
            image_filename = f"{url_hash}{image_ext}"
            image_path = self.images_dir / image_filename
            
            if image_path.exists():
                return str(image_path.relative_to(self.output_dir))
            
            # دانلود تصویر
            response = self.session.get(image_url, timeout=30, stream=True)
            if response.status_code == 200:
                # بررسی نوع فایل
                content_type = response.headers.get('content-type', '')
                if 'image' not in content_type:
                    return None
                
                # ذخیره تصویر
                image_data = response.content
                with open(image_path, 'wb') as f:
                    f.write(image_data)
                
                # بررسی و بهینه‌سازی تصویر
                try:
                    img = Image.open(io.BytesIO(image_data))
                    # اگر تصویر خیلی بزرگ است، resize کنیم
                    if img.width > 1920 or img.height > 1920:
                        img.thumbnail((1920, 1920), Image.Resampling.LANCZOS)
                        img.save(image_path, optimize=True, quality=85)
                except:
                    pass
                
                return str(image_path.relative_to(self.output_dir))
        except Exception as e:
            print(f"خطا در دانلود تصویر {image_url}: {e}")
        
        return None
    
    def scrape_page(self, url: str) -> Optional[Dict]:
        """اسکرپ یک صفحه"""
        if url in self.scraped_urls:
            return None
        
        try:
            print(f"در حال اسکرپ: {url}")
            response = self.session.get(url, timeout=30)
            
            if response.status_code != 200:
                return None
            
            # بررسی encoding
            response.encoding = response.apparent_encoding or 'utf-8'
            
            soup = BeautifulSoup(response.text, 'html.parser')
            
            # استخراج Meta Tags
            meta_data = self.extract_meta_tags(soup)
            
            # استخراج محتوا
            content = self.extract_content(soup)
            
            # دانلود تصاویر
            downloaded_images = []
            for img_info in content['images'][:10]:  # حداکثر 10 تصویر
                img_path = self.download_image(img_info['url'], url)
                if img_path:
                    downloaded_images.append({
                        'path': img_path,
                        'alt': img_info['alt'],
                        'title': img_info['title']
                    })
            
            # ایجاد slug از title
            title = meta_data['title'] or content['headings'][0]['text'] if content['headings'] else 'بدون عنوان'
            slug = self.create_slug(title)
            
            # ترکیب محتوا
            full_text = ' '.join([p for p in content['paragraphs']])
            
            scraped_data = {
                'id': hashlib.md5(url.encode()).hexdigest()[:12],
                'url': url,
                'slug': slug,
                'title': title,
                'meta_description': meta_data['description'],
                'meta_keywords': meta_data['keywords'],
                'content': full_text,
                'excerpt': full_text[:300] + '...' if len(full_text) > 300 else full_text,
                'headings': content['headings'],
                'images': downloaded_images,
                'scraped_at': datetime.now().isoformat(),
                'source': urlparse(url).netloc,
            }
            
            self.scraped_urls.add(url)
            return scraped_data
            
        except Exception as e:
            print(f"خطا در اسکرپ {url}: {e}")
            return None
    
    def create_slug(self, text: str) -> str:
        """ایجاد slug از متن فارسی"""
        # تبدیل به لاتین برای slug
        persian_to_latin = {
            'ا': 'a', 'ب': 'b', 'پ': 'p', 'ت': 't', 'ث': 's',
            'ج': 'j', 'چ': 'ch', 'ح': 'h', 'خ': 'kh', 'د': 'd',
            'ذ': 'z', 'ر': 'r', 'ز': 'z', 'ژ': 'zh', 'س': 's',
            'ش': 'sh', 'ص': 's', 'ض': 'z', 'ط': 't', 'ظ': 'z',
            'ع': 'a', 'غ': 'gh', 'ف': 'f', 'ق': 'gh', 'ک': 'k',
            'گ': 'g', 'ل': 'l', 'م': 'm', 'ن': 'n', 'و': 'v',
            'ه': 'h', 'ی': 'y', ' ': '-'
        }
        
        slug = text.lower()
        for persian, latin in persian_to_latin.items():
            slug = slug.replace(persian, latin)
        
        # حذف کاراکترهای غیرمجاز
        slug = re.sub(r'[^a-z0-9\-]', '', slug)
        slug = re.sub(r'-+', '-', slug)
        slug = slug.strip('-')
        
        return slug[:100]  # محدود کردن طول
    
    def find_article_urls(self, base_url: str, search_paths: List[str], keywords: List[str]) -> List[str]:
        """پیدا کردن URL های مقالات"""
        article_urls = []
        
        for path in search_paths:
            try:
                url = urljoin(base_url, path)
                response = self.session.get(url, timeout=30)
                
                if response.status_code == 200:
                    soup = BeautifulSoup(response.text, 'html.parser')
                    
                    # پیدا کردن لینک‌های مقالات
                    links = soup.find_all('a', href=True)
                    for link in links:
                        href = link.get('href')
                        text = link.get_text().lower()
                        
                        # بررسی اینکه آیا لینک مرتبط با اسب است
                        if any(keyword in text for keyword in keywords):
                            full_url = urljoin(base_url, href)
                            if full_url not in article_urls:
                                article_urls.append(full_url)
                
                time.sleep(self.delay)  # تاخیر برای رعایت اخلاقی
                
            except Exception as e:
                print(f"خطا در پیدا کردن مقالات از {path}: {e}")
        
        return article_urls[:20]  # حداکثر 20 مقاله از هر سایت
    
    def scrape_site(self, site_config: Dict):
        """اسکرپ یک سایت کامل"""
        print(f"\n{'='*60}")
        print(f"شروع اسکرپ سایت: {site_config['name']}")
        print(f"URL: {site_config['base_url']}")
        print(f"{'='*60}\n")
        
        # بررسی robots.txt
        if not self.check_robots_txt(site_config['base_url']):
            print(f"⚠️  robots.txt اجازه اسکرپ نمی‌دهد: {site_config['base_url']}")
            return
        
        # پیدا کردن URL های مقالات
        article_urls = self.find_article_urls(
            site_config['base_url'],
            site_config['search_paths'],
            site_config['keywords']
        )
        
        print(f"تعداد مقالات پیدا شده: {len(article_urls)}")
        
        # اسکرپ هر مقاله
        for url in article_urls:
            data = self.scrape_page(url)
            if data:
                self.scraped_content.append(data)
                print(f"✓ محتوا ذخیره شد: {data['title'][:50]}...")
            
            time.sleep(self.delay)  # تاخیر بین درخواست‌ها
    
    def save_to_json(self, filename: str = 'scraped_content.json'):
        """ذخیره داده‌ها در فایل JSON"""
        output_file = self.data_dir / filename
        
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(self.scraped_content, f, ensure_ascii=False, indent=2)
        
        print(f"\n✓ داده‌ها در {output_file} ذخیره شدند")
        print(f"تعداد کل محتواهای جمع‌آوری شده: {len(self.scraped_content)}")
    
    def save_to_sql(self, filename: str = 'scraped_content.sql'):
        """ذخیره داده‌ها در فایل SQL برای import به دیتابیس"""
        output_file = self.data_dir / filename
        
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write("-- محتوای جمع‌آوری شده از سایت‌های فارسی\n")
            f.write("-- تاریخ تولید: " + datetime.now().strftime("%Y-%m-%d %H:%M:%S") + "\n\n")
            
            for item in self.scraped_content:
                # Escape برای SQL
                title = item['title'].replace("'", "''")
                content = item['content'].replace("'", "''")
                excerpt = item['excerpt'].replace("'", "''")
                meta_desc = item['meta_description'].replace("'", "''")
                
                # تصویر اصلی
                main_image = item['images'][0]['path'] if item['images'] else None
                
                f.write(f"""
INSERT INTO blog_posts (
    title, slug, excerpt, content, featured_image,
    meta_description, meta_keywords, author_id, category_id,
    is_published, published_at, created_at
) VALUES (
    '{title}',
    '{item['slug']}',
    '{excerpt}',
    '{content}',
    {f"'{main_image}'" if main_image else 'NULL'},
    '{meta_desc}',
    '{item['meta_keywords']}',
    1, -- author_id (باید تغییر دهید)
    1, -- category_id (باید تغییر دهید)
    true,
    NOW(),
    NOW()
);
""")
        
        print(f"\n✓ فایل SQL در {output_file} ایجاد شد")
    
    def run(self):
        """اجرای اسکرپر"""
        print("🚀 شروع جمع‌آوری محتوا...")
        print(f"📁 پوشه خروجی: {self.output_dir}\n")
        
        for site in self.target_sites:
            try:
                self.scrape_site(site)
            except Exception as e:
                print(f"❌ خطا در اسکرپ سایت {site['name']}: {e}")
        
        # ذخیره نتایج
        if self.scraped_content:
            self.save_to_json()
            self.save_to_sql()
            
            # خلاصه
            print(f"\n{'='*60}")
            print("✅ جمع‌آوری محتوا با موفقیت انجام شد!")
            print(f"📊 تعداد کل محتواها: {len(self.scraped_content)}")
            print(f"🖼️  تعداد تصاویر دانلود شده: {len(list(self.images_dir.glob('*')))}")
            print(f"{'='*60}\n")
        else:
            print("⚠️  هیچ محتوایی جمع‌آوری نشد!")


def main():
    scraper = ContentScraper(output_dir="scraped_content")
    scraper.run()


if __name__ == "__main__":
    main()

