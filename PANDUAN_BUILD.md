# ChillBill - Panduan Build Project

## Masalah: Java 25 Tidak Kompatibel

Project ini **TIDAK BISA** di-build dengan Java 25 karena Kotlin compiler belum mendukung Java 25.

## Solusi: Gunakan JDK 17

### Cara 1: Otomatis (Recommended)

Jalankan script setup:

```powershell
cd c:\chillbill\chillbill_new
.\setup-jdk17.ps1
```

Script ini akan:
1. Download JDK 17 portable (~180MB)
2. Extract ke folder `jdk-17`
3. Otomatis build project

### Cara 2: Manual

1. **Download JDK 17**:
   - Link: https://adoptium.net/temurin/releases/?version=17
   - Pilih: Windows x64, JDK, .zip

2. **Extract JDK 17**:
   - Extract file zip ke folder: `c:\chillbill\chillbill_new\jdk-17`

3. **Build Project**:
   ```cmd
   build-with-jdk17.bat clean build
   ```

### Cara 3: Install JDK 17 Globally

1. Download dan install JDK 17 dari link di atas
2. Set environment variable `JAVA_HOME` ke path JDK 17
3. Restart terminal
4. Build dengan: `./gradlew clean build`

### Cara 4: Build dari Android Studio

1. Buka Android Studio
2. Open project: `c:\chillbill\chillbill_new`
3. Klik: Build > Rebuild Project

Android Studio akan otomatis menggunakan embedded JDK yang kompatibel.

## Struktur Project Baru

```
chillbill_new/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/chillbill/  # Semua source code
│   │       └── res/                          # Resources (layouts, drawables)
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew / gradlew.bat
├── setup-jdk17.ps1          # Script download JDK 17
└── build-with-jdk17.bat     # Build dengan JDK 17

Backup project lama ada di: c:\chillbill_backup
```

## Perubahan dari Project Lama

1. ✅ **Kotlin 2.1.0** (dari 1.9.10) - Versi terbaru
2. ✅ **Android Gradle Plugin 8.7.3** (dari 8.13.2) - Lebih stabil
3. ✅ **Updated dependencies** - Semua library ke versi terbaru
4. ✅ **Gradle 8.13** - Dengan configuration cache
5. ✅ **Java 17 target** - Lebih kompatibel

## Troubleshooting

### Error: "25.0.1"
- **Penyebab**: Gradle masih menggunakan Java 25
- **Solusi**: Gunakan `build-with-jdk17.bat` atau jalankan `setup-jdk17.ps1`

### Error: "Cannot find JDK 17"
- **Solusi**: Pastikan folder `jdk-17` ada di root project
- Atau install JDK 17 secara global

### Build lambat
- **Solusi**: Gradle daemon mungkin perlu restart
- Jalankan: `./gradlew --stop` lalu build lagi

## Next Steps

Setelah berhasil build:

1. **Run di emulator/device**:
   ```cmd
   build-with-jdk17.bat installDebug
   ```

2. **Open di Android Studio**:
   - File > Open > Pilih folder `chillbill_new`
   - Sync Gradle
   - Run app

3. **Generate APK**:
   ```cmd
   build-with-jdk17.bat assembleRelease
   ```
   APK ada di: `app/build/outputs/apk/release/`
