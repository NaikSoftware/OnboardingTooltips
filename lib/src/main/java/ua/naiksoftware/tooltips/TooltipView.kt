package ua.naiksoftware.tooltips

import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.SpannedString
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.setPadding


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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

    constructor(context: Context, spannedString: SpannedString) : super(context) {
        init(context, null, 0, 0, null, spannedString)
    }

    private fun init(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
        text: String?,
        spannedString: SpannedString? = null
    ) {
        setWillNotDraw(false)
        val density = context.resources.displayMetrics.density
        arrowWidth = (density * 32).toInt()
        arrowHeight = (density * 16).toInt()
        arrowRadius = arrowHeight / 4f
        bubbleRadius = density * 4
        bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bubblePaint.color = Color.WHITE
        bubblePaint.style = Paint.Style.FILL
        val textView = TextView(context)
        textView.text = text ?: spannedString
        textView.textSize = 18f
        textView.setTextColor(0xde000000.toInt())
        textView.setPadding((density * 16).toInt())
        contentView = textView
        addView(contentView)
    }

    fun setContentView(contentView: View) {
        this.contentView = contentView
        removeView(contentView)
        addView(contentView)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(getBubblePath(), bubblePaint)
    }

    private fun getBubblePath(): Path {
        val path = Path()
        when(position) {


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
                path.moveTo(arrowTargetX - arrowWidth / 2, bubbleBottom)
                path.lineTo(arrowTargetX, height - paddingBottom.toFloat())
                path.lineTo(arrowTargetX + arrowWidth / 2, bubbleBottom)
                path.close()
            }
        }

//        val spacingTop = (if (this.position === TooltipPosition.BOTTOM) arrowHeight else 0f)
//        val spacingBottom = (if (this.position === TooltipPosition.TOP) arrowHeight else 0f)
//
//        val left = spacingLeft + myRect.left
//        val top = spacingTop + myRect.top
//        val right = myRect.right - spacingRight
//        val bottom = myRect.bottom - spacingBottom
//        val centerX = viewRect.centerX() - x
//
//        val arrowSourceX = if (Arrays.asList(TooltipPosition.TOP, TooltipPosition.BOTTOM).contains(this.position))
//            centerX + arrowSourceMargin
//        else
//            centerX
//        val arrowTargetX = if (Arrays.asList(TooltipPosition.TOP, TooltipPosition.BOTTOM).contains(this.position)) centerX + arrowTargetMargin
//        else val arrowSourceY = bottom / 2f - arrowSourceMargin
//        else val arrowTargetY = bottom / 2f - arrowTargetMargin
//        else bottom / 2f
//
//        path.moveTo(left + topLeftDiameter / 2f, top)
//        //LEFT, TOP
//
//        if (position === TooltipPosition.BOTTOM) {
//            path.lineTo(arrowSourceX - arrowWidth, top)
//            path.lineTo(arrowTargetX, myRect.top)
//            path.lineTo(arrowSourceX + arrowWidth, top)
//        }
//        path.lineTo(right - topRightDiameter / 2f, top)
//
//        path.quadTo(right, top, right, top + topRightDiameter / 2)
//        //RIGHT, TOP
//
//        path.lineTo(right, bottom - bottomRightDiameter / 2)
//
//        path.quadTo(right, bottom, right - bottomRightDiameter / 2, bottom)
//        //RIGHT, BOTTOM
//
//        if (position === TooltipPosition.TOP) {
//            path.lineTo(arrowSourceX + arrowWidth, bottom)
//            path.lineTo(arrowTargetX, myRect.bottom)
//            path.lineTo(arrowSourceX - arrowWidth, bottom)
//        }
//        path.lineTo(left + bottomLeftDiameter / 2, bottom)
//
//        path.quadTo(left, bottom, left, bottom - bottomLeftDiameter / 2)
//        //LEFT, BOTTOM
//
//        path.lineTo(left, top + topLeftDiameter / 2)
//
//        path.quadTo(left, top, left + topLeftDiameter / 2, top)

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
            TooltipPosition.BOTTOM -> contentView.layout(l, t + arrowHeight, r, b)
            else -> contentView.layout(
                paddingLeft,
                paddingTop,
                contentView.measuredWidth + paddingLeft,
                contentView.measuredHeight + paddingTop
            )
        }
    }
}