package com.ezteam.applocker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ezteam.applocker.databinding.LayoutItemVideoBinding
import com.ezteam.applocker.item.ItemVideo
import com.ezteam.applocker.utils.AppUtil
import com.ezteam.baseproject.adapter.BaseRecyclerAdapter

class AdapterVideo(var context: Context, list: MutableList<ItemVideo>) :
    BaseRecyclerAdapter<ItemVideo, AdapterVideo.ViewHolder>(context, list) {
    var listenerOnClick: ((Int) -> Unit)? = null

    inner class ViewHolder(var binding: LayoutItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bindData(data: ItemVideo) {
            Glide.with(context)
                .load(data.file.absolutePath)
                .into(binding.video)
            binding.txtDuration.text = AppUtil.convertDuration(data.duration)
            //
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
        return ViewHolder(LayoutItemVideoBinding.inflate(layoutInflater, parent, false))
    }
}