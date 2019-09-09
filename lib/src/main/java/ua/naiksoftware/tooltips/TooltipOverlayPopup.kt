package ua.naiksoftware.tooltips

import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.view.*
import android.widget.FrameLayout
import android.widget.PopupWindow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TooltipOverlayPopup() {

    companion object {
        val TAG = TooltipOverlayPopup::class.java.simpleName
    }

    private lateinit var popupWindow: PopupWindow

    fun show(
        params: TooltipOverlayParams,
        activity: Activity,
        onDismissListener: TooltipDismissListener? = null
    ) {

        val popupRootView = FrameLayout(activity)
        val overlayView = TooltipOverlayView(activity)
        overlayView.setBackgroundColor(if (params.overlayTransparent) Color.TRANSPARENT else params.overlayColor)

        val screenRect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(screenRect)
        val overlayRect = getOverlayRect(params, screenRect)

        val overlayLayoutParams =
            ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        overlayLayoutParams.leftMargin = overlayRect.left
        overlayLayoutParams.topMargin = overlayRect.top
        overlayLayoutParams.rightMargin = screenRect.width() - overlayRect.right
        overlayLayoutParams.bottomMargin = screenRect.height() - overlayRect.bottom

        val anchorLocation = IntArray(2)
        params.anchorView?.getLocationOnScreen(anchorLocation)
        val anchorViewX = anchorLocation[0] - overlayLayoutParams.leftMargin
        val anchorViewY = anchorLocation[1] - overlayLayoutParams.topMargin - screenRect.top
        val anchorRect = Rect(
            anchorLocation[0],
            anchorLocation[1] - screenRect.top,
            anchorLocation[0] + (params.anchorView?.width ?: 0),
            anchorLocation[1] + (params.anchorView?.height ?: 0) - screenRect.top
        )

        popupRootView.addView(overlayView, overlayLayoutParams)

        popupWindow =
            PopupWindow(
                popupRootView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        popupWindow.showAtLocation(activity.window.decorView.rootView, Gravity.NO_GRAVITY, 0, 0)

        if (params.overlayFadeDuration > 0) {
            popupRootView.alpha = 0f
            popupRootView.animate().alpha(1f).setDuration(params.overlayFadeDuration).start()
        }

        popupWindow.isFocusable = true
        popupWindow.contentView.isFocusableInTouchMode = true

        val tooltipView = params.tooltipView

        if (tooltipView is AnchoredTooltip && params.tooltipPosition != TooltipPosition.CENTER && params.tooltipPosition != null) {
            val lp = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            lp.leftMargin = overlayLayoutParams.leftMargin
            lp.rightMargin = overlayLayoutParams.rightMargin
            val anchorX: Float
            when (params.tooltipPosition) {
                TooltipPosition.TOP -> {
                    anchorX = anchorViewX + (params.anchorView?.width ?: 0) / 2f
                    lp.bottomMargin = screenRect.height() - anchorLocation[1] + screenRect.top
                    lp.gravity = Gravity.BOTTOM
                }
                TooltipPosition.BOTTOM -> {
                    anchorX = anchorViewX + (params.anchorView?.width ?: 0) / 2f
                    lp.topMargin =
                        anchorLocation[1] - screenRect.top + (params.anchorView?.height ?: 0)
                    lp.gravity = Gravity.TOP
                }
                else -> {
                    anchorX = 0f
                }
            }
            tooltipView.setTooltipAnchor(anchorX, params.tooltipPosition!!)
            popupRootView.addView(tooltipView, lp)
        } else {
            val lp = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            lp.topMargin = overlayLayoutParams.topMargin
            lp.bottomMargin = overlayLayoutParams.bottomMargin
            lp.leftMargin = overlayLayoutParams.leftMargin
            lp.rightMargin = overlayLayoutParams.rightMargin
            lp.gravity = Gravity.CENTER_VERTICAL
            popupRootView.addView(tooltipView, lp)
        }

        var tooltipRect : Rect? = null
        var dismissDelayed = false

        var startClickPosX = 0F
        var startClickPosY = 0F
        popupWindow.setTouchInterceptor { _, event ->
            val clickedOnOverlay = clickedOnRect(overlayRect, event.x, event.y)
            val clickedOnAnchor = clickedOnRect(anchorRect, event.x, event.y)

            if (tooltipRect == null) {
                tooltipRect = getTooltipRect(tooltipView, screenRect)
            }

            val clickedOnTooltip = clickedOnRect(tooltipRect!!, event.x, event.y)

            if (event.action == MotionEvent.ACTION_UP && Math.abs(event.x - startClickPosX) < 30 && Math.abs(
                    event.y - startClickPosY
                ) < 30
            ) {

                return@setTouchInterceptor when {
                    clickedOnAnchor -> {
                        if (params.dismissOnTouchAnchor) dismiss()
                        if (params.anchorClickable) activity.dispatchTouchEvent(event)
                        !params.anchorClickable
                    }
                    clickedOnOverlay -> {
                        if (params.dismissOnTouchOverlay) dismiss()
                        if (params.overlayTransparent && !clickedOnTooltip) {
                            activity.dispatchTouchEvent(event)
                            false
                        } else {
                            true
                        }
                    }
                    else -> {
                        if (params.dismissOnTouchOutside) {
                            activity.dispatchTouchEvent(event)
                            dismiss()
                        }
                        false
                    }
                }
            } else {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    startClickPosX = event.x
                    startClickPosY = event.y
                }

                if (params.overlayTransparent && !clickedOnTooltip) {
                    activity.dispatchTouchEvent(event)
                    if (event.action != MotionEvent.ACTION_UP) {
                        if (!dismissDelayed) {
                            dismissDelayed = true
                            dismissAsync(200)
                        }
                    } else {
                        dismiss()
                    }

                } else if (!clickedOnOverlay
                    || clickedOnAnchor && params.anchorClickable && event.action != MotionEvent.ACTION_MOVE
                ) {
                    activity.dispatchTouchEvent(event)
                }
                return@setTouchInterceptor false
            }
        }

        popupWindow.setOnDismissListener {
            onDismissListener?.onTooltipDismissed()
        }

        params.anchorView?.let {
            overlayView.setAnchorView(it, anchorViewX.toFloat(), anchorViewY.toFloat())
        }
    }

    private fun clickedOnRect(overlayRect: Rect, x: Float, y: Float): Boolean {
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

    private fun getTooltipRect(
        tooltipView: View,
        screenRect: Rect
    ): Rect? {
        val tooltipLocation = IntArray(2)
        tooltipView.getLocationOnScreen(tooltipLocation)
        return Rect(
            tooltipLocation[0] + tooltipView.paddingLeft,
            tooltipLocation[1] - screenRect.top + tooltipView.paddingTop,
            tooltipLocation[0] + tooltipView.width - tooltipView.paddingRight,
            tooltipLocation[1] + tooltipView.height - screenRect.top - tooltipView.paddingBottom
        )
    }

    fun dismiss() {
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }

    fun dismissAsync(timeout: Long) {
        GlobalScope.launch(Dispatchers.Main) {
            delay(timeout)
            dismiss()
        }
    }

    fun isShowing() = popupWindow.isShowing
}
