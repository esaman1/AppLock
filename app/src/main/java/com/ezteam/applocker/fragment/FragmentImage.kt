package com.ezteam.applocker.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ezteam.applocker.activity.ShowImage
import com.ezteam.applocker.adapter.AdapterImageHide
import com.ezteam.applocker.databinding.LayoutFragmentImageBinding
import com.ezteam.applocker.item.ItemImageHide
import com.ezteam.applocker.key.Vault
import com.ezteam.applocker.viewmodel.HideImageViewModel
import com.ezteam.baseproject.fragment.BaseFragment
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.android.inject

class FragmentImage : BaseFragment<LayoutFragmentImageBinding>() {
    private lateinit var adapterImageHide: AdapterImageHide
    private var listImageHide = mutableListOf<ItemImageHide>()
    private val viewModel by inject<HideImageViewModel>()
    private val list = Hawk.get(Vault.KEY_FILE_NAME_IMAGE, mutableListOf<ItemImageHide>())
    override fun initView() {
        adapterImageHide = AdapterImageHide(requireContext(), listImageHide)
        binding.rclImageHide.adapter = adapterImageHide
    }

    override fun initData() {
        //list fist
        listImageHide.clear()
        viewModel.listImageHide.observe(this) {
            if (it?.bitmap!=null) {
                binding.animationView.visibility = View.INVISIBLE
                listImageHide.add(it)
                adapterImageHide.notifyItemInserted(listImageHide.indexOf(it))
            } else {
                if (listImageHide.isNotEmpty()) {
                    binding.animationView.visibility = View.INVISIBLE
                } else {
                    binding.animationView.visibility = View.VISIBLE
                }
            }
        }
        //
        val listImage = Hawk.get(Vault.KEY_FILE_NAME_IMAGE, mutableListOf<ItemImageHide>())
        if (!listImage.isNullOrEmpty()) {
            binding.animationView.visibility = View.INVISIBLE
            viewModel.decryptListImage(listImage)
        } else {
            binding.animationView.visibility = View.VISIBLE
        }
        //
        viewModel.imageHideSelected.observe(this) {
            if (it!=null) {
                for (i in it) {
                    if (i.bitmap!=null) {
                        if (listImageHide.size > 0) {
                            if (!checkSameBitmap(i.bitmap!!, listImageHide)) {
                                listImageHide.add(i)
                                adapterImageHide.notifyItemInserted(listImageHide.size - 1)
                            }
                        } else {
                            listImageHide.add(i)
                            adapterImageHide.notifyItemInserted(listImageHide.size - 1)
                        }
                    }
                    if (listImageHide.isEmpty()) {
                        binding.animationView.visibility = View.VISIBLE
                    } else {
                        binding.animationView.visibility = View.INVISIBLE
                    }
                }
                viewModel.imageHideSelected.value = null
            }
        }

        viewModel.recoverImage.observe(this) {
//            // ads
//            EzAdControl.getInstance(activity as ActivityVault).showAds()
            //
            for (item in listImageHide) {
                if (item.fileName==it.fileName) {
                    val pos = listImageHide.indexOf(item)
                    listImageHide.remove(item)
                    adapterImageHide.notifyItemRemoved(pos)
                    if (pos > 0) {
                        adapterImageHide.notifyItemChanged(pos - 1)
                    }
                    break
                }
            }
            if (listImageHide.isEmpty()) {
                binding.animationView.visibility = View.VISIBLE
            } else {
                binding.animationView.visibility = View.INVISIBLE
            }
        }
    }

    override fun initListener() {
        adapterImageHide.listenerOnClick = {
            val image = listImageHide[it]
            val name = image.fileName
            val path = image.thumbnailPath
            startActivity(
                Intent(requireContext(), ShowImage::class.java).apply {
                    putExtra(Vault.KEY_IMAGE_NAME, name)
                    putExtra(Vault.KEY_IMAGE_PATH, path)
                }
            )
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutFragmentImageBinding {
        return LayoutFragmentImageBinding.inflate(inflater, container, false)
    }

    override fun onStop() {
        super.onStop()
//        viewModel.decryptListImage(list)
    }

    fun checkSameBitmap(b: Bitmap, list: MutableList<ItemImageHide>): Boolean {
        for (i in list) {
            if (b.sameAs(i.bitmap)) {
                return true
            }
        }
        return false
    }
}