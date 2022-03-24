package com.ezteam.applocker.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ezteam.applocker.databinding.LayoutItemLockScreenBinding
import com.ezteam.applocker.item.ItemLockScreen
import com.ezteam.baseproject.adapter.BaseRecyclerAdapter


class AdapterLockScreen(var context: Context, list: MutableList<ItemLockScreen>) :
    BaseRecyclerAdapter<ItemLockScreen, AdapterLockScreen.ViewHolder>(context, list) {
    var listenerOnClick: ((Int) -> Unit)? = null
//    var adsViewItem: RelativeLayout? = null

    inner class ViewHolder(var binding: LayoutItemLockScreenBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: ItemLockScreen) {
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
            Glide.with(context).load(data.photo)
                .apply(RequestOptions().override(binding.imgScreen.width, binding.imgScreen.height))
                .into(binding.imgScreen)
            if (data.isSelected) {
                binding.layoutCheck.visibility = View.VISIBLE
            } else {
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
        return ViewHolder(LayoutItemLockScreenBinding.inflate(layoutInflater, parent, false))
    }

//    fun addAdsItem(model: ItemLockScreen, position: Int) {
//        if (list.size < position) {
//            list.add(0, model)
//            notifyItemInserted(0)
//        } else {
//            list.add(position, model)
//            notifyItemInserted(position)
//        }
//    }
}