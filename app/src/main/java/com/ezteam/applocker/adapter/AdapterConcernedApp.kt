package com.ezteam.applocker.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ezteam.applocker.databinding.LayoutItemConcernedAppBinding
import com.ezteam.applocker.item.ItemAppLock
import com.ezteam.baseproject.adapter.BaseRecyclerAdapter

class AdapterConcernedApp(val context: Context, list: MutableList<ItemAppLock>) :
    BaseRecyclerAdapter<ItemAppLock, AdapterConcernedApp.ViewHolder>(context, list) {
    var onClickListener: ((Int) -> Unit)? = null

    inner class ViewHolder(var binding: LayoutItemConcernedAppBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: ItemAppLock) {
            Glide.with(context).load(data.resId).into(binding.icApp)
            binding.txtNameApp.text = data.name
            binding.cbLocked.isChecked = data.isLocked
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position])
        holder.itemView.setOnClickListener {
            onClickListener?.invoke(holder.adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutItemConcernedAppBinding.inflate(layoutInflater, parent, false))
    }
}