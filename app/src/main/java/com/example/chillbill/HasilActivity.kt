package com.example.chillbill

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chillbill.adapter.HasilAdapter
import com.example.chillbill.databinding.ActivityHasilBinding
import com.example.chillbill.model.Orang

class HasilActivity : AppCompatActivity() {

    private lateinit var b: ActivityHasilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityHasilBinding.inflate(layoutInflater)
        setContentView(b.root)

        @Suppress("DEPRECATION")
        val hasil = try {
             androidx.core.content.IntentCompat.getParcelableArrayListExtra(intent, "hasil", Orang::class.java)
        } catch (e: Exception) {
            null
        }
        
        if (hasil == null) {
            android.widget.Toast.makeText(this, "Data hasil tidak ditemukan", android.widget.Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        b.rvHasil.layoutManager = LinearLayoutManager(this)
        b.rvHasil.adapter = HasilAdapter(hasil)

        // Tombol Selesai (Kembali ke Awal)
        b.btnSelesai.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        // Tombol Back
        b.btnBack.setOnClickListener {
            finish()
        }
    }
}
