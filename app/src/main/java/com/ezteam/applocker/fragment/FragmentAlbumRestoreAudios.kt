package com.ezteam.applocker.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezteam.applocker.activity.ActivitySearchRestore
import com.ezteam.applocker.adapter.AdapterAlbumRestoreAudios
import com.ezteam.applocker.databinding.LayoutFragmentAlbumAudioBinding
import com.ezteam.applocker.item.ItemAlbumRestoreAudios
import com.ezteam.applocker.utils.RecycleViewUtils
import com.ezteam.applocker.viewmodel.RestoreAudioViewModel
import com.ezteam.baseproject.fragment.BaseFragment
import org.koin.android.ext.android.inject
import java.io.File

class FragmentAlbumRestoreAudios : BaseFragment<LayoutFragmentAlbumAudioBinding>() {
    private val viewModelRestoreAudios by inject<RestoreAudioViewModel>()
    private var adapterAlbumRestoreAudios: AdapterAlbumRestoreAudios? = null
    private var listAlbumRestoreAudios = mutableListOf<ItemAlbumRestoreAudios>()
    override fun initView() {
        adapterAlbumRestoreAudios =
            AdapterAlbumRestoreAudios(requireContext(), listAlbumRestoreAudios)
        binding.rclRestoreAudios.adapter = adapterAlbumRestoreAudios
        RecycleViewUtils.clearAnimation(binding.rclRestoreAudios)
    }

    override fun initData() {
        listAlbumRestoreAudios.clear()
        viewModelRestoreAudios.listAlbumRestoreAudioLiveData.observe(this) {
            if (it!=null&&listAlbumRestoreAudios.isEmpty()) {
//                listAlbumRestoreAudios.clear()
                listAlbumRestoreAudios.addAll(it)
            }
        }
//        listAlbumRestoreAudios.clear()
        viewModelRestoreAudios.albumRestoreAudioLiveData.observe(this) {
            if (it!=null&&it.listAudios.isNotEmpty()&&!listAlbumRestoreAudios.contains(it)&&!checkDuplicateFolder(
                    File(it.path).name
                )
            ) {
                listAlbumRestoreAudios.add(it)
                adapterAlbumRestoreAudios?.notifyItemInserted(listAlbumRestoreAudios.size - 1)
            }
            if (listAlbumRestoreAudios.isEmpty()) {
                binding.txtNoAudios.visibility = View.VISIBLE
            } else {
                binding.txtNoAudios.visibility = View.INVISIBLE
            }
        }
        viewModelRestoreAudios.getAudiosRestore(requireContext())
        //
        viewModelRestoreAudios.position.observe(this) {
            binding.rclRestoreAudios.layoutManager?.apply {
                if (itemCount > it) {
                    scrollToPosition(it)
                }
            }
        }
    }

    private fun checkDuplicateFolder(name: String): Boolean {
        for (i in listAlbumRestoreAudios) {
            if (File(i.path).name==name) {
                return true
            }
        }
        return false
    }

    override fun initListener() {
        adapterAlbumRestoreAudios?.listenerOnClick = {
            (activity as ActivitySearchRestore).fragmentDetailAlbumAudio(listAlbumRestoreAudios[it])
            viewModelRestoreAudios.listAlbumRestoreAudioLiveData.value =
                (listAlbumRestoreAudios).toMutableList()
            viewModelRestoreAudios.position.value =
                ((binding.rclRestoreAudios.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition())
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutFragmentAlbumAudioBinding {
        return LayoutFragmentAlbumAudioBinding.inflate(inflater, container, false)
    }
}