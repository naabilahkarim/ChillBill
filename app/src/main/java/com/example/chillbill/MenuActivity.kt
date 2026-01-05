package com.example.chillbill

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chillbill.adapter.MenuAdapter
import com.example.chillbill.databinding.ActivityMenuBinding
import com.example.chillbill.model.Menu
import java.text.NumberFormat

class MenuActivity : AppCompatActivity() {

    private lateinit var b: ActivityMenuBinding
    private val menuList = mutableListOf<Menu>()
    private lateinit var adapter: MenuAdapter

    // ===== FIX FLAG SCAN BILL =====
    private var dariScanBill = false
    private var subtotalScan = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(b.root)

        // ===== Ambil menu hasil OCR =====
        @Suppress("DEPRECATION")
        val dataDariScan =
            androidx.core.content.IntentCompat.getParcelableArrayListExtra(
                intent,
                "menu",
                Menu::class.java
            )

        if (dataDariScan != null) {
            menuList.addAll(dataDariScan)
        }

        setupRecyclerView()
        setupCalculationListeners()

        // ===== AMBIL DATA PAYMENT DARI SCAN BILL =====
        subtotalScan = intent.getIntExtra("subtotal", 0)
        val pajakScan = intent.getIntExtra("pajak", 0)
        val serviceScan = intent.getIntExtra("service", 0)
        val diskonScan = intent.getIntExtra("diskon", 0)
        val totalScan = intent.getIntExtra("total", 0)

        if (subtotalScan > 0) {
            dariScanBill = true

            b.tvSubtotal.text =
                "Rp${NumberFormat.getIntegerInstance().format(subtotalScan)}"

            if (pajakScan > 0) b.edtPajak.setText(pajakScan.toString())
            if (serviceScan > 0) b.edtService.setText(serviceScan.toString())
            if (diskonScan > 0) b.edtDiskon.setText(diskonScan.toString())
            if (totalScan > 0) {
                b.tvTotalAkhir.text =
                    "Rp${NumberFormat.getIntegerInstance().format(totalScan)}"
            }
        } else {
            calculateTotal()
        }

        setupButtons()

        // ===== Foto Ulang =====
        b.btnFotoUlang.setOnClickListener {
            finish()
        }

        // ===== Back =====
        b.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        b.rvMenu.layoutManager = LinearLayoutManager(this)
        b.rvMenu.isNestedScrollingEnabled = false
        adapter = MenuAdapter(menuList) { menu, position ->
            showFormDialog(menu, position)
        }
        b.rvMenu.adapter = adapter
    }

    private fun setupCalculationListeners() {
        val watcher = object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                calculateTotal()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        b.edtPajak.addTextChangedListener(watcher)
        b.edtService.addTextChangedListener(watcher)
        b.edtDiskon.addTextChangedListener(watcher)
    }

    // ===== FIX TOTAL LOGIC (TIDAK TIMPA HASIL SCAN) =====
    private fun calculateTotal() {

        val subtotal = if (dariScanBill) {
            subtotalScan
        } else {
            menuList.sumOf { it.harga * it.qty }
        }

        b.tvSubtotal.text =
            "Rp${NumberFormat.getIntegerInstance().format(subtotal)}"

        val pajak = b.edtPajak.text.toString().toIntOrNull() ?: 0
        val service = b.edtService.text.toString().toIntOrNull() ?: 0
        val diskon = b.edtDiskon.text.toString().toIntOrNull() ?: 0

        val total = (subtotal + pajak + service - diskon).coerceAtLeast(0)

        b.tvTotalAkhir.text =
            "Rp${NumberFormat.getIntegerInstance().format(total)}"
    }

    private fun setupButtons() {

        b.btnLanjut.setOnClickListener {
            if (menuList.isEmpty()) {
                android.widget.Toast.makeText(
                    this,
                    "Tambahkan menu minimal satu dulu!",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val explodedList = ArrayList<Menu>()
            var newId = 1

            menuList.forEach { menu ->
                if (menu.qty > 50) menu.qty = 50

                if (menu.qty > 1) {
                    repeat(menu.qty) {
                        explodedList.add(
                            Menu(
                                newId++,
                                "${menu.nama} (${it + 1})",
                                menu.harga,
                                1
                            )
                        )
                    }
                } else {
                    explodedList.add(
                        Menu(
                            newId++,
                            menu.nama,
                            menu.harga,
                            1
                        )
                    )
                }
            }

            val intent = Intent(this, PilihMenuActivity::class.java)
            intent.putParcelableArrayListExtra("menu", explodedList)

            intent.putExtra("pajak", b.edtPajak.text.toString().toIntOrNull() ?: 0)
            intent.putExtra("service", b.edtService.text.toString().toIntOrNull() ?: 0)
            intent.putExtra("diskon", b.edtDiskon.text.toString().toIntOrNull() ?: 0)

            startActivity(intent)
        }

        b.btnTambahManual.setOnClickListener {
            showFormDialog(null, -1)
        }
    }

    fun showFormDialog(menu: Menu?, position: Int) {
        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this)

        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val inputNama = android.widget.EditText(this)
        inputNama.hint = "Nama Menu"
        inputNama.setText(menu?.nama ?: "")
        layout.addView(inputNama)

        val inputHarga = android.widget.EditText(this)
        inputHarga.hint = "Harga Satuan"
        inputHarga.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        inputHarga.setText(menu?.harga?.toString() ?: "")
        layout.addView(inputHarga)

        val inputQty = android.widget.EditText(this)
        inputQty.hint = "Jumlah (Qty)"
        inputQty.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        inputQty.setText(menu?.qty?.toString() ?: "1")
        layout.addView(inputQty)

        dialogBuilder.setView(layout)
        dialogBuilder.setTitle(if (menu == null) "Tambah Menu" else "Edit Menu")

        dialogBuilder.setPositiveButton("Simpan") { _, _ ->
            val nama = inputNama.text.toString()
            val harga = inputHarga.text.toString().toIntOrNull() ?: 0
            val qty = inputQty.text.toString().toIntOrNull() ?: 1

            if (nama.isNotEmpty()) {
                if (menu == null) {
                    val newId =
                        if (menuList.isEmpty()) 1 else menuList.maxOf { it.id } + 1
                    menuList.add(Menu(newId, nama, harga, qty))
                    adapter.notifyItemInserted(menuList.size - 1)
                } else {
                    menu.nama = nama
                    menu.harga = harga
                    menu.qty = qty
                    adapter.notifyItemChanged(position)
                }
                dariScanBill = false
                calculateTotal()
            }
        }

        if (menu != null) {
            dialogBuilder.setNegativeButton("Hapus") { _, _ ->
                menuList.removeAt(position)
                adapter.notifyItemRemoved(position)
                calculateTotal()
            }
        } else {
            dialogBuilder.setNegativeButton("Batal", null)
        }

        dialogBuilder.show()
    }
}
