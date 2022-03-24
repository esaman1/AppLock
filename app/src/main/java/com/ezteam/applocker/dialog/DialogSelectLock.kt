package com.ezteam.applocker.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezteam.applocker.R
import com.ezteam.applocker.adapter.SelectLockAdapter
import com.ezteam.applocker.databinding.LayoutDialogSelectLockBinding
import com.ezteam.applocker.item.ItemSelectLock
import com.ezteam.applocker.key.KeyLock
import com.ezteam.applocker.utils.RecycleViewUtils
import com.ezteam.baseproject.utils.PreferencesUtils

class DialogSelectLock(context: Context, var style: String?) : AlertDialog(context) {
    private lateinit var listItemSelectLock: MutableList<ItemSelectLock>
    private lateinit var binding: LayoutDialogSelectLockBinding
    var listenerClickOkay: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setCancelable(true)
        initData()
        initView()
        initListener()
    }

    private fun initData() {
        listItemSelectLock = mutableListOf()
        listItemSelectLock.apply {
            add(
                ItemSelectLock(
                    isSelected(context.getString(R.string.pattern)),
                    context.getString(R.string.pattern)
                )
            )
            add(
                ItemSelectLock(
                    isSelected(context.getString(R.string.pin)), context.getString(R.string.pin)
                )
            )
        }
    }

    private fun initListener() {
        binding.btnOk.setOnClickListener {
            for (item in listItemSelectLock) {
                if (item.isSelected) {
//                    PreferencesUtils.putString(KeyLock.LOCK, item.name)
                    dismiss()
                    listenerClickOkay?.invoke(item.name)
                }
            }
        }
    }

    private fun initView() {
        binding = LayoutDialogSelectLockBinding.inflate(LayoutInflater.from(context))
        val adapter = SelectLockAdapter(context, listItemSelectLock)
        adapter.onClickListener = {
            for (item in listItemSelectLock) {
                val valueBl = item.isSelected
                if (valueBl!=(item==listItemSelectLock[it])) {
                    item.isSelected = item==listItemSelectLock[it]
                    adapter.notifyItemChanged(listItemSelectLock.indexOf(item))
                }
            }
        }
        binding.rclSelectedLock.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rclSelectedLock.adapter = adapter
        // remove animation rcl
        RecycleViewUtils.clearAnimation(binding.rclSelectedLock)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(binding.root)
    }

    private fun isSelected(name: String): Boolean {
        return if (style.isNullOrEmpty()) {
            PreferencesUtils.getString(KeyLock.LOCK, context.getString(R.string.pattern))==name
        } else {
            style==name
        }

    }
}