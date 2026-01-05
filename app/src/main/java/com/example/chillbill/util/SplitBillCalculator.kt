package com.example.chillbill.util

import com.example.chillbill.model.Menu
import com.example.chillbill.model.Orang

object SplitBillCalculator {

    fun hitung(
        orangList: List<Orang>,
        menuList: List<Menu>,
        pajakNominal: Int,
        serviceNominal: Int,
        diskonNominal: Int
    ): List<Orang> {

        // 1. Reset
        orangList.forEach { it.totalBayar = 0 }

        // 2. Hitung Subtotal per Orang (Total harga menu yg mereka makan)
        var subtotalGlobal = 0

        menuList.forEach { menu ->
            val jumlahOrang = menu.dipilihOleh.size
            if (jumlahOrang > 0) {
                // Gunakan floating point agar pembagian lebih akurat sebelum dibulatkan di akhir
                val hargaPerOrang = menu.harga.toDouble() / jumlahOrang.toDouble()
                
                menu.dipilihOleh.forEach { idOrang ->
                    val orang = orangList.find { it.id == idOrang }
                    if (orang != null) {
                        // Simpan sementara sebagai double (atau simpan di field totalBayar dengan pembulatan nanti)
                        // Untuk kesederhanaan, kita tambahkan pembulatan di sini atau akumulasi double
                        // Mari akumulasi secara presisi
                    }
                }
                subtotalGlobal += menu.harga
            }
        }
        
        // Re-implementing with better precision
        val subtotalPerOrang = mutableMapOf<Int, Double>()
        orangList.forEach { subtotalPerOrang[it.id] = 0.0 }
        
        menuList.forEach { menu ->
            val numPeople = menu.dipilihOleh.size
            if (numPeople > 0) {
                val share = menu.harga.toDouble() / numPeople
                menu.dipilihOleh.forEach { personId ->
                    subtotalPerOrang[personId] = (subtotalPerOrang[personId] ?: 0.0) + share
                }
            }
        }

        if (subtotalGlobal == 0) return orangList

        // 3. Total Akhir = Subtotal + Pajak + Service - Diskon
        val totalAkhir = (subtotalGlobal + pajakNominal + serviceNominal) - diskonNominal
        val finalTotal = if (totalAkhir < 0) 0 else totalAkhir

        // 4. Hitung Multiplier Proporsional (GoPay Style)
        // Jika total tagihan naik/turun karena pajak/diskon, setiap orang menanggung secara proporsional
        val multiplier = finalTotal.toDouble() / subtotalGlobal.toDouble()

        // 5. Terapkan ke setiap orang
        orangList.forEach { orang ->
            val baseShare = subtotalPerOrang[orang.id] ?: 0.0
            orang.totalBayar = Math.round(baseShare * multiplier).toInt()
        }

        return orangList
    }
}