package ua.naiksoftware.tooltips

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class TooltipView : ViewGroup, AnchoredTooltip {

    private var position: TooltipPosition = TooltipPosition.CENTER
    private var arrowHeight = 0
    private var arrowWidth = 0
    private var arrowRadius = 0f
    private var bubbleRadius = 0f
    private var arrowTargetX = -1f
    private lateinit var contentView: View
    private lateinit var bubblePaint: Paint
    private var sideLeft = 0f
    private var sideRight = 0f

    constructor(context: Context) : super(context) {
        init(context, null, 0, 0, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0, 0, null)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr, 0, null)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context, attrs, defStyleAttr, defStyleRes, null)
    }

    constructor(context: Context, text: String) : super(context) {
        init(context, null, 0, 0, text)
    }

    constructor(context: Context, contentView: View) : super(context) {
        init(context, null, 0, 0, null, contentView)
    }

    private fun init(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
        text: String?,
        contentView: View? = null
    ) {
        setWillNotDraw(false)
        val density = context.resources.displayMetrics.density
        arrowWidth = (density * 42).toInt()
        arrowHeight = (density * 24).toInt()
        arrowRadius = density * 8
        bubbleRadius = density * 4
        bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bubblePaint.color = Color.WHITE
        bubblePaint.style = Paint.Style.FILL
        if (contentView == null) {
            val textView = TextView(context)
            textView.text = text
            textView.textSize = 18f
            textView.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            textView.setTextColor(0xde000000.toInt())
            val spacing = (density * 16).toInt()
            textView.setPadding(spacing, spacing, spacing, spacing)
            this.contentView = textView
        } else {
            this.contentView = contentView
        }
        addView(this.contentView)
    }

    fun setContentView(contentView: View) {
        this.contentView = contentView
        removeView(contentView)
        addView(contentView)
    }

    fun setTextColor(color: Int) {
        if (contentView is TextView) {
            (contentView as? TextView)?.setTextColor(color)
        }
    }

    fun setTextSize(textSize: Float) {
        if (contentView is TextView) {
            (contentView as? TextView)?.textSize = textSize
        }
    }

    fun setTypeface(typeface: Typeface) {
        if (contentView is TextView) {
            (contentView as? TextView)?.typeface = typeface
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(getBubblePath(), bubblePaint)
    }

    private fun getBubblePath(): Path {

        val minArrowSpacing = arrowWidth / 4f

        if (arrowTargetX < paddingLeft + minArrowSpacing + arrowWidth / 2) {
            arrowTargetX = paddingLeft + minArrowSpacing + arrowWidth / 2
        } else if (arrowTargetX > width - paddingRight - minArrowSpacing - arrowWidth / 2) {
            arrowTargetX = width - paddingRight - minArrowSpacing - arrowWidth / 2
        }

        val path = Path()
        when (position) {

            TooltipPosition.CENTER -> {
                path.addRoundRect(
                    RectF(
                        sideLeft,
                        paddingTop.toFloat(),
                        sideRight,
                        height - paddingBottom.toFloat()
                    ),
                    bubbleRadius, bubbleRadius, Path.Direction.CW
                )
                path.close()
            }

            TooltipPosition.TOP -> {
                val bubbleBottom = height - paddingBottom.toFloat() - arrowHeight
                path.addRoundRect(
                    RectF(
                        sideLeft,
                        paddingTop.toFloat(),
                        sideRight,
                        bubbleBottom
                    ),
                    bubbleRadius, bubbleRadius, Path.Direction.CW
                )
                if (arrowTargetX < 0) arrowTargetX = width / 2f

                val k = arrowWidth / arrowRadius
                val h = arrowHeight / k

                path.moveTo(arrowTargetX - arrowWidth / 2f, bubbleBottom)
                path.lineTo(arrowTargetX - arrowRadius / 2f, height - h - paddingBottom)
                path.quadTo(
                    arrowTargetX,
                    height - paddingBottom.toFloat(),
                    arrowTargetX + arrowRadius / 2f,
                    height - h - paddingBottom
                )
                path.lineTo(arrowTargetX + arrowWidth / 2f, bubbleBottom)
                path.close()
            }

            TooltipPosition.BOTTOM -> {
                val bubbleTop = paddingTop.toFloat() + arrowHeight
                path.addRoundRect(
                    RectF(
                        sideLeft,
                        bubbleTop,
                        sideRight,
                        height - paddingBottom.toFloat()
                    ),
                    bubbleRadius, bubbleRadius, Path.Direction.CW
                )
                if (arrowTargetX < 0) arrowTargetX = width / 2f

                val k = arrowWidth / arrowRadius
                val h = arrowHeight / k

                path.moveTo(arrowTargetX - arrowWidth / 2f, bubbleTop)
                path.lineTo(arrowTargetX - arrowRadius / 2f, paddingTop + h)
                path.quadTo(
                    arrowTargetX,
                    paddingTop.toFloat(),
                    arrowTargetX + arrowRadius / 2f,
                    paddingTop + h
                )
                path.lineTo(arrowTargetX + arrowWidth / 2f, bubbleTop)
                path.close()
            }
        }

        return path
    }

    fun setBubbleColor(color: Int) {
        bubblePaint.color = color
        invalidate()
    }

    fun setArrowHeight(arrowHeight: Int) {
        this.arrowHeight = arrowHeight
        arrowRadius = Math.min(arrowRadius, arrowHeight / 2f)
        invalidate()
    }

    fun setArrowWidth(arrowWidth: Int) {
        this.arrowWidth = arrowWidth
        invalidate()
    }

    fun setArrowRadius(radius: Float) {
        arrowRadius = Math.min(radius, arrowHeight / 2f)
        invalidate()
    }

    override fun setTooltipAnchor(x: Float, tooltipPosition: TooltipPosition) {
        this.arrowTargetX = x
        this.position = tooltipPosition
        invalidate()
    }

    private fun getSides() : Pair<Float, Float> {
        val minArrowSpacing = arrowWidth / 4f
        val width = measuredWidth
        val bubbleWidth = contentView.measuredWidth.coerceAtLeast(minimumWidth)
        val leftSide: Float
        val rightSide: Float
        if (arrowTargetX >= 0) {
            if (arrowTargetX > width / 2f + bubbleWidth / 2f - (minArrowSpacing + arrowWidth / 2f)) {
                rightSide = (arrowTargetX + (minArrowSpacing + arrowWidth / 2f)).coerceAtMost((width - paddingRight).toFloat())
                leftSide = (rightSide - bubbleWidth).coerceAtLeast(paddingLeft.toFloat())
            } else if (arrowTargetX < width / 2f - bubbleWidth / 2 + (minArrowSpacing + arrowWidth / 2f)) {
                leftSide = (arrowTargetX - (minArrowSpacing + arrowWidth / 2f)).coerceAtMost(paddingLeft.toFloat())
                rightSide = (leftSide + bubbleWidth).coerceAtMost((width - paddingRight).toFloat())
            } else {
                leftSide = (width / 2f - bubbleWidth / 2f).coerceAtMost(paddingLeft.toFloat())
                rightSide = (width / 2f + bubbleWidth / 2f).coerceAtLeast((width - paddingRight).toFloat())
            }
        } else {
            leftSide = paddingLeft.toFloat()
            rightSide = (width - paddingRight).toFloat()
        }
        return leftSide to rightSide
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthUsed = paddingLeft + paddingRight
        var heightUsed = paddingTop + paddingBottom
        if (position != TooltipPosition.CENTER) {
            heightUsed += arrowHeight
        }

        contentView.measure(
            getChildMeasureSpec(widthMeasureSpec, widthUsed, LayoutParams.WRAP_CONTENT),
            getChildMeasureSpec(heightMeasureSpec, heightUsed, LayoutParams.WRAP_CONTENT)
        )

        setMeasuredDimension(
            View.resolveSizeAndState(
                Math.max(contentView.measuredWidth, if (position == TooltipPosition.CENTER) 0 else arrowWidth),
                widthMeasureSpec,
                contentView.measuredState
            ),
            View.resolveSizeAndState(
                contentView.measuredHeight + heightUsed,
                heightMeasureSpec,
                contentView.measuredState shl View.MEASURED_HEIGHT_STATE_SHIFT
            )
        )

        val (sideLeft, sideRight) = getSides()
        this.sideLeft = sideLeft
        this.sideRight = sideRight

        setPadding(
            sideLeft.toInt(),
            paddingTop,
            measuredWidth - sideRight.toInt(),
            paddingBottom
        )

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        when (position) {
            TooltipPosition.TOP -> contentView.layout(
                sideLeft.toInt(),
                paddingTop,
                sideRight.toInt(),
                contentView.measuredHeight + paddingTop
            )
            TooltipPosition.BOTTOM -> contentView.layout(
                sideLeft.toInt(),
                paddingTop + arrowHeight,
                sideRight.toInt(),
                paddingTop + contentView.measuredHeight + arrowHeight
            )
            else -> contentView.layout(
                sideLeft.toInt(),
                paddingTop,
                sideRight.toInt(),
                contentView.measuredHeight + paddingTop
            )
        }
    }
}