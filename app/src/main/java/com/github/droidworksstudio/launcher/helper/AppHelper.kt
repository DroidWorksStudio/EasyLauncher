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
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.github.droidworksstudio.launcher.Constants
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.accessibility.MyAccessibilityService
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.listener.OnSwipeTouchListener
import com.github.droidworksstudio.launcher.ui.activities.FakeHomeActivity
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

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
            // Additional step to open the launcher settings if the first method fails
            try {
                val intent = Intent("android.settings.HOME_SETTINGS")
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Fallback to general settings if specific launcher settings are not found
                try {
                    val intent = Intent(Settings.ACTION_SETTINGS)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    @SuppressLint("WrongConstant", "PrivateApi")
    fun expandNotificationDrawer(context: Context) {
        try {
            Class.forName(Constants.NOTIFICATION_MANAGER)
                .getMethod(Constants.NOTIFICATION_METHOD)
                .invoke(context.getSystemService(Constants.NOTIFICATION_SERVICE))
        } catch (exception: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                MyAccessibilityService.runAccessibilityMode(context)
                MyAccessibilityService.instance()?.openNotifications()
            }
            exception.printStackTrace()
        }
    }

    @SuppressLint("WrongConstant", "PrivateApi")
    private fun expandQuickSettings(context: Context) {
        try {
            Class.forName(Constants.QUICKSETTINGS_MANAGER)
                .getMethod(Constants.QUICKSETTINGS_METHOD)
                .invoke(context.getSystemService(Constants.QUICKSETTINGS_SERVICE))
        } catch (exception: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                MyAccessibilityService.runAccessibilityMode(context)
                MyAccessibilityService.instance()?.openQuickSettings()
            }
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
                view.setBackgroundColor(context.resources.getColor(R.color.blackTrans25, context.theme))
            }

            Configuration.UI_MODE_NIGHT_NO -> {
                view.setBackgroundColor(context.resources.getColor(R.color.whiteTrans25, context.theme))
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

    fun isTablet(context: Context): Boolean {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(metrics)
        val widthInches = metrics.widthPixels / metrics.xdpi
        val heightInches = metrics.heightPixels / metrics.ydpi
        val diagonalInches = sqrt(widthInches.toDouble().pow(2.0) + heightInches.toDouble().pow(2.0))
        if (diagonalInches >= 7.0) return true
        return false
    }

    fun wordOfTheDay(resources: Resources): String {
        val dailyWordsArray = resources.getStringArray(R.array.settings_appearance_daily_word_default)
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val wordIndex = (dayOfYear - 1) % dailyWordsArray.size // Subtracting 1 to align with array indexing
        return dailyWordsArray[wordIndex]
    }

    fun shareAppButton(context: Context){
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Application")
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://f-droid.org/packages/" + context.packageName)
        context.startActivity(Intent.createChooser(shareIntent, "Share Application"))
    }

    fun githubButton(context: Context){
        val uri = Uri.parse("https://github.com/DroidWorksStudio/EasyLauncher")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    fun feedbackButton(context: Context){
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:droidworksstuido@063240.xyz")
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Easy Launcher")
        context.startActivity(Intent.createChooser(emailIntent, "Choose Mail Application"))
    }

    fun backupSharedPreferences(context: Context) {
        context.backupSharedPreferences(context.getString(R.string.settings_backups_file))
    }

    fun restoreSharedPreferences(context: Context) {
        context.restoreSharedPreferences(context.getString(R.string.settings_backups_file))
    }

    fun enableAppAsAccessibilityService(context: Context, accessibilityState: Boolean) {
        val state: String = if (accessibilityState) {
            context.getString(R.string.accessibility_settings_disable)
        } else {
            context.getString(R.string.accessibility_settings_enable)
        }

        when (state) {
            "Enable" -> {
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
            else -> {
                return
            }
        }
    }
}