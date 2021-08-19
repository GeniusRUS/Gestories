package com.genius.gestories

import android.graphics.PointF
import android.graphics.Rect
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration

class InstagramGestureDetector(
    private val actionsListener: ActionsListener? = null,
    private val timeToDetectLongTap: Long = ViewConfiguration.getLongPressTimeout().toLong(),
    private val zoneOfPreviousStories: Rect? = null
) : View.OnTouchListener {

    private val pointOfFirstTouch: PointF = PointF()

    private var tapTime = 0L
    private var longTapRunnable: Runnable? = null
    private var isTapIsActive: Boolean = false
    private var isProgressIsPaused: Boolean = false

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pointOfFirstTouch.set(event.x, event.y)
                tapTime = System.currentTimeMillis()
                isTapIsActive = true
                longTapRunnable = object : Runnable {
                    override fun run() {
                        if (!isTapIsActive) return
                        actionsListener?.onPauseProgress()
                        isProgressIsPaused = true
                    }
                }
                longTapRunnable?.let { runnable ->
                    Handler(view.context.mainLooper).postDelayed(runnable, timeToDetectLongTap)
                }
                true
            }
            MotionEvent.ACTION_UP -> {
                isTapIsActive = false
                isProgressIsPaused = false
                if (!isRegularTap(tapTime)) {
                    actionsListener?.onResumeProgress()
                    true
                } else if (isInPreviousZoneTap(view, event.x, event.y)) {
                    actionsListener?.onShowPreviousStories()
                    view.performClick()
                } else {
                    actionsListener?.onShowNextStories()
                    view.performClick()
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                isTapIsActive = false
                if (isProgressIsPaused) {
                    actionsListener?.onResumeProgress()
                    isProgressIsPaused = false
                }
                true
            }
            else -> {
                false
            }
        }
    }

    /**
     * Detects that tap is in zone of previous stories
     * @param view - [View] that corresponding tap event
     * @param relativeX - x coordinate on the [view] of tap
     * @param relativeY - y coordinate on the [view] of tap
     * @return is in zone tap or not
     */
    private fun isInPreviousZoneTap(view: View, relativeX: Float, relativeY: Float): Boolean {
        val widthOfZonePreviousStories = zoneOfPreviousStories?.width()?.toFloat() ?: view.width / 100F * 33F
        val heightOfZonePreviousStories = zoneOfPreviousStories?.height()?.toFloat() ?: view.height.toFloat()
        return widthOfZonePreviousStories >= relativeX && heightOfZonePreviousStories >= relativeY
    }

    /**
     * Detects that tap is regular single tap
     * @param time - time of current touch event, when finger is holding on screen
     * @return is the tap regular or not
     */
    private fun isRegularTap(time: Long): Boolean {
        return time + timeToDetectLongTap > System.currentTimeMillis()
    }

    interface ActionsListener {
        fun onShowPreviousStories()
        fun onShowNextStories()
        fun onPauseProgress()
        fun onResumeProgress()
    }
}