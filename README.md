# KasirProbe

KasirProbe adalah aplikasi **Point of Sale (POS)** berbasis **Android** yang dibuat menggunakan **Kotlin** dan **Firebase**.  
Aplikasi ini dirancang untuk membantu pencatatan penjualan, pengelolaan produk, dan perhitungan laporan secara sederhana.

---

## ✨ Fitur Utama
- 🔐 **Login & Autentikasi** dengan Firebase Authentication
- 🏠 **Home Dashboard** untuk ringkasan transaksi
- 📦 **Manajemen Produk**
    - Tambah produk
    - Edit & hapus produk
    - List produk dengan detail harga, stok, dan kategori
- 💰 **Transaksi Penjualan** (Point of Sale)
- 📊 **Laporan sederhana** (pendapatan, stok, modal)
- ☁️ **Database Realtime** dengan Firebase Firestore

---

## 🛠️ Teknologi
- **Bahasa:** Kotlin
- **Framework UI:** Jetpack Compose
- **Database & Backend:** Firebase Firestore
- **Authentication:** Firebase Auth
- **Library:** Android Jetpack (Room, ViewModel, LiveData, dll.)

---

## 📂 Struktur Project (ringkas)


- KasirProbe/
- ├── app/ # Main app module
- │ └── src/ # Source code (Kotlin, XML, dll)
- ├── build.gradle.kts # Gradle config
- ├── settings.gradle.kts # Gradle settings
- └── .gitignore # Git ignore rules

---

## 🚀 Cara Menjalankan Project
1. Clone repo ini:
   ```bash
   git clone https://github.com/PRO3B/KasirProbe.git
   
1. Buka project di Android Studio (Arctic Fox atau lebih baru).
2. Sync Gradle dan tunggu dependensi selesai diunduh.
3. Pastikan file google-services.json sudah ada di folder app/ (untuk Firebase).
4. Jalankan aplikasi di emulator atau device Android.

📌 Catatan

Pastikan koneksi internet aktif karena aplikasi menggunakan Firebase.
File sensitif seperti google-services.json tidak disertakan di repo (silakan tambahkan sendiri untuk menjalankan aplikasi).

📜 Lisensi

Project ini dibuat untuk tujuan pembelajaran.
Silakan modifikasi, gunakan, dan kembangkan sesuai kebutuhan.
