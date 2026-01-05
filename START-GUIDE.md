# راهنمای راه‌اندازی اپلیکیشن

## پیش‌نیازها

1. **Node.js** (v18 یا بالاتر)
2. **PostgreSQL** (در حال اجرا)
3. **Redis** (در حال اجرا)

## نصب Dependencies

```bash
npm run install:all
```

## راه‌اندازی

### روش 1: راه‌اندازی همزمان (توصیه می‌شود)

```bash
npm run dev
```

این دستور هم Backend و هم Frontend را همزمان راه‌اندازی می‌کند:
- **Backend**: http://localhost:3000
- **Frontend**: http://localhost:3001

### روش 2: راه‌اندازی جداگانه

#### Backend
```bash
cd backend
npm run dev
```

#### Frontend
```bash
cd frontend
npm run dev
```

## بررسی وضعیت

### Backend Health Check
```bash
curl http://localhost:3000/health
```

یا در مرورگر:
http://localhost:3000/health

### Frontend
در مرورگر:
http://localhost:3001

## مشکلات رایج

### ERR_CONNECTION_REFUSED

**علت**: اپلیکیشن متوقف شده است

**راه حل**:
1. مطمئن شوید که `npm run dev` در حال اجرا است
2. بررسی کنید که PostgreSQL و Redis در حال اجرا هستند
3. پورت‌ها را بررسی کنید:
   ```bash
   netstat -ano | findstr ":3000 :3001"
   ```

### پورت در حال استفاده است

**راه حل**:
1. Process را متوقف کنید:
   ```bash
   # Windows PowerShell
   Get-Process -Name node | Stop-Process -Force
   ```
2. یا پورت را تغییر دهید (در `.env` یا `package.json`)

### Database Connection Error

**راه حل**:
1. مطمئن شوید PostgreSQL در حال اجرا است
2. تنظیمات `.env` را بررسی کنید
3. Database را ایجاد کنید

## توقف اپلیکیشن

در ترمینال که `npm run dev` در حال اجرا است:
- `Ctrl + C` (Windows/Linux)
- `Cmd + C` (Mac)

## ساخت برای Production

```bash
npm run build
```

سپس:
```bash
# Backend
cd backend
npm start

# Frontend
cd frontend
npm start
```

