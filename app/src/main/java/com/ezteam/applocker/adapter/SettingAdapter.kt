package com.ezteam.applocker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ezteam.applocker.databinding.LayoutItemSettingBinding
import com.ezteam.applocker.item.ItemSetting

class SettingAdapter(val context: Context, val list: MutableList<ItemSetting>) :
    RecyclerView.Adapter<SettingAdapter.ViewHolder>() {
    var listenerClick: ((Int) -> Unit)? = null

    class ViewHolder(val binding: LayoutItemSettingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutItemSettingBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.binding.imgIc.setImageResource(data.resId)
        holder.binding.name.text = data.name
        if (data.content == null) {
            holder.binding.content.visibility = View.GONE
        } else {
            holder.binding.content.text = data.content
        }
        // click
        holder.itemView.setOnClickListener {
            listenerClick?.invoke(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}