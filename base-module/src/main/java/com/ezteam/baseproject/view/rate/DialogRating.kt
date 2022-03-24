package com.ezteam.baseproject.view.rate

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.daimajia.androidanimations.library.Techniques
import com.ezteam.baseproject.R
import com.ezteam.baseproject.databinding.DialogRateAppBinding
import com.ezteam.baseproject.dialog.BaseDialog
import com.ezteam.baseproject.dialog.BuilderDialog
import com.ezteam.baseproject.listener.EzItemListener
import com.ezteam.baseproject.utils.PreferencesUtils
import com.ezteam.baseproject.utils.PresKey
import com.ezteam.baseproject.utils.ViewUtils.showViewBase
import java.util.*

open class DialogRating(
    context: Context,
        extendBuilder: ExtendBuilder
) : BaseDialog<DialogRateAppBinding, DialogRating.ExtendBuilder>(extendBuilder, context) {
    companion object {
        private const val COUNT_ACTION_SHOW = 2
    }
    private var listener: EzItemListener<DialogRatingState>? = null
    private val stars: MutableList<ImageView> = ArrayList()
    private var starPoint = 0
    override fun show() {
        val t = javaClass.simpleName
        setListener(builder.listener)
        val count = PreferencesUtils.getInteger(PresKey.COUNT_ACTION, 1)
        val isRate = PreferencesUtils.getBoolean(PresKey.RATE_APP)
        if ((count == 0 || builder.hardShow) && !isRate) {
            super.show()
        } else {
            if (listener != null) {
                listener!!.onListener(DialogRatingState.COUNT_TIME)
            }
        }

        if (!builder.hardShow) {
            if (count == COUNT_ACTION_SHOW) {
                PreferencesUtils.putInteger(PresKey.COUNT_ACTION, 0)
            } else {
                PreferencesUtils.putInteger(PresKey.COUNT_ACTION, count + 1)
            }
        }
    }

    override val viewBinding: DialogRateAppBinding
        get() = DialogRateAppBinding.inflate(
                LayoutInflater.from(
                        context
                )
        )

    override fun initView() {
        super.initView()
        builder.cancelable = false
        builder.canOnTouchOutside = true
        stars.add(binding.star1)
        stars.add(binding.star2)
        stars.add(binding.star3)
        stars.add(binding.star4)
        stars.add(binding.star5)

        binding.root.postDelayed({
            animationStart()
        }, 300)
    }

    open fun animationStart() {
        val duration = 100
        var timeDelay: Long = 0
        for (index in stars.indices) {
            timeDelay += (index * (duration / 2)).toLong()
            stars[index].visibility = View.GONE
            stars[index].setImageResource(R.drawable.ic_star_rate_fill)
            showViewBase(Techniques.ZoomIn, stars[index], duration.toLong(), timeDelay)
        }
        Handler().postDelayed({ animationEnd() }, timeDelay + 500)
    }

    open fun animationEnd() {
        for (star in stars) {
            star.setImageResource(R.drawable.ic_star_rate)
        }
    }

    private fun rating(vote: Int) {
        for (star in stars) {
            star.setImageResource(R.drawable.ic_star_rate)
        }
        for (index in 0 until vote) {
            stars[index].setImageResource(R.drawable.ic_star_rate_fill)
        }
        starPoint = vote
    }

    private fun rateResult(isGood: Boolean) {
        PreferencesUtils.putBoolean(PresKey.RATE_APP, isGood)
        if (listener != null) {
            listener?.onListener(if (isGood) DialogRatingState.RATE_GOOD else DialogRatingState.RATE_BAD)
        }
        dismiss()
    }

    override fun initListener() {
        val onClickListener = View.OnClickListener { v: View ->
            when (v.id) {
                R.id.star1 -> rating(1)
                R.id.star2 -> rating(2)
                R.id.star3 -> rating(3)
                R.id.star4 -> rating(4)
                R.id.star5 -> rating(5)
            }
        }
        for (imageView in stars) {
            imageView.setOnClickListener(onClickListener)
        }
        binding.tvRemind.setOnClickListener { v: View? ->
            if (listener != null) {
                listener!!.onListener(DialogRatingState.COUNT_TIME)
            }
            dismiss()
        }
        binding.buttonRate.setOnClickListener { v: View? ->
            if (starPoint == 0) {
                rateResult(true)
                return@setOnClickListener
            }
            rateResult(starPoint == 5)
        }
    }

    fun setListener(listener: EzItemListener<DialogRatingState>?) {
        this.listener = listener
    }

    class ExtendBuilder(context: Context) : BuilderDialog(context) {
        var listener: EzItemListener<DialogRatingState>? = null
        var hardShow: Boolean = false
        override fun build(): BaseDialog<*, *> {
            return DialogRating(context, this)
        }

        fun setListener(listener: EzItemListener<DialogRatingState>?): ExtendBuilder {
            this.listener = listener
            return this
        }

        fun setHardShow(hardShow: Boolean = false): ExtendBuilder {
            this.hardShow = hardShow
            return this
        }
    }
}