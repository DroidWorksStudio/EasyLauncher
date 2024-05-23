package com.github.droidworksstudio.launcher.listener

interface ScrollEventListener {
    fun onScroll(isTopReached: Boolean, isBottomReached: Boolean) {}
}