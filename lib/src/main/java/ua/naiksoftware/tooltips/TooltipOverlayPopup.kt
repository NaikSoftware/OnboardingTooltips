package ua.naiksoftware.tooltips

import android.app.Activity
import android.graphics.Rect
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.PopupWindow

class TooltipOverlayPopup() {

    companion object {
        val TAG = TooltipOverlayPopup::class.java.simpleName
    }

    private lateinit var popupWindow: PopupWindow

    fun show(params: TooltipOverlayParams, activity: Activity, onDismissListener: (() -> Unit)? = null) {

        val popupRootView = FrameLayout(activity)
        val overlayView = TooltipOverlayView(activity)
        overlayView.setBackgroundColor(params.overlayColor)

        val screenRect = Rect()
        val window = activity.window.decorView.getWindowVisibleDisplayFrame(screenRect)
        val overlayRect = getOverlayRect(params, screenRect)

        val overlayLayoutParams =
            ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        overlayLayoutParams.leftMargin = overlayRect.left
        overlayLayoutParams.topMargin = overlayRect.top
        overlayLayoutParams.rightMargin = screenRect.width() - overlayRect.right
        overlayLayoutParams.bottomMargin = screenRect.height() - overlayRect.bottom

        popupRootView.addView(overlayView, overlayLayoutParams)

        popupWindow =
            PopupWindow(popupRootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        popupWindow.showAtLocation(activity.window.decorView.rootView, Gravity.NO_GRAVITY, 0, 0)

        popupWindow.isFocusable = true
        popupWindow.contentView.isFocusableInTouchMode = true;

        var startClickPosX = 0F
        var startClickPosY = 0F
        popupWindow.setTouchInterceptor { _, event ->
            val clickedOnOverlay = clickedOnOverlay(screenRect, overlayRect, event.x, event.y)
            val clickedOnAnchor = clickedOnAnchor(screenRect, event.x, event.y)
            if (event.action == MotionEvent.ACTION_UP && Math.abs(event.x - startClickPosX) < 30 && Math.abs(event.y - startClickPosY) < 30) {
                Log.d(TAG, "clickedOnOverlay = $clickedOnOverlay clickedOnAnchor = $clickedOnAnchor")

                return@setTouchInterceptor when {
                    clickedOnAnchor -> {
                        if (params.dismissOnTouchAnchor) dismiss()
                        if (params.anchorClickable) activity.dispatchTouchEvent(event)
                        !params.anchorClickable
                    }
                    clickedOnOverlay -> {
                        if (params.dismissOnTouchOverlay) dismiss()
                        true
                    }
                    else -> {
                        if (params.dismissOnTouchOutside) dismiss()
                        activity.dispatchTouchEvent(event)
                        false
                    }
                }
            } else {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    startClickPosX = event.x
                    startClickPosY = event.y
                }
                if (!clickedOnOverlay || clickedOnAnchor && params.anchorClickable) {
                    activity.dispatchTouchEvent(event)
                }
                return@setTouchInterceptor false
            }
        }

        popupWindow.setOnDismissListener(onDismissListener)

        val anchorLocation = IntArray(2)
        params.anchorView.getLocationOnScreen(anchorLocation)
        val anchorViewX = (anchorLocation[0] - overlayLayoutParams.leftMargin).toFloat()
        val anchorViewY = (anchorLocation[1] - overlayLayoutParams.topMargin - screenRect.top).toFloat()
        overlayView.setAnchorView(params.anchorView, anchorViewX, anchorViewY)

        val tooltipView = params.tooltipView

        if (tooltipView is AnchoredTooltip && params.tooltipPosition != TooltipPosition.CENTER && params.tooltipPosition != null) {
            val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.leftMargin = overlayLayoutParams.leftMargin
            lp.rightMargin = overlayLayoutParams.rightMargin
            val anchorX: Float
            when (params.tooltipPosition) {
                TooltipPosition.TOP -> {
                    anchorX = anchorViewX + params.anchorView.width / 2f
                    lp.bottomMargin = screenRect.height() - anchorLocation[1] + screenRect.top
                    lp.gravity = Gravity.BOTTOM
                }
                TooltipPosition.BOTTOM -> {
                    anchorX = anchorViewX + params.anchorView.width / 2f
                    lp.topMargin = anchorLocation[1] - screenRect.top + params.anchorView.height
                    lp.gravity = Gravity.TOP
                }
                else -> {
                    anchorX = 0f
                }
            }
            tooltipView.setTooltipAnchor(anchorX, params.tooltipPosition!!)
            popupRootView.addView(tooltipView, lp)
        } else {
            val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.topMargin = overlayLayoutParams.topMargin
            lp.bottomMargin = overlayLayoutParams.bottomMargin
            lp.leftMargin = overlayLayoutParams.leftMargin
            lp.rightMargin = overlayLayoutParams.rightMargin
            lp.gravity = Gravity.CENTER_VERTICAL
            popupRootView.addView(tooltipView, lp)
        }
    }

    private fun clickedOnAnchor(screenRect: Rect, x: Float, y: Float): Boolean {
        return false
    }

    private fun clickedOnOverlay(screenRect: Rect, overlayRect: Rect, x: Float, y: Float): Boolean {
        return x > overlayRect.left && x < overlayRect.right
                && y > overlayRect.top && y < overlayRect.bottom
    }

    private fun getOverlayRect(params: TooltipOverlayParams, screenRect: Rect): Rect {
        val rect = Rect()
        val location = IntArray(2)

        params.leftBarrier?.let {
            it.getLocationOnScreen(location)
            rect.left = location[0] + it.width
        }

        when (params.rightBarrier) {
            null -> rect.right = screenRect.width()
            else -> params.rightBarrier?.let {
                it.getLocationOnScreen(location)
                rect.right = location[0]
            }
        }

        params.topBarrier?.let {
            it.getLocationOnScreen(location)
            rect.top = location[1] + it.height - screenRect.top
        }
        params.bottomBarrier?.let {
            it.getLocationOnScreen(location)
            rect.bottom = location[1]
        }
        when (params.bottomBarrier) {
            null -> rect.bottom = screenRect.height()
            else -> params.bottomBarrier?.let {
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
