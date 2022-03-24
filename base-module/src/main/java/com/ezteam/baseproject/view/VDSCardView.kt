package com.ezteam.baseproject.view

import android.content.Context
import android.util.AttributeSet
import com.ezteam.baseproject.R
import com.ezteam.baseproject.enum.ShadowType

/**
 *
 */
open class VDSCardView @JvmOverloads constructor(
  context: Context,
  attributeSet: AttributeSet? = null,
  defStyleInt: Int = 0
) : ShadowView(context, attributeSet, defStyleInt) {

  var shadowType = ShadowType.TokenShadowsCard
    set(value) {
      field = value
      value.apply(this, shadowLeft, shadowTop, shadowRight, shadowBottom)
    }

  init {
    context.obtainStyledAttributes(attributeSet, R.styleable.VDSCardView).apply {
      shadowType = ShadowType.values()[getInteger(R.styleable.VDSCardView_cvShadowType, 1)]
      val cornerRadius = getDimension(R.styleable.VDSCardView_cvCornerRadius, -1f)
      if (cornerRadius >= 0) {
        shadowType.apply(this@VDSCardView, cornerRadius, cornerRadius, cornerRadius, cornerRadius)
      } else {
        val topLeft = getDimension(R.styleable.VDSCardView_cvTopLeftRadius, -1f)
        val topRight = getDimension(R.styleable.VDSCardView_cvTopRightRadius, -1f)
        val bottomRight = getDimension(R.styleable.VDSCardView_cvBottomRightRadius, -1f)
        val bottomLeft = getDimension(R.styleable.VDSCardView_cvBottomLeftRadius, -1f)
        shadowType.apply(this@VDSCardView, topLeft, topRight, bottomRight, bottomLeft)
      }
      val shadowMargin = getDimension(R.styleable.VDSCardView_cvShadowMargin, -1f)
      if (shadowMargin >= 0) {
        shadowLeft = shadowMargin
        shadowTop = shadowMargin
        shadowRight = shadowMargin
        shadowBottom = shadowMargin
      } else {
        shadowLeft = getDimension(R.styleable.VDSCardView_cvShadowMarginLeft, -1f)
        shadowTop = getDimension(R.styleable.VDSCardView_cvShadowMarginTop, -1f)
        shadowRight = getDimension(R.styleable.VDSCardView_cvShadowMarginRight, -1f)
        shadowBottom = getDimension(R.styleable.VDSCardView_cvShadowMarginBottom, -1f)
      }
      recycle()
    }
  }
}
