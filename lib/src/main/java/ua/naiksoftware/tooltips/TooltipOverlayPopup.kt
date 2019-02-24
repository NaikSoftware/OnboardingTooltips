package ua.naiksoftware.tooltips

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow

class TooltipOverlayPopup(val context: Context) {

    private lateinit var anchorView: View
    private lateinit var tooltipView: View

    private var leftBarrier: View? = null
    private var rightBarrier: View? = null
    private var topBarrier: View? = null

    private var bottomBarrier: View? = null

    private var overlayColor = 0

    private var dismissOnTouchTooltip: Boolean = true
    private var dismissOnTouchOutside: Boolean = true

    private lateinit var overlayView: TooltipOverlayLayout
    private lateinit var popupWindow: PopupWindow

    fun show(params: TooltipOverlayParams, tooltip: View, anchor: View) {
        tooltipView = tooltip
        anchorView = anchor
        leftBarrier = params.leftBarrier
        rightBarrier = params.rightBarrier
        topBarrier = params.topBarrier
        bottomBarrier = params.bottomBarrier
        overlayColor = params.overlayColor
        dismissOnTouchTooltip = params.dismissOnTouchTooltip
        dismissOnTouchOutside = params.dismissOnTouchOutside

        overlayView = TooltipOverlayLayout(context)
        overlayView.setBackgroundColor(overlayColor)

        popupWindow = PopupWindow(overlayView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0)
    }

    fun dismiss() {
        popupWindow.dismiss()
    }
}
