package com.example.chillbill.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chillbill.databinding.ItemHasilBinding
import com.example.chillbill.model.Orang

class HasilAdapter(private val listOrang: List<Orang>) :
    RecyclerView.Adapter<HasilAdapter.HasilViewHolder>() {

    inner class HasilViewHolder(val b: ItemHasilBinding) :
        RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HasilViewHolder {
        val binding = ItemHasilBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HasilViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HasilViewHolder, position: Int) {
        val orang = listOrang[position]
        holder.b.tvNama.text = orang.nama
        holder.b.tvJumlah.text = "Rp ${orang.totalBayar}" // diganti dari jumlah ke totalBayar
    }

    override fun getItemCount(): Int = listOrang.size
}

