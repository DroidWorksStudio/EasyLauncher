package com.github.droidworksstudio.launcher.helper

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
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
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.github.droidworksstudio.launcher.Constants
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.accessibility.MyAccessibilityService
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.ui.activities.FakeHomeActivity
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class AppHelper @Inject constructor() {

    fun resetDefaultLauncher(context: Context) {
        try {
            val packageManager = context.packageManager
            val componentName = ComponentName(context, FakeHomeActivity::class.java)
            packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            val selector = Intent(Intent.ACTION_MAIN)
            selector.addCategory(Intent.CATEGORY_HOME)
            context.startActivity(selector)
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
        } catch (exception: Exception) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                initActionService(context)?.openNotifications()
//            }
            exception.printStackTrace()
        }
    }

    @SuppressLint("WrongConstant", "PrivateApi")
    private fun expandQuickSettings(context: Context) {
        try {
            val statusBarService = context.getSystemService(Constants.QUICKSETTINGS_SERVICE)
            val statusBarManager = Class.forName(Constants.QUICKSETTINGS_MANAGER)
            val method = statusBarManager.getMethod(Constants.QUICKSETTINGS_METHOD)
            method.invoke(statusBarService)
        } catch (exception: Exception) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                initActionService(context)?.openQuickSettings()
//            }
            exception.printStackTrace()
        }
    }

    fun searchView(context: Context) {
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, "")
        context.startActivity(intent)
    }

    fun dayNightMod(context: Context, view: View) {
        when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                view.setBackgroundColor(context.resources.getColor(R.color.blackTrans50, context.theme))
            }

            Configuration.UI_MODE_NIGHT_NO -> {
                view.setBackgroundColor(context.resources.getColor(R.color.whiteTrans50, context.theme))
            }
        }
    }

    fun updateUI(view: View, gravity: Int, selectColor: Int, textSize: Float, isVisible: Boolean) {
        val layoutParams = view.layoutParams as LinearLayoutCompat.LayoutParams
        layoutParams.gravity = gravity
        view.layoutParams = layoutParams

        if (view is TextView) {
            view.setTextColor(selectColor)
            view.textSize = textSize
            view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
            view.isClickable = if (isVisible) view.isClickable else !view.isClickable
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

    fun launchCalendar(context: Context) {
        try {
            val cal: Calendar = Calendar.getInstance()
            cal.time = Date()
            val time = cal.time.time
            val builder: Uri.Builder = CalendarContract.CONTENT_URI.buildUpon()
            builder.appendPath("time")
            builder.appendPath(time.toString())
            context.startActivity(Intent(Intent.ACTION_VIEW, builder.build()))
        } catch (e: Exception) {
            try {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_APP_CALENDAR)
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.d("openCalendar", e.toString())
            }
        }
    }

    fun openDigitalWellbeing(context: Context) {
        try {
            val packageName = "com.google.android.apps.wellbeing"
            val className = "com.google.android.apps.wellbeing.settings.TopLevelSettingsActivity"

            val intent = Intent()
            intent.component = ComponentName(packageName, className)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Digital Wellbeing app is not installed or cannot be opened
            // Handle this case as needed
            showToast(context,"Digital Wellbeing is not available on this device.")
        }
    }

    fun openBatteryManager(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Battery manager settings cannot be opened
            // Handle this case as needed
            showToast(context, "Battery manager settings are not available on this device.")
        }
    }

    fun unInstallApp(context: Context, appInfo: AppInfo) {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:${appInfo.packageName}")
        context.startActivity(intent)
    }

    fun appInfo(context: Context, appInfo: AppInfo) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(WindowInsets.Type.statusBars())
        } else
            @Suppress("DEPRECATION", "InlinedApi")
            window.decorView.apply {
                systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
    }

    fun hideStatusBar(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        else {
            @Suppress("DEPRECATION")
            window.decorView.apply {
                systemUiVisibility =
                    View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_FULLSCREEN
            }
        }
    }

    fun showSoftKeyboard(context: Context, view: View) {
        if (view.requestFocus()) {
            val inputMethodManager: InputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun View.showSoftKeyboard() {
        if (this.requestFocus()) {
            val inputMethodManager: InputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }

    fun wordOfTheDay(resources: Resources): String {
        val dailyWordsArray = resources.getStringArray(R.array.settings_appearance_daily_word_default)
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val wordIndex = (dayOfYear - 1) % dailyWordsArray.size // Subtracting 1 to align with array indexing
        return dailyWordsArray[wordIndex]
    }

    fun enableAppAsAccessibilityService(context: Context, accessibilityState: Boolean) {

        val myAccessibilityService = MyAccessibilityService.instance()

        val state: String = if (myAccessibilityService != null) {
            context.getString(R.string.accessibility_settings_disable)
        } else {
            context.getString(R.string.accessibility_settings_enable)
        }

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