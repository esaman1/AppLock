package com.ezteam.applocker.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.ezteam.applocker.R
import com.ezteam.applocker.adapter.SelectQuestionAdapter
import com.ezteam.applocker.databinding.LayoutDialogSelectQuestionBinding
import com.ezteam.applocker.item.ItemSelectQuestion
import com.ezteam.applocker.key.KeyQuestion
import com.ezteam.baseproject.utils.PreferencesUtils

class DialogSelectQuestion(context: Context) : AlertDialog(context) {
    private lateinit var listItemSelectQuestion: MutableList<ItemSelectQuestion>
    private lateinit var binding: LayoutDialogSelectQuestionBinding
    var listenerClickOkay: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setCancelable(true)
        initData()
        initView()
        initListener()
    }

    private fun initData() {
        listItemSelectQuestion = mutableListOf()
        listItemSelectQuestion.apply {
            add(
                ItemSelectQuestion(
                    isSelected(context.getString(R.string.name_of_your_high_school)),
                    context.getString(R.string.name_of_your_high_school)
                )
            )
            add(
                ItemSelectQuestion(
                    isSelected(context.getString(R.string.name_of_your_first_pet)),
                    context.getString(R.string.name_of_your_first_pet)
                )
            )
            add(
                ItemSelectQuestion(
                    isSelected(context.getString(R.string.your_birthday)),
                    context.getString(R.string.your_birthday)
                )
            )
            add(
                ItemSelectQuestion(
                    isSelected(context.getString(R.string.your_lucky_number)),
                    context.getString(R.string.your_lucky_number)
                )
            )
            add(
                ItemSelectQuestion(
                    isSelected(context.getString(R.string.your_idol_name)),
                    context.getString(R.string.your_idol_name)
                )
            )
        }
    }

    private fun initListener() {
        binding.btnOk.setOnClickListener {
            for (item in listItemSelectQuestion) {
                if (item.isSelected) {
                    PreferencesUtils.putString(KeyQuestion.KEY_QUESTION, item.name)
                    dismiss()
                    listenerClickOkay?.invoke()
                }
            }
        }
    }

    private fun initView() {
        binding = LayoutDialogSelectQuestionBinding.inflate(LayoutInflater.from(context))
        val adapter = SelectQuestionAdapter(context, listItemSelectQuestion)
        adapter.onClickListener = {
            for (item in listItemSelectQuestion) {
                val begin = item.isSelected
                item.isSelected = item==listItemSelectQuestion[it]
                if (begin!=item.isSelected) {
                    adapter.notifyItemChanged(listItemSelectQuestion.indexOf(item))
                }
            }
        }
        binding.rclSelectedQuestion.adapter = adapter
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val animator: RecyclerView.ItemAnimator? = binding.rclSelectedQuestion.itemAnimator
        animator?.let {
            if (it is SimpleItemAnimator) {
                (it).supportsChangeAnimations = false
            }
        }
        setContentView(binding.root)
    }

    private fun isSelected(name: String): Boolean {
        return PreferencesUtils.getString(
            KeyQuestion.KEY_QUESTION, context.getString(R.string.recovery_code)
        )==name
    }
}