package ua.naiksoftware.tooltips

import android.view.View

class TooltipOverlayParams() {

    var leftBarrier: View? = null
    var rightBarrier: View? = null
    var topBarrier: View? = null
    var bottomBarrier: View? = null

    var overlayColor = 0x7F000000

    var dismissOnTouchTooltip = true
    var dismissOnTouchOutside = true
    var dismissOnTouchOverlay = true
    var dismissOnTouchAnchor = true
    var anchorClickable = true

    lateinit var tooltipView: View
    lateinit var anchorView: View

    fun withLeftBarrier(leftBarrier: View): TooltipOverlayParams {
        this.leftBarrier = leftBarrier
        return this
    }

    fun withRightBarrier(rightBarrier: View) : TooltipOverlayParams {
        this.rightBarrier = rightBarrier
        return this
    }

    fun withTopBarrier(topBarrier: View): TooltipOverlayParams {
        this.topBarrier = topBarrier
        return this
    }

    fun withBottomBarrier(bottomBarrier: View): TooltipOverlayParams {
        this.bottomBarrier = bottomBarrier
        return this
    }

    fun withOverlayColor(color: Int): TooltipOverlayParams {
        this.overlayColor = color
        return this
    }

    fun setDismissOnTouchTooltip(value: Boolean) : TooltipOverlayParams {
        this.dismissOnTouchTooltip = value
        return this
    }

    fun setDismissOnTouchOutside(value: Boolean) : TooltipOverlayParams {
        this.dismissOnTouchOutside = value
        return this
    }

    fun setDismissOnTouchOverlay(value: Boolean) : TooltipOverlayParams {
        this.dismissOnTouchOverlay = value
        return this
    }

    fun setDismissOnTouchAnchor(value: Boolean) : TooltipOverlayParams {
        this.dismissOnTouchAnchor = value
        return this
    }

    fun setAnchorClickable(value: Boolean) : TooltipOverlayParams {
        this.anchorClickable = value
        return this
    }
}