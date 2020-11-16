package com.github.osmplayer.gestures

import android.view.MotionEvent


interface IGestureListener {
    fun onTap()
    fun onHorizontalScroll(event: MotionEvent?, delta: Float)
    fun onVerticalScroll(event: MotionEvent?, delta: Float)
    fun onSwipeRight()
    fun onSwipeLeft()
    fun onSwipeBottom()
    fun onSwipeTop()
}