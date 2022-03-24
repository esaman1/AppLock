package com.ezteam.applocker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ezteam.applocker.databinding.LayoutItemVideoHideBinding
import com.ezteam.applocker.item.ItemVideoHide
import com.ezteam.baseproject.adapter.BaseRecyclerAdapter
import java.io.File
import java.text.SimpleDateFormat

class AdapterVideoHide(var context: Context, list: MutableList<ItemVideoHide>) :
    BaseRecyclerAdapter<ItemVideoHide, AdapterVideoHide.ViewHolder>(context, list) {
    var listenerOnClick: ((Int) -> Unit)? = null

    inner class ViewHolder(var binding: LayoutItemVideoHideBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bindData(data: ItemVideoHide) {
            var duration: Long = 0
            Glide.with(context)
                .load(data.decryptPath)
                .into(binding.video)
            //
            data.decryptPath?.let {

                val file = File(it)
                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setDataSource(context, Uri.fromFile(file))
                    val time: String? =
                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    time?.let { t ->
                        duration = t.toLong()
                    }
                } catch (e: Exception) {
                }
            }
            binding.txtDuration.text = if (duration >= 3600000)
                SimpleDateFormat("hh:mm:ss").format(duration)
            else
                SimpleDateFormat("mm:ss").format(duration)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position])
        holder.itemView.setOnClickListener {
            listenerOnClick?.invoke(holder.adapterPosition)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutItemVideoHideBinding.inflate(layoutInflater, parent, false))
    }
}