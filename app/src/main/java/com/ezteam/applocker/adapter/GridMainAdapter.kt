package com.ezteam.applocker.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.ezteam.applocker.R
import com.ezteam.applocker.activity.IntrusionAlertActivity
import com.ezteam.applocker.databinding.LayoutItemGridMainBinding
import com.ezteam.applocker.item.ItemGridMain

class GridMainAdapter(val context: Context, var list: MutableList<ItemGridMain>) :
    RecyclerView.Adapter<GridMainAdapter.ViewHolder>() {
    var listenerCLick: ((String) -> Unit)? = null

    class ViewHolder(val binding: LayoutItemGridMainBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutItemGridMainBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        setColorDrawable(holder.binding.layout, data.color)
        holder.binding.imgIcon.setImageResource(data.resId)
        holder.binding.name.text = data.name
        holder.itemView.setOnClickListener {
            listenerCLick?.invoke(list[holder.adapterPosition].name)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun setColorDrawable(layout: LinearLayout, color: String) {
        val bgShape = layout.background as GradientDrawable
        bgShape.mutate()
        bgShape.setColor(Color.parseColor(color))
    }

}