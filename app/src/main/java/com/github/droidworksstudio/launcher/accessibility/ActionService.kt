package com.github.droidworksstudio.launcher.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.github.droidworksstudio.launcher.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.ref.WeakReference

class ActionService : AccessibilityService() {

    private var info: AccessibilityServiceInfo = AccessibilityServiceInfo()

    override fun onServiceConnected() {
        mInstance = WeakReference(this)

        info.apply {
            eventTypes =
                AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_VIEW_FOCUSED

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

    @RequiresApi(Build.VERSION_CODES.P)
    fun openPowerDialog(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun takeScreenShot(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun toggleSplitScreen(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)
    }

    companion object {
        fun runAccessibilityMode(context: Context) {
            if (instance() == null) {
                // Create a Handler that posts to the main thread
                Handler(Looper.getMainLooper()).post {
                    val state: String = context.getString(R.string.accessibility_settings_enable)

                    val builder = MaterialAlertDialogBuilder(context)
                    builder.setTitle(R.string.accessibility_settings_title)
                    builder.setMessage(R.string.accessibility_service_desc)
                    builder.setPositiveButton(state) { _, _ ->
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        context.startActivity(intent)
                    }
                    builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                }
            }
        }

        private var mInstance: WeakReference<ActionService> = WeakReference(null)
        fun instance(): ActionService? {
            return mInstance.get()
        }
    }
}