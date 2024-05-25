package com.github.droidworksstudio.launcher.listener

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.github.droidworksstudio.launcher.Constants
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.abs

/*
 * This function is based on the solution provided in the following tutorial:
 * "How to handle swipe gestures in Kotlin?"
 * Tutorial URL: https://www.tutorialspoint.com/how-to-handle-swipe-gestures-in-kotlin
 *
 * The original code has been modified to fit the specific requirements of this project.
 */

internal open class OnSwipeTouchListener(c: Context?) : OnTouchListener {
    private var longPressOn = false
    private var doubleTapOn = false
    private val gestureDetector: GestureDetector

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        if (motionEvent.action == MotionEvent.ACTION_UP)
            longPressOn = false
        return gestureDetector.onTouchEvent(motionEvent)
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        private val swipeThreshold: Int = 100
        private val swipeVelocityThreshold: Int = 100

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (doubleTapOn) {
                doubleTapOn = false
                onTripleClick()
            }
            return super.onSingleTapUp(e)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            doubleTapOn = true
            Timer().schedule(Constants.TRIPLE_TAP_DELAY_MS.toLong()) {
                if (doubleTapOn) {
                    doubleTapOn = false
                    onDoubleClick()
                }
            }
            return super.onDoubleTap(e)
        }

        override fun onLongPress(e: MotionEvent) {
            longPressOn = true
            Timer().schedule(Constants.LONG_PRESS_DELAY_MS.toLong()) {
                if (longPressOn) onLongClick()
            }
            super.onLongPress(e)
        }

        override fun onFling(
            event1: MotionEvent?,
            event2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            try {
                val diffY = event2.y - event1!!.y
                val diffX = event2.x - event1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
                        if (diffX > 0) onSwipeRight() else onSwipeLeft()
                    }
                } else {
                    if (abs(diffY) > swipeThreshold && abs(velocityY) > swipeVelocityThreshold) {
                        if (diffY < 0) onSwipeUp() else onSwipeDown()
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return false
        }
    }

    open fun onSwipeRight() {}
    open fun onSwipeLeft() {}
    open fun onSwipeUp() {}
    open fun onSwipeDown() {}
    open fun onLongClick() {}
    open fun onDoubleClick() {}
    open fun onTripleClick() {}

    init {
        gestureDetector = GestureDetector(c, GestureListener())
    }
}