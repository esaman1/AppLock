package com.ezteam.applocker.interfaces

interface OnZoomLayoutListener {
    fun onScale(f: Float)
    fun onScaleFinished(f: Float)
    fun onTouchDown()
    fun onTouchUp()
    fun onZoomLayoutScale(f: Float)
}