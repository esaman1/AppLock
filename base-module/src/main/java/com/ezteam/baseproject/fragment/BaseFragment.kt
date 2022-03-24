package com.ezteam.baseproject.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.ezteam.baseproject.activity.BaseActivity
import com.ezteam.baseproject.activity.BetterActivityResult

open abstract class BaseFragment<B : ViewBinding> : Fragment() {
    protected lateinit var binding: B
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getViewBinding(inflater, container)
        return binding.root
    }

    protected val activityLauncher: BetterActivityResult<Intent, ActivityResult> =
        BetterActivityResult.registerActivityForResult(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, null)
        initView()
        initData()
        initListener()
    }

    protected abstract fun initView()
    protected abstract fun initData()
    protected abstract fun initListener()
    protected abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): B

    protected val baseActivity: BaseActivity<*>?
        protected get() = activity as BaseActivity<*>?

    fun toast(content: String?) {
        baseActivity?.toast(content)
    }

    fun backPress() {
        baseActivity?.onBackPressed()
    }
}