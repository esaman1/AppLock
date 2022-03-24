package com.ezteam.applocker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.ezteam.applocker.R
import com.ezteam.applocker.databinding.LayoutItemDetailNotificationBinding
import com.ezteam.applocker.model.ItemNotificationModel
import java.text.SimpleDateFormat
import java.util.*

class DetailNotificationAdapter(
    val context: Context,
    var list: MutableList<ItemNotificationModel>
) :
    RecyclerView.Adapter<DetailNotificationAdapter.ViewHolder>() {
    private val animClickIcon =
        AnimationUtils.loadAnimation(context, R.anim.anim_click_icon)
    var listenerClose: ((Int) -> Unit)? = null
    var listenerClickItem: ((Int) -> Unit)? = null
    var listenerMore: ((Int) -> Unit)? = null

    class ViewHolder(var binding: LayoutItemDetailNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutItemDetailNotificationBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    @SuppressLint("SimpleDateFormat", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        if (!data.isParent) {
            holder.binding.layoutAppMarket.visibility = View.GONE
        } else {
            holder.binding.layoutAppMarket.visibility = View.VISIBLE
            val anim = AnimationUtils.loadAnimation(context, R.anim.anim_hide_view)
            holder.binding.layoutAppMarket.startAnimation(anim)
        }
        var iconApp: Drawable? = null
        try {
            iconApp = context.packageManager.getApplicationIcon(data.packageName ?: "")
        } catch (e: PackageManager.NameNotFoundException) {
        }
        var icon: Drawable? = null
        icon = if (data.resId!=0) {
            val packageContext =
                holder.binding.icNotification.context.createPackageContext(
                    data.packageName,
                    0
                )
            packageContext.resources.getDrawable(data.resId)
        } else {
            iconApp
        }

        //
        val packageManager = holder.binding.icNotification.context.packageManager
        val name = packageManager.getApplicationLabel(
            packageManager.getApplicationInfo(
                data.packageName!!,
                PackageManager.GET_META_DATA
            )
        )
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = data.time
        //
        holder.binding.apply {
            imgAppMarket.setImageDrawable(iconApp)
            icNotification.setImageDrawable(icon)
            txtTitle.text = data.title
            time.text = SimpleDateFormat("h:mm a").format(calendar.time)
            appName.text = name
        }
        holder.binding.txtMessage.text = if (data.message==null) "" else data.message
        if (holder.binding.txtMessage.lineCount > 3) {
            if (!data.isMore) {
                holder.binding.apply {
                    more.text = context.getString(R.string.see_more)
                    more.visibility = View.VISIBLE
                    txtMessage.maxLines = 3
                    txtMessage.invalidate()
                }

            } else {
                holder.binding.apply {
                    more.text = context.getString(R.string.collapse_)
                    more.visibility = View.VISIBLE
                    txtMessage.maxLines = Int.MAX_VALUE
                    txtMessage.invalidate()
                }
            }
        } else {
            holder.binding.apply {
                more.visibility = View.GONE
                txtMessage.maxLines = Int.MAX_VALUE
            }
        }
        // close
        // click Item
        holder.itemView.setOnClickListener {
            holder.binding.layoutContent.startAnimation(animClickIcon)
            listenerClickItem?.invoke(holder.adapterPosition)
        }
        // click more
        holder.binding.layoutMore.setOnClickListener {
            listenerMore?.invoke(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun swipe(position: Int, direction: Int) {
        listenerClose?.invoke(position)
    }
}