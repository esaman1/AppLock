package com.ezteam.baseproject.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ezteam.baseproject.adapter.model.PageModel
import java.util.*

class BasePagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {
    private val pageModels: MutableList<PageModel> = mutableListOf()

    fun addFragment(fragment: Fragment?, title: String?, resIconId: Int) {
        val model = PageModel(fragment, title, resIconId)
        pageModels.add(model)
    }

    fun addFragment(fragment: Fragment?, title: String?) {
        val model = PageModel(fragment, title)
        pageModels.add(model)
    }

    fun getTitle(position: Int): String {
        return pageModels[position].title
    }

    fun getResIconId(position: Int): Int {
        return pageModels[position].resIconId
    }

    override fun getItem(position: Int): Fragment {
        return pageModels[position].fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return pageModels[position].title
    }

    override fun getCount(): Int {
        return pageModels.size
    }
}