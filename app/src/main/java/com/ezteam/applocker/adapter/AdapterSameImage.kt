package com.ezteam.applocker.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ezteam.applocker.databinding.LayoutItemSameImageBinding
import com.ezteam.applocker.item.ItemSameImage
import com.ezteam.baseproject.adapter.BaseRecyclerAdapter

class AdapterSameImage(var context: Context, list: MutableList<ItemSameImage>) :
    BaseRecyclerAdapter<ItemSameImage, AdapterSameImage.ViewHolder>(context, list) {
    var listenerOnClick: ((Int) -> Unit)? = null

    inner class ViewHolder(var binding: LayoutItemSameImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: ItemSameImage) {
            Glide.with(context)
                .load(data.file.path)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.sameImage.setImageDrawable(resource)
                        return true
                    }
                })
                .into(binding.sameImage)

            //
            if(data.isSelected){
                binding.layoutCheck.visibility = View.VISIBLE
            }else {
                binding.layoutCheck.visibility = View.INVISIBLE
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
        return ViewHolder(LayoutItemSameImageBinding.inflate(layoutInflater, parent, false))
    }
}