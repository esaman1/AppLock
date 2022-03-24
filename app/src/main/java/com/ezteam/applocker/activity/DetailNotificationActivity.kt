package com.ezteam.applocker.activity

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ezstudio.controlcenter.touchswipe.SimpleItemTouchHelperCallback
import com.ezteam.applocker.adapter.DetailNotificationAdapter
import com.ezteam.applocker.databinding.LayoutDetailNotificationBinding
import com.ezteam.applocker.model.ItemNotificationModel
import com.ezteam.applocker.service.LockService
import com.ezteam.applocker.utils.NotificationLockUtils
import com.ezteam.applocker.viewmodel.NotifyViewModel
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.extensions.getHeightStatusBar
import com.ezteam.baseproject.extensions.launchActivity
import com.google.android.gms.ads.ez.EzAdControl
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension

class DetailNotificationActivity : BaseActivity<LayoutDetailNotificationBinding>() {
    private lateinit var adapterDetail: DetailNotificationAdapter
    private var listNotification: MutableList<ItemNotificationModel> = mutableListOf()
    private val viewModel by inject<NotifyViewModel>()

    @KoinApiExtension
    override fun initView() {
        // ads
        EzAdControl.getInstance(this).showAds()
        //
        LockService.showDetailNotify = false
        setStatusBarHomeTransparent(this)
        binding.detail.setPadding(0, getHeightStatusBar(), 0, 0)
        //
        adapterDetail = DetailNotificationAdapter(this, listNotification)
        binding.rclNotification.adapter = adapterDetail
        // add touch callback
        addItemTouchCallback(binding.rclNotification)
    }

    private fun addItemTouchCallback(recyclerView: RecyclerView) {
        val callBack = SimpleItemTouchHelperCallback()
        callBack.listenerSwipe = { position, direction ->
            adapterDetail.swipe(position, direction)
        }
        val itemTouchHelper = ItemTouchHelper(callBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun viewBinding(): LayoutDetailNotificationBinding {
        return LayoutDetailNotificationBinding.inflate(LayoutInflater.from(this))
    }

    override fun initListener() {
        binding.icBackNotification.setOnClickListener {
            onBackPressed()
        }
        adapterDetail.listenerClose = {
            NotificationLockUtils.listNotification.removeAt(it)
            listNotification.removeAt(it)
            adapterDetail.notifyItemRemoved(it)
            checkNoNotification()
        }

        adapterDetail.listenerClickItem = {
            NotificationLockUtils.listNotification.removeAt(it)
            listNotification.removeAt(it)
            adapterDetail.notifyItemChanged(it)
            listNotification[it].contentIntent?.send()
            checkNoNotification()
        }
        adapterDetail.listenerMore = {
            listNotification[it].isMore = !listNotification[it].isMore
            NotificationLockUtils.listNotification[it].isMore =
                !NotificationLockUtils.listNotification[it].isMore
            adapterDetail.notifyItemChanged(it)
        }
        binding.txtClearAll.setOnClickListener {
            NotificationLockUtils.listNotification.clear()
            listNotification.clear()
            adapterDetail.notifyDataSetChanged()
            checkNoNotification()
        }
    }

    override fun initData() {
        listNotification.clear()
        listNotification.addAll(NotificationLockUtils.listNotification)
        checkNoNotification()
        viewModel.listNotifyLiveData.observe(this) { data ->
            if (data!=null) {
                var check = false
                if (data.time!=-1L) {
                    if (data.title==null) {
                        check = true
                    } else {
                        for (item in listNotification) {
                            if (item.key==data.key) {
                                if (item.time!=data.time) {
                                    item.message = "${item.message}\n${data.message}"
                                    adapterDetail.notifyItemChanged(
                                        listNotification.indexOf(
                                            item
                                        )
                                    )
                                }
                                check = true
                                break
                            }
                        }
                    }

                    if (!check) {
                        if (listNotification.size > 0) {
                            for (i in (listNotification.size - 1) downTo 0) {
                                val item = listNotification[i]
                                if (item.packageName==data.packageName) {
                                    data.isParent = false
                                    listNotification.add(i + 1, data)
                                    adapterDetail.notifyItemInserted(i + 1)
                                    break
                                }
                            }
                        }
                        //
                        if (data.isParent) {
                            listNotification.add(0, data)
                            adapterDetail.notifyItemInserted(0)
                        }
                    }
                }
            }
            NotificationLockUtils.listNotification.clear()
            NotificationLockUtils.listNotification.addAll(listNotification)
        }
    }

    private fun checkNoNotification() {
        if (listNotification.isEmpty()) {
            binding.txtNoNotifications.visibility = View.VISIBLE
            binding.rclNotification.visibility = View.INVISIBLE
        } else {
            binding.txtNoNotifications.visibility = View.INVISIBLE
            binding.rclNotification.visibility = View.VISIBLE

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        launchActivity<MainLockActivity> { flags = Intent.FLAG_ACTIVITY_CLEAR_TASK }
    }

}