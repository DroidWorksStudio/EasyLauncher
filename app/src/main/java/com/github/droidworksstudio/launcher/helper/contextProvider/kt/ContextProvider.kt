package com.github.droidworksstudio.launcher.helper.contextProvider.kt

import android.content.Context

object ContextProvider {
    lateinit var applicationContext: Context
        private set

    fun init(context: Context) {
        applicationContext = context
    }
}


