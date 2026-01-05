package com.example.chillbill

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chillbill.adapter.OrangAdapter
import com.example.chillbill.databinding.ActivityPilihMenuBinding
import com.example.chillbill.model.Menu
import com.example.chillbill.model.Orang
import com.example.chillbill.util.SplitBillCalculator

class PilihMenuActivity : AppCompatActivity() {

    private lateinit var b: ActivityPilihMenuBinding
    private val orangList = mutableListOf<Orang>()
    private lateinit var menuList: ArrayList<Menu>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPilihMenuBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Ambil list menu dari intent dengan pengamanan
        @Suppress("DEPRECATION")
        val dataMenu = try {
            androidx.core.content.IntentCompat.getParcelableArrayListExtra(intent, "menu", Menu::class.java)
        } catch (e: Exception) {
            null
        }
        menuList = dataMenu ?: arrayListOf()

        if (menuList.isEmpty()) {
            android.util.Log.e("PilihMenuActivity", "MenuList is empty! Redirecting back.")
            android.widget.Toast.makeText(this, "Data menu tidak ditemukan", android.widget.Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Setup RecyclerView orang
        val adapter = OrangAdapter(orangList, menuList)
        b.rvOrang.layoutManager = LinearLayoutManager(this)
        b.rvOrang.adapter = adapter

        // Tombol tambah orang
        b.btnTambahOrang.setOnClickListener {
            val input = EditText(this)
            AlertDialog.Builder(this)
                .setTitle("Nama Orang")
                .setView(input)
                .setPositiveButton("Tambah") { _, _ ->
                    val nama = input.text.toString().trim()
                    if (nama.isNotEmpty()) {
                        orangList.add(Orang(orangList.size + 1, nama))
                        adapter.notifyDataSetChanged()
                    }
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        // Tombol hitung (lanjut ke HasilActivity dengan perhitungan)
        b.btnHitung.setOnClickListener {

            // Validasi: ada orang
            if (orangList.isEmpty()) {
                Toast.makeText(this, "Tambah orang dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi: minimal satu menu dipilih
            val adaMenuDipilih = menuList.any { it.dipilihOleh.isNotEmpty() }
            if (!adaMenuDipilih) {
                Toast.makeText(this, "Pilih menu minimal satu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ambil nilai pajak, service, dan diskon dari Intent (yang dikirim MenuActivity)
            val pajak = intent.getIntExtra("pajak", 0)
            val service = intent.getIntExtra("service", 0)
            val diskon = intent.getIntExtra("diskon", 0)

            // Hitung pembagian tagihan (GoPay Logic)
            val hasil = SplitBillCalculator.hitung(
                orangList,
                menuList,
                pajak,
                service,
                diskon
            )

            // Kirim hasil ke HasilActivity
            val intent = Intent(this, HasilActivity::class.java)
            intent.putParcelableArrayListExtra("hasil", ArrayList(hasil))
            startActivity(intent)
        }

        // Tombol Back
        b.btnBack.setOnClickListener {
            finish()
        }
    }
}