package com.ezteam.applocker.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezteam.applocker.activity.ActivitySearchRestore
import com.ezteam.applocker.adapter.AdapterAlbumRestoreVideo
import com.ezteam.applocker.databinding.LayoutFragmentAlbumVideosBinding
import com.ezteam.applocker.item.ItemAlbumRestoreVideos
import com.ezteam.applocker.utils.RecycleViewUtils
import com.ezteam.applocker.viewmodel.RestoreVideoViewModel
import com.ezteam.baseproject.fragment.BaseFragment
import org.koin.android.ext.android.inject
import java.io.File

class FragmentAlbumRestoreVideos : BaseFragment<LayoutFragmentAlbumVideosBinding>() {
    private val viewModelRestoreVideos by inject<RestoreVideoViewModel>()
    private var adapterAlbumRestoreVideo: AdapterAlbumRestoreVideo? = null
    private var listAlbumRestoreVideo = mutableListOf<ItemAlbumRestoreVideos>()
    override fun initView() {
        adapterAlbumRestoreVideo = AdapterAlbumRestoreVideo(requireContext(), listAlbumRestoreVideo)
        binding.rclRestoreVideo.adapter = adapterAlbumRestoreVideo
        RecycleViewUtils.clearAnimation(binding.rclRestoreVideo)
    }

    override fun initData() {
        listAlbumRestoreVideo.clear()
        viewModelRestoreVideos.listAlbumRestoreVideosLiveData.observe(this) {
            if (it!=null&&listAlbumRestoreVideo.isEmpty()) {
//                listAlbumRestoreVideo.clear()
                listAlbumRestoreVideo.addAll(it)
            }
        }
//        listAlbumRestoreVideo.clear()
        viewModelRestoreVideos.albumRestoreVideosLiveData.observe(this) {
            if (it!=null&&it.listVideos.isNotEmpty()&&!listAlbumRestoreVideo.contains(it)&&!checkDuplicateFolder(
                    File(it.path).name
                )
            ) {
                listAlbumRestoreVideo.add(it)
                adapterAlbumRestoreVideo?.notifyItemInserted(listAlbumRestoreVideo.size - 1)
            }
            if (listAlbumRestoreVideo.isEmpty()) {
                binding.txtNoVideos.visibility = View.VISIBLE
            } else {
                binding.txtNoVideos.visibility = View.INVISIBLE
            }
        }
        viewModelRestoreVideos.getVideoRestore(requireContext())
        //
        viewModelRestoreVideos.position.observe(this) {
            binding.rclRestoreVideo.layoutManager?.apply {
                if (itemCount > it) {
                    scrollToPosition(it)
                }
            }
        }
    }

    override fun initListener() {
        adapterAlbumRestoreVideo?.listenerOnClick = {
            (activity as ActivitySearchRestore).fragmentDetailAlbumVideo(listAlbumRestoreVideo[it])
            viewModelRestoreVideos.listAlbumRestoreVideosLiveData.value =
                (listAlbumRestoreVideo).toMutableList()
            viewModelRestoreVideos.position.value =
                ((binding.rclRestoreVideo.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition())
        }
    }


    private fun checkDuplicateFolder(name: String): Boolean {
        for (i in listAlbumRestoreVideo) {
            if (File(i.path).name==name) {
                return true
            }
        }
        return false
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutFragmentAlbumVideosBinding {
        return LayoutFragmentAlbumVideosBinding.inflate(inflater, container, false)
    }
}