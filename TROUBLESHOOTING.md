# راهنمای رفع مشکلات

## مشکل: فایل‌های Next.js از پورت 3000 درخواست می‌شوند

### علت
- مرورگر cache کرده است که فایل‌های static از پورت 3000 باید درخواست شوند
- یا Service Worker قدیمی cache کرده است

### راه حل

#### 1. Hard Refresh مرورگر
- **Windows/Linux**: `Ctrl + Shift + R` یا `Ctrl + F5`
- **Mac**: `Cmd + Shift + R`

#### 2. پاک کردن Cache
1. Developer Tools را باز کنید (F12)
2. به تب **Application** بروید
3. در سمت چپ، **Clear storage** را انتخاب کنید
4. روی **Clear site data** کلیک کنید

#### 3. Unregister Service Worker
1. Developer Tools > Application > Service Workers
2. روی **Unregister** کلیک کنید
3. صفحه را refresh کنید

#### 4. Restart اپلیکیشن
```bash
# توقف اپلیکیشن (Ctrl+C)
# سپس دوباره اجرا کنید
npm run dev
```

## مشکل: آیکون‌های PWA موجود نیستند

### راه حل
1. آیکون‌های واقعی را ایجاد کنید (مطابق `ICON-SETUP.md`)
2. یا از placeholder استفاده کنید (برای تست)

## مشکل: MIME Type Error

### علت
- Headers برای فایل‌های JavaScript به درستی تنظیم نشده

### راه حل
- `next.config.js` اصلاح شده است
- اپلیکیشن را restart کنید

## بررسی وضعیت

### مطمئن شوید که:
- ✅ Backend روی پورت **3000** اجرا می‌شود
- ✅ Frontend روی پورت **3001** اجرا می‌شود
- ✅ فایل‌های static از پورت **3001** درخواست می‌شوند

### بررسی در مرورگر:
1. Developer Tools > Network tab
2. صفحه را refresh کنید
3. بررسی کنید که فایل‌های `_next/static` از کدام پورت درخواست می‌شوند

