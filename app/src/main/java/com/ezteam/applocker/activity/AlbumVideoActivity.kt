package com.ezteam.applocker.activity

import android.os.Handler
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.ezteam.applocker.R
import com.ezteam.applocker.adapter.AdapterVideo
import com.ezteam.applocker.databinding.ActivityAlbumVideoBinding
import com.ezteam.applocker.databinding.LayoutDialogDeleteImageBinding
import com.ezteam.applocker.dialog.DialogDeleteImage
import com.ezteam.applocker.dialog.DialogHiding
import com.ezteam.applocker.item.ItemVideo
import com.ezteam.applocker.utils.RecycleViewUtils
import com.ezteam.applocker.viewmodel.HideImageViewModel
import com.ezteam.applocker.viewmodel.HideVideoModel
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.extensions.getHeightStatusBar
import com.google.android.gms.ads.ez.EzAdControl
import org.koin.android.ext.android.inject

class AlbumVideoActivity : BaseActivity<ActivityAlbumVideoBinding>() {
    private lateinit var adapterVideo: AdapterVideo
    private val listVideo = mutableListOf<ItemVideo>()
    private val viewModel by inject<HideVideoModel>()
    private val viewModelImage by inject<HideImageViewModel>()
    private var dialogQuestion: DialogDeleteImage? = null
    private var dialogHiding: DialogHiding? = null
    override fun initView() {
        // ads
        EzAdControl.getInstance(this).showAds()
        //
        setStatusBarHomeTransparent(this)
        binding.album.setPadding(0, getHeightStatusBar(), 0, 0)
        //
        adapterVideo = AdapterVideo(this, listVideo)
        binding.rclAlbum.adapter = adapterVideo
        // clear animation rcl
        RecycleViewUtils.clearAnimation(binding.rclAlbum)
    }

    override fun initData() {
        listVideo.clear()
        viewModel.videos.observe(this) {
            listVideo.add((it))
            adapterVideo.notifyItemInserted(listVideo.size - 1)
        }
        viewModel.getVideoGallery(this)
    }

    override fun initListener() {
        adapterVideo.listenerOnClick = {
            selectImage(it)
        }
        binding.txtSelectAll.setOnClickListener {
            var i = 0
            listVideo.forEach {
                if (it.isSelected) {
                    i++
                }
            }
            listVideo.forEach {
                if (!it.isSelected) {
                    it.isSelected = true
                    it.number = ++i
                    adapterVideo.notifyItemChanged(listVideo.indexOf(it))
                }
            }
        }
        binding.txtHide.setOnClickListener {
            showDialogDeleteVideo(getString(R.string.delete_video_after_hiding))
        }
        binding.icBackAlbumVideo.setOnClickListener {
            onBackPressed()
        }
    }

    override fun viewBinding(): ActivityAlbumVideoBinding {
        return ActivityAlbumVideoBinding.inflate(LayoutInflater.from(this))
    }

    private fun selectImage(it: Int) {
        var i = 0
        listVideo[it].isSelected = !listVideo[it].isSelected
        if (listVideo[it].isSelected) {
            listVideo.forEach { p ->
                if (p.isSelected) {
                    i++
                }
            }
            listVideo[it].number = i
            adapterVideo.notifyItemChanged(it)
        } else {
            listVideo.forEach { item ->
                if (item.number > listVideo[it].number) {
                    item.number -= 1
                    adapterVideo.notifyItemChanged(listVideo.indexOf(item))
                }
            }
            listVideo[it].number = 0
            adapterVideo.notifyItemChanged(it)
        }
    }

    private fun showDialogDeleteVideo(content: String? = null) {
        val bindDialog = LayoutDialogDeleteImageBinding.inflate(LayoutInflater.from(this))
        dialogQuestion = DialogDeleteImage(this, bindDialog)
        dialogQuestion?.apply {
            setCancelable(false)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            content?.let { c -> binding.txtContent.text = c }
        }
        dialogQuestion?.listenerYes = {
            dialogQuestion?.dismiss()
            encryptVideo()
            listVideo.forEach {
                if (it.isSelected) {
                    val file = it.file
                    viewModel.deleteVideo(file.absolutePath, this)
                }
            }
        }
        dialogQuestion?.listenerNo = {
            dialogQuestion?.dismiss()
            encryptVideo()
        }
        if (!isFinishing) {
            try {
                dialogQuestion?.show()

            } catch (e: WindowManager.BadTokenException) {
            }
        }
    }

    private fun encryptVideo() {
        showDialogHiding()
        viewModel.isHide.observe(this) {
            if (it) {
                viewModelImage.isHide.postValue(it)
                viewModel.isHide.postValue(false)
            }
            Handler().postDelayed({
                dialogHiding?.dismiss()
            }, 300)
        }
        viewModel.encryptVideo(listVideo, this, filesDir.path)
    }

    private fun showDialogHiding() {
        dialogHiding = DialogHiding(this)
        dialogHiding?.setCancelable(false)
        dialogHiding?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogHiding?.show()
        dialogHiding?.setOnDismissListener {
            onBackPressed()
            EzAdControl.getInstance(this).showAds()
        }
    }
}