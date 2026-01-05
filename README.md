# ChillBill - Split Bill Android App

Aplikasi Android untuk membagi tagihan dengan mudah menggunakan OCR untuk scan bill.

# Masalah yang Ingin Diselesaikan
Saat berkumpul dan membayar tagihan bersama, pengguna sering menghadapi tantangan berikut:
1. Mekanisme pembagian biaya yang manual dan memakan waktu.
2. Perbedaan porsi konsumsi di antara anggota yang membuat pembagian sama rata tidak adil.
3. Kesalahan perhitungan karena input manual.
4. Kesulitan menghitung komponen tambahan seperti pajak, layanan, dan tips.
5. Perlu mengirimkan hasil pembagian kepada semua anggota.
# Solusi yang Diberikan Chill Bill
Chill Bill menawarkan solusi untuk menyelesaikan masalah di atas melalui fitur-fitur berikut:
# 1. Pemindaian Struk
Pengguna dapat memindai struk atau nota pembayaran secara langsung. Aplikasi akan mengenali teks dan harga setiap item dari struk yang di-scan.
# Keunggulan
a. Mengurangi kebutuhan input manual.
b. Mempercepat proses pengambilan data tagihan.
# 2. Perhitungan Pembagian Otomatis
Setelah data struk dikenali, aplikasi melakukan pembagian tagihan berdasarkan aturan yang ditentukan, seperti:
a. Pembagian rata untuk semua anggota.
b. Pembagian berdasarkan item yang dipesan masing-masing.
c. Pembagian dengan komponen pajak, layanan, atau tips.
# Keunggulan
Hasil pembagian menjadi adil dan transparan.
# 3. Penanganan Biaya Tambahan
Aplikasi menghitung komponen lain seperti pajak, biaya layanan, dan tips, kemudian memasukkannya ke dalam jumlah total pembagian.
# Keunggulan
a. Total biaya rill tampil secara otomatis dan akurat.
b. Tidak perlu perhitungan manual tambahan.
# 4. Bagikan Hasil Pembagian
Hasil perhitungan dapat dibagikan kepada semua anggota melalui tautan atau pesan, sehingga setiap orang dapat melihat jumlah yang harus dibayar.
# Keunggulan:
a. Mempermudah komunikasi hasil perhitungan.
b. Tidak perlu screenshot dan kirim satu per satu.
# 5. Riwayat Transaksi
Setiap pembagian dapat disimpan sebagai riwayat sehingga pengguna dapat melihat kembali detail transaksi sebelumnya.
# Keunggulan:
a. Membantu pencatatan pengeluaran pribadi atau kelompok.
b. Memudahkan pengecekan bila diperlukan di kemudian hari.

# Target Pengguna
Teman nongkrong, keluarga, rekan kerja, atau siapa saja yang sering melakukan aktivitas bersama dan ingin membagi biaya secara mudah.
# Masalah yang Diselesaikan:
Mempercepat dan mempermudah proses pembagian tagihan tanpa perhitungan manual dan kesalahan.
# Solusi Utama:
Pemindaian struk → Perhitungan otomatis → Pembagian hasil → Pengiriman kepada anggota.

## Fitur
- Scan bill menggunakan kamera
- OCR untuk mendeteksi menu dan harga
- Pembagian tagihan otomatis per orang
- Support pajak, service, dan diskon

## Tech Stack
- Kotlin
- Android SDK 36
- CameraX
- ML Kit Text Recognition
- ViewBinding

## Build Requirements
- JDK 17 atau 21 (Java 25 tidak didukung oleh Kotlin)
- Android Studio Ladybug atau lebih baru
- Gradle 8.13.2

## Setup
1. Install JDK 17
2. Clone repository
3. Open di Android Studio
4. Sync Gradle
5. Run app
