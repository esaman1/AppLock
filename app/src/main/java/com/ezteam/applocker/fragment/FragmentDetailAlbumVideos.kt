package com.ezteam.applocker.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import com.ezteam.applocker.R
import com.ezteam.applocker.adapter.AdapterDetailAlbumRestoreVideos
import com.ezteam.applocker.databinding.LayoutDetailAlbumVideoBinding
import com.ezteam.applocker.databinding.LayoutDialogDeleteImageBinding
import com.ezteam.applocker.dialog.DialogDeleteImage
import com.ezteam.applocker.item.ItemAlbumRestoreVideos
import com.ezteam.applocker.utils.DialogLoadingUtils
import com.ezteam.applocker.utils.ImageUtil
import com.ezteam.applocker.utils.RecycleViewUtils
import com.ezteam.baseproject.fragment.BaseFragment
import com.google.android.gms.ads.ez.EzAdControl
import java.io.File

class FragmentDetailAlbumVideos(var item: ItemAlbumRestoreVideos) :
    BaseFragment<LayoutDetailAlbumVideoBinding>() {
    private var dialogQuestion: DialogDeleteImage? = null
    private var adapterDetailAlbumRestoreVideos: AdapterDetailAlbumRestoreVideos? = null
    var onBack: (() -> Unit)? = null

    override fun initView() {
        adapterDetailAlbumRestoreVideos =
            AdapterDetailAlbumRestoreVideos(requireContext(), item.listVideos)
        binding.rclVideos.adapter = adapterDetailAlbumRestoreVideos
        RecycleViewUtils.clearAnimation(binding.rclVideos)
    }

    override fun initData() {
    }

    override fun initListener() {
        adapterDetailAlbumRestoreVideos?.listenerOnClick = {
            selectImage(it)
        }
        binding.txtSelectAll.setOnClickListener {
            var i = 0
            item.listVideos.forEach {
                if (it.isSelected) {
                    i++
                }
            }
            item.listVideos.forEach {
                if (!it.isSelected) {
                    it.isSelected = true
                    it.number = ++i
                    adapterDetailAlbumRestoreVideos?.notifyItemChanged(item.listVideos.indexOf(it))
                }
            }
        }
        //
        binding.txtRestore.setOnClickListener {
            showDialogQuestion(requireContext().getString(R.string.do_want_restore_videos))
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutDetailAlbumVideoBinding {
        return LayoutDetailAlbumVideoBinding.inflate(inflater, container, false)
    }

    private fun selectImage(it: Int) {
        var i = 0
        item.listVideos[it].isSelected = !item.listVideos[it].isSelected
        if (item.listVideos[it].isSelected) {
            item.listVideos.forEach { p ->
                if (p.isSelected) {
                    i++
                }
            }
            item.listVideos[it].number = i
            adapterDetailAlbumRestoreVideos?.notifyItemChanged(it)
        } else {
            item.listVideos.forEach { i ->
                if (i.number > item.listVideos[it].number) {
                    i.number -= 1
                    adapterDetailAlbumRestoreVideos?.notifyItemChanged(item.listVideos.indexOf(i))
                }
            }
            item.listVideos[it].number = 0
            adapterDetailAlbumRestoreVideos?.notifyItemChanged(it)
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
                restoreVideo()
            }
            listenerNo = {
                dialogQuestion?.dismiss()
            }
        }
        content?.let { dialogQuestion?.binding?.txtContent?.text = it }
        dialogQuestion?.show()
    }

    private fun restoreVideo() {
        DialogLoadingUtils.showDialogHiding(requireContext(), true)
        val listVideos = mutableListOf<File>()
        item.listVideos.forEach {
            if (it.isSelected) {
                listVideos.add(File(it.path))
                it.isSelected = false
            }
        }
        ImageUtil.recoverListVideo(listVideos, requireContext())
        DialogLoadingUtils.showDialogHiding(requireContext(), false)
        EzAdControl.getInstance(requireActivity()).showAds()
        onBack?.invoke()
    }
}