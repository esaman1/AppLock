package com.ezteam.applocker.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ezteam.applocker.databinding.LayoutItemLockThemeBinding
import com.ezteam.applocker.item.ItemLockTheme
import com.ezteam.baseproject.adapter.BaseRecyclerAdapter

class AdapterLockTheme(var context: Context, list: MutableList<ItemLockTheme>) :
    BaseRecyclerAdapter<ItemLockTheme, AdapterLockTheme.ViewHolder>(context, list) {

    var listenerOnClick: ((Int) -> Unit)? = null
//    var adsViewItem: RelativeLayout? = null

    inner class ViewHolder(var binding: LayoutItemLockThemeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: ItemLockTheme) {
            binding.contentView.isVisible = !data.isAds
            binding.adsView.isVisible = data.isAds
            //load ads
            if (data.isAds) {
                adsView?.let {
                    if (it.parent!=null) {
                        (it.parent as ViewGroup).removeView(it)
                    }
                    binding.adsView.addView(it)
                }
                return
            }
            //
            Glide.with(context).load(data.themPattern).apply(
                RequestOptions().override(
                    binding.imgTheme.width,
                    binding.imgTheme.height
                )
            ).into(binding.imgTheme)
            binding.layoutCheck.visibility = if (data.isSelected) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position])
        holder.itemView.setOnClickListener {
            listenerOnClick?.invoke(holder.adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutItemLockThemeBinding.inflate(layoutInflater, parent, false))
    }

//     fun addAdsItem(model: ItemLockTheme, position: Int) {
//        if (list.size < position) {
//            list.add(0, model)
//            notifyItemInserted(0)
//        } else {
//            list.add(position, model)
//            notifyItemInserted(position)
//        }
//    }
}