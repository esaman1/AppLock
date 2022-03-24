package com.ezteam.applocker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ezteam.applocker.databinding.LayoutItemAlbumRestoreAudioBinding
import com.ezteam.applocker.item.ItemAlbumRestoreAudios
import com.ezteam.baseproject.adapter.BaseRecyclerAdapter
import java.io.File

class AdapterAlbumRestoreAudios(var context: Context, list: MutableList<ItemAlbumRestoreAudios>) :
    BaseRecyclerAdapter<ItemAlbumRestoreAudios, AdapterAlbumRestoreAudios.ViewHolder>(
        context,
        list
    ) {
    var listenerOnClick: ((Int) -> Unit)? = null

    inner class ViewHolder(var binding: LayoutItemAlbumRestoreAudioBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindData(data: ItemAlbumRestoreAudios) {
            binding.txtFolder.text = File(data.path).name
            binding.txtNumber.text = "${data.listAudios.size} audios"
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position])
        holder.itemView.setOnClickListener {
            listenerOnClick?.invoke(holder.adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutItemAlbumRestoreAudioBinding.inflate(layoutInflater, parent, false))
    }
}