# دریافت Fingerprint برای کافه‌بازار

## دستور دریافت SHA-1 و SHA-256

### اگر keystore دارید:

```bash
cd android
keytool -list -v -keystore asb-ban-key.jks -alias asb-ban
```

### اگر از debug keystore استفاده می‌کنید:

```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

## خروجی نمونه

```
Alias name: asb-ban
Creation date: ...
Entry type: PrivateKeyEntry
Certificate chain length: 1
Certificate[1]:
Owner: CN=Asb Ban, OU=Development, O=Asb Ban, L=Tehran, ST=Tehran, C=IR
Issuer: CN=Asb Ban, OU=Development, O=Asb Ban, L=Tehran, ST=Tehran, C=IR
Serial number: ...
Valid from: ... until: ...
Certificate fingerprints:
         SHA1: XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX
         SHA256: XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX
```

## فقط SHA-1

```bash
keytool -list -v -keystore asb-ban-key.jks -alias asb-ban | grep SHA1
```

## فقط SHA-256

```bash
keytool -list -v -keystore asb-ban-key.jks -alias asb-ban | grep SHA256
```

## اطلاعات مورد نیاز کافه‌بازار

کافه‌بازار معمولاً نیاز به:
- **SHA-1 Fingerprint** دارد
- **SHA-256 Fingerprint** (اختیاری)

این اطلاعات را در پنل توسعه‌دهنده کافه‌بازار وارد کنید.

