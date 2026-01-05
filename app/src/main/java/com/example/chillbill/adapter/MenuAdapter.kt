package com.example.chillbill.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chillbill.R
import com.example.chillbill.model.Menu

class MenuAdapter(
    private val menuList: List<Menu>,
    private val onItemClick: (Menu, Int) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(val b: com.example.chillbill.databinding.ItemMenuEditableBinding) : 
        RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = com.example.chillbill.databinding.ItemMenuEditableBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = menuList[position]
        holder.b.tvNama.text = menu.nama
        holder.b.tvHarga.text = "Rp${menu.harga}"
        
        holder.b.root.setOnClickListener {
            onItemClick(menu, position)
        }
    }

    override fun getItemCount(): Int = menuList.size
}
