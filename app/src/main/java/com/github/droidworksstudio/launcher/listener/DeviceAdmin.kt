package com.github.droidworksstudio.launcher.listener

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import com.github.droidworksstudio.common.showLongToast

class DeviceAdmin : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        context.showLongToast("Enabled")
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        context.showLongToast("Disabled")
    }
}