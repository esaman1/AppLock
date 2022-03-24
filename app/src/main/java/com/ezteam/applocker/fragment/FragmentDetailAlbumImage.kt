package com.ezteam.applocker.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import com.ezteam.applocker.R
import com.ezteam.applocker.adapter.AdapterDetailAlbumRestoreImage
import com.ezteam.applocker.databinding.LayoutDetailAlbumRestoreIamgeBinding
import com.ezteam.applocker.databinding.LayoutDialogDeleteImageBinding
import com.ezteam.applocker.dialog.DialogDeleteImage
import com.ezteam.applocker.item.ItemAlbumRestoreImages
import com.ezteam.applocker.utils.DialogLoadingUtils
import com.ezteam.applocker.utils.ImageUtil
import com.ezteam.applocker.utils.RecycleViewUtils
import com.ezteam.baseproject.fragment.BaseFragment
import com.google.android.gms.ads.ez.EzAdControl
import java.io.File

class FragmentDetailAlbumImage(var item: ItemAlbumRestoreImages) :
    BaseFragment<LayoutDetailAlbumRestoreIamgeBinding>() {
    var onBack: (() -> Unit)? = null
    private var dialogQuestion: DialogDeleteImage? = null
    private var adapterDetailAlbumRestoreImage: AdapterDetailAlbumRestoreImage? = null
    override fun initView() {
        adapterDetailAlbumRestoreImage =
            AdapterDetailAlbumRestoreImage(requireContext(), item.listPhoto)
        binding.rclImages.adapter = adapterDetailAlbumRestoreImage
        RecycleViewUtils.clearAnimation(binding.rclImages)
    }

    override fun initData() {

    }

    override fun initListener() {
        adapterDetailAlbumRestoreImage?.listenerOnClick = {
            selectImage(it)
        }
        binding.txtSelectAll.setOnClickListener {
            var i = 0
            item.listPhoto.forEach {
                if (it.isSelected) {
                    i++
                }
            }
            item.listPhoto.forEach {
                if (!it.isSelected) {
                    it.isSelected = true
                    it.number = ++i
                    adapterDetailAlbumRestoreImage?.notifyItemChanged(item.listPhoto.indexOf(it))
                }
            }
        }
        //
        binding.txtRestore.setOnClickListener {
            showDialogQuestion(requireContext().getString(R.string.do_want_restore_photos))
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutDetailAlbumRestoreIamgeBinding {
        return LayoutDetailAlbumRestoreIamgeBinding.inflate(inflater, container, false)
    }

    private fun selectImage(it: Int) {
        var i = 0
        item.listPhoto[it].isSelected = !item.listPhoto[it].isSelected
        if (item.listPhoto[it].isSelected) {
            item.listPhoto.forEach { p ->
                if (p.isSelected) {
                    i++
                }
            }
            item.listPhoto[it].number = i
            adapterDetailAlbumRestoreImage?.notifyItemChanged(it)
        } else {
            item.listPhoto.forEach { i ->
                if (i.number > item.listPhoto[it].number) {
                    i.number -= 1
                    adapterDetailAlbumRestoreImage?.notifyItemChanged(item.listPhoto.indexOf(i))
                }
            }
            item.listPhoto[it].number = 0
            adapterDetailAlbumRestoreImage?.notifyItemChanged(it)
        }
    }

    private fun showDialogQuestion(content: String? = null) {
        val bindDialog =
            LayoutDialogDeleteImageBinding.inflate(LayoutInflater.from(requireContext()))
        dialogQuestion = DialogDeleteImage(requireContext(), bindDialog).apply {
            setCancelable(false)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            listenerYes = {
                dialogQuestion?.dismiss()
                restoreImage()
            }
            listenerNo = {
                dialogQuestion?.dismiss()
            }
        }
        content?.let { dialogQuestion?.binding?.txtContent?.text = it }
        dialogQuestion?.show()
    }

    private fun restoreImage() {
        DialogLoadingUtils.showDialogHiding(requireContext(), true)
        val listImage = mutableListOf<File>()
        item.listPhoto.forEach {
            if (it.isSelected) {
                listImage.add(File(it.path))
                it.isSelected = false
            }
        }
        ImageUtil.recoverListFileImage(listImage, requireContext())
        DialogLoadingUtils.showDialogHiding(requireContext(), false)
        EzAdControl.getInstance(requireActivity()).showAds()
        onBack?.invoke()
    }
}