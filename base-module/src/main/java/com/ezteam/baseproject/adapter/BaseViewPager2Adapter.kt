package com.ezteam.baseproject.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ezteam.baseproject.adapter.model.PageModel

class BaseViewPager2Adapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {

    private val pageModels = mutableListOf<PageModel>()

    override fun getItemCount(): Int = pageModels.size

    override fun createFragment(position: Int): Fragment = pageModels[position].fragment

    fun addFragment(fragment: Fragment?, title: String?, resIconId: Int) {
        val model = PageModel(fragment, title, resIconId)
        pageModels.add(model)
    }

    fun addFragment(fragment: Fragment?, title: String?) {
        val model = PageModel(fragment, title)
        pageModels.add(model)
    }

    fun getItem(position: Int): Fragment {
        return pageModels[position].fragment
    }

    fun deletePage(position: Int) {
        pageModels.removeAt(position)
        notifyDataSetChanged()
    }

}