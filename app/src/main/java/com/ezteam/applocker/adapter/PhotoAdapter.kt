package com.ezteam.applocker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ezteam.applocker.R
import com.ezteam.applocker.databinding.LayoutItemPhotoBinding
import com.ezteam.applocker.item.PhotoImage

class PhotoAdapter(val context: Context, val list: MutableList<PhotoImage>) :
    RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {
    var onClickListener: ((Int) -> Unit)? = null

    class ViewHolder(val binding: LayoutItemPhotoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutItemPhotoBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        val stringTime = data.time.split(" ")
        val day = stringTime[0]
        val time = stringTime[1]
        holder.binding.day.text = day
        holder.binding.time.text = time
        data.bitmap?.let {
            val paddingSize = context.resources.getDimensionPixelSize(R.dimen._30sdp)
            val size = context.resources.displayMetrics.widthPixels
            val width = size - paddingSize
            Glide
                .with(context)
                .asBitmap()
                .load(it)
                .override(width, width - paddingSize * 2)
                .centerCrop()
                .into(holder.binding.imgPhoto)
        }
        holder.itemView.setOnClickListener {
            onClickListener?.invoke(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}