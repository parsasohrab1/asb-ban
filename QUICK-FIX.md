# راهنمای سریع رفع مشکل

## مشکل: npm error ENOENT

### علت
شما در مسیر اشتباه هستید. `package.json` در پوشه `asb-ban` قرار دارد.

### راه حل

#### 1. به مسیر صحیح بروید:
```bash
cd asb-ban
```

#### 2. سپس دستور را اجرا کنید:
```bash
npm run dev:backend
```

یا برای اجرای همزمان Backend و Frontend:
```bash
npm run dev
```

## مسیرهای صحیح

- **پروژه اصلی**: `C:\Users\asus\Documents\asbban\asb-ban`
- **Backend**: `C:\Users\asus\Documents\asbban\asb-ban\backend`
- **Frontend**: `C:\Users\asus\Documents\asbban\asb-ban\frontend`

## دستورات مفید

### بررسی مسیر فعلی
```bash
pwd
# یا در PowerShell:
Get-Location
```

### رفتن به مسیر پروژه
```bash
cd C:\Users\asus\Documents\asbban\asb-ban
```

### اجرای Backend
```bash
npm run dev:backend
```

### اجرای Frontend
```bash
npm run dev:frontend
```

### اجرای هر دو
```bash
npm run dev
```

