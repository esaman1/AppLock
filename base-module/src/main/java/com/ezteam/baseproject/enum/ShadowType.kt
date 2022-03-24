package com.ezteam.baseproject.enum

import android.graphics.Color
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import com.ezteam.baseproject.R
import com.ezteam.baseproject.extensions.retrieveColor
import com.ezteam.baseproject.view.VDSCardView

enum class ShadowType(
  @ColorRes val colorRes: Int = 0,
  @DimenRes val yOffset: Int = 0,
  @DimenRes val blurRadius: Int = 0,
  @DimenRes val cornerRes: Int = 0
) {
  TokenNone,
  TokenShadowsCard(
    colorRes = R.color.tokenBlack08,
    yOffset = R.dimen.tokenSpacing04,
    blurRadius = R.dimen.tokenBorderRadius12,
    cornerRes = R.dimen.tokenBorderRadius08
  ),
  TokenShadowsBottomTab(
    colorRes = R.color.tokenBlack08,
    yOffset = R.dimen._minus3sdp,
    blurRadius = R.dimen.tokenBorderRadius12
  );

  fun apply(
    cardView: VDSCardView,
    topLeftRadius: Float = -1f,
    topRightRadius: Float = -1f,
    bottomRightRadius: Float = -1f,
    bottomLeftRadius: Float = -1f
  ) {
    if (this == TokenNone) {
      cardView.setShadowColor(Color.TRANSPARENT)
      cardView.shadowLeft = 0f
      cardView.shadowTop = 0f
      cardView.shadowRight = 0f
      cardView.shadowBottom = 0f
      cardView.shadowSpace = 0f
    } else {
      val dY = cardView.resources.getDimension(yOffset)
      cardView.shadowDy = dY
      val radius = cardView.resources.getDimension(blurRadius)
      cardView.setShadowColor(cardView.context.retrieveColor(colorRes))
      cardView.setShadowBlur(radius)
      cardView.shadowSpace = radius
      radius.let {
        cardView.shadowLeft = if (this == TokenShadowsBottomTab) 0f else it
        cardView.shadowTop = it - dY
        cardView.shadowRight = if (this == TokenShadowsBottomTab) 0f else it
        cardView.shadowBottom = if (this == TokenShadowsBottomTab) 0f else it + dY.toInt()
      }
      if (this == TokenShadowsBottomTab || this == TokenShadowsCard) {
        val corner = if (cornerRes == 0) 0f else cardView.resources.getDimension(cornerRes)
        cardView.setCorners(
          if (topLeftRadius >= 0) topLeftRadius else corner,
          if (topRightRadius >= 0) topRightRadius else corner,
          if (bottomRightRadius >= 0) bottomRightRadius else corner,
          if (bottomLeftRadius >= 0) bottomLeftRadius else corner
        )
      } else {
        val corner = cardView.resources.getDimension(cornerRes)
        cardView.setCorners(corner, corner, corner, corner)
      }
    }
  }
}
