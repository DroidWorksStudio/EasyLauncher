package com.github.droidworksstudio.launcher.helper

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.viewModels
import com.github.droidworksstudio.ktx.backupSharedPreferences
import com.github.droidworksstudio.ktx.restoreSharedPreferences
import com.github.droidworksstudio.ktx.showLongToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.github.droidworksstudio.launcher.Constants
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.accessibility.ActionService
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
import java.util.Calendar
import javax.inject.Inject

class AppHelper @Inject constructor() {

    @SuppressLint("WrongConstant", "PrivateApi")
    fun expandNotificationDrawer(context: Context) {
        try {
            Class.forName(Constants.NOTIFICATION_MANAGER)
                .getMethod(Constants.NOTIFICATION_METHOD)
                .invoke(context.getSystemService(Constants.NOTIFICATION_SERVICE))
        } catch (exception: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ActionService.runAccessibilityMode(context)
                ActionService.instance()?.openNotifications()
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
                ActionService.runAccessibilityMode(context)
                ActionService.instance()?.openQuickSettings()
            }
            exception.printStackTrace()
        }
    }

    fun dayNightMod(context: Context, view: View) {
        when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                view.setBackgroundColor(
                    context.resources.getColor(
                        R.color.blackTrans25,
                        context.theme
                    )
                )
            }

            Configuration.UI_MODE_NIGHT_NO -> {
                view.setBackgroundColor(
                    context.resources.getColor(
                        R.color.whiteTrans25,
                        context.theme
                    )
                )
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
            context.showLongToast("Digital Wellbeing is not available on this device.")
        }
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

    fun wordOfTheDay(resources: Resources): String {
        val dailyWordsArray =
            resources.getStringArray(R.array.settings_appearance_daily_word_default)
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val wordIndex =
            (dayOfYear - 1) % dailyWordsArray.size // Subtracting 1 to align with array indexing
        return dailyWordsArray[wordIndex]
    }

    fun shareAppButton(context: Context) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Application")
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "https://f-droid.org/packages/" + context.packageName
        )
        context.startActivity(Intent.createChooser(shareIntent, "Share Application"))
    }

    fun githubButton(context: Context) {
        val uri = Uri.parse("https://github.com/DroidWorksStudio/EasyLauncher")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    fun feedbackButton(context: Context) {
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