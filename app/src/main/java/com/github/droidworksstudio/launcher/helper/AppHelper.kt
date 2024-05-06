package com.github.droidworksstudio.launcher.helper

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.accessibility.AccessibilityManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.github.droidworksstudio.launcher.Constants
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.accessibility.MyAccessibilityService
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.ui.activities.LauncherActivity
import javax.inject.Inject

class AppHelper @Inject constructor(){

    fun resetDefaultLauncher(context: Context) {
        try {
            val packageManager = context.packageManager
            val componentName = ComponentName(context, LauncherActivity::class.java)
            packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            val launcherPicker = Intent(Intent.ACTION_MAIN)
            launcherPicker.addCategory(Intent.CATEGORY_HOME)
            context.startActivity(launcherPicker)
            packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("WrongConstant", "PrivateApi")
    fun expandNotificationDrawer(context: Context) {
        try {
            val statusBarService = context.getSystemService(Constants.NOTIFICATION_SERVICE)
            val statusBarManager = Class.forName(Constants.NOTIFICATION_MANAGER)
            val method = statusBarManager.getMethod(Constants.NOTIFICATION_METHOD)
            method.invoke(statusBarService)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun searchView(context: Context){
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, "")
        context.startActivity(intent)
    }

    fun updateUI(view: View, gravity: Int, selectColor: Int, isVisible: Boolean) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        layoutParams.gravity = gravity
        view.layoutParams = layoutParams

        if (view is TextView) {
            view.setTextColor(selectColor)
            view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
            view.isClickable = if(isVisible) view.isClickable else !view.isClickable
        }
    }

    fun launchApp(context: Context, appInfo: AppInfo) {
        val intent = context.packageManager.getLaunchIntentForPackage(appInfo.packageName)
        if (intent != null) {
            context.startActivity(intent)
        } else {
            showToast(context, "Failed to open the application")
        }
    }
    fun launchClock(context: Context) {
        try {
            val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("launchClock", "Error launching clock app: ${e.message}")
        }
    }

    fun launchCalendar(context: Context){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = CalendarContract.CONTENT_URI
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // If unable to launch the calendar, try using the app picker
            val pickerIntent = Intent(Intent.ACTION_MAIN)
            pickerIntent.addCategory(Intent.CATEGORY_APP_CALENDAR)
            try {
                context.startActivity(pickerIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun unInstallApp(context: Context, appInfo: AppInfo){
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:${appInfo.packageName}")
        context.startActivity(intent)
    }

    fun appInfo(context: Context, appInfo: AppInfo){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", appInfo.packageName, null)
        context.startActivity(intent)
    }

    fun gravityToString(gravity: Int): String? {
        return when (gravity) {
            Gravity.CENTER -> "CENTER"
            Gravity.START -> "LEFT"
            Gravity.END -> "RIGHT"
            Gravity.TOP -> "TOP"
            Gravity.BOTTOM -> "BOTTOM"
            else -> null
        }
    }

    fun getGravityFromSelectedItem(selectedItem: String): Int {
        return when (selectedItem) {
            "Left" -> Gravity.START
            "Center" -> Gravity.CENTER
            "Right" -> Gravity.END
            else -> Gravity.START
        }
    }


    fun showToast(context: Context,message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showStatusBar(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.show(WindowInsets.Type.statusBars())
        } else
            @Suppress("DEPRECATION", "InlinedApi")
            window.decorView.apply { systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN }
    }

    fun hideStatusBar(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        else {
            @Suppress("DEPRECATION")
            window.decorView.apply { systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_FULLSCREEN }
        }
    }

    fun enableAppAsAccessibilityService(context: Context, accessibilityState: Boolean) {

        val myAccessibilityService = MyAccessibilityService.instance()

        val state: String = if(myAccessibilityService != null){
            context.getString(R.string.accessibility_settings_disable)
        }else{
            context.getString(R.string.accessibility_settings_enable)
        }

        val builder = MaterialAlertDialogBuilder(context, R.style.CustomDialogTheme)

        builder.setTitle(R.string.accessibility_settings_title)
        builder.setMessage(R.string.accessibility_service_desc)
        builder.setPositiveButton(state) { _, _ ->
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            context.startActivity(intent)
        }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    fun isAccessServiceEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        val serviceClassName = MyAccessibilityService::class.java.name // Replace to your full packageName
        val packageName = Constants.PACKAGE_NAME
        return enabledServices?.contains("$packageName/$serviceClassName") == true &&
                am.isEnabled && am.isTouchExplorationEnabled
    }

}