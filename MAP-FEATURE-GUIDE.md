# راهنمای قابلیت نقشه (مشابه اسنپ)

## خلاصه

قابلیت نقشه برای انتخاب دامپزشک و اسب‌کش با نمایش موقعیت جغرافیایی اضافه شده است. کاربران می‌توانند:

1. موقعیت خود را روی نقشه ببینند
2. دامپزشکان و اسب‌کش‌های نزدیک را روی نقشه مشاهده کنند
3. بر اساس فاصله فیلتر کنند (10، 25، 50، 100 کیلومتر)
4. با کلیک روی marker، اطلاعات ارائه‌دهنده را ببینند
5. مستقیماً از روی نقشه رزرو کنند

## تغییرات در Backend

### 1. Schema دیتابیس

فیلدهای زیر به جداول اضافه شده:
- `latitude` (DECIMAL): عرض جغرافیایی
- `longitude` (DECIMAL): طول جغرافیایی  
- `address` (TEXT): آدرس کامل

### 2. API Updates

#### GET /api/services/veterinarians
پارامترهای جدید:
- `latitude`: عرض جغرافیایی کاربر
- `longitude`: طول جغرافیایی کاربر
- `radius`: شعاع جستجو (کیلومتر)

پاسخ شامل فیلد `distance` (فاصله به کیلومتر) است.

#### GET /api/services/transporters
همان پارامترهای بالا

### 3. محاسبه فاصله

از فرمول Haversine برای محاسبه فاصله استفاده می‌شود:
```sql
6371 * acos(
  cos(radians(lat1)) *
  cos(radians(lat2)) *
  cos(radians(lng2) - radians(lng1)) +
  sin(radians(lat1)) *
  sin(radians(lat2))
)
```

## تغییرات در Frontend (PWA)

### 1. صفحه نقشه

مسیر: `/services/map`

ویژگی‌ها:
- دریافت موقعیت کاربر با Geolocation API
- نمایش نقشه با Google Maps
- نمایش markers برای دامپزشکان (سبز) و اسب‌کش‌ها (نارنجی)
- فیلتر بر اساس نوع سرویس و فاصله
- لیست ارائه‌دهندگان در پایین صفحه
- امکان رزرو مستقیم

### 2. کامپوننت MapComponent

استفاده از `@googlemaps/js-api-loader` برای نمایش نقشه

### 3. تنظیمات

در `.env.local` اضافه کنید:
```
NEXT_PUBLIC_GOOGLE_MAPS_API_KEY=your_api_key
```

## تغییرات در Android

### 1. Dependencies

اضافه شده:
- `play-services-maps`: Google Maps SDK
- `play-services-location`: Location Services
- `maps-compose`: Compose integration

### 2. Permissions

در `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

### 3. Google Maps API Key

در `AndroidManifest.xml`:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_GOOGLE_MAPS_API_KEY" />
```

### 4. MapScreen

- دریافت موقعیت کاربر
- نمایش نقشه با Google Maps Compose
- نمایش markers
- فیلتر و جستجو
- لیست ارائه‌دهندگان

## نحوه استفاده

### برای کاربران

1. به صفحه "خدمات" بروید
2. روی "مشاهده روی نقشه" کلیک کنید
3. اجازه دسترسی به موقعیت را بدهید
4. دامپزشکان یا اسب‌کش‌ها را انتخاب کنید
5. فاصله را تنظیم کنید (10، 25، 50، 100 کیلومتر)
6. روی marker کلیک کنید یا از لیست انتخاب کنید
7. "رزرو" را بزنید

### برای توسعه‌دهندگان

#### ثبت موقعیت ارائه‌دهنده

هنگام ثبت دامپزشک یا اسب‌کش:
```json
{
  "full_name": "دکتر احمدی",
  "latitude": 35.6892,
  "longitude": 51.3890,
  "address": "تهران، خیابان ولیعصر"
}
```

#### دریافت Google Maps API Key

1. به [Google Cloud Console](https://console.cloud.google.com/) بروید
2. پروژه جدید ایجاد کنید
3. Maps JavaScript API را فعال کنید
4. API Key ایجاد کنید
5. در فایل‌های `.env` و `AndroidManifest.xml` قرار دهید

## نکات مهم

1. **امنیت**: API Key را در repository commit نکنید
2. **بهینه‌سازی**: برای کاهش هزینه‌ها، از caching استفاده کنید
3. **دقت**: مطمئن شوید که موقعیت‌ها دقیق هستند
4. **تجربه کاربری**: در صورت عدم دسترسی به موقعیت، از موقعیت پیش‌فرض (تهران) استفاده می‌شود

## مشکلات رایج

### نقشه نمایش داده نمی‌شود
- بررسی کنید API Key درست باشد
- بررسی کنید API در Google Cloud Console فعال باشد
- در مرورگر، Console را بررسی کنید

### موقعیت کاربر دریافت نمی‌شود
- بررسی کنید permission داده شده باشد
- در HTTPS یا localhost تست کنید (Geolocation در HTTP کار نمی‌کند)

### فاصله درست محاسبه نمی‌شود
- بررسی کنید latitude و longitude درست باشند
- فرمت باید decimal باشد (مثلاً 35.6892 نه 35°41'21")

## توسعه آینده

- [ ] مسیریابی (Navigation)
- [ ] نمایش مسیر روی نقشه
- [ ] تخمین زمان رسیدن
- [ ] Push notification هنگام نزدیک شدن
- [ ] تاریخچه مکان‌های بازدید شده

