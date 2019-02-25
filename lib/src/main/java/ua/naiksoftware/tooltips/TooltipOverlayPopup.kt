package ua.naiksoftware.tooltips

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.PopupWindow

class TooltipOverlayPopup(val context: Context) {

    companion object {
        val TAG = TooltipOverlayPopup::class.java.simpleName!!
    }

    private lateinit var anchorView: View
    private lateinit var tooltipView: View

    private var leftBarrier: View? = null
    private var rightBarrier: View? = null
    private var topBarrier: View? = null

    private var bottomBarrier: View? = null

    private var overlayColor = 0

    private var dismissOnTouchTooltip: Boolean = true
    private var dismissOnTouchOutside: Boolean = true
    private var dismissOnTouchOverlay: Boolean = true
    private var dismissOnTouchAnchor: Boolean = true
    private var anchorClickable: Boolean = true

    private lateinit var overlayView: TooltipOverlayLayout
    private lateinit var popupWindow: PopupWindow

    fun show(params: TooltipOverlayParams, tooltip: View, anchor: View, activity: Activity) {
        tooltipView = tooltip
        anchorView = anchor
        leftBarrier = params.leftBarrier
        rightBarrier = params.rightBarrier
        topBarrier = params.topBarrier
        bottomBarrier = params.bottomBarrier
        overlayColor = params.overlayColor
        dismissOnTouchTooltip = params.dismissOnTouchTooltip
        dismissOnTouchOutside = params.dismissOnTouchOutside
        dismissOnTouchOverlay = params.dismissOnTouchOverlay
        dismissOnTouchAnchor = params.dismissOnTouchAnchor
        anchorClickable = params.anchorClickable

        val popupRootView = FrameLayout(context)
        overlayView = TooltipOverlayLayout(context)
        overlayView.setBackgroundColor(overlayColor)

        val screenRect = Rect()
        val window = activity.window.decorView.getWindowVisibleDisplayFrame(screenRect)
        val overlayRect = getOverlayRect(screenRect)

        val layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.leftMargin = overlayRect.left
        layoutParams.topMargin = overlayRect.top
        layoutParams.rightMargin = screenRect.width() - overlayRect.right
        layoutParams.bottomMargin = screenRect.height() - overlayRect.bottom

        popupRootView.addView(overlayView, layoutParams)

        popupWindow = PopupWindow(popupRootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        popupWindow.showAtLocation(activity.window.decorView.rootView, Gravity.NO_GRAVITY, 0, 0)

        popupWindow.isFocusable = true
        popupWindow.contentView.isFocusableInTouchMode = true;

        popupWindow.setTouchInterceptor { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val clickedOnOverlay = clickedOnOverlay(screenRect, overlayRect, event.x, event.y)
                val clickedOnAnchor = clickedOnAnchor(screenRect, event.x, event.y)
                Log.d(TAG, "clickedOnOverlay = $clickedOnOverlay clickedOnAnchor = $clickedOnAnchor")

                return@setTouchInterceptor when {
                    clickedOnAnchor -> {
                        if (dismissOnTouchAnchor) dismiss()
                        !anchorClickable
                    }
                    clickedOnOverlay -> {
                        if (dismissOnTouchOverlay) dismiss()
                        true
                    }
                    else -> {
                        if (dismissOnTouchOutside) dismiss()
                        activity.dispatchTouchEvent(event)
                        false
                    }
                }
            } else {
                return@setTouchInterceptor false
            }
        }
    }

    private fun clickedOnAnchor(screenRect: Rect, x: Float, y: Float): Boolean {
        return false
    }

    private fun clickedOnOverlay(screenRect: Rect, overlayRect: Rect, x: Float, y: Float): Boolean {
        return x > overlayRect.left && x < overlayRect.right
                && y > overlayRect.top && y < overlayRect.bottom
    }

    private fun getOverlayRect(screenRect: Rect): Rect {
        val rect = Rect()
        val location = IntArray(2)

        leftBarrier?.let {
            it.getLocationOnScreen(location)
            rect.left = location[0] + it.width
        }

        when (rightBarrier) {
            null -> rect.right = screenRect.width()
            else -> rightBarrier?.let {
                it.getLocationOnScreen(location)
                rect.right = location[0]
            }
        }

        topBarrier?.let {
            it.getLocationOnScreen(location)
            rect.top = location[1] + it.height - screenRect.top
        }
        bottomBarrier?.let {
            it.getLocationOnScreen(location)
            rect.bottom = location[1]
        }
        when (bottomBarrier) {
            null -> rect.bottom = screenRect.height()
            else -> bottomBarrier?.let {
                it.getLocationOnScreen(location)
                rect.bottom = location[1] - screenRect.top
            }
        }
        return rect
    }

    fun dismiss() {
        popupWindow.dismiss()
    }
}
