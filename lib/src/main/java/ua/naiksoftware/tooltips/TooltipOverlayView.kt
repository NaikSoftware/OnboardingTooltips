package ua.naiksoftware.tooltips

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi

class TooltipOverlayView : View {

    private var anchorViewBitmap: Bitmap? = null
    private var backgroundColor: Int = 0
    private lateinit var maskPaint: Paint
    private var anchorX = 0f
    private var anchorY = 0f

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
        maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)
        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    fun setAnchorView(anchorView: View, anchorX: Float, anchorY: Float) {
        this.anchorX = anchorX
        this.anchorY = anchorY
        anchorViewBitmap = Bitmap.createBitmap(anchorView.width, anchorView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(anchorViewBitmap!!)
        anchorView.draw(canvas)
        invalidate()
    }

    override fun setBackgroundColor(color: Int) {
        this.backgroundColor = color
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(backgroundColor)
        if (anchorViewBitmap != null) {
            canvas.drawBitmap(anchorViewBitmap, anchorX, anchorY, maskPaint)
        }
    }
}
