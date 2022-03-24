package com.ezteam.applocker.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.ezteam.applocker.adapter.PagerRestoreAdapter
import com.ezteam.applocker.databinding.LayoutFragmentRestoreBinding
import com.ezteam.baseproject.fragment.BaseFragment

class FragmentRestore(var position: Int) : BaseFragment<LayoutFragmentRestoreBinding>() {

    override fun initView() {
        fragmentManager?.let {
            val adapter = PagerRestoreAdapter(childFragmentManager, requireContext())
            binding.viewPager.adapter = adapter
            binding.tabLayout.setupWithViewPager(binding.viewPager)
            binding.viewPager.setCurrentItem(position, true)
            binding.viewPager.offscreenPageLimit = 3
        }
    }

    override fun initData() {
    }

    override fun initListener() {
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutFragmentRestoreBinding {
        return LayoutFragmentRestoreBinding.inflate(inflater, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}