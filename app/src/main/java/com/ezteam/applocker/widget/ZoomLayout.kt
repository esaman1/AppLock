package com.ezteam.applocker.widget

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.LinearLayout
import com.ezteam.applocker.interfaces.OnZoomLayoutListener
import kotlin.math.sign


class ZoomLayout : LinearLayout, ScaleGestureDetector.OnScaleGestureListener,
    GestureDetector.OnDoubleTapListener,
    GestureDetector.OnGestureListener {
    private var dx = 0.0f
    private var dy = 0.0f
    var isEnableTouch = true
    private var lastScaleFactor = 0.0f
    private var mListener: OnZoomLayoutListener? = null
    private var mode = Mode.NONE
    var prevDx = 0.0f
        private set
    var prevDy = 0.0f
        private set
    private var scale = 1.0f
    private var startX = 0.0f
    private var startY = 0.0f
    private var scaleFactor = 0f

    private enum class Mode {
        NONE, DRAG, ZOOM
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        init(context)
    }

    constructor(context: Context, attributeSet: AttributeSet?, i: Int) : super(
        context,
        attributeSet,
        i
    ) {
        init(context)
    }

    private fun init(context: Context) {
        val gestureDetector = GestureDetector(context, this)
        gestureDetector.setOnDoubleTapListener(this)
        val scaleGestureDetector = ScaleGestureDetector(context, this)
        setOnTouchListener(object : OnTouchListener {
            override fun onTouch(view: View?, motionEvent: MotionEvent): Boolean {
                if (!isEnableTouch) {
                    return true
                }
                when (motionEvent.action and 255) {
                    MotionEvent.ACTION_DOWN -> {
                        setTouch(1)
                        if (scale > MIN_ZOOM) {
                            mode = Mode.DRAG
                            startX = motionEvent.x - prevDx
                            startY = motionEvent.y - prevDy
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        setTouch(2)
                        mode = Mode.NONE
                        prevDx = dx
                        prevDy = dy
                    }
                    MotionEvent.ACTION_MOVE -> if (mode == Mode.DRAG) {
                        dx = motionEvent.x - startX
                        dy = motionEvent.y - startY
                    }
                    MotionEvent.ACTION_POINTER_DOWN -> mode = Mode.ZOOM
                    MotionEvent.ACTION_POINTER_UP -> mode = Mode.NONE
                    else -> {
                    }
                }
                scaleGestureDetector.onTouchEvent(motionEvent)
                gestureDetector.onTouchEvent(motionEvent)
                if (mode == Mode.DRAG && scale >= MIN_ZOOM || mode == Mode.ZOOM) {
                    parent.requestDisallowInterceptTouchEvent(true)
                    val maxDx: Float = (child().getWidth() - child().getWidth() / scale) / 2 * scale
                    val maxDy: Float =
                        (child().getHeight() - child().getHeight() / scale) / 2 * scale
                    dx = Math.min(Math.max(dx, -maxDx), maxDx)
                    dy = Math.min(Math.max(dy, -maxDy), maxDy)
                    applyScaleAndTranslation()
                    if (maxDx != 0f && dx != 0f && dx == maxDx || dx * -1 == maxDx) {
                        prevDx = dx
                        prevDy = dy
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                return true
            }
        })
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent?) {}
    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {}
    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        val view: View = child()
        if (scale == 1.0f) {
            scale = 3.0f
            view.setPivotX(e.getX())
            view.setPivotY(e.getY())
        } else {
            scale = 1.0f
        }
        view.animate().scaleX(scale).scaleY(scale)
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScaleBegin(scaleGestureDetector: ScaleGestureDetector?): Boolean {
        return true
    }

    override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
        scaleFactor = scaleGestureDetector.scaleFactor
        val str = TAG
        val stringBuilder = StringBuilder()
        stringBuilder.append("onScale")
        stringBuilder.append(scaleGestureDetector)
        if (lastScaleFactor != 0.0f) {
            if (sign(scaleFactor) != sign(lastScaleFactor)) {
                lastScaleFactor = 0.0f
                mListener?.onScale(lastScaleFactor)
                return true
            }
        }
        scale *= scaleFactor
        scale = Math.max(MIN_ZOOM, scale.coerceAtMost(MAX_ZOOM))
        mListener?.onZoomLayoutScale(scale)
        lastScaleFactor = scaleFactor
        mListener?.onScale(lastScaleFactor)
        return true
    }

    override fun onScaleEnd(scaleGestureDetector: ScaleGestureDetector?) {
        mListener?.onScaleFinished(lastScaleFactor)
    }

    fun applyScaleAndTranslation() {
        val child: View = child()
        child.scaleX = scale
        child.scaleY = scale
        child.translationX = dx
        child.translationY = dy
    }

    fun child(): View {
        return getChildAt(0)
    }

    private fun setTouch(i: Int) {
        if (mListener == null) {
            return
        }
        if (i == 1) {
            mListener!!.onTouchDown()
        } else if (i == 2) {
            mListener!!.onTouchUp()
        }
    }

    fun reset() {
        val child: View = child()
        child.scaleX = 1.0f
        child.scaleY = 1.0f
        child.translationX = -0.0f
        child.translationY = -0.0f
        setLastScale(1.0f)
        setDxDy(-0.0f, 0.0f)
    }

    fun setLastScale(f: Float) {
        scale = f
    }

    fun setDxDy(f: Float, f2: Float) {
        dx = f
        dy = f2
        prevDx = f
        prevDy = f2
    }

    fun getDx(): Float {
        return dx
    }

    fun getDy(): Float {
        return dy
    }

    fun setDx(f: Float) {
        dx = f
    }

    fun setDy(f: Float) {
        dy = f
    }

    fun setListener(onZoomLayoutListener: OnZoomLayoutListener?) {
        mListener = onZoomLayoutListener
    }

    companion object {
        const val MAX_ZOOM = 8.0f
        const val MIN_ZOOM = 1.0f
        private const val TAG = "ZoomLayout"
    }
}
