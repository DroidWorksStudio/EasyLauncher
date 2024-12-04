package com.github.droidworksstudio.launcher.helper

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import kotlin.system.exitProcess

object AppReloader {
    fun restartApp(context: Context) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent?.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)

        // Schedule the restart
        Handler(Looper.getMainLooper()).postDelayed({
            context.startActivity(mainIntent)
            // Kill the app process
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(0)
        }, 250)
    }
}