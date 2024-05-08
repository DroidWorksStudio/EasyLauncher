package com.github.droidworksstudio.launcher.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import java.lang.ref.WeakReference

class MyAccessibilityService : AccessibilityService() {

    private var info: AccessibilityServiceInfo = AccessibilityServiceInfo()

    override fun onServiceConnected() {
        mInstance = WeakReference(this)

        info.apply {
            eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_VIEW_FOCUSED

            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC

            notificationTimeout = 100
        }

        this.serviceInfo = info
    }

    override fun onUnbind(intent: Intent?): Boolean {
        mInstance = WeakReference(null)

        return super.onUnbind(intent)
    }

    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    @RequiresApi(Build.VERSION_CODES.P)
    fun lockScreen(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun showRecents(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_RECENTS)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun openNotifications(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun openQuickSettings(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)
    }

    fun openPowerDialog(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun takeScreenShot(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT)
    }

    companion object {
        private var mInstance: WeakReference<MyAccessibilityService> = WeakReference(null)
        fun instance(): MyAccessibilityService? {
            return mInstance.get()
        }
    }
}