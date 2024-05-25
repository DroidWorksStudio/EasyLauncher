package com.github.droidworksstudio.launcher.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.github.droidworksstudio.launcher.listener.ScrollEventListener

class GestureNestedScrollView(context: Context, attrs: AttributeSet) :
    NestedScrollView(context, attrs) {

    var scrollEventListener: ScrollEventListener? = null


    init {
        isNestedScrollingEnabled = true
    }

    fun isTopReached(): Boolean {
        return !canScrollVertically(-1)
    }

    fun isBottomReached(): Boolean {
        return !canScrollVertically(1)
    }

    fun registerRecyclerView(recyclerView: RecyclerView, eventListener: ScrollEventListener) {
        scrollEventListener = eventListener
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                scrollEventListener?.onScroll(isTopReached(), isBottomReached())
            }
        })
    }
}
