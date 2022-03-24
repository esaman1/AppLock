package com.ezteam.applocker.activity

import android.Manifest
import android.annotation.SuppressLint
import android.os.Handler
import android.view.LayoutInflater
import android.view.Window
import android.widget.MediaController
import android.widget.Toast
import com.ezteam.applocker.R
import com.ezteam.applocker.databinding.ActivityShowVideoBinding
import com.ezteam.applocker.databinding.LayoutDialogDeleteImageBinding
import com.ezteam.applocker.databinding.LayoutDialogInfoImageBinding
import com.ezteam.applocker.dialog.DialogDeleteImage
import com.ezteam.applocker.dialog.DialogHiding
import com.ezteam.applocker.dialog.DialogInfoImage
import com.ezteam.applocker.item.ItemVideoHide
import com.ezteam.applocker.key.Vault
import com.ezteam.applocker.service.LockService
import com.ezteam.applocker.utils.ImageUtil
import com.ezteam.applocker.viewmodel.HideVideoModel
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.extensions.getFileLength
import com.ezteam.baseproject.extensions.getHeightStatusBar
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@KoinApiExtension
class ShowVideo : BaseActivity<ActivityShowVideoBinding>() {
    private val viewModel by inject<HideVideoModel>()
    private var dialogHiding: DialogHiding? = null
    private var dialogQuestion: DialogDeleteImage? = null
    private var dialogInfo: DialogInfoImage? = null
    private var fileVideo: File? = null
    private var decryptPath: String? = null
    private var pathThumbNail: String? = null
    override fun initView() {
        setStatusBarHomeTransparent(this)
        binding.showVideo.setPadding(0, getHeightStatusBar(), 0, 0)
        //
        decryptPath = intent.getStringExtra(Vault.KEY_VIDEO_DECRYPT_PATH)
        pathThumbNail = intent.getStringExtra(Vault.KEY_VIDEO_THUMBNAIL_PATH)
        decryptPath?.let {
            binding.video.setVideoPath(decryptPath)
            val mediaController = MediaController(this)
            mediaController.setMediaPlayer(binding.video)
            binding.video.setMediaController(mediaController)
            binding.video.requestFocus()
            binding.video.start()
        }
        if (decryptPath==null) {
            binding.zom.setBackgroundResource(R.color.black)
            toast(getString(R.string.dont_open_video))
        }

        decryptPath?.let {
            fileVideo = File(it)
            binding.nameVideo.text = fileVideo?.name
        }
    }

    override fun initData() {
    }

    override fun initListener() {
        binding.icDelete.setOnClickListener {
            fileVideo?.let {
                showDialogDeleteVideo(it.name, getString(R.string.delete_video))
            }
        }
        binding.icLock.setOnClickListener {
            showDialogRecoverVideo(resources.getString(R.string.recover_dialog_title_video))
        }
        binding.icCircleFill.setOnClickListener {
            showDialogInfo()
        }
        binding.icBackShowVideo.setOnClickListener {
            onBackPressed()
        }
    }

    override fun viewBinding(): ActivityShowVideoBinding {
        return ActivityShowVideoBinding.inflate(LayoutInflater.from(this))
    }

    private fun showDialogRecoverVideo(content: String? = null) {
        val bindDialog = LayoutDialogDeleteImageBinding.inflate(LayoutInflater.from(this))
        dialogQuestion = DialogDeleteImage(this, bindDialog)
        dialogQuestion?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        content?.let { dialogQuestion?.binding?.txtContent?.text = it }
        dialogQuestion?.show()
        dialogQuestion?.listenerYes = {
            LockService.isCreate = true // not lock when request any permission
            requestPermission(complete = {
                if (it) {
                    val msg: Int
                    fileVideo?.let { fv ->
                        showDialogHiding()
                        if (ImageUtil.recoverFileVideo(fv, this)) {
                            deleteVideo()
                            msg = R.string.video_recovered_msg
                        } else
                            msg = R.string.video_not_recovered_msg
                        Toast.makeText(this, getString(msg), Toast.LENGTH_SHORT).show()
                        Handler().postDelayed({
                            dialogHiding?.dismiss()
                        }, 250)
                    }
                } else {
                    Toast.makeText(
                        this,
                        R.string.permission_denied_to_recover_msg_video,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        dialogQuestion?.listenerNo = {
            dialogQuestion?.dismiss()
        }
    }

    private fun showDialogDeleteVideo(name: String, content: String? = null) {
        val bindDialog = LayoutDialogDeleteImageBinding.inflate(LayoutInflater.from(this))
        dialogQuestion = DialogDeleteImage(this, bindDialog)
        dialogQuestion?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        content?.let { dialogQuestion?.binding?.txtContent?.text = it }
        dialogQuestion?.show()
        dialogQuestion?.listenerYes = {
            deleteVideo()
            dialogQuestion?.dismiss()
            showDialogHiding()
            Handler().postDelayed({
                dialogHiding?.dismiss()
            }, 250)
        }
        dialogQuestion?.listenerNo = {
            dialogQuestion?.dismiss()
        }
    }

    private fun deleteVideo() {
        pathThumbNail?.let { path ->
            val name = File(path).name
            val list = Hawk.get(Vault.KEY_FILE_NAME_VIDEO, mutableListOf<ItemVideoHide>())
            for (it in list) {
                if (it.fileName==name) {
                    list.remove(it)
                    viewModel.recoverVideo.value = path
                    viewModel.recoverVideo(path)
                    break
                }
            }
            Hawk.put(Vault.KEY_FILE_NAME_VIDEO, list)
        }
    }

    private fun showDialogHiding() {
        dialogHiding = DialogHiding(this)
        dialogHiding?.apply {
            setCancelable(false)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            show()
        }
        dialogHiding?.setOnDismissListener {
            onBackPressed()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun showDialogInfo() {
        val bindDialog = LayoutDialogInfoImageBinding.inflate(LayoutInflater.from(this))
        var date: String? = null
        fileVideo?.let {
            date = SimpleDateFormat("dd/MM/yyyy").format(Date(it.lastModified()))
        }

        dialogInfo = DialogInfoImage(this, bindDialog)
        dialogInfo?.setCancelable(true)
        dialogInfo?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogInfo?.binding?.apply {
            txtName.text = fileVideo?.name
            txtSize.text = fileVideo?.getFileLength()
            txtDate.text = date
            txtPath.text = fileVideo?.absolutePath
        }
        dialogInfo?.show()
    }
}