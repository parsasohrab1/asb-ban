# راهنمای تنظیمات سیستم ایمیل

## 📧 تنظیمات SMTP

سیستم ایمیل با استفاده از `nodemailer` پیاده‌سازی شده است. برای فعال‌سازی ارسال ایمیل، باید متغیرهای محیطی زیر را در فایل `.env` تنظیم کنید.

### متغیرهای محیطی مورد نیاز

```env
# فعال/غیرفعال کردن سیستم ایمیل
EMAIL_ENABLED=true

# تنظیمات SMTP
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_SECURE=false
SMTP_USER=your-email@gmail.com
SMTP_PASS=your-app-password

# آدرس فرانت‌اند (برای لینک‌های ایمیل)
FRONTEND_URL=http://localhost:3001
```

### تنظیمات برای سرویس‌های مختلف

#### Gmail
```env
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_SECURE=false
SMTP_USER=your-email@gmail.com
SMTP_PASS=your-app-password  # باید از App Password استفاده کنید
```

**نکته**: برای Gmail باید از [App Password](https://support.google.com/accounts/answer/185833) استفاده کنید، نه رمز عبور اصلی.

#### Outlook/Hotmail
```env
SMTP_HOST=smtp-mail.outlook.com
SMTP_PORT=587
SMTP_SECURE=false
SMTP_USER=your-email@outlook.com
SMTP_PASS=your-password
```

#### Yahoo
```env
SMTP_HOST=smtp.mail.yahoo.com
SMTP_PORT=587
SMTP_SECURE=false
SMTP_USER=your-email@yahoo.com
SMTP_PASS=your-app-password
```

#### سرویس‌های ایرانی (مثل میل ایران)
```env
SMTP_HOST=smtp.your-provider.com
SMTP_PORT=587
SMTP_SECURE=false
SMTP_USER=your-email@your-provider.com
SMTP_PASS=your-password
```

### تنظیمات برای Production

برای محیط Production، توصیه می‌شود از سرویس‌های حرفه‌ای ایمیل استفاده کنید:

#### SendGrid
```env
SMTP_HOST=smtp.sendgrid.net
SMTP_PORT=587
SMTP_SECURE=false
SMTP_USER=apikey
SMTP_PASS=your-sendgrid-api-key
```

#### Mailgun
```env
SMTP_HOST=smtp.mailgun.org
SMTP_PORT=587
SMTP_SECURE=false
SMTP_USER=your-mailgun-username
SMTP_PASS=your-mailgun-password
```

#### Amazon SES
```env
SMTP_HOST=email-smtp.region.amazonaws.com
SMTP_PORT=587
SMTP_SECURE=false
SMTP_USER=your-ses-username
SMTP_PASS=your-ses-password
```

## 📝 انواع ایمیل‌های ارسالی

سیستم ایمیل از تمپلیت‌های زیر پشتیبانی می‌کند:

### 1. تایید ثبت‌نام
- **زمان ارسال**: پس از ثبت‌نام موفق
- **محتوای ایمیل**: خوش‌آمدگویی و تایید ثبت‌نام

### 2. بازیابی رمز عبور
- **زمان ارسال**: درخواست بازیابی رمز عبور
- **محتوای ایمیل**: لینک بازیابی رمز عبور (معتبر برای 1 ساعت)

### 3. تایید سفارش
- **زمان ارسال**: پس از ثبت سفارش موفق
- **محتوای ایمیل**: جزئیات کامل سفارش شامل:
  - شماره سفارش
  - لیست محصولات
  - مبلغ کل
  - آدرس ارسال

### 4. تغییر وضعیت سفارش
- **زمان ارسال**: هنگام تغییر وضعیت سفارش
- **محتوای ایمیل**: اطلاع از تغییر وضعیت (در حال پردازش، ارسال شده، تحویل داده شده، لغو شده)

### 5. یادآوری رزرو خدمات
- **زمان ارسال**: پس از ثبت رزرو موفق
- **محتوای ایمیل**: جزئیات رزرو شامل:
  - نوع خدمات (دامپزشک یا اسب‌کش)
  - نام ارائه‌دهنده خدمات
  - تاریخ و ساعت رزرو

## 🔧 استفاده در کد

### ارسال ایمیل تایید ثبت‌نام
```typescript
import { sendRegistrationEmail } from '../services/emailService';

await sendRegistrationEmail(user.email, user.full_name);
```

### ارسال ایمیل بازیابی رمز عبور
```typescript
import { sendPasswordResetEmail } from '../services/emailService';

const resetToken = jwt.sign({ id: user.id, email: user.email }, JWT_SECRET, { expiresIn: '1h' });
await sendPasswordResetEmail(user.email, user.full_name, resetToken);
```

### ارسال ایمیل تایید سفارش
```typescript
import { sendOrderConfirmationEmail } from '../services/emailService';

await sendOrderConfirmationEmail(
  user.email,
  user.full_name,
  {
    orderNumber: 'ORD-123456',
    totalAmount: 500000,
    items: [
      { name: 'محصول 1', quantity: 2, price: 250000 }
    ],
    shippingAddress: 'تهران، خیابان...'
  }
);
```

### ارسال ایمیل تغییر وضعیت سفارش
```typescript
import { sendOrderStatusUpdateEmail } from '../services/emailService';

await sendOrderStatusUpdateEmail(
  user.email,
  user.full_name,
  'ORD-123456',
  'shipped'
);
```

### ارسال ایمیل یادآوری رزرو
```typescript
import { sendBookingReminderEmail } from '../services/emailService';

await sendBookingReminderEmail(
  user.email,
  user.full_name,
  'veterinarian',
  'دکتر احمدی',
  '1403/12/20',
  '14:00'
);
```

## 🧪 تست در محیط Development

در محیط Development، اگر `EMAIL_ENABLED=false` باشد، سیستم ایمیل غیرفعال می‌شود و فقط در console لاگ می‌زند:

```typescript
// در emailService.ts
if (process.env.EMAIL_ENABLED !== 'true') {
  console.log('Email service is disabled. Email would be sent to:', to);
  return true;
}
```

## ⚠️ نکات مهم

1. **امنیت**: هرگز رمز عبور یا اطلاعات حساس را در کد hardcode نکنید. همیشه از متغیرهای محیطی استفاده کنید.

2. **Rate Limiting**: برای جلوگیری از سوء استفاده، توصیه می‌شود Rate Limiting برای endpoint های مربوط به ایمیل (مثل بازیابی رمز عبور) اعمال شود.

3. **Error Handling**: سیستم ایمیل طوری طراحی شده که در صورت خطا در ارسال ایمیل، عملیات اصلی (مثل ثبت‌نام یا ثبت سفارش) متوقف نمی‌شود.

4. **Logging**: تمام خطاهای مربوط به ایمیل در console لاگ می‌شوند. در Production، این لاگ‌ها را به یک سیستم logging مرکزی ارسال کنید.

5. **Spam**: برای جلوگیری از spam، از سرویس‌های حرفه‌ای ایمیل استفاده کنید و SPF، DKIM و DMARC را تنظیم کنید.

## 📚 منابع بیشتر

- [Nodemailer Documentation](https://nodemailer.com/about/)
- [Gmail App Passwords](https://support.google.com/accounts/answer/185833)
- [Email Best Practices](https://www.campaignmonitor.com/dev-resources/guides/best-practices/)

---

**تاریخ به‌روزرسانی**: ۱۴۰۳/۱۲/۱۵

