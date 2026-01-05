package com.example.chillbill.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chillbill.databinding.ItemOrangBinding
import com.example.chillbill.model.Menu
import com.example.chillbill.model.Orang

class OrangAdapter(
    private val orangList: List<Orang>,
    private val menuList: List<Menu>
) : RecyclerView.Adapter<OrangAdapter.ViewHolder>() {

    inner class ViewHolder(val b: ItemOrangBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(orang: Orang) {
            b.tvNamaOrang.text = orang.nama

            // RecyclerView untuk menu per orang
            b.rvMenuOrang.layoutManager = LinearLayoutManager(b.root.context)
            b.rvMenuOrang.adapter = MenuOrangAdapter(menuList, orang.id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orangList[position])
    }

    override fun getItemCount(): Int = orangList.size
}
