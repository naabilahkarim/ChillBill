package com.example.chillbill

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chillbill.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnMulai.setOnClickListener {
            showChoiceDialog()
        }
    }

    private fun showChoiceDialog() {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_choice, null)
        dialog.setContentView(view)

        // Option 1: Scan
        view.findViewById<android.view.View>(R.id.btnOptionScan).setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, ScanBillActivity::class.java))
        }

        // Option 2: Manual
        view.findViewById<android.view.View>(R.id.btnOptionManual).setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, MenuActivity::class.java))
        }

        dialog.show()
    }
}
