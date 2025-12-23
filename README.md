# 🛒 Greeceri Store - Backend API

Backend API untuk aplikasi e-commerce Greeceri Store berbasis **Spring Boot 3**.

## 📋 Daftar Isi

- [Teknologi](#-teknologi)
- [Fitur](#-fitur)
- [Prasyarat](#-prasyarat)
- [Instalasi](#-instalasi)
- [Konfigurasi Environment](#-konfigurasi-environment)
- [Menjalankan Aplikasi](#-menjalankan-aplikasi)
- [API Endpoints](#-api-endpoints)
- [Struktur Project](#-struktur-project)

---

## 🛠 Teknologi

| Teknologi | Versi |
|-----------|-------|
| Java | 21 |
| Spring Boot | 3.x |
| PostgreSQL | 14+ |
| Maven | 3.8+ |

### Dependencies Utama
- **Spring Security + JWT** - Autentikasi & otorisasi
- **Spring Data JPA** - Database ORM
- **Brevo SMTP** - Email service
- **Xendit** - Payment gateway
- **Cloudinary** - Image hosting
- **Google API Client** - Google OAuth

---

## ✨ Fitur

### Autentikasi
- ✅ Register dengan verifikasi email
- ✅ Login dengan JWT
- ✅ Google OAuth Login
- ✅ Forgot/Reset Password
- ✅ Refresh Token

### User
- ✅ Profile management
- ✅ Address management (multi-alamat)
- ✅ Change password

### Produk
- ✅ List produk dengan pagination & search
- ✅ Filter berdasarkan kategori
- ✅ Best sellers minggu ini

### Keranjang
- ✅ Add/update/remove item
- ✅ Increment quantity otomatis

### Wishlist
- ✅ Add/remove produk favorit
- ✅ Check status wishlist

### Order
- ✅ Checkout dengan Xendit payment
- ✅ Pilih tanggal & slot pengiriman
- ✅ Kalkulasi ongkos kirim berdasarkan jarak
- ✅ Cancel order (PENDING_PAYMENT)
- ✅ Konfirmasi penerimaan

### Admin
- ✅ Dashboard statistik
- ✅ CRUD Produk & Kategori
- ✅ Manajemen User
- ✅ Manajemen Order
- ✅ Notifikasi (new order, low stock, dll)

---

## 📦 Prasyarat

Pastikan sudah terinstall:
- Java 17+
- Maven 3.8+
- PostgreSQL 14+

---

## 🚀 Instalasi

### 1. Clone Repository

```bash
git clone https://github.com/Wannzh/greeceri-store-be.git
cd greeceri-store-be
```

### 2. Buat Database PostgreSQL

```sql
CREATE DATABASE greeceri_store;
```

### 3. Konfigurasi Environment

Buat file `application-dev.properties` di `src/main/resources/`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/greeceri_store
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret-key=YOUR_JWT_SECRET_KEY_MIN_256_BIT
jwt.expiration-ms=86400000
jwt.refresh-token.expiration-ms=604800000

# Mail (Brevo SMTP)
spring.mail.host=smtp-relay.brevo.com
spring.mail.port=2525
spring.mail.username=YOUR_BREVO_EMAIL
spring.mail.password=YOUR_BREVO_SMTP_KEY
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.from.email=noreply@greeceri.store
spring.mail.from.name=Greeceri Store

# Xendit Payment
xendit.api.key=YOUR_XENDIT_API_KEY
xendit.callback.token=YOUR_XENDIT_CALLBACK_TOKEN

# Cloudinary
cloudinary.cloud_name=YOUR_CLOUD_NAME
cloudinary.api_key=YOUR_API_KEY
cloudinary.api_secret=YOUR_API_SECRET

# Google OAuth
google.client.id=YOUR_GOOGLE_CLIENT_ID

# App URLs
app.base.url=http://localhost:8080
app.verification.redirect.success=http://localhost:5173/login?verified=true
app.verification.redirect.failure=http://localhost:5173/verification-failed
app.password.reset.url=http://localhost:5173/reset-password
app.payment.redirect.success=http://localhost:5173/payment/success
app.payment.redirect.failure=http://localhost:5173/payment/failed
app.logo.url=https://your-logo-url.png

# Admin Default
admin.username=admin
admin.email=admin@greeceri.store
admin.password=YOUR_ADMIN_PASSWORD
```

### 4. Install Dependencies

```bash
./mvnw clean install
```

---

## ▶ Menjalankan Aplikasi

### Development
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Production
```bash
./mvnw clean package -DskipTests
java -jar target/store-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

Aplikasi akan berjalan di `http://localhost:8080`

---

## 📡 API Endpoints

### Public Endpoints

| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| POST | `/api/auth/register` | Register user baru |
| POST | `/api/auth/login` | Login |
| POST | `/api/auth/google` | Login dengan Google |
| POST | `/api/auth/refresh-token` | Refresh access token |
| POST | `/api/auth/forgot-password` | Request reset password |
| POST | `/api/auth/reset-password` | Reset password |
| GET | `/api/auth/verify?token=` | Verifikasi email |
| GET | `/api/products` | List produk (pagination) |
| GET | `/api/products/best-sellers` | 4 produk terlaris minggu ini |
| GET | `/api/products/{id}` | Detail produk |
| GET | `/api/categories` | List kategori |

### User Endpoints (Requires Auth)

| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| GET | `/api/user/profile` | Get profile |
| PUT | `/api/user/profile` | Update profile |
| PUT | `/api/user/change-password` | Change password |
| GET | `/api/addresses` | List alamat |
| POST | `/api/addresses` | Tambah alamat |
| PUT | `/api/addresses/{id}` | Update alamat |
| DELETE | `/api/addresses/{id}` | Hapus alamat |
| GET | `/api/cart` | Get keranjang |
| POST | `/api/cart` | Add item ke keranjang |
| PUT | `/api/cart/item/{id}` | Update quantity item |
| DELETE | `/api/cart/item/{id}` | Hapus item |
| GET | `/api/wishlist` | Get wishlist |
| POST | `/api/wishlist/{productId}` | Add ke wishlist |
| DELETE | `/api/wishlist/{productId}` | Hapus dari wishlist |
| GET | `/api/orders/my` | List order user |
| GET | `/api/orders/my/{id}` | Detail order |
| POST | `/api/orders/checkout` | Checkout keranjang |
| PUT | `/api/orders/my/{id}/cancel` | Cancel order |
| PUT | `/api/orders/my/{id}/confirm-delivery` | Konfirmasi terima |

### Admin Endpoints (Requires Admin Role)

| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| GET | `/api/admin/dashboard` | Dashboard statistik |
| GET | `/api/admin/dashboard/best-sellers` | Produk terlaris |
| GET | `/api/admin/dashboard/user-growth` | Pertumbuhan user |
| GET | `/api/admin/products` | List produk |
| POST | `/api/admin/products` | Tambah produk |
| PUT | `/api/admin/products/{id}` | Update produk |
| DELETE | `/api/admin/products/{id}` | Hapus produk |
| GET | `/api/admin/categories` | List kategori |
| POST | `/api/admin/categories` | Tambah kategori |
| PUT | `/api/admin/categories/{id}` | Update kategori |
| DELETE | `/api/admin/categories/{id}` | Hapus kategori |
| GET | `/api/admin/users` | List user |
| GET | `/api/admin/users/{id}` | Detail user |
| PUT | `/api/admin/users/{id}/status` | Enable/disable user |
| GET | `/api/admin/orders` | List order |
| GET | `/api/admin/orders/{id}` | Detail order |
| PUT | `/api/admin/orders/{id}/status` | Update status order |
| GET | `/api/admin/notifications` | List notifikasi |
| PUT | `/api/admin/notifications/{id}/read` | Mark as read |
| PUT | `/api/admin/notifications/read-all` | Mark all as read |

---

## 📁 Struktur Project

```
src/main/java/com/greeceri/store/
├── configs/          # Konfigurasi (Security, CORS, dll)
├── controllers/      # REST Controllers
│   ├── admin/        # Admin endpoints
│   ├── auth/         # Authentication
│   ├── cart/         # Keranjang
│   ├── order/        # Order
│   ├── product/      # Produk
│   ├── user/         # User profile
│   └── wishlist/     # Wishlist
├── models/
│   ├── entity/       # JPA Entities
│   ├── enums/        # Enumerations
│   ├── request/      # Request DTOs
│   └── response/     # Response DTOs
├── repositories/     # Spring Data Repositories
├── services/         # Business Logic
│   └── impl/         # Service Implementations
└── StoreApplication.java
```

---

## 📧 Kontak

- **Developer**: Alwan
- **Repository**: [github.com/Wannzh/greeceri-store-be](https://github.com/Wannzh/greeceri-store-be)

---

## 📄 Lisensi

Project ini dibuat untuk keperluan UAS.
