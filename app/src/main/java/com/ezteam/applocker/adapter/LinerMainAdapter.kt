package com.ezteam.applocker.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.ezteam.applocker.databinding.LayoutItemSpeedUpBinding
import com.ezteam.applocker.item.ItemSpeedUp
import com.ezteam.applocker.utils.AppUtil.toUnitMemory

class LinerMainAdapter(val context: Context, var list: MutableList<ItemSpeedUp>) :
    RecyclerView.Adapter<LinerMainAdapter.ViewHolder>() {
    var listenerOnClick: ((Int) -> Unit)? = null

    class ViewHolder(val binding: LayoutItemSpeedUpBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutItemSpeedUpBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        setColorDrawable(holder.binding.layoutIcon, data.color)
        holder.binding.icItem.setImageResource(data.resId)
        holder.binding.nameSpeedUp.text = data.name
        holder.binding.memory.text =
            if (data.content!=null) data.content else "${data.remainingMemory.toUnitMemory()} / ${data.totalMemory.toUnitMemory()}"
        holder.binding.btn.text = data.contentBtn

        if (data.content!=null) {
            holder.binding.seekbar.visibility = View.GONE
        } else {
            holder.binding.seekbar.progress =
                ((data.remainingMemory / data.totalMemory) * 100.0).toInt()
        }
        //

        //
        holder.binding.seekbar.isEnabled = false
        //
        holder.itemView.setOnClickListener {
            listenerOnClick?.invoke(holder.adapterPosition)
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