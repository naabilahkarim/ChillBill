# Script untuk download dan setup JDK 17 portable
# Ini akan mengatasi masalah Java 25 compatibility

$jdk17Url = "https://download.oracle.com/java/17/archive/jdk-17.0.12_windows-x64_bin.zip"
$jdk17Path = "$PSScriptRoot\jdk-17"
$jdk17Zip = "$PSScriptRoot\jdk-17.zip"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "ChillBill - JDK 17 Setup Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if JDK 17 already exists
if (Test-Path $jdk17Path) {
    Write-Host "JDK 17 sudah terinstall di: $jdk17Path" -ForegroundColor Green
    Write-Host ""
    Write-Host "Menggunakan JDK 17 untuk build..." -ForegroundColor Yellow
    
    # Set JAVA_HOME untuk session ini
    $env:JAVA_HOME = $jdk17Path
    $env:PATH = "$jdk17Path\bin;$env:PATH"
    
    # Verify
    Write-Host ""
    Write-Host "Java Version:" -ForegroundColor Cyan
    & "$jdk17Path\bin\java.exe" -version
    
    Write-Host ""
    Write-Host "Menjalankan Gradle build..." -ForegroundColor Yellow
    & "$PSScriptRoot\gradlew.bat" clean build
    
} else {
    Write-Host "JDK 17 belum terinstall." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "OPSI 1: Download JDK 17 Otomatis (Memerlukan koneksi internet)" -ForegroundColor Cyan
    Write-Host "OPSI 2: Install JDK 17 Manual" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Untuk opsi 1, script ini akan download JDK 17 portable (~180MB)" -ForegroundColor Yellow
    Write-Host "Untuk opsi 2, download dari: https://adoptium.net/temurin/releases/?version=17" -ForegroundColor Yellow
    Write-Host ""
    
    $choice = Read-Host "Pilih opsi (1/2)"
    
    if ($choice -eq "1") {
        Write-Host ""
        Write-Host "Downloading JDK 17... (ini mungkin memakan waktu beberapa menit)" -ForegroundColor Yellow
        
        try {
            # Download JDK 17 from Adoptium
            $adoptiumUrl = "https://api.adoptium.net/v3/binary/latest/17/ga/windows/x64/jdk/hotspot/normal/eclipse"
            Invoke-WebRequest -Uri $adoptiumUrl -OutFile $jdk17Zip -UseBasicParsing
            
            Write-Host "Download selesai! Extracting..." -ForegroundColor Green
            Expand-Archive -Path $jdk17Zip -DestinationPath "$PSScriptRoot\jdk-temp" -Force
            
            # Find the JDK folder (it might be nested)
            $jdkFolder = Get-ChildItem "$PSScriptRoot\jdk-temp" -Directory | Select-Object -First 1
            Move-Item -Path $jdkFolder.FullName -Destination $jdk17Path -Force
            
            # Cleanup
            Remove-Item "$PSScriptRoot\jdk-temp" -Recurse -Force
            Remove-Item $jdk17Zip -Force
            
            Write-Host "JDK 17 berhasil diinstall!" -ForegroundColor Green
            Write-Host ""
            Write-Host "Menjalankan build dengan JDK 17..." -ForegroundColor Yellow
            
            # Set JAVA_HOME dan build
            $env:JAVA_HOME = $jdk17Path
            $env:PATH = "$jdk17Path\bin;$env:PATH"
            
            & "$PSScriptRoot\gradlew.bat" clean build
            
        } catch {
            Write-Host "Error saat download JDK 17: $_" -ForegroundColor Red
            Write-Host ""
            Write-Host "Silakan download manual dari: https://adoptium.net/temurin/releases/?version=17" -ForegroundColor Yellow
            Write-Host "Extract ke folder: $jdk17Path" -ForegroundColor Yellow
            Write-Host "Lalu jalankan script ini lagi." -ForegroundColor Yellow
        }
        
    } else {
        Write-Host ""
        Write-Host "Silakan:" -ForegroundColor Yellow
        Write-Host "1. Download JDK 17 dari: https://adoptium.net/temurin/releases/?version=17" -ForegroundColor White
        Write-Host "2. Extract ke folder: $jdk17Path" -ForegroundColor White
        Write-Host "3. Jalankan script ini lagi" -ForegroundColor White
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Script selesai" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
