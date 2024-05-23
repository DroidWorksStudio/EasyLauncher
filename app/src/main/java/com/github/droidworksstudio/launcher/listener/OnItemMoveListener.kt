package com.github.droidworksstudio.launcher.listener

class OnItemMoveListener {
    interface OnItemActionListener {
        fun onViewMoved(oldPosition: Int, newPosition: Int): Boolean
        fun onViewSwiped(position: Int) {}
    }
}