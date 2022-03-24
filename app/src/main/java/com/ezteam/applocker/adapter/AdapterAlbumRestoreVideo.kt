package com.ezteam.applocker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ezteam.applocker.R
import com.ezteam.applocker.databinding.LayoutItemAlbumRestoreVideosBinding
import com.ezteam.applocker.item.ItemAlbumRestoreVideos
import com.ezteam.baseproject.adapter.BaseRecyclerAdapter
import java.io.File

class AdapterAlbumRestoreVideo(var context: Context, list: MutableList<ItemAlbumRestoreVideos>) :
    BaseRecyclerAdapter<ItemAlbumRestoreVideos, AdapterAlbumRestoreVideo.ViewHolder>(
        context,
        list
    ) {
    var listenerOnClick: ((Int) -> Unit)? = null

    inner class ViewHolder(var binding: LayoutItemAlbumRestoreVideosBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindData(data: ItemAlbumRestoreVideos) {
            Glide.with(context).load(data.listVideos[0].path).into(binding.albumVideo1)
            if (data.listVideos.size > 1) {
                binding.icPlay1.visibility = View.INVISIBLE
                binding.card2.visibility = View.VISIBLE
                Glide.with(context).load(data.listVideos[1].path).into(binding.albumVideo2)
            } else {
                binding.icPlay1.visibility = View.VISIBLE
                val set = ConstraintSet()
                set.clone(binding.layoutCard)
                set.connect(R.id.card1, ConstraintSet.END, R.id.layout_card, ConstraintSet.END, 0)
                set.applyTo(binding.layoutCard)
                binding.card2.visibility = View.GONE
            }
            binding.txtFolder.text = File(data.path).name
            binding.txtNumber.text = "${data.listVideos.size} videos"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutItemAlbumRestoreVideosBinding.inflate(
                layoutInflater,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position])
        holder.itemView.setOnClickListener {
            listenerOnClick?.invoke(holder.adapterPosition)
        }
    }
}