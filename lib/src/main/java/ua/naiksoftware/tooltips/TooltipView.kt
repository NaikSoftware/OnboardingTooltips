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

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(getBubblePath(), bubblePaint)
    }

    private fun getBubblePath(): Path {
        val path = Path()
        when (position) {


            TooltipPosition.CENTER -> {
                path.addRoundRect(
                    RectF(
                        paddingLeft.toFloat(),
                        paddingTop.toFloat(),
                        width - paddingRight.toFloat(),
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
                        paddingLeft.toFloat(),
                        paddingTop.toFloat(),
                        width - paddingRight.toFloat(),
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
                        paddingLeft.toFloat(),
                        bubbleTop,
                        width - paddingRight.toFloat(),
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthUsed = paddingLeft + paddingRight
        var heightUsed = paddingTop + paddingBottom
        if (position != TooltipPosition.CENTER) {
            heightUsed += arrowHeight
        }

        contentView.measure(
            getChildMeasureSpec(widthMeasureSpec, widthUsed, LayoutParams.MATCH_PARENT),
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
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        when (position) {
            TooltipPosition.TOP -> contentView.layout(
                paddingLeft,
                paddingTop,
                contentView.measuredWidth + paddingLeft,
                contentView.measuredHeight + paddingTop
            )
            TooltipPosition.BOTTOM -> contentView.layout(
                paddingLeft,
                paddingTop + arrowHeight,
                contentView.measuredWidth + paddingLeft,
                paddingTop + contentView.measuredHeight + arrowHeight
            )
            else -> contentView.layout(
                paddingLeft,
                paddingTop,
                contentView.measuredWidth + paddingLeft,
                contentView.measuredHeight + paddingTop
            )
        }
    }
}