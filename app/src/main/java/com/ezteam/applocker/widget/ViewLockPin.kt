package com.ezteam.applocker.widget

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.ezteam.applocker.R
import com.ezteam.applocker.adapter.EnterPinAdapter
import com.ezteam.applocker.adapter.NumberAdapter
import com.ezteam.applocker.databinding.LayoutPinLockBinding
import com.ezteam.applocker.item.ItemEnterPin
import com.ezteam.applocker.item.ItemNumber
import com.ezteam.applocker.utils.ThemeUtils

class ViewLockPin(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    lateinit var binding: LayoutPinLockBinding
    val listEnterPin: MutableList<ItemEnterPin> = mutableListOf()
    private lateinit var listNumber: MutableList<ItemNumber>
    lateinit var adapterPin: EnterPinAdapter
    private lateinit var adapterNumber: NumberAdapter
    var listenerEnterComplete: ((MutableList<ItemEnterPin>) -> Unit)? = null
    var listenerEnter: ((MutableList<ItemEnterPin>) -> Unit)? = null
    var size = 0

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LockPin)
        size = typedArray.getInt(R.styleable.LockPin_size, 8)
        initData()
        initView()
        initListener()
    }

    private fun initData() { // list pin
        listEnterPin.clear()
        for (i in 1..size) {
            listEnterPin.add(ItemEnterPin())
        } // list number
        listNumber = mutableListOf()
        listNumber.addAll(ThemeUtils.listThemeNumber(context))
    }

    private fun initListener() {
        adapterNumber.listenerClickNumber = {
            var checkCompletePin = true
            when (listNumber[it].number) {
                -1 -> {
                    for (i in (listEnterPin.size - 1) downTo 0) {
                        if (listEnterPin[i].isEnterPin) {
                            listEnterPin[i].isEnterPin = false
                            listEnterPin[i].numberPin = -1
                            adapterPin.notifyItemChanged(i)
                            break
                        }
                    }
                }
                -2 -> {

                }
                else -> {
                    for (i in 0 until listEnterPin.size) {
                        val item = listEnterPin[i]
                        if (!item.isEnterPin) {
                            checkCompletePin = listEnterPin.indexOf(item)==(listEnterPin.size - 1)
                            item.isEnterPin = true
                            item.numberPin = it
                            adapterPin.notifyItemChanged(i)
                            break
                        }
                    }
                    //
                }
            }
            listenerEnter?.invoke(listEnterPin)
            if (checkCompletePin) {
                Handler().postDelayed({
                    listenerEnterComplete?.invoke(listEnterPin)
                }, 500)
            }
        }
    }

    private fun initView() {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_pin_lock, this, true)
        binding = LayoutPinLockBinding.bind(view)
        // rcl enter pin
        adapterPin = EnterPinAdapter(context, listEnterPin)
        binding.rclEnterPin.adapter = adapterPin
        // rcl number
        adapterNumber = NumberAdapter(context, listNumber)
        binding.rclNumber.layoutManager = object : GridLayoutManager(context, 3) {
            override fun canScrollVertically(): Boolean {
                return false
            }

            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        binding.rclNumber.adapter = adapterNumber
        //
    }

    fun clearEnterPin() {
        for (item in listEnterPin) {
            item.isEnterPin = false
            item.numberPin = -1
        }
        adapterPin.notifyDataSetChanged()
    }
}