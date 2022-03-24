package com.ezteam.applocker.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ezteam.applocker.databinding.LayoutImageHideBinding
import com.ezteam.applocker.item.ItemImageHide
import com.ezteam.baseproject.adapter.BaseRecyclerAdapter

class AdapterImageHide(var context: Context, list: MutableList<ItemImageHide>) :
    BaseRecyclerAdapter<ItemImageHide, AdapterImageHide.ViewHolder>(context, list) {
    var listenerOnClick: ((Int) -> Unit)? = null

    inner class ViewHolder(var binding: LayoutImageHideBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: ItemImageHide) {
            data.bitmap?.let {
                Glide.with(context).load(it).into(binding.imgHide)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position])
        holder.itemView.setOnClickListener {
            listenerOnClick?.invoke(holder.adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutImageHideBinding.inflate(layoutInflater, parent, false))
    }

}