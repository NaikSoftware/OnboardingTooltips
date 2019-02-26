package ua.naiksoftware.tooltips

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import android.R.attr.left
import android.R.attr.bottom
import android.R.attr.right
import android.R.attr.top
import java.util.Arrays.asList
import android.R.attr.centerX
import android.graphics.*
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import java.util.*


class TooltipView : BaseTooltipView {

    private var position: Position = Position.BOTTOM
    private var arrowHeight = 0f
    private var arrowWidth = 0f
    private var arrowTargetX = -1f
    private var arrowTargetY = -1f
    private lateinit var contentView: View
    private lateinit var bubblePaint: Paint

    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr, 0)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        setWillNotDraw(false)
        bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bubblePaint.color = Color.WHITE
        contentView = TextView(context)
        contentView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(contentView)
    }

    fun setContentView(contentView: View) {
        this.contentView = contentView
        contentView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(contentView)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(getBubblePath(), bubblePaint)
    }

    private fun getBubblePath(): Path {
        val path = Path()

        val spacingTop = (if (this.position === Position.BOTTOM) arrowHeight else 0f)
        val spacingBottom = (if (this.position === Position.TOP) arrowHeight else 0f)

        val left = spacingLeft + myRect.left
        val top = spacingTop + myRect.top
        val right = myRect.right - spacingRight
        val bottom = myRect.bottom - spacingBottom
        val centerX = viewRect.centerX() - x

        val arrowSourceX = if (Arrays.asList(Position.TOP, Position.BOTTOM).contains(this.position))
            centerX + arrowSourceMargin
        else
            centerX
        val arrowTargetX = if (Arrays.asList(Position.TOP, Position.BOTTOM).contains(this.position)) centerX + arrowTargetMargin
        else val arrowSourceY = bottom / 2f - arrowSourceMargin
        else val arrowTargetY = bottom / 2f - arrowTargetMargin
        else bottom / 2f

        path.moveTo(left + topLeftDiameter / 2f, top)
        //LEFT, TOP

        if (position === Position.BOTTOM) {
            path.lineTo(arrowSourceX - arrowWidth, top)
            path.lineTo(arrowTargetX, myRect.top)
            path.lineTo(arrowSourceX + arrowWidth, top)
        }
        path.lineTo(right - topRightDiameter / 2f, top)

        path.quadTo(right, top, right, top + topRightDiameter / 2)
        //RIGHT, TOP

        path.lineTo(right, bottom - bottomRightDiameter / 2)

        path.quadTo(right, bottom, right - bottomRightDiameter / 2, bottom)
        //RIGHT, BOTTOM

        if (position === Position.TOP) {
            path.lineTo(arrowSourceX + arrowWidth, bottom)
            path.lineTo(arrowTargetX, myRect.bottom)
            path.lineTo(arrowSourceX - arrowWidth, bottom)
        }
        path.lineTo(left + bottomLeftDiameter / 2, bottom)

        path.quadTo(left, bottom, left, bottom - bottomLeftDiameter / 2)
        //LEFT, BOTTOM

        path.lineTo(left, top + topLeftDiameter / 2)

        path.quadTo(left, top, left + topLeftDiameter / 2, top)

        path.close()

        return path
    }

    fun setBubbleColor(color: Int) {
        bubblePaint.color = color
        postInvalidate()
    }

    fun setPosition(position: Position) {
        this.position = position
        postInvalidate()
    }

    fun setArrowHeight(arrowHeight: Float) {
        this.arrowHeight = arrowHeight
        postInvalidate()
    }

    fun setArrowWidth(arrowWidth: Float) {
        this.arrowWidth = arrowWidth
        postInvalidate()
    }

    override fun setTooltipAnchorPoint(x: Float, y: Float) {
        this.arrowTargetX = x
        postInvalidate()
    }

    enum class Position {
        TOP,
        BOTTOM
    }
}