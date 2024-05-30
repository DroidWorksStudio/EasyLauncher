package com.github.droidworksstudio.ktx

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService

fun Activity.getDisplayWidth(): Int {
    val windowManager = getSystemService<WindowManager>() ?: return 0
    val width = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = windowManager.currentWindowMetrics
        val bounds = windowMetrics.bounds
        bounds.width()
    } else {
        val displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }
    return width
}


fun Activity.enablePortraitScreenOrientationForMobile(isPortrait: Boolean) {
    if (!isTabletConfig()) {
        requestedOrientation = if (isPortrait) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        else ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }
}

fun Activity.lockScreenOrientationChanges(lock: Boolean) {
    requestedOrientation =
        if (lock) ActivityInfo.SCREEN_ORIENTATION_LOCKED else ActivityInfo.SCREEN_ORIENTATION_SENSOR
}

fun Activity.openEmailClient(emailTo: String, subject: String) {
    val uriString = "mailto:$emailTo?subject=$subject"
    val emailIntent = Intent(Intent.ACTION_SENDTO)

    emailIntent.data = Uri.parse(uriString)

    if (emailIntent.resolveActivity(packageManager) != null) {
        startActivity(emailIntent)
    } else {
        startActivity(Intent.createChooser(emailIntent, null))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun Activity.openNotificationSettings() {
    val intent = Intent()
    intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
    intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    intent.putExtra("app_package", packageName)
    intent.putExtra("app_uid", applicationInfo.uid)
    startActivity(intent)
}

/**
 * Starting from appcompat v.1.1.0 system overrides baseContext after [attachBaseContext]
 * causing resetting locale settings. To prevent this we must call [applyOverrideConfiguration]
 * with modified [overrideConfiguration] object.
 *
 * That's a workaround until this behaviour will be fixed in future appcompat releases.
 */
fun Activity.setupOverrideConfiguration(overrideConfiguration: Configuration?): Configuration? {
    if (overrideConfiguration != null) {
        val uiMode = overrideConfiguration.uiMode
        overrideConfiguration.setTo(baseContext.resources.configuration)
        overrideConfiguration.uiMode = uiMode
    }

    return overrideConfiguration
}

/**
 * Navigates to the current Google user's account
 * subscriptions in Google Play app.
 */
fun Activity.openGooglePlaySubscriptions() {
    val uriString = "https://play.google.com/store/account/subscriptions"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
    startActivity(intent)
}

fun Activity.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Activity.showShortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}