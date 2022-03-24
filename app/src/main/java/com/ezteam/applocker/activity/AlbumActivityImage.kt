package com.ezteam.applocker.activity

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.Window
import com.ezteam.applocker.R
import com.ezteam.applocker.adapter.AdapterAlbum
import com.ezteam.applocker.databinding.ActivityAlbumBinding
import com.ezteam.applocker.databinding.LayoutDialogDeleteImageBinding
import com.ezteam.applocker.dialog.DialogDeleteImage
import com.ezteam.applocker.dialog.DialogHiding
import com.ezteam.applocker.item.ItemDetailPhoto
import com.ezteam.applocker.key.Vault
import com.ezteam.applocker.utils.RecycleViewUtils
import com.ezteam.applocker.viewmodel.HideImageViewModel
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.extensions.getHeightStatusBar
import com.google.android.gms.ads.ez.EzAdControl
import org.koin.android.ext.android.inject


class AlbumActivityImage : BaseActivity<ActivityAlbumBinding>() {
    private lateinit var adapterAlbum: AdapterAlbum
    private val listDetailPhoto = mutableListOf<ItemDetailPhoto>()
    private var dialogQuestion: DialogDeleteImage? = null
    private var dialogHiding: DialogHiding? = null
    private val viewModel by inject<HideImageViewModel>()

    override fun initView() {
        // ads
        EzAdControl.getInstance(this).showAds()
        //
        setStatusBarHomeTransparent(this)
        binding.album.setPadding(0, getHeightStatusBar(), 0, 0)
//
        adapterAlbum = AdapterAlbum(this, listDetailPhoto)
        binding.rclAlbum.adapter = adapterAlbum
        RecycleViewUtils.clearAnimation(binding.rclAlbum)
    }

    override fun initData() {
        listDetailPhoto.clear()
        viewModel.imageStoreLiveData.observe(this) {
            listDetailPhoto.add(ItemDetailPhoto(it))
            adapterAlbum.notifyItemInserted(listDetailPhoto.size - 1)
        }
        viewModel.getImagesGallery(this, true)
        //
    }

    override fun initListener() {
        adapterAlbum.listenerOnClick = {
            selectImage(it)
        }
        //
        binding.icBackAlbum.setOnClickListener {
            onBackPressed()
        }
        //
        binding.txtSelectAll.setOnClickListener {
            var i = 0
            listDetailPhoto.forEach {
                if (it.isSelected) {
                    i++
                }
            }
            listDetailPhoto.forEach {
                if (!it.isSelected) {
                    it.isSelected = true
                    it.number = ++i
                    adapterAlbum.notifyItemChanged(listDetailPhoto.indexOf(it))
                }
            }
        }
        //hide
        binding.txtHide.setOnClickListener {
            showDialogDeleteImage(getString(R.string.delete_after_hiding))
        }
    }

    override fun viewBinding(): ActivityAlbumBinding {
        return ActivityAlbumBinding.inflate(LayoutInflater.from(this))
    }

    private fun encryptImage() {
        showDialogHiding()
        viewModel.isHide.observe(this) {
            if (it) {
                Handler().postDelayed({
                    dialogHiding?.dismiss()
                }, 200)
            }
        }
        viewModel.encryptFile(listDetailPhoto, this, filesDir.path)
    }

    private fun selectImage(it: Int) {
        var i = 0
        listDetailPhoto[it].isSelected = !listDetailPhoto[it].isSelected
        if (listDetailPhoto[it].isSelected) {
            listDetailPhoto.forEach { p ->
                if (p.isSelected) {
                    i++
                }
            }
            listDetailPhoto[it].number = i
            adapterAlbum.notifyItemChanged(it)
        } else {
            listDetailPhoto.forEach { item ->
                if (item.number > listDetailPhoto[it].number) {
                    item.number -= 1
                    adapterAlbum.notifyItemChanged(listDetailPhoto.indexOf(item))
                }
            }
            listDetailPhoto[it].number = 0
            adapterAlbum.notifyItemChanged(it)
        }
    }

    private fun showDialogDeleteImage(content: String? = null) {
        val bindDialog = LayoutDialogDeleteImageBinding.inflate(LayoutInflater.from(this))
        dialogQuestion = DialogDeleteImage(this, bindDialog)
        dialogQuestion?.let {
            it.setCancelable(false)
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            content?.let { c -> it.binding.txtContent.text = c }
            it.show()
        }
        dialogQuestion?.listenerYes = {
            dialogQuestion?.dismiss()
            encryptImage()
            listDetailPhoto.forEach {
                if (it.isSelected) {
                    val file = it.file
                    viewModel.deleteImage(file.absolutePath, this)
                }
            }
        }
        dialogQuestion?.listenerNo = {
            dialogQuestion?.dismiss()
            encryptImage()
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
            EzAdControl.getInstance(this).showAds()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==Vault.REQUEST_CODE_DELETE_IMAGE) {
            if (resultCode==Activity.RESULT_OK) {
                //                    remove success)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}