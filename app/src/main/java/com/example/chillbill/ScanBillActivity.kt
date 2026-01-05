package com.example.chillbill

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chillbill.databinding.ActivityScanBillBinding
import com.example.chillbill.model.Menu
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs

class ScanBillActivity : AppCompatActivity() {

    private lateinit var b: ActivityScanBillBinding
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var preview: Preview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityScanBillBinding.inflate(layoutInflater)
        setContentView(b.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                100
            )
        }

        b.btnCapture.setOnClickListener {
            b.btnCapture.isEnabled = false
            b.btnCapture.text = "Memproses..."
            ambilFoto()
        }
    }

    // ================= CAMERA =================

    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            cameraProvider = providerFuture.get()

            preview = Preview.Builder().build().also {
                it.setSurfaceProvider(b.previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun stopCamera() {
        if (::cameraProvider.isInitialized && ::preview.isInitialized) {
            cameraProvider.unbind(preview)
        }
    }

    private fun ambilFoto() {
        val file = File(cacheDir, "bill.jpg")
        val output = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            output,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    stopCamera()
                    prosesOCR(file)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@ScanBillActivity, "Gagal ambil foto", Toast.LENGTH_SHORT).show()
                    resetButton()
                }
            }
        )
    }

    // ================= OCR =================

    data class TextObj(val text: String, val rect: Rect)

    private fun prosesOCR(file: File) {
        val image = InputImage.fromFilePath(this, android.net.Uri.fromFile(file))
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { result ->

                val objects = result.textBlocks
                    .flatMap { it.lines }
                    .mapNotNull { line ->
                        line.boundingBox?.let { TextObj(line.text, it) }
                    }
                    .sortedBy { it.rect.top }

                if (objects.isEmpty()) {
                    Toast.makeText(this, "Teks tidak ditemukan", Toast.LENGTH_LONG).show()
                    resetButton()
                    return@addOnSuccessListener
                }

                // ===== GROUP ROW =====
                val rows = mutableListOf<MutableList<TextObj>>()
                for (obj in objects) {
                    val row = rows.firstOrNull {
                        abs(it.first().rect.centerY() - obj.rect.centerY()) < 25
                    }
                    if (row != null) row.add(obj) else rows.add(mutableListOf(obj))
                }

                // ===== DATA =====
                val menuList = mutableListOf<Menu>()

                var subtotal = 0
                var pajak = 0
                var service = 0
                var diskon = 0
                var total = 0

                var subtotalFromOCR = false
                var totalFromOCR = false
                var hargaInclude = false
                var confidence = 100

                var masukZonaPayment = false

                // ===== LOOP ROW =====
                for ((index, row) in rows.withIndex()) {
                    row.sortBy { it.rect.left }
                    val rowText = row.joinToString(" ") { it.text }
                    val rowTextLower = normalize(rowText)

                    // detect include tax
                    if (
                        rowTextLower.contains("include") ||
                        rowTextLower.contains("included") ||
                        rowTextLower.contains("nett") ||
                        rowTextLower.contains("termasuk")
                    ) {
                        hargaInclude = true
                    }

                    val isPaymentLine =
                        rowTextLower.contains("subtotal") ||
                                rowTextLower.contains("total") ||
                                rowTextLower.contains("tax") ||
                                rowTextLower.contains("pb1") ||
                                rowTextLower.contains("pajak") ||
                                rowTextLower.contains("service") ||
                                rowTextLower.contains("disc")

                    if (isPaymentLine) masukZonaPayment = true

                    // ===== PAYMENT =====
                    if (masukZonaPayment) {
                        var harga = ambilHargaDariRow(row)

                        if (harga == null && index + 1 < rows.size) {
                            harga = ambilHargaDariRow(rows[index + 1])
                        }

                        if (harga != null) {
                            when {
                                rowTextLower.contains("subtotal") -> {
                                    subtotal = harga
                                    subtotalFromOCR = true
                                }

                                rowTextLower.contains("disc") -> {
                                    diskon = harga
                                }

                                rowTextLower.contains("service") -> {
                                    service = harga
                                }

                                rowTextLower.contains("pb1") ||
                                        rowTextLower.contains("tax") ||
                                        rowTextLower.contains("pajak") -> {
                                    pajak = harga
                                }

                                rowTextLower.contains("total") &&
                                        !rowTextLower.contains("subtotal") -> {
                                    total = harga
                                    totalFromOCR = true
                                }
                            }
                        }
                        continue
                    }

                    // ===== MENU =====
                    val hargaMenu = ambilHargaDariRow(row)
                    if (hargaMenu != null && hargaMenu >= 1000) {
                        val nama = rowText
                            .replace(Regex("[0-9., ]+"), "")
                            .replace(Regex("(?i)rp"), "")
                            .trim()

                        if (nama.isNotEmpty()) {
                            menuList.add(
                                Menu(
                                    id = menuList.size + 1,
                                    nama = nama,
                                    harga = hargaMenu,
                                    qty = 1
                                )
                            )
                        }
                    }
                }

                // ===== FINAL VALIDATION =====
                if (!subtotalFromOCR) {
                    subtotal = menuList.sumOf { it.harga * it.qty }
                    confidence -= 20
                }

                diskon = abs(diskon)

                val totalHitung = if (hargaInclude) {
                    subtotal - diskon
                } else {
                    subtotal + pajak + service - diskon
                }

                if (!totalFromOCR || abs(total - totalHitung) > 500) {
                    total = totalHitung
                    confidence -= 20
                }

                confidence = confidence.coerceAtLeast(40)

                // ===== SEND RESULT =====
                val intent = Intent(this, MenuActivity::class.java)
                intent.putParcelableArrayListExtra("menu", ArrayList(menuList))
                intent.putExtra("subtotal", subtotal)
                intent.putExtra("pajak", pajak)
                intent.putExtra("service", service)
                intent.putExtra("diskon", diskon)
                intent.putExtra("total", total)
                intent.putExtra("confidence", confidence)
                intent.putExtra("hargaInclude", hargaInclude)
                intent.putExtra("imagePath", file.absolutePath)

                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "OCR gagal", Toast.LENGTH_LONG).show()
                resetButton()
            }
    }

    // ================= HELPER =================

    private fun ambilHargaDariRow(row: List<TextObj>): Int? {
        return row.mapNotNull { extractHarga(it.text) }.maxOrNull()
    }

    private fun extractHarga(text: String): Int? {
        val regex = Regex("[0-9]+([., ]?[0-9]{3})+")
        val match = regex.find(text) ?: return null
        return match.value.replace(Regex("[., ]"), "").toIntOrNull()
    }

    private fun normalize(text: String): String {
        return text.lowercase()
            .replace("1", "i")
            .replace("|", "i")
            .replace("0", "o")
    }

    private fun resetButton() {
        b.btnCapture.text = "Scan Bill"
        b.btnCapture.isEnabled = true
        startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
