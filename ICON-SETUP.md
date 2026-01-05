# راهنمای ایجاد آیکون‌های PWA

## فایل‌های مورد نیاز

برای PWA به آیکون‌های زیر نیاز دارید:

1. **icon-192x192.png** - آیکون 192x192 پیکسل
2. **icon-512x512.png** - آیکون 512x512 پیکسل  
3. **favicon.ico** - آیکون 16x16 یا 32x32 پیکسل

## محل قرارگیری

همه فایل‌ها باید در پوشه `frontend/public/` قرار گیرند.

## روش‌های ایجاد آیکون

### روش 1: استفاده از ابزارهای آنلاین

1. **Favicon Generator**: https://www.favicon-generator.org/
   - تصویر اصلی را آپلود کنید
   - همه سایزها را دانلود کنید

2. **RealFaviconGenerator**: https://realfavicongenerator.net/
   - پشتیبانی کامل از PWA
   - ایجاد همه فرمت‌ها

### روش 2: ایجاد دستی (برای تست)

می‌توانید از یک تصویر ساده استفاده کنید:

```bash
# استفاده از ImageMagick (اگر نصب است)
convert -size 192x192 xc:#0ea5e9 icon-192x192.png
convert -size 512x512 xc:#0ea5e9 icon-512x512.png
```

### روش 3: استفاده از طراحی

1. یک لوگو یا آیکون طراحی کنید
2. آن را به سایزهای مورد نیاز resize کنید
3. در پوشه `frontend/public/` قرار دهید

## بررسی

بعد از ایجاد آیکون‌ها:

1. مرورگر را refresh کنید (Ctrl+F5)
2. Developer Tools > Application > Manifest را بررسی کنید
3. باید آیکون‌ها بدون خطا نمایش داده شوند

## نکات

- فرمت PNG با transparency بهتر است
- رنگ theme: #0ea5e9 (آبی)
- برای maskable icons، padding مناسب اضافه کنید

