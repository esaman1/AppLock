package com.ezteam.applocker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ezteam.applocker.R
import com.ezteam.applocker.databinding.LayoutItemAppLockBinding
import com.ezteam.applocker.databinding.LayoutTxtBinding
import com.ezteam.applocker.item.ItemAppLock

class AppLockAdapter(val context: Context, var list: MutableList<ItemAppLock>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listenerClickItem: ((Int) -> Unit)? = null

    class ViewHolderType1(val bindingType1: LayoutItemAppLockBinding) :
        RecyclerView.ViewHolder(bindingType1.root)

    class ViewHolderType2(val bindingType2: LayoutTxtBinding) :
        RecyclerView.ViewHolder(bindingType2.root)

    //
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                ViewHolderType2(
                    LayoutTxtBinding.inflate(LayoutInflater.from(context), parent, false)
                )
            }
            else -> {
                ViewHolderType1(
                    LayoutItemAppLockBinding.inflate(LayoutInflater.from(context), parent, false)
                )
            }
        }
    }

    //
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = list[position]
        holder.apply {
            when (holder) {
                is ViewHolderType1 -> {
                    data.resId?.let {
                        Glide.with(context).load(it.toBitmap()).into(holder.bindingType1.icApp)
                    }
                    holder.bindingType1.txtNameApp.text = data.name
                    holder.bindingType1.descriptionApp.text = data.description
                    holder.bindingType1.icLocked.setImageResource(if (!data.isLocked) R.drawable.ic_unlock else R.drawable.ic_lock)
                    holder.itemView.setOnClickListener {
                        listenerClickItem?.invoke(holder.adapterPosition)
                    }
                }
                is ViewHolderType2 -> {
                    holder.bindingType2.txt.text = data.name
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position].packageName) {
            null -> {
                1
            }
            else -> {
                0
            }
        }
    }

    //
    override fun getItemCount(): Int {
        return list.size
    }

}