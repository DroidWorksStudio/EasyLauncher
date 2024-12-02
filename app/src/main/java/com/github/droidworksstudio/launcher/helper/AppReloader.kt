package com.github.droidworksstudio.launcher.helper

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper

object AppReloader {
    fun restartApp(context: Context) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent?.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        // Delay the restart slightly to ensure all current activities are finished
        Handler(Looper.getMainLooper()).postDelayed({
            context.startActivity(mainIntent)
            Runtime.getRuntime().exit(0)
        }, 250)
    }
}