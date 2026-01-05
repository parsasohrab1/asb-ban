# راهنمای تنظیمات Push Notifications

## 📱 پشتیبانی از Push Notifications

سیستم Push Notifications برای پلتفرم‌های زیر پیاده‌سازی شده است:
- ✅ **Web Push Notifications** (PWA) - برای مرورگرهای مدرن
- ⚠️ **Android** - نیاز به Firebase Cloud Messaging (FCM)
- ⚠️ **iOS** - نیاز به Apple Push Notification Service (APNs)

## 🔧 تنظیمات Backend

### 1. نصب وابستگی‌ها

وابستگی `web-push` قبلاً نصب شده است. برای Android و iOS نیاز به تنظیمات اضافی است.

### 2. تولید VAPID Keys

برای Web Push Notifications، نیاز به VAPID (Voluntary Application Server Identification) keys دارید:

```bash
cd backend
npx web-push generate-vapid-keys
```

این دستور دو کلید تولید می‌کند:
- **Public Key**: برای استفاده در Frontend
- **Private Key**: برای استفاده در Backend (محرمانه!)

### 3. تنظیمات Environment Variables

در فایل `.env` در پوشه `backend`:

```env
# Push Notifications
PUSH_NOTIFICATIONS_ENABLED=true

# VAPID Keys (برای Web Push)
VAPID_PUBLIC_KEY=your-public-key-here
VAPID_PRIVATE_KEY=your-private-key-here
VAPID_SUBJECT=mailto:support@asb-ban.ir

# برای Android (Firebase)
FCM_SERVER_KEY=your-fcm-server-key
FCM_PROJECT_ID=your-fcm-project-id

# برای iOS (APNs)
APNS_KEY_ID=your-apns-key-id
APNS_TEAM_ID=your-apns-team-id
APNS_BUNDLE_ID=ir.asbban.app
APNS_KEY_PATH=./path/to/AuthKey.p8
```

### 4. ایجاد جدول در دیتابیس

جدول `push_subscriptions` باید در دیتابیس ایجاد شود:

```sql
-- این جدول در schema.sql اضافه شده است
CREATE TABLE IF NOT EXISTS push_subscriptions (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    endpoint TEXT NOT NULL UNIQUE,
    p256dh TEXT NOT NULL,
    auth TEXT NOT NULL,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🌐 تنظیمات Frontend (Web)

### 1. Service Worker

Service Worker (`frontend/public/sw.js`) برای handle کردن push events به‌روزرسانی شده است.

### 2. کامپوننت PushNotificationManager

کامپوننت `PushNotificationManager` در صفحه پروفایل اضافه شده است که:
- بررسی می‌کند آیا Push Notifications پشتیبانی می‌شود
- VAPID public key را از سرور دریافت می‌کند
- از کاربر مجوز می‌گیرد
- Subscription را ثبت می‌کند

### 3. استفاده

کامپوننت به صورت خودکار در صفحه پروفایل نمایش داده می‌شود. کاربر می‌تواند با کلیک روی دکمه، Push Notifications را فعال یا غیرفعال کند.

## 📱 تنظیمات Android (Firebase Cloud Messaging)

### 1. ایجاد پروژه Firebase

1. به [Firebase Console](https://console.firebase.google.com/) بروید
2. پروژه جدید ایجاد کنید
3. Android app را اضافه کنید
4. `google-services.json` را دانلود کنید

### 2. تنظیمات در Android App

فایل `google-services.json` را در `android/app/` قرار دهید.

در `android/app/build.gradle.kts`:

```kotlin
plugins {
    id("com.google.gms.google-services")
}

dependencies {
    implementation("com.google.firebase:firebase-messaging:23.4.0")
}
```

### 3. پیاده‌سازی FCM Service

یک `FirebaseMessagingService` در Android app ایجاد کنید:

```kotlin
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        // ارسال token به سرور
        sendTokenToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle notification
        showNotification(remoteMessage)
    }
}
```

## 🍎 تنظیمات iOS (Apple Push Notification Service)

### 1. تنظیمات در Apple Developer

1. به [Apple Developer Portal](https://developer.apple.com/) بروید
2. App ID را ایجاد کنید و Push Notifications را فعال کنید
3. APNs Key را ایجاد و دانلود کنید (`.p8` file)
4. Certificate را در Xcode تنظیم کنید

### 2. تنظیمات در iOS App

در Xcode:
1. Capabilities → Push Notifications را فعال کنید
2. Background Modes → Remote notifications را فعال کنید

### 3. پیاده‌سازی در iOS

```swift
import UserNotifications

func application(_ application: UIApplication, 
                 didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
    UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
        // Handle authorization
    }
    application.registerForRemoteNotifications()
    return true
}
```

## 🔌 API Endpoints

### دریافت VAPID Public Key
```
GET /api/push/vapid-key
```

### ثبت Subscription
```
POST /api/push/subscribe
Authorization: Bearer <token>
Body: {
  "subscription": {
    "endpoint": "...",
    "keys": {
      "p256dh": "...",
      "auth": "..."
    }
  }
}
```

### حذف Subscription
```
POST /api/push/unsubscribe
Authorization: Bearer <token>
Body: {
  "endpoint": "..."
}
```

### دریافت Subscriptions کاربر
```
GET /api/push/subscriptions
Authorization: Bearer <token>
```

### تست Push Notification
```
POST /api/push/test
Authorization: Bearer <token>
Body: {
  "title": "عنوان",
  "message": "پیام",
  "link": "/optional-link",
  "type": "system"
}
```

## 🧪 تست

### تست Web Push

1. Backend را راه‌اندازی کنید
2. Frontend را اجرا کنید
3. وارد حساب کاربری شوید
4. به صفحه پروفایل بروید
5. روی دکمه "فعال‌سازی اعلان‌ها" کلیک کنید
6. مجوز را تایید کنید
7. از API endpoint `/api/push/test` برای ارسال تست استفاده کنید

### تست در Development

برای تست در localhost، باید از HTTPS استفاده کنید یا از `localhost` استفاده کنید (که مرورگرها آن را به عنوان secure می‌شناسند).

## ⚠️ نکات مهم

1. **HTTPS**: Push Notifications فقط روی HTTPS یا localhost کار می‌کند
2. **VAPID Keys**: هرگز private key را در frontend قرار ندهید
3. **Permissions**: کاربر باید مجوز را بدهد
4. **Service Worker**: باید Service Worker ثبت شده باشد
5. **Browser Support**: همه مرورگرها Push Notifications را پشتیبانی نمی‌کنند

## 📚 منابع بیشتر

- [Web Push Protocol](https://web.dev/push-notifications-overview/)
- [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging)
- [Apple Push Notifications](https://developer.apple.com/notifications/)
- [web-push Library](https://github.com/web-push-libs/web-push)

---

**تاریخ به‌روزرسانی**: ۱۴۰۳/۱۲/۱۵

