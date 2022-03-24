package com.ezteam.applocker.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ezteam.applocker.databinding.LayoutItemRestoreImageBinding
import com.ezteam.applocker.item.ItemImageRestore
import com.ezteam.baseproject.adapter.BaseRecyclerAdapter

class AdapterDetailAlbumRestoreImage(var context: Context, list: MutableList<ItemImageRestore>) :
    BaseRecyclerAdapter<ItemImageRestore, AdapterDetailAlbumRestoreImage.ViewHolder>(
        context,
        list
    ) {
    var listenerOnClick: ((Int) -> Unit)? = null

    inner class ViewHolder(var binding: LayoutItemRestoreImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: ItemImageRestore) {
            Glide.with(context).load(data.path).into(binding.img)
            if (data.isSelected) {
                if (!binding.layoutCheck.isShown) {
                    binding.layoutCheck.visibility = View.VISIBLE
                }
                binding.txtNumber.text = data.number.toString()
            } else {
                binding.layoutCheck.visibility = View.GONE
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
        return ViewHolder(
            LayoutItemRestoreImageBinding.inflate(
                layoutInflater,
                parent,
                false
            )
        )
    }
}