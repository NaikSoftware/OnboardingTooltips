package ua.naiksoftware.tooltips

import android.view.View

class TooltipOverlayParams(val tooltipView: View, val anchorView: View?) {

    var leftBarrier: View? = null
    var rightBarrier: View? = null
    var topBarrier: View? = null
    var bottomBarrier: View? = null

    var overlayColor = 0x7F000000

    var dismissOnTouchOutside = true
    var dismissOnTouchOverlay = true
    var dismissOnTouchAnchor = true
    var anchorClickable = true
    var overlayTransparent = false

    var overlayFadeDuration = 500L // ms

    var tooltipPosition: TooltipPosition? = TooltipPosition.CENTER

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

    fun withTooltipPosition(tooltipPosition: TooltipPosition) : TooltipOverlayParams {
        this.tooltipPosition = tooltipPosition
        return this
    }

    fun withOverlayColor(color: Int): TooltipOverlayParams {
        this.overlayColor = color
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

    fun setOverlayFadeDuration(value: Long) : TooltipOverlayParams {
        this.overlayFadeDuration = value
        return this
    }

    fun withTransparentOverlay(value: Boolean) : TooltipOverlayParams {
        this.overlayTransparent = value
        return this
    }
}