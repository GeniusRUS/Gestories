package com.genius.gestories

import android.graphics.PointF
import android.graphics.Rect
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.annotation.StringDef
import kotlin.math.abs

class InstagramGestureDetector @JvmOverloads constructor(
    private val actionsListener: ActionsListener? = null,
    private val gestureListener: GestureListener? = null,
    private val timeToDetectSingleTap: Long = ViewConfiguration.getPressedStateDuration().toLong(),
    private val timeToDetectLongTap: Long = ViewConfiguration.getLongPressTimeout().toLong(),
    private val zoneOfPreviousStories: Rect? = null,
    private val distanceToSwipeDetect: Float = 250F
) : View.OnTouchListener {

    private val pointOfFirstTouch: PointF = PointF()

    private var tapTime = 0L
    private var regularTapRunnable: Runnable? = null
    private var longTapRunnable: Runnable? = null
    private var isTapIsActive: Boolean = false
    private var isProgressIsPaused: Boolean = false

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pointOfFirstTouch.set(event.x, event.y)
                tapTime = System.currentTimeMillis()
                isTapIsActive = true
                regularTapRunnable = Runnable {
                    if (!isTapIsActive) return@Runnable
                    actionsListener?.onPauseProgress()
                    isProgressIsPaused = true
                }
                longTapRunnable = Runnable {
                    if (!isTapIsActive) return@Runnable
                    actionsListener?.onLongTapDetected()
                }
                regularTapRunnable?.let { runnable ->
                    Handler(view.context.mainLooper).postDelayed(runnable, timeToDetectSingleTap)
                }
                longTapRunnable?.let { runnable ->
                    Handler(view.context.mainLooper).postDelayed(runnable, timeToDetectLongTap)
                }
                true
            }
            MotionEvent.ACTION_UP -> {
                val eventResult = if (isTapIsActive && event.isGestureIsSwipe()) {
                    when (event.swipeDirection()) {
                        TOP -> gestureListener?.onSwipeToUp()
                        DOWN -> gestureListener?.onSwipeToDown()
                        RIGHT -> gestureListener?.onSwipeToRight()
                        LEFT -> gestureListener?.onSwipeToLeft()
                        else -> Unit
                    }
                    true
                } else if (!isRegularTap(tapTime)) {
                    actionsListener?.onResumeProgress()
                    true
                } else if (isInPreviousZoneTap(view, event.x, event.y)) {
                    actionsListener?.onShowPreviousStories()
                    view.performClick()
                } else {
                    actionsListener?.onShowNextStories()
                    view.performClick()
                }
                isTapIsActive = false
                isProgressIsPaused = false
                eventResult
            }
            MotionEvent.ACTION_CANCEL -> {
                if (isProgressIsPaused) {
                    actionsListener?.onResumeProgress()
                    isProgressIsPaused = false
                }
                isTapIsActive = false
                true
            }
            MotionEvent.ACTION_MOVE -> {
                val isSwipe = event.isGestureIsSwipe()
                if (isSwipe && isProgressIsPaused) {
                    isTapIsActive = false
                }
                isSwipe
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

    /**
     * Detects that is current gesture is the swipe
     * Difference from start point must be greater that [distanceToSwipeDetect]
     * @receiver is that current [MotionEvent] of end current gesture
     * @return is the gesture is swipe or not
     */
    private fun MotionEvent.isGestureIsSwipe(): Boolean {
        return abs(pointOfFirstTouch.length() - PointF(x, y).length()) >= distanceToSwipeDetect
    }

    /**
     * Detects in which side [SwipeOrientation] moving the current swipe
     * Difference from start point must be greater that [distanceToSwipeDetect]
     * @receiver is that current [MotionEvent] of end current gesture
     * @return direction, one from [SwipeOrientation], in which side swipe is detected, or null
     */
    @SwipeOrientation
    private fun MotionEvent.swipeDirection(): String? {
        return when {
            abs(x - pointOfFirstTouch.x) < distanceToSwipeDetect && y - pointOfFirstTouch.y <= distanceToSwipeDetect -> TOP
            abs(x - pointOfFirstTouch.x) < distanceToSwipeDetect && y - pointOfFirstTouch.y >= distanceToSwipeDetect -> DOWN
            x - pointOfFirstTouch.x >= distanceToSwipeDetect && abs(y - pointOfFirstTouch.y) < distanceToSwipeDetect -> RIGHT
            x - pointOfFirstTouch.x <= -distanceToSwipeDetect && abs(y - pointOfFirstTouch.y) < distanceToSwipeDetect -> LEFT
            else -> null
        }
    }

    @StringDef(TOP, DOWN, RIGHT, LEFT)
    @Retention(AnnotationRetention.SOURCE)
    private annotation class SwipeOrientation

    interface ActionsListener {
        fun onShowPreviousStories()
        fun onShowNextStories()
        fun onPauseProgress()
        fun onLongTapDetected()
        fun onResumeProgress()
    }

    interface GestureListener {
        fun onSwipeToRight()
        fun onSwipeToLeft()
        fun onSwipeToUp()
        fun onSwipeToDown()
    }

    private companion object {
        private const val TOP = "top"
        private const val DOWN = "down"
        private const val RIGHT = "right"
        private const val LEFT = "left"
    }
}