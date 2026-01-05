package com.example.chillbill.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Orang(
    val id: Int,
    var nama: String,
    val menuDipilih: ArrayList<Int> = arrayListOf(), // id menu yang dipilih
    var totalBayar: Int = 0
): Parcelable
