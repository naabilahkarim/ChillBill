package com.example.chillbill.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Menu(
    val id: Int,
    var nama: String,
    var harga: Int,
    var qty: Int = 1, // Quantity (e.g. 2x)
    val dipilihOleh: ArrayList<Int> = arrayListOf()
): Parcelable
