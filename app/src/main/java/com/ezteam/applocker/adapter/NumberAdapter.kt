package com.ezteam.applocker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ezteam.applocker.databinding.LayoutItemNumberBinding
import com.ezteam.applocker.item.ItemNumber

class NumberAdapter(val context: Context, var list: MutableList<ItemNumber>) :
    RecyclerView.Adapter<NumberAdapter.ViewHolder>() {
    var listenerClickNumber: ((Int) -> Unit)? = null

    class ViewHolder(val binding: LayoutItemNumberBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutItemNumberBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        if (data.number==-2) {
            holder.binding.layoutItemNumber.visibility = View.INVISIBLE
        }
        Glide.with(context).load(data.themeDefault).into(holder.binding.layoutItemNumber)
        //change width , height
        val widthScreen = context.resources.displayMetrics.widthPixels
        holder.binding.layoutItemNumber.layoutParams.apply {
            width = (widthScreen / 5.6F).toInt()
            height = (widthScreen / 5.6F).toInt()
        }
        // click
        holder.binding.layoutItemNumber.setOnTouchListener { v, event ->
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    listenerClickNumber?.invoke(holder.adapterPosition)
                    Glide.with(context).load(data.themDown).into(holder.binding.layoutItemNumber)
//                    notifyItemChanged(holder.adapterPosition)
                }
                MotionEvent.ACTION_UP -> {
                    Glide.with(context).load(data.themeDefault)
                        .into(holder.binding.layoutItemNumber)
//                    notifyItemChanged(holder.adapterPosition)
                }
                MotionEvent.ACTION_MOVE -> {
                }
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}