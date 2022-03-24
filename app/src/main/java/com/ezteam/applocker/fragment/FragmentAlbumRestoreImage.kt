package com.ezteam.applocker.fragment

import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezteam.applocker.activity.ActivitySearchRestore
import com.ezteam.applocker.adapter.AdapterAlbumRestoreImage
import com.ezteam.applocker.databinding.LayoutFragmentAlbumRestoreImageBinding
import com.ezteam.applocker.item.ItemAlbumRestoreImages
import com.ezteam.applocker.utils.RecycleViewUtils
import com.ezteam.applocker.viewmodel.RestoreImageViewModel
import com.ezteam.baseproject.fragment.BaseFragment
import org.koin.android.ext.android.inject
import java.io.File

class FragmentAlbumRestoreImage : BaseFragment<LayoutFragmentAlbumRestoreImageBinding>() {
    private val viewModelRestoreImages by inject<RestoreImageViewModel>()
    private var adapterAlbumRestoreImage: AdapterAlbumRestoreImage? = null
    private var listAlbumRestorePhoto = mutableListOf<ItemAlbumRestoreImages>()
    override fun initView() {
//        binding.animationView.visibility = View.VISIBLE
        adapterAlbumRestoreImage = AdapterAlbumRestoreImage(requireContext(), listAlbumRestorePhoto)
        binding.rclRestoreImage.adapter = adapterAlbumRestoreImage
        RecycleViewUtils.clearAnimation(binding.rclRestoreImage)
//        loadData = false
    }

    override fun initData() {
        listAlbumRestorePhoto.clear()
        viewModelRestoreImages.listAlbumRestorePhotoLiveData.observe(this) {
            if (it!=null&&listAlbumRestorePhoto.isEmpty()) {
//                listAlbumRestorePhoto.clear()
                listAlbumRestorePhoto.addAll(it)
            }
        }
//        listAlbumRestorePhoto.clear()
        viewModelRestoreImages.albumRestorePhotoLiveData.observe(this) {
            if (it!=null&&it.listPhoto.isNotEmpty()&&!listAlbumRestorePhoto.contains(it)&&!checkDuplicateFolder(
                    File(it.path).name
                )
            ) {
                listAlbumRestorePhoto.add(it)
                adapterAlbumRestoreImage?.notifyItemInserted(listAlbumRestorePhoto.size - 1)
//                }
            }
            if (listAlbumRestorePhoto.isEmpty()) {
                binding.txtNoImage.visibility = View.VISIBLE
            } else {
                binding.txtNoImage.visibility = View.INVISIBLE
            }
        }
        viewModelRestoreImages.getImageRestore(requireContext())
        //
        viewModelRestoreImages.position.observe(this) {
            binding.rclRestoreImage.layoutManager?.apply {
                if (itemCount > it) {
                    scrollToPosition(it)
                }
            }
        }
    }

    override fun initListener() {
        adapterAlbumRestoreImage?.listenerOnClick = {
            (activity as ActivitySearchRestore).fragmentDetailAlbumImage(listAlbumRestorePhoto[it])
            viewModelRestoreImages.listAlbumRestorePhotoLiveData.value = (listAlbumRestorePhoto).toMutableList()
            viewModelRestoreImages.position.value = ((binding.rclRestoreImage.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition())
        }
    }

    private fun checkDuplicateFolder(name: String): Boolean {
        for (i in listAlbumRestorePhoto) {
            if (File(i.path).name==name) {
                return true
            }
        }
        return false
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutFragmentAlbumRestoreImageBinding {
        return LayoutFragmentAlbumRestoreImageBinding.inflate(inflater, container, false)
    }
}