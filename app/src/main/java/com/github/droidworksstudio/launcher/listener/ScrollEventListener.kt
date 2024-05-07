package com.github.droidworksstudio.launcher.listener

interface ScrollEventListener {
    fun onTopReached()
    fun onBottomReached()
    fun onScroll(isTopReached: Boolean, isBottomReached: Boolean)
}