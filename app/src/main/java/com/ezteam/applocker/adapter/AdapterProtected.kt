package com.ezteam.applocker.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ezteam.applocker.databinding.LayoutItemProtectedBinding
import com.ezteam.applocker.item.ItemAppLock
import com.ezteam.baseproject.adapter.BaseRecyclerAdapter

class AdapterProtected(var context: Context, list: MutableList<ItemAppLock>) :
    BaseRecyclerAdapter<ItemAppLock, AdapterProtected.ViewHolder>(context, list) {

    inner class ViewHolder(var binding: LayoutItemProtectedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: ItemAppLock) {
            data.resId?.let {
                Glide.with(context).load(it.toBitmap()).into(binding.imgIc)
            }

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutItemProtectedBinding.inflate(layoutInflater, parent, false))
    }
}