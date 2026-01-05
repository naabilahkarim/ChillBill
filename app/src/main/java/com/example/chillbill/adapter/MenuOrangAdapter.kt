package com.example.chillbill.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView

import com.example.chillbill.model.Menu

class MenuOrangAdapter(
    private val menuList: List<Menu>,
    private val orangId: Int
) : RecyclerView.Adapter<MenuOrangAdapter.ViewHolder>() {

    inner class ViewHolder(val b: com.example.chillbill.databinding.ItemMenuAssignBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(menu: Menu) {
            // Set nama menu dan harga
            b.tvNama.text = menu.nama
            b.tvHarga.text = "Rp ${menu.harga}"

            // Reset listener sebelum set checked
            b.cbMenu.setOnCheckedChangeListener(null)
            b.cbMenu.isChecked = menu.dipilihOleh.contains(orangId)

            // Listener checkbox
            b.cbMenu.setOnCheckedChangeListener { _: CompoundButton, isChecked ->
                if (isChecked) {
                    if (!menu.dipilihOleh.contains(orangId)) {
                        menu.dipilihOleh.add(orangId)
                    }
                } else {
                    menu.dipilihOleh.remove(orangId)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = com.example.chillbill.databinding.ItemMenuAssignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(menuList[position])
    }

    override fun getItemCount(): Int = menuList.size
}
