package com.ezteam.baseproject.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import com.ezteam.baseproject.R
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Suppress("MemberVisibilityCanBePrivate", "unused")
open class ShadowView : FrameLayout {

  private val defaultShadowArea = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    8f,
    resources.displayMetrics
  )
  private val defaultStrokeDashSize = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    8f, resources.displayMetrics
  )
  private val defaultStrokeGapSize = defaultStrokeDashSize
  private val shadowMaskError = 0.5f

  enum class BackgroundType {
    Fill,
    Stroke,
    FillStroke
  }

  enum class StrokeType {
    Solid,
    Dash
  }

  enum class ColorType {
    Solid,
    GradientLinear,
    GradientRadial,
    GradientSweep
  }

  enum class CapType {
    Square,
    Butt,
    Round
  }

  enum class CornerType {
    Custom,
    Rectangular,
    Circular,
    Third,
    Quarter
  }

  var backgroundType: BackgroundType = DEFAULT_BACKGROUND_TYPE
  var backgroundColorType: ColorType = DEFAULT_BACKGROUND_COLOR_TYPE
  var bgColor: Int = DEFAULT_COLOR_BACKGROUND

  private var backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private var backgroundPath = Path()
  var backgroundAlpha: Float = NOT_DEFINED_ALPHA
    set(value) {
      field = floatPercentCheck(value)
    }
  private lateinit var backgroundPathRadius: FloatArray
  private var backgroundPathRadiusUpdated = false

  private var bgGradientColorsXml = IntArray(8) { NOT_DEFINED_COLOR }
  lateinit var bgGradientColors: IntArray
  var bgGradientAngle: Float = NOT_DEFINED_ANGLE
    set(value) {
      field = angleCheck(value)
    }
  var bgGradientOffCenterX = DEFAULT_OFF_CENTER_X
    set(value) {
      field = boundaryCheck(value, 1f)
    }
  var bgGradientOffCenterY = DEFAULT_OFF_CENTER_Y
    set(value) {
      field = boundaryCheck(value, 1f)
    }

  var strokeType: StrokeType = DEFAULT_STROKE_TYPE
  var strokeColorType: ColorType = DEFAULT_STROKE_COLOR_TYPE
  var strokeColor: Int = DEFAULT_COLOR_STROKE
  private var strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private var strokeMask = Path()
  private var strokePath = Path()
  private var noStrokePath = Path()
  var strokeAlpha: Float = NOT_DEFINED_ALPHA
    set(value) {
      field = floatPercentCheck(value)
    }
  private lateinit var strokeMaskRadius: FloatArray
  private var strokeMaskRadiusUpdated = false
  private lateinit var strokePathRadius: FloatArray
  private var strokePathRadiusUpdated = false

  var strWidth: Float = DEFAULT_STROKE_SIZE
    set(value) {
      field = dimenCheck(value, null)
    }
  var strokeDashSize: Float = defaultStrokeDashSize
    set(value) {
      field = dimenCheck(value, null)
    }
  var strokeGapSize: Float = defaultStrokeGapSize
    set(value) {
      field = dimenCheck(value, null)
    }
  var strokeCapType: CapType = DEFAULT_STROKE_CAP_TYPE

  private var strokeGradientColorsXml = IntArray(8) { NOT_DEFINED_COLOR }
  lateinit var strokeGradientColors: IntArray
  var strokeGradientAngle = NOT_DEFINED_ANGLE
    set(value) {
      field = angleCheck(value)
    }

  var strokeGradientOffCenterX: Float = DEFAULT_OFF_CENTER_X
    set(value) {
      field = boundaryCheck(value, 1f)
    }
  var strokeGradientOffCenterY: Float = DEFAULT_OFF_CENTER_Y
    set(value) {
      field = boundaryCheck(value, 1f)
    }

  private class ShadowObject {
    var color = DEFAULT_SHADOW_COLOR
    var alpha = NOT_DEFINED_ALPHA
    var distance = NOT_DEFINED_DIMEN
    var blur = NOT_DEFINED_DIMEN
    var angle = NOT_DEFINED_ANGLE
    var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var path = Path()
    var mask = Path()
  }

  private var shadow = ShadowObject()

  var shadowSpace: Float = NOT_DEFINED_SHADOW_SHADOW_AREA
    set(value) {
      field = dimenCheck(value, NOT_DEFINED_SHADOW_SHADOW_AREA)
      initPadding()
    }

  var shadowLeft: Float = NOT_DEFINED_SHADOW_SHADOW_AREA
    set(value) {
      field = dimenCheck(value, NOT_DEFINED_SHADOW_SHADOW_AREA)
      initPadding()
    }
  var shadowRight: Float = NOT_DEFINED_SHADOW_SHADOW_AREA
    set(value) {
      field = dimenCheck(value, NOT_DEFINED_SHADOW_SHADOW_AREA)
      initPadding()
    }
  var shadowTop: Float = NOT_DEFINED_SHADOW_SHADOW_AREA
    set(value) {
      field = dimenCheck(value, NOT_DEFINED_SHADOW_SHADOW_AREA)
      initPadding()
    }
  var shadowBottom: Float = NOT_DEFINED_SHADOW_SHADOW_AREA
    set(value) {
      field = dimenCheck(value, NOT_DEFINED_SHADOW_SHADOW_AREA)
      initPadding()
    }

  var shadowDy: Float = NOT_DEFINED_SHADOW_SHADOW_AREA

  // Corner Fields
  var cornersRadius: Float = NOT_DEFINED_CORNER_RADIUS
    set(value) {
      field = dimenCheck(value, NOT_DEFINED_CORNER_RADIUS)
      cornerUpdated()
    }
  var cornerRadiusTopLeft: Float = NOT_DEFINED_CORNER_RADIUS
    set(value) {
      field = dimenCheck(value, NOT_DEFINED_CORNER_RADIUS)
      cornerUpdated()
    }
  var cornerRadiusTopRight: Float = NOT_DEFINED_CORNER_RADIUS
    set(value) {
      field = dimenCheck(value, NOT_DEFINED_CORNER_RADIUS)
      cornerUpdated()
    }
  var cornerRadiusBottomLeft: Float = NOT_DEFINED_CORNER_RADIUS
    set(value) {
      field = dimenCheck(value, NOT_DEFINED_CORNER_RADIUS)
      cornerUpdated()
    }
  var cornerRadiusBottomRight: Float = NOT_DEFINED_CORNER_RADIUS
    set(value) {
      field = dimenCheck(value, NOT_DEFINED_CORNER_RADIUS)
      cornerUpdated()
    }
  var cornerType = DEFAULT_CORNER_TYPE
    set(value) {
      field = value
      cornerUpdated()
    }

  var onTouchedStyle = NOT_DEFINED_STYLE
  var onTouchedAnimate = NOT_DEFINED_ANIMATE
  var onTouchedDuration = DEFAULT_DURATION

  constructor(context: Context) : super(context) {
    initialize(context, null)
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    initialize(context, attrs)
  }

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    initialize(context, attrs)
  }

  private fun initialize(context: Context, attrs: AttributeSet?) {
    attrs.let {
      val typedArray = context.obtainStyledAttributes(it, R.styleable.ShadowView)
      // Get Background Style from XML
      var index = typedArray.getInteger(R.styleable.ShadowView_svBackgroundType, DEFAULT_BACKGROUND_TYPE.ordinal)
      backgroundType = BackgroundType.values()[index]
      index = typedArray.getInteger(R.styleable.ShadowView_svBackgroundColorType, DEFAULT_BACKGROUND_COLOR_TYPE.ordinal)
      backgroundColorType = ColorType.values()[index]
      bgColor = typedArray.getColor(R.styleable.ShadowView_svBackgroundColor, DEFAULT_COLOR_BACKGROUND)
      val defaultAlpha = NOT_DEFINED_ALPHA
      backgroundAlpha = floatPercentCheck(typedArray.getFloat(R.styleable.ShadowView_svBackgroundAlpha, defaultAlpha))
      val defaultColor = NOT_DEFINED_COLOR
      bgGradientColorsXml[0] = typedArray.getColor(R.styleable.ShadowView_svBackgroundGradientColor0, defaultColor)
      bgGradientColorsXml[1] = typedArray.getColor(R.styleable.ShadowView_svBackgroundGradientColor1, defaultColor)
      bgGradientColorsXml[2] = typedArray.getColor(R.styleable.ShadowView_svBackgroundGradientColor2, defaultColor)
      bgGradientColorsXml[3] = typedArray.getColor(R.styleable.ShadowView_svBackgroundGradientColor3, defaultColor)
      bgGradientColorsXml[4] = typedArray.getColor(R.styleable.ShadowView_svBackgroundGradientColor4, defaultColor)
      bgGradientColorsXml[5] = typedArray.getColor(R.styleable.ShadowView_svBackgroundGradientColor5, defaultColor)
      bgGradientColorsXml[6] = typedArray.getColor(R.styleable.ShadowView_svBackgroundGradientColor6, defaultColor)
      bgGradientColorsXml[7] = typedArray.getColor(R.styleable.ShadowView_svBackgroundGradientColorEnd, defaultColor)
      val defaultAngle = NOT_DEFINED_ANGLE
      bgGradientAngle = angleCheck(typedArray.getFloat(R.styleable.ShadowView_svBackgroundGradientAngle, defaultAngle))
      bgGradientOffCenterX = boundaryCheck(
        typedArray.getFloat(
          R.styleable.ShadowView_svBackgroundGradientOffCenterX,
          DEFAULT_OFF_CENTER_X
        ),
        1f
      )
      bgGradientOffCenterY = boundaryCheck(
        typedArray.getFloat(
          R.styleable.ShadowView_svBackgroundGradientOffCenterY,
          DEFAULT_OFF_CENTER_Y
        ),
        1f
      )
      index = typedArray.getInteger(R.styleable.ShadowView_svStrokeColorType, DEFAULT_STROKE_COLOR_TYPE.ordinal)
      strokeColorType = ColorType.values()[index]
      strokeColor = typedArray.getColor(R.styleable.ShadowView_svStrokeColor, DEFAULT_COLOR_STROKE)
      strokeAlpha = floatPercentCheck(typedArray.getFloat(R.styleable.ShadowView_svStrokeAlpha, defaultAlpha))
      strWidth = dimenCheck(typedArray.getDimension(R.styleable.ShadowView_svStrokeWidth, DEFAULT_STROKE_SIZE), null)
      strokeGradientColorsXml[0] = typedArray.getColor(R.styleable.ShadowView_svStrokeGradientColor0, defaultColor)
      strokeGradientColorsXml[1] = typedArray.getColor(R.styleable.ShadowView_svStrokeGradientColor1, defaultColor)
      strokeGradientColorsXml[2] = typedArray.getColor(R.styleable.ShadowView_svStrokeGradientColor2, defaultColor)
      strokeGradientColorsXml[3] = typedArray.getColor(R.styleable.ShadowView_svStrokeGradientColor3, defaultColor)
      strokeGradientColorsXml[4] = typedArray.getColor(R.styleable.ShadowView_svStrokeGradientColor4, defaultColor)
      strokeGradientColorsXml[5] = typedArray.getColor(R.styleable.ShadowView_svStrokeGradientColor5, defaultColor)
      strokeGradientColorsXml[6] = typedArray.getColor(R.styleable.ShadowView_svStrokeGradientColor6, defaultColor)
      strokeGradientColorsXml[7] = typedArray.getColor(R.styleable.ShadowView_svStrokeGradientColorEnd, defaultColor)
      strokeGradientAngle = angleCheck(typedArray.getFloat(R.styleable.ShadowView_svStrokeGradientAngle, defaultAngle))
      strokeGradientOffCenterX = boundaryCheck(
        typedArray.getFloat(
          R.styleable.ShadowView_svStrokeGradientOffCenterX,
          DEFAULT_OFF_CENTER_X
        ),
        1f
      )
      strokeGradientOffCenterY = boundaryCheck(
        typedArray.getFloat(
          R.styleable.ShadowView_svStrokeGradientOffCenterY,
          DEFAULT_OFF_CENTER_Y
        ),
        1f
      )
      strokeDashSize = dimenCheck(
        typedArray.getDimension(
          R.styleable.ShadowView_svStrokeDashSize,
          defaultStrokeDashSize
        ),
        null
      )
      strokeGapSize = dimenCheck(
        typedArray.getDimension(
          R.styleable.ShadowView_svStrokeGapSize,
          defaultStrokeGapSize
        ),
        null
      )
      index = typedArray.getInteger(R.styleable.ShadowView_svStrokeCapType, DEFAULT_STROKE_CAP_TYPE.ordinal)
      strokeCapType = CapType.values()[index]
      // Get Shadow Data from XML
      shadow.color = typedArray.getColor(R.styleable.ShadowView_svShadowColor, DEFAULT_SHADOW_COLOR)
      shadow.alpha = floatPercentCheck(typedArray.getFloat(R.styleable.ShadowView_svShadowAlpha, NOT_DEFINED_ALPHA))
      shadow.distance = dimenCheck(
        typedArray.getDimension(
          R.styleable.ShadowView_svShadowDistance, NOT_DEFINED_DIMEN
        ),
        NOT_DEFINED_DIMEN
      )
      shadow.blur = dimenCheck(
        typedArray.getDimension(
          R.styleable.ShadowView_svShadowBlur, NOT_DEFINED_DIMEN
        ),
        NOT_DEFINED_DIMEN
      )
      shadow.angle = angleCheck(typedArray.getFloat(R.styleable.ShadowView_svShadowAngle, NOT_DEFINED_ANGLE))
      shadowSpace = dimenCheck(
        typedArray.getDimension(
          R.styleable.ShadowView_svShadowArea,
          NOT_DEFINED_SHADOW_SHADOW_AREA
        ),
        NOT_DEFINED_SHADOW_SHADOW_AREA
      )
      shadowLeft = dimenCheck(
        typedArray.getDimension(
          R.styleable.ShadowView_svShadowLeft,
          NOT_DEFINED_SHADOW_SHADOW_AREA
        ),
        NOT_DEFINED_SHADOW_SHADOW_AREA
      )
      shadowTop = dimenCheck(
        typedArray.getDimension(
          R.styleable.ShadowView_svShadowTop,
          NOT_DEFINED_SHADOW_SHADOW_AREA
        ),
        NOT_DEFINED_SHADOW_SHADOW_AREA
      )
      shadowRight = dimenCheck(
        typedArray.getDimension(
          R.styleable.ShadowView_svShadowRight,
          NOT_DEFINED_SHADOW_SHADOW_AREA
        ),
        NOT_DEFINED_SHADOW_SHADOW_AREA
      )
      shadowBottom = dimenCheck(
        typedArray.getDimension(
          R.styleable.ShadowView_svShadowBottom,
          NOT_DEFINED_SHADOW_SHADOW_AREA
        ),
        NOT_DEFINED_SHADOW_SHADOW_AREA
      )

      shadowDy = typedArray.getDimension(R.styleable.ShadowView_svShadowDy, NOT_DEFINED_SHADOW_DY)

      // Get Corner Radius from XML
      val default = NOT_DEFINED_CORNER_RADIUS
      cornersRadius = typedArray.getDimension(R.styleable.ShadowView_svCornerRadius, default)
      cornerRadiusTopLeft = typedArray.getDimension(R.styleable.ShadowView_svCornerTopLeft, default)
      cornerRadiusTopRight = typedArray.getDimension(R.styleable.ShadowView_svCornerTopRight, default)
      cornerRadiusBottomRight = typedArray.getDimension(R.styleable.ShadowView_svCornerBottomRight, default)
      cornerRadiusBottomLeft = typedArray.getDimension(R.styleable.ShadowView_svCornerBottomLeft, default)
      val position = typedArray.getInteger(R.styleable.ShadowView_svCornerType, DEFAULT_CORNER_TYPE.ordinal)
      cornerType = CornerType.values()[position]
      typedArray.recycle()
    }

    setWillNotDraw(false)
    clipToPadding = false
    initLayerTypes(backgroundPaint, strokePaint, shadow.paint)
    initShadowArea()
    initPadding()
    initBackgroundGradientColors()
    initStrokeGradientColors()
  }

  private fun initShadowArea() {
    if (shadowSpace == NOT_DEFINED_SHADOW_SHADOW_AREA)
      if (haveShadow() && shadowSpace != 0f)
        shadowSpace = defaultShadowArea
  }

  private fun initPadding() {
    setPadding(
      getShadowShadowLeft().toInt(),
      getShadowShadowTop().toInt(),
      getShadowShadowRight().toInt(),
      getShadowShadowBottom().toInt()
    )
  }

  private fun initLayerTypes(vararg paints: Paint) {
    for (p in paints)
      if (android.os.Build.VERSION.SDK_INT < 28)
        setLayerType(View.LAYER_TYPE_SOFTWARE, p)
      else
        setLayerType(View.LAYER_TYPE_HARDWARE, p)
  }

  private fun initShadow(shadow: ShadowObject) {
    initShadowPaint(shadow)
    initShadowPath(shadow)
    initShadowMask(shadow)
  }

  private fun initShadowPaint(shadow: ShadowObject) {
    shadow.paint.style = Paint.Style.FILL
    shadow.paint.color = Color.rgb(128, 128, 128)
    shadow.paint.setShadowLayer(
      shadow.blur,
      getDx(shadow.distance, -shadow.angle),
      getDy(shadow.distance, -shadow.angle),
      assignColorAlpha(shadow.color, shadow.alpha)
    )
  }

  private fun initShadowPath(shadow: ShadowObject) {
    shadow.path.reset()
    addBackgroundRectF(shadow.path)
  }

  private fun initShadowMask(shadow: ShadowObject) {
    shadow.mask.reset()
    addBoundaryRectF(shadow.mask)
//        addBackgroundRectF(shadow.mask)
    addBackgroundShadowMaskRectF(shadow.mask)
    shadow.mask.fillType = Path.FillType.EVEN_ODD
  }

  /**
   * Init background paint, style is fill.
   *
   */
  private fun initBackgroundPaint() {
    backgroundPaint.style = Paint.Style.FILL
    backgroundPaint.alpha = mapAlphaTo255(backgroundAlpha)
    backgroundPaint.color = bgColor
    when (backgroundColorType) {
      ColorType.GradientLinear -> backgroundPaint.shader = getLinearShader(
        bgGradientColors,
        bgGradientAngle
      )
      ColorType.GradientRadial -> backgroundPaint.shader = getRadialShader(
        bgGradientColors,
        bgGradientOffCenterX,
        bgGradientOffCenterY
      )
      ColorType.GradientSweep -> backgroundPaint.shader = getSweepShader(
        bgGradientColors,
        bgGradientAngle,
        bgGradientOffCenterX,
        bgGradientOffCenterY
      )
      else -> {
      }
    }
  }

  /**
   * Init stroke paint for dashed and non-dashed stroke path effect.
   * If path effect is dashed, style will be stroke else style will be fill and we will use mask.
   *
   */
  private fun initStrokePaint() {
    if (isDashed()) {
      strokePaint.style = Paint.Style.STROKE
      strokePaint.pathEffect = DashPathEffect(floatArrayOf(strokeDashSize, strokeGapSize), 0f)
      strokePaint.strokeCap = getStrokeCap(strokeCapType)
      strokePaint.strokeWidth = getStrokeWidth()
    } else {
      strokePaint.style = Paint.Style.FILL
      strokePaint.alpha = mapAlphaTo255(strokeAlpha)
      strokePaint.color = strokeColor
    }
    when (strokeColorType) {
      ColorType.GradientLinear -> strokePaint.shader = getLinearShader(
        strokeGradientColors,
        strokeGradientAngle
      )
      ColorType.GradientSweep -> strokePaint.shader = getSweepShader(
        strokeGradientColors,
        strokeGradientAngle,
        strokeGradientOffCenterX,
        strokeGradientOffCenterY
      )
      else -> {
      }
    }
  }

  /**
   * Init stroke path radius (to be used in non-dashed style)
   *
   */
  private fun initStrokeMaskRadius() {
    if (!strokeMaskRadiusUpdated) {
      strokeMaskRadius = getCornerRadius(
        getCornerRadius(),
        cornerRadiusTopLeft,
        cornerRadiusTopRight,
        cornerRadiusBottomRight,
        cornerRadiusBottomLeft,
        // We ignore stroke part, so we inset all stroke width
        // Because we want to use this as cut out.
        getStrokeWidth()
      )
      strokeMaskRadiusUpdated = true
    }
  }

  /**
   * Init stroke path radius (to be used in dashed style)
   *
   */
  private fun initStrokePathRadius() {
    if (!strokePathRadiusUpdated) {
      strokePathRadius = getCornerRadius(
        getCornerRadius(),
        cornerRadiusTopLeft,
        cornerRadiusTopRight,
        cornerRadiusBottomRight,
        cornerRadiusBottomLeft,
        // Half of stroke width for inset because stroke is drawn on path
        getStrokeWidth() / 2
      )
      strokePathRadiusUpdated = true
    }
  }

  /**
   * Init background corner radius.
   *
   */
  private fun initBackgroundCornerRadius() {
    if (!backgroundPathRadiusUpdated) {
      backgroundPathRadius = getCornerRadius(
        getCornerRadius(),
        cornerRadiusTopLeft,
        cornerRadiusTopRight,
        cornerRadiusBottomRight,
        cornerRadiusBottomLeft,
        0f
      )
      backgroundPathRadiusUpdated = true
    }
  }

  /**
   *  Init background path
   * Adds background rectF.
   *
   */
  private fun initBackgroundPath() {
    backgroundPath.reset()
    addBackgroundRectF(backgroundPath)
  }

  /**
   * Init stroke path for dashed stroke style.
   * Adds stroke path rectF.
   *
   */
  private fun initStrokePath() {
    strokePath.reset()
    addStrokePath(strokePath)
  }

  /**
   * Init stroke mask for non-dashed stroke style.
   * Adds background rectF and cuts it by no stroke rectF.
   *
   */
  private fun initStrokeMask() {
    strokeMask.reset()
    addBackgroundRectF(strokeMask)
    addNoStrokeAreaRectF(strokeMask)
    strokeMask.fillType = Path.FillType.EVEN_ODD
  }

  /**
   * Init no stroke area path.
   * Will be used to clip children of this container.
   *
   */
  private fun initNoStrokePath() {
    noStrokePath.reset()
    addNoStrokeAreaRectF(noStrokePath)
  }

  /**
   * Assign alpha to background gradient colors
   *
   */
  private fun initBackgroundGradientColors() {
    bgGradientColors = getColorArray(bgGradientColorsXml, backgroundAlpha)
  }

  /**
   * Assign alpha to stroke gradient colors
   *
   */
  private fun initStrokeGradientColors() {
    strokeGradientColors = getColorArray(strokeGradientColorsXml, strokeAlpha)
  }

  /**
   * Assign inset to corner radius
   * Positive inset will make corners less curve (because boundaries are smaller)
   * Negative inset will make corners more curve (because boundaries are larger)
   * Also check if individual corner radius is set.
   *
   */
  private fun getCornerRadius(
    radius: Float,
    topLeft: Float,
    topRight: Float,
    bottomRight: Float,
    bottomLeft: Float,
    inset: Float
  ): FloatArray {
    val cornerRadius = if (radius == NOT_DEFINED_CORNER_RADIUS) {
      DEFAULT_CORNER_RADIUS
    } else {
      dimenCheck(radius - inset, null)
    }
    val cornerRadiusTopLeft = if (topLeft == NOT_DEFINED_CORNER_RADIUS) {
      cornerRadius
    } else {
      dimenCheck(topLeft - inset, null)
    }
    val cornerRadiusTopRight = if (topRight == NOT_DEFINED_CORNER_RADIUS) {
      cornerRadius
    } else {
      dimenCheck(topRight - inset, null)
    }
    val cornerRadiusBottomRight = if (bottomRight == NOT_DEFINED_CORNER_RADIUS) {
      cornerRadius
    } else {
      dimenCheck(bottomRight - inset, null)
    }
    val cornerRadiusBottomLeft = if (bottomLeft == NOT_DEFINED_CORNER_RADIUS) {
      cornerRadius
    } else {
      dimenCheck(bottomLeft - inset, null)
    }
    return floatArrayOf(
      cornerRadiusTopLeft, cornerRadiusTopLeft,
      cornerRadiusTopRight, cornerRadiusTopRight,
      cornerRadiusBottomRight, cornerRadiusBottomRight,
      cornerRadiusBottomLeft, cornerRadiusBottomLeft
    )
  }

  /**
   * Normalize color array that is send from xml,
   * It will append color in series, for example if only colors of 3, 5, 6 is provided, it will map these color indexes, to 0, 1, 2.
   * It will append first color in array, if last color is not defined.
   * It will set default color values, if no colors are defined.
   *
   */
  private fun getColorArray(colors: IntArray, alpha: Float): IntArray {
    val colorArray = ArrayList<Int>()
    for (c in colors)
      if (c != NOT_DEFINED_COLOR)
        colorArray.add(
          assignColorAlpha(c, alpha)
        )
    if (colors.last() == NOT_DEFINED_COLOR && colorArray.size != 0)
      colorArray.add(colorArray.first())
    else if (colorArray.size == 0) {
      colorArray.add(DEFAULT_COLOR_STROKE)
      colorArray.add(DEFAULT_COLOR_STROKE)
    }
    return colorArray.toIntArray()
  }

  /**
   * Assign alpha to color even if the color has its own alpha.
   * For example if the color has alpha of 0.5, assigning the alpha of 0.5 to it, will make the result color alpha to be 0.5 * 0.5 = 0.25
   *
   */
  private fun assignColorAlpha(color: Int, alpha: Float): Int {
    return Color.argb(
      mapAlphaTo255(
        alpha * mapAlphaTo1(Color.alpha(color))
      ),
      Color.red(color),
      Color.green(color),
      Color.blue(color)
    )
  }

  // It automates corner radius.
  private fun getCornerRadius(): Float {
    return when (cornerType) {
      CornerType.Custom -> cornersRadius
      CornerType.Rectangular -> 0f
      CornerType.Circular -> min(getActualWidth(), getActualHeight()) / 2
      CornerType.Third -> min(getActualWidth(), getActualHeight()) / 3
      CornerType.Quarter -> min(getActualWidth(), getActualHeight()) / 4
    }
  }

  /**
   * Check dimen to be not negative.
   * Allowed dimen is used when we want dimen to be negative (For example, when we not set it)
   *
   */
  private fun dimenCheck(dimen: Float, allowedDimen: Float?): Float {
    if (allowedDimen != null)
      if (dimen == allowedDimen)
        return dimen
    return if (dimen >= 0f)
      dimen
    else
      0f
  }

  /**
   * Helper function that calculates left, top, right, bottom values base on inputs and creates the RectF
   *
   */
  private fun getRectF(width: Float, height: Float, inset: Float): RectF {
    return getRectF(width, height, inset, inset, inset, inset)
  }

  private fun getRectF(
    width: Float,
    height: Float,
    insetLeft: Float,
    insetTop: Float,
    insetRight: Float,
    insetBottom: Float
  ): RectF {
    return RectF(0f + insetLeft, 0f + insetTop, width - insetRight, height - insetBottom)
  }

  /**
   * Create inset background path rectF
   *
   */
  private fun addBackgroundRectF(path: Path) {
    path.addRoundRect(
      getRectF(
        measuredWidth.toFloat(),
        measuredHeight.toFloat(),
        getShadowShadowLeft(),
        getShadowShadowTop(),
        getShadowShadowRight(),
        getShadowShadowBottom()
      ),
      getBackgroundPathRadius(),
      Path.Direction.CW
    )
  }

  /**
   * Shadow is drawn between actual boundary (view boundary) and the inset boundary.
   * So if we have stroke type background color, we don't want shadow to draw inside stroked path
   * (because path is a cut out path) so we need to mask shadow drawing to only draw outside inset boundary
   *
   */
  private fun addBackgroundShadowMaskRectF(path: Path) {
    path.addRoundRect(
      getRectF(
        measuredWidth.toFloat(),
        measuredHeight.toFloat(),
        getShadowShadowLeft() - shadowMaskError,
        getShadowShadowTop() - shadowMaskError,
        getShadowShadowRight() - shadowMaskError,
        getShadowShadowBottom() - shadowMaskError
      ),
      getBackgroundPathRadius(),
      Path.Direction.CW
    )
  }

  /**
   * This function is used when we have dashed type stroke. As the stroke type drawing, draws stroke on path.
   * So we need to create rectF smaller by half of stroke width.
   *
   */
  private fun addStrokePath(path: Path) {
    val strokeWidth = getStrokeWidth() / 2
    path.addRoundRect(
      getRectF(
        measuredWidth.toFloat(),
        measuredHeight.toFloat(),
        getShadowShadowLeft() + strokeWidth,
        getShadowShadowTop() + strokeWidth,
        getShadowShadowRight() + strokeWidth,
        getShadowShadowBottom() + strokeWidth
      ),
      getStrokePathRadius(),
      Path.Direction.CW
    )
  }

  /**
   * Adds cut out rectF that is not include any stroke inside.
   * This function will be used by stroke path initializer, and by inner shadow path initializer.
   * Both of these function, will use this rectF as cut out.
   *
   */
  private fun addNoStrokeAreaRectF(path: Path) {
    path.addRoundRect(
      getRectF(
        measuredWidth.toFloat(),
        measuredHeight.toFloat(),
        getShadowShadowLeft() + getStrokeWidth(),
        getShadowShadowTop() + getStrokeWidth(),
        getShadowShadowRight() + getStrokeWidth(),
        getShadowShadowBottom() + getStrokeWidth()
      ),
      getStrokeMaskRadius(),
      Path.Direction.CW
    )
  }

  /**
   * Adds boundary rectF without considering corner radius
   *
   */
  private fun addBoundaryRectF(path: Path) {
    path.addRect(
      getRectF(
        measuredWidth.toFloat(),
        measuredHeight.toFloat(),
        0f
      ),
      Path.Direction.CW
    )
  }

  /**
   * Map angle to 0 - 360 range
   *
   */
  private fun angleCheck(angle: Float): Float {
    return if (angle < 0)
      angleCheck(angle + 360)
    else
      angle % 360
  }

  /**
   * Checks if a float number is in 0 - 1 range and clamps it
   *
   */
  private fun floatPercentCheck(alpha: Float): Float {
    return when {
      alpha < 0f -> 0f
      alpha > 1f -> 1f
      else -> alpha
    }
  }

  /**
   * Maps alpha to 0 - 255 range
   *
   */
  private fun mapAlphaTo255(alpha: Float): Int {
    return (floatPercentCheck(alpha) * 255).toInt()
  }

  /**
   *  Maps alpha to 0 - 1 range
   *
   */
  private fun mapAlphaTo1(alpha: Int): Float {
    return alpha / 255f
  }

  /**
   * Converts angle to radian
   *
   */
  private fun angleToRadians(angle: Float): Double {
    return Math.toRadians(angle.toDouble())
  }

  /**
   * Calculates dx base on angle and radius
   *
   */
  private fun getDx(radius: Float, angle: Float): Float {
    return (radius * sin(angleToRadians(angle))).toFloat()
  }

  /**
   * Calculates dy base on angle and radius
   *
   */
  private fun getDy(radius: Float, angle: Float): Float {
    return if (shadowDy == NOT_DEFINED_SHADOW_DY) {
      (radius * cos(angleToRadians(angle))).toFloat()
    } else {
      shadowDy
    }
  }

  /**
   * Calculates diameter
   *
   */
  private fun getDiameter(x: Float, y: Float): Float {
    return sqrt(x.pow(2) + y.pow(2))
  }

  /**
   * Checks if a value is outside of both positive and negative boundary
   *
   */
  private fun boundaryCheck(value: Float, boundary: Float): Float {
    val absValue = abs(value)
    val sign = (value / absValue).toInt()
    return if (absValue >= boundary) sign * boundary
    else sign * absValue
  }

  /**
   * Creates sweep shader
   *
   */
  private fun getSweepShader(colorArray: IntArray, angle: Float, offCenterX: Float, offCenterY: Float): SweepGradient {
    val sweepGradient = SweepGradient(getCenterX(offCenterX), getCenterY(offCenterY), colorArray, null)
    val matrix = Matrix()
    matrix.postRotate(angle - 90, getCenterX(offCenterX), getCenterY(offCenterY))
    sweepGradient.setLocalMatrix(matrix)
    return sweepGradient
  }

  /**
   * Creates radial shader
   *
   */
  private fun getRadialShader(colorArray: IntArray, offCenterX: Float, offCenterY: Float): RadialGradient {
    return RadialGradient(
      getCenterX(offCenterX),
      getCenterY(offCenterY),
      getShaderRadius(offCenterX, offCenterY),
      colorArray,
      null,
      Shader.TileMode.CLAMP
    )
  }

  /**
   * Calculate offCenter value of X axis base on multiplier
   *
   */
  private fun getCenterX(offCenter: Float): Float {
    val halfWidth = getActualWidth() / 2 + (getShadowShadowLeft() + getShadowShadowRight()) / 2
    return halfWidth + offCenter * halfWidth
  }

  /**
   * Calculate offCenter value of Y axis base on multiplier
   *
   */
  private fun getCenterY(offCenter: Float): Float {
    val halfHeight = getActualHeight() / 2 + (getShadowShadowTop() + getShadowShadowBottom()) / 2
    return halfHeight + offCenter * halfHeight
  }

  /**
   * Calculate how much radial shader radius must be, to fill out entire boundary
   *
   */
  private fun getShaderRadius(offCenterX: Float, offCenterY: Float): Float {
    val offCenter = getDiameter(offCenterX, offCenterY)
    val halfDiameter = getDiameter(getActualWidth(), getActualHeight()) / 2
    return halfDiameter + offCenter * halfDiameter
  }

  /**
   * Creating linear shader that start and end point is on path
   *
   */
  private fun getLinearShader(colorArray: IntArray, angle: Float): LinearGradient {
    val startPoint = getLinearGradientCircularStartPoint(angle)
    val endPoint = getLinearGradientCircularStartPoint(angle + 180)
    return LinearGradient(
      startPoint.first,
      startPoint.second,
      endPoint.first,
      endPoint.second,
      colorArray,
      null,
      Shader.TileMode.CLAMP
    )
  }

  /**
   * Calculate position of linear gradient starting point,with considering each corner radius of shape.
   * In this approach starting point of linear gradient,will fall on the path even on the curved corners.
   * So when we have multiple colors in linear gradient,no colors will fall outside of path when corner is curved.
   *
   */
  private fun getLinearGradientCircularStartPoint(angle: Float): Pair<Float, Float> {
    val width = getActualWidth()
    val height = getActualHeight()
    // These values will be used to calculate diameters:
    val halfWidth = width / 2
    val halfHeight = height / 2
    // We need half diameter because we move from center of both X and Y axises:
    val halfDiameter = getDiameter(width, height) / 2
    // Now we calculate how much we need to move from center.
    val dx = getDx(halfDiameter, angle)
    val dy = getDy(halfDiameter, angle)
    // Because dx and dy are calculated by half diameter,
    // if we don't have square, dx and dy will be,
    // more than half width/height in some conditions,
    // so we need to clamp values to maximum of half width/height:
    var x = halfWidth + boundaryCheck(dx, halfWidth)
    var y = halfHeight + boundaryCheck(-dy, halfHeight) // -dy because y axis in android is upside down.
    // Now We Make Coordinates of X and Y Circular Base on Corner Radius:
    val pathRadius = getBackgroundPathRadius()
    val ctr = pathRadius[2] // Corner Top Right
    val cbr = pathRadius[4] // Corner Bottom Right
    val cbl = pathRadius[6] // Corner Bottom Left
    val ctl = pathRadius[0] // Corner Top Left
    var circularAngle: Float
    if (x >= width - ctr && y <= ctr) {
      // Top Right
      circularAngle = determineCornerCircularAngle(x - (width - ctr), y, ctr)
      // No Addition
      x = width - ctr + getDx(ctr, circularAngle)
      y = ctr - getDy(ctr, circularAngle)
    } else if (x >= width - cbr && y >= height - cbr) {
      // Bottom Right
      circularAngle = determineCornerCircularAngle(width - x, y - (height - cbr), cbr)
      circularAngle += 90
      x = width - cbr + getDx(cbr, circularAngle)
      y = height - cbr - getDy(cbr, circularAngle)
    } else if (x <= cbl && y >= height - cbl) {
      // Bottom Left
      circularAngle = determineCornerCircularAngle(cbl - x, height - y, cbl)
      circularAngle += 180
      x = cbl + getDx(cbl, circularAngle)
      y = height - cbl - getDy(cbl, circularAngle)
    } else if (x <= ctl && y <= ctl) {
      // Top Left
      circularAngle = determineCornerCircularAngle(x, ctl - y, ctl)
      circularAngle += 270
      x = ctl + getDx(ctl, circularAngle)
      y = ctl - getDy(ctl, circularAngle)
    }
    return Pair(x + getShadowArea(), y + getShadowArea())
  }

  /**
   * Determine how much we traverse corner to calculate angle
   * For example here we travel 5 unit in X axis, and there is 3 more left,
   * And we didn't travel on y axis. So {max} travel on each axis is 8.
   * And maximum travel angle is 90 degrees. and we traveled 5 + 0 / 8 + 8.
   * So angle is 5 / 16 * 90 = 28.125 degree.
   *
   */
  private fun determineCornerCircularAngle(x: Float, y: Float, max: Float): Float {
    return ((abs(x) + abs(y)) / (2 * abs(max))) * 90f
  }

  /**
   * Init background path radius and returns it
   *
   */
  private fun getBackgroundPathRadius(): FloatArray {
    initBackgroundCornerRadius()
    return backgroundPathRadius
  }

  /**
   * Init stroke mask radius and returns it
   *
   */
  private fun getStrokeMaskRadius(): FloatArray {
    initStrokeMaskRadius()
    return strokeMaskRadius
  }

  /**
   * Init stroke path radius and returns it
   *
   */
  private fun getStrokePathRadius(): FloatArray {
    initStrokePathRadius()
    return strokePathRadius
  }

  /**
   * Adapter for converting capType to [Paint.Cap]
   *
   */
  private fun getStrokeCap(capType: CapType): Paint.Cap {
    return when (capType) {
      CapType.Butt -> Paint.Cap.BUTT
      CapType.Square -> Paint.Cap.SQUARE
      CapType.Round -> Paint.Cap.ROUND
    }
  }

  /**
   * It's the trigger for corner related mathematics to update themselves according to new values.
   * It makes performance improvement to not calculate, if it is not changed.
   *
   */
  private fun cornerUpdated() {
    backgroundPathRadiusUpdated = false
    strokePathRadiusUpdated = false
    strokeMaskRadiusUpdated = false
  }

  /**
   * Init stroke path and returns it
   *
   */
  private fun getStrokePath(): Path {
    initStrokePath()
    return strokePath
  }

  /**
   * Init stroke mask and returns it
   *
   */
  private fun getStrokeMask(): Path {
    initStrokeMask()
    return strokeMask
  }

  /**
   * Init stroke paint and returns it
   *
   */
  private fun getStrokePaint(): Paint {
    initStrokePaint()
    return strokePaint
  }

  /**
   * Init no stroke path and returns it
   *
   */
  private fun getNoStrokePath(): Path {
    initNoStrokePath()
    return noStrokePath
  }

  /**
   * If the stroke is dashed or not
   *
   */
  private fun isDashed(): Boolean {
    return strokeType == StrokeType.Dash
  }

  /**
   * Init background path and returns it
   *
   */
  private fun getBackgroundPath(): Path {
    initBackgroundPath()
    return backgroundPath
  }

  /**
   * Init background paint and returns it
   *
   */
  private fun getBackgroundPaint(): Paint {
    initBackgroundPaint()
    return backgroundPaint
  }

  /**
   * It returns 0 if can not draw stroke (because of background color type)
   *
   */
  private fun getStrokeWidth(): Float {
    return if (canDrawStroke())
      strWidth
    else
      0f
  }

  /**
   * Returns default shadow area size if it is not set, or the actual shadow are it it is set.
   *
   */
  private fun getShadowArea(): Float {
    return if (shadowSpace == NOT_DEFINED_SHADOW_SHADOW_AREA)
      0f
    else
      shadowSpace
  }

  private fun getShadowShadowLeft(): Float {
    return if (shadowLeft == NOT_DEFINED_SHADOW_SHADOW_AREA)
      getShadowArea()
    else
      shadowLeft
  }

  private fun getShadowShadowTop(): Float {
    return if (shadowTop == NOT_DEFINED_SHADOW_SHADOW_AREA)
      getShadowArea()
    else
      shadowTop
  }

  private fun getShadowShadowRight(): Float {
    return if (shadowRight == NOT_DEFINED_SHADOW_SHADOW_AREA)
      getShadowArea()
    else
      shadowRight
  }

  private fun getShadowShadowBottom(): Float {
    return if (shadowBottom == NOT_DEFINED_SHADOW_SHADOW_AREA)
      getShadowArea()
    else
      shadowBottom
  }

  /** External Interface Area */

  /**
   * Checks blur and sets it
   *
   */
  fun setShadowBlur(blur: Float) {
    shadow.blur = dimenCheck(blur, null)
  }

  /**
   * Checks distance and sets it
   *
   */
  fun setShadowDistance(distance: Float) {
    shadow.distance = dimenCheck(distance, null)
  }

  /**
   * Normalize alpha and sets it
   *
   */
  fun setShadowAlpha(alpha: Float) {
    shadow.alpha = floatPercentCheck(alpha)
  }

  /**
   * It sets the color
   *
   */
  fun setShadowColor(color: Int) {
    shadow.color = color
  }

  /**
   * Checks the angle and sets it
   *
   */
  fun setShadowAngle(angle: Float) {
    shadow.angle = angleCheck(angle)
  }

  fun setShadow(blur: Float, distance: Float, angle: Float, alpha: Float, color: Int) {
    setShadowBlur(blur)
    setShadowDistance(distance)
    setShadowAlpha(alpha)
    setShadowColor(color)
    setShadowAngle(angle)
  }

  fun setCorners(
    cornerRadius: Float,
    cornerRadius_TopLeft: Float,
    cornerRadius_TopRight: Float,
    cornerRadius_BottomRight: Float,
    cornerRadius_BottomLeft: Float
  ) {
    this.cornersRadius = cornerRadius
    this.cornerRadiusTopLeft = cornerRadius_TopLeft
    this.cornerRadiusTopRight = cornerRadius_TopRight
    this.cornerRadiusBottomRight = cornerRadius_BottomRight
    this.cornerRadiusBottomLeft = cornerRadius_BottomLeft
  }

  fun setCorners(
    cornerRadius_TopLeft: Float,
    cornerRadius_TopRight: Float,
    cornerRadius_BottomRight: Float,
    cornerRadius_BottomLeft: Float
  ) {
    this.cornerRadiusTopLeft = cornerRadius_TopLeft
    this.cornerRadiusTopRight = cornerRadius_TopRight
    this.cornerRadiusBottomRight = cornerRadius_BottomRight
    this.cornerRadiusBottomLeft = cornerRadius_BottomLeft
  }

  fun haveShadow(): Boolean {
    return shadow.blur != NOT_DEFINED_DIMEN
  }

  fun haveAnyShadow(): Boolean {
    return haveShadow()
  }

  fun getActualWidth(): Float {
    return measuredWidth - getShadowShadowLeft() - getShadowShadowRight()
  }

  fun getActualHeight(): Float {
    return measuredHeight - getShadowShadowTop() - getShadowShadowBottom()
  }

  private fun canDrawBackground(): Boolean {
    return backgroundType == BackgroundType.Fill || backgroundType == BackgroundType.FillStroke
  }

  private fun canDrawStroke(): Boolean {
    return backgroundType == BackgroundType.Stroke || backgroundType == BackgroundType.FillStroke
  }

  private fun canDrawShadow(shadow: ShadowObject): Boolean {
    if (getShadowShadowLeft() == 0f &&
      getShadowShadowTop() == 0f &&
      getShadowShadowRight() == 0f &&
      getShadowShadowBottom() == 0f
    )
      return false
    return shadow.blur != NOT_DEFINED_DIMEN && (canDrawStroke() || canDrawBackground())
  }

  private fun drawBackground(canvas: Canvas?) {
    if (canDrawBackground()) {
      canvas?.drawPath(getBackgroundPath(), getBackgroundPaint())
    }
  }

  private fun drawStroke(canvas: Canvas?) {
    if (canDrawStroke()) {
      if (isDashed()) {
        canvas?.save()
        canvas?.clipPath(getStrokeMask())
        canvas?.drawPath(getStrokePath(), getStrokePaint())
        canvas?.restore()
      } else {
        canvas?.drawPath(getStrokeMask(), getStrokePaint())
      }
    }
  }

  private fun drawShadow(canvas: Canvas?, shadow: ShadowObject) {
    canvas?.save()
    canvas?.clipPath(shadow.mask)
    canvas?.drawPath(shadow.path, shadow.paint)
    canvas?.restore()
  }

  private fun drawShadows(canvas: Canvas?) {
    if (canDrawShadow(shadow)) {
      initShadow(shadow)
      drawShadow(canvas, shadow)
    }
  }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    drawBackground(canvas)
    drawStroke(canvas)
    drawShadows(canvas)
  }

  override fun drawChild(canvas: Canvas?, child: View?, drawingTime: Long): Boolean {
    canvas?.clipPath(getNoStrokePath())
    return super.drawChild(canvas, child, drawingTime)
  }

  companion object {
    val DEFAULT_BACKGROUND_COLOR_TYPE = ColorType.Solid
    val DEFAULT_BACKGROUND_TYPE = BackgroundType.Fill
    val DEFAULT_COLOR_BACKGROUND = Color.rgb(255, 255, 255)
    val DEFAULT_COLOR_STROKE = Color.rgb(128, 128, 128)
    const val DEFAULT_CORNER_RADIUS = 0f
    val DEFAULT_CORNER_TYPE = CornerType.Custom
    const val DEFAULT_OFF_CENTER_X = 0f
    const val DEFAULT_OFF_CENTER_Y = 0f
    val DEFAULT_SHADOW_COLOR = Color.rgb(0, 0, 0)
    val DEFAULT_STROKE_CAP_TYPE = CapType.Butt
    val DEFAULT_STROKE_COLOR_TYPE = ColorType.Solid
    const val DEFAULT_STROKE_SIZE = 0f
    val DEFAULT_STROKE_TYPE = StrokeType.Solid
    const val NOT_DEFINED_ALPHA = 1f
    const val NOT_DEFINED_ANGLE = 0f
    const val NOT_DEFINED_COLOR = -10
    const val NOT_DEFINED_CORNER_RADIUS = Float.MIN_VALUE
    const val NOT_DEFINED_DIMEN = -1f
    const val NOT_DEFINED_SHADOW_SHADOW_AREA = -1f
    const val NOT_DEFINED_STYLE = -1
    const val NOT_DEFINED_ANIMATE = false
    const val DEFAULT_DURATION = 400L
    const val NOT_DEFINED_SHADOW_DX = -1f
    const val NOT_DEFINED_SHADOW_DY = -1f
  }
}
