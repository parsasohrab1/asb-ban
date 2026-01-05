# راهنمای بهینه‌سازی عملکرد

## بهینه‌سازی‌های پیاده‌سازی شده

### Backend

#### 1. Caching با Redis
- ✅ Cache برای مقالات (1 ساعت)
- ✅ Cache برای محصولات (30 دقیقه)
- ✅ Cache برای اعلان‌ها (5 دقیقه)
- ✅ Cache برای تعداد اعلان‌های خوانده نشده (1 دقیقه)

#### 2. Database Indexing
- ✅ Index روی category_id برای مقالات و محصولات
- ✅ Index روی user_id برای سفارشات
- ✅ Index روی published_at برای مقالات
- ✅ Index روی created_at برای اعلان‌ها
- ✅ Full-text search indexes (GIN) برای جستجوی پیشرفته

#### 3. Compression
- ✅ Gzip compression برای همه response ها
- ✅ Level 6 compression (تعادل بین سرعت و حجم)

#### 4. Security Headers
- ✅ Helmet.js برای امنیت HTTP headers
- ✅ Content Security Policy
- ✅ X-Frame-Options
- ✅ X-Content-Type-Options

#### 5. Performance Monitoring
- ✅ Middleware برای مانیتورینگ درخواست‌های کند
- ✅ Logging برای درخواست‌های بیشتر از 1 ثانیه

### Frontend

#### 1. Next.js Optimizations
- ✅ Image optimization با next/image
- ✅ Automatic code splitting
- ✅ SWC minification
- ✅ Compression enabled

#### 2. Code Splitting
- ✅ Vendor chunk separation
- ✅ Common chunk extraction
- ✅ Dynamic imports برای کامپوننت‌های بزرگ

#### 3. Image Optimization
- ✅ استفاده از next/image
- ✅ AVIF و WebP formats
- ✅ Lazy loading
- ✅ Responsive images

#### 4. Caching Strategy
- ✅ Static page caching
- ✅ API response caching
- ✅ Service Worker برای offline caching

## جستجوی پیشرفته

### ویژگی‌ها
- ✅ جستجوی سراسری در مقالات، محصولات و مسابقات
- ✅ فیلتر بر اساس نوع محتوا
- ✅ فیلتر بر اساس دسته‌بندی
- ✅ مرتب‌سازی بر اساس مرتبط‌ترین، جدیدترین، قیمت
- ✅ Auto-complete در SearchBar
- ✅ صفحه نتایج جستجو

### API Endpoints
- `GET /api/search?q=query&type=blog&category=slug&sort=relevance`

### استفاده
```typescript
// در کامپوننت
import { searchAPI } from '@/lib/api';

const results = await searchAPI.globalSearch({
  q: 'اسب',
  type: 'all', // 'blog' | 'product' | 'competition' | 'all'
  category: 'horse-breeds',
  sort: 'relevance', // 'relevance' | 'date' | 'price'
  page: 1,
  limit: 20
});
```

## سیستم اعلان‌ها

### ویژگی‌ها
- ✅ اعلان‌های سفارش
- ✅ اعلان‌های رزرو
- ✅ اعلان‌های سیستم
- ✅ اعلان‌های پیشنهاد ویژه
- ✅ نمایش تعداد خوانده نشده
- ✅ علامت‌گذاری به عنوان خوانده شده
- ✅ حذف اعلان‌ها
- ✅ Real-time polling (هر 30 ثانیه)

### API Endpoints
- `GET /api/notifications` - دریافت اعلان‌ها
- `GET /api/notifications/unread-count` - تعداد خوانده نشده
- `PUT /api/notifications/:id/read` - علامت‌گذاری به عنوان خوانده شده
- `PUT /api/notifications/read-all` - همه را خوانده شده علامت بزن
- `DELETE /api/notifications/:id` - حذف اعلان

### استفاده
```typescript
import { notificationsAPI } from '@/lib/api';

// دریافت اعلان‌ها
const notifications = await notificationsAPI.getNotifications(20);

// تعداد خوانده نشده
const { count } = await notificationsAPI.getUnreadCount();

// علامت‌گذاری به عنوان خوانده شده
await notificationsAPI.markAsRead(notificationId);
```

### کامپوننت‌ها
- `NotificationBell` - آیکون اعلان در Header
- `/notifications` - صفحه لیست اعلان‌ها

## بهینه‌سازی‌های بیشتر

### پیشنهادات برای آینده

1. **CDN**: استفاده از CDN برای فایل‌های استاتیک
2. **Database Connection Pooling**: بهینه‌سازی اتصالات دیتابیس
3. **Query Optimization**: بهینه‌سازی کوئری‌های پیچیده
4. **Lazy Loading**: Lazy loading برای تصاویر و کامپوننت‌ها
5. **Service Worker**: بهبود caching در PWA
6. **Bundle Analysis**: تحلیل و بهینه‌سازی bundle size
7. **API Rate Limiting**: محدودیت نرخ درخواست‌ها
8. **Database Replication**: برای خواندن‌های بیشتر

## معیارهای عملکرد

### هدف
- زمان بارگذاری صفحه: < 3 ثانیه
- Time to First Byte (TTFB): < 500ms
- First Contentful Paint (FCP): < 1.5s
- Largest Contentful Paint (LCP): < 2.5s

### مانیتورینگ
- درخواست‌های کند (> 1s) در console لاگ می‌شوند
- می‌توانید از ابزارهای مانیتورینگ مثل New Relic یا Datadog استفاده کنید

