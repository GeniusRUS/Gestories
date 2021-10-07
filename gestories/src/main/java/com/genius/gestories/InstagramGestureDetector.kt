package com.genius.gestories

import android.graphics.PointF
import android.graphics.Rect
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.annotation.StringDef
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.abs

/**
 * Instagram-like behaviour to handle gesture on stories view
 *
 * It must be used like smart [View.OnTouchListener] with attaching to view, with which the user will interact
 */
class InstagramGestureDetector @JvmOverloads constructor(
    private val actionsListener: ActionsListener? = null,
    private val gestureListener: GestureListener? = null,
    private val timeToDetectSingleTap: Long = ViewConfiguration.getPressedStateDuration().toLong(),
    private val timeToDetectLongTap: Long = ViewConfiguration.getLongPressTimeout().toLong(),
    private val zoneOfPreviousStories: Rect? = null,
    private val distanceToSwipeDetect: Float = 250F
) : View.OnTouchListener, OnApplyWindowInsetsListener {

    private val pointOfFirstTouch: PointF = PointF()

    private var tapTime = 0L
    private var regularTapRunnable: ClickRunnable? = null
    private var longTapRunnable: ClickRunnable? = null
    private var isTapIsActive: Boolean = false
    private var isProgressIsPaused: Boolean = false
    private var insetsRect: Rect? = null

    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
        val gestureInsets = insets.getInsets(WindowInsetsCompat.Type.systemGestures())
        insetsRect = Rect(
            gestureInsets.left,
            gestureInsets.top,
            v.measuredWidth - gestureInsets.right,
            v.measuredHeight - gestureInsets.bottom
        )
        return insets
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isInSystemGestureZone(event.x, event.y)) return false
                pointOfFirstTouch.set(event.x, event.y)
                tapTime = System.currentTimeMillis()
                isTapIsActive = true
                regularTapRunnable = ClickRunnable(isTapIsActive) {
                    actionsListener?.onActionReceive(GestureAction.PAUSE)
                    isProgressIsPaused = true
                }
                longTapRunnable = ClickRunnable(isTapIsActive) {
                    actionsListener?.onActionReceive(GestureAction.LONG_TAP)
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
                val isLongClick = isLongTap(tapTime)
                val eventResult = when {
                    isLongClick || (event.isGestureIsSwipe() && gestureListener != null) -> {
                        actionsListener?.onActionReceive(GestureAction.RESUME)
                        if (isLongClick) view.performLongClick()
                        true
                    }
                    isInPreviousZoneTap(view, event.x, event.y) -> {
                        actionsListener?.onActionReceive(GestureAction.PREVIOUS)
                        view.performClick()
                        true
                    }
                    else -> {
                        actionsListener?.onActionReceive(GestureAction.NEXT)
                        view.performClick()
                        true
                    }
                }

                if (isTapIsActive && event.isGestureIsSwipe()) {
                    when (val direction = event.swipeDirection()) {
                        SwipeDirection.TOP,
                        SwipeDirection.DOWN,
                        SwipeDirection.RIGHT,
                        SwipeDirection.LEFT -> gestureListener?.onGestureSwipe(direction)
                        else -> Unit
                    }
                }

                isTapIsActive = false
                regularTapRunnable?.isActive = false
                longTapRunnable?.isActive = false
                isProgressIsPaused = false
                eventResult
            }
            MotionEvent.ACTION_CANCEL -> {
                if (isProgressIsPaused) {
                    actionsListener?.onActionReceive(GestureAction.RESUME)
                    regularTapRunnable?.isActive = false
                    longTapRunnable?.isActive = false
                    isProgressIsPaused = false
                }
                isTapIsActive = false
                true
            }
            MotionEvent.ACTION_MOVE -> {
                val isSwipe = event.isGestureIsSwipe()
                if (isSwipe && isProgressIsPaused) {
                    regularTapRunnable?.isActive = false
                    longTapRunnable?.isActive = false
                }
                gestureListener?.onSwipeProgress(
                    PointF(
                        event.x - pointOfFirstTouch.x,
                        event.y - pointOfFirstTouch.y
                    )
                )
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
     * Detects that tap is long single tap
     * @param time - time of current touch event, when finger is holding on screen
     * @return is the tap long or not
     */
    private fun isLongTap(time: Long): Boolean {
        return time + timeToDetectLongTap < System.currentTimeMillis()
    }

    /**
     * Detects that is current touch is start on system gesture zone
     * This is usually will be useful on devices with API 30+ with gesture system navigation
     * This function will not work without setting [androidx.core.view.WindowCompat.setDecorFitsSystemWindows] to false
     * @param x - horizontal coordinate of touch
     * @param y - vertical coordinate of touch
     * @return is in touch in system gesture zone
     */
    private fun isInSystemGestureZone(x: Float, y: Float): Boolean {
        return insetsRect?.let { rect ->
            rect.left >= x || rect.right <= x || rect.top >= y || rect.bottom <= y
        } ?: return false
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
     * Detects in which side [SwipeDirection] moving the current swipe
     * Difference from start point must be greater that [distanceToSwipeDetect]
     * @receiver is that current [MotionEvent] of end current gesture
     * @return direction, one from [SwipeDirection], in which side swipe is detected, or null
     */
    @SwipeDirection
    private fun MotionEvent.swipeDirection(): String? {
        return when {
            abs(x - pointOfFirstTouch.x) < distanceToSwipeDetect && y - pointOfFirstTouch.y <= distanceToSwipeDetect -> SwipeDirection.TOP
            abs(x - pointOfFirstTouch.x) < distanceToSwipeDetect && y - pointOfFirstTouch.y >= distanceToSwipeDetect -> SwipeDirection.DOWN
            x - pointOfFirstTouch.x >= distanceToSwipeDetect && abs(y - pointOfFirstTouch.y) < distanceToSwipeDetect -> SwipeDirection.RIGHT
            x - pointOfFirstTouch.x <= -distanceToSwipeDetect && abs(y - pointOfFirstTouch.y) < distanceToSwipeDetect -> SwipeDirection.LEFT
            else -> null
        }
    }

    private class ClickRunnable(
        var isActive: Boolean,
        private val action: () -> Unit
    ): Runnable {

        override fun run() {
            if (isActive) {
                action.invoke()
            }
        }
    }

    @StringDef(
        SwipeDirection.TOP,
        SwipeDirection.DOWN,
        SwipeDirection.RIGHT,
        SwipeDirection.LEFT
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class SwipeDirection {
        companion object {
            const val TOP = "top"
            const val DOWN = "down"
            const val RIGHT = "right"
            const val LEFT = "left"
        }
    }

    @StringDef(
        GestureAction.PREVIOUS,
        GestureAction.NEXT,
        GestureAction.PAUSE,
        GestureAction.LONG_TAP,
        GestureAction.RESUME
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class GestureAction {
        companion object {
            /**
             * Calls to show previous stories
             *
             * It is invoked on click to first 1/3 width of view, or it zone can be specified with [zoneOfPreviousStories]
             */
            const val PREVIOUS = "previous"

            /**
             * Calls to show next stories
             *
             * It is invoked on click to the all remaining zone, exclude for previous stories, defined in [zoneOfPreviousStories]
             */
            const val NEXT = "next"

            /**
             * Calls to pause of stories progress
             *
             * It is invoked after finger is tapping the screen, and after [timeToDetectSingleTap] milliseconds delay
             */
            const val PAUSE = "pause"

            /**
             * Calls to hide the progress of stories or another interface to see it better
             *
             * It is invoked during long tap behaviour, and waiting [timeToDetectLongTap] milliseconds after finger is down on screen
             */
            const val LONG_TAP = "long_tap"

            /**
             * Calls to resume stories progress
             *
             * It is invoked after any events to stop interacting with stories: on finger up from screen or cancel gesture by moving finger on screen with much distance
             */
            const val RESUME = "resume"
        }
    }

    /**
     * Main interface on Instagram like gestures
     */
    interface ActionsListener {
        fun onActionReceive(@GestureAction action: String)
    }

    /**
     * Secondary interface to detect gestures on target view
     * - [onGestureSwipe] is called on detection completed gestures in one of main [SwipeDirection]
     * - [onSwipeProgress] is called on any pointer movement on target view
     */
    interface GestureListener {
        fun onGestureSwipe(@SwipeDirection direction: String) {}
        fun onSwipeProgress(offsetFromFirstPoint: PointF) {}
    }

    companion object {
        @JvmStatic
        fun View.attachDetector(instagramGestureDetector: InstagramGestureDetector) {
            setOnTouchListener(instagramGestureDetector)
            ViewCompat.setOnApplyWindowInsetsListener(this, instagramGestureDetector)
        }

        @JvmStatic
        fun View.detachDetector() {
            setOnTouchListener(null)
            ViewCompat.setOnApplyWindowInsetsListener(this, null)
        }
    }
}