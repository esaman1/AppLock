package com.ezteam.applocker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ezteam.applocker.databinding.LayoutItemDialogSelectedLockBinding
import com.ezteam.applocker.item.ItemSelectLock

class SelectLockAdapter(val context: Context, var list: MutableList<ItemSelectLock>) :
    RecyclerView.Adapter<SelectLockAdapter.ViewHolder>() {
    var onClickListener: ((Int) -> Unit)? = null

    class ViewHolder(var binding: LayoutItemDialogSelectedLockBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutItemDialogSelectedLockBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.binding.name.text = data.name
        holder.binding.radio.isChecked = data.isSelected
        holder.itemView.setOnClickListener {
            onClickListener?.invoke(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}