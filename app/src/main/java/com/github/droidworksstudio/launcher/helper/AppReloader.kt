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

        // Delay the restart slightly to ensure all current activities are finished
        Handler(Looper.getMainLooper()).post {
            context.startActivity(mainIntent)
            Runtime.getRuntime().exit(0)
        }
    }
}