package com.github.droidworksstudio.ktx

import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.AdaptiveIconDrawable
import android.net.Uri
import android.os.Build
import android.os.UserHandle
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.github.droidworksstudio.launcher.Constants
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.ui.activities.FakeHomeActivity
import java.io.File
import java.io.IOException
import java.util.Calendar
import java.util.Date
import kotlin.math.pow
import kotlin.math.sqrt

fun Context.isTabletConfig(): Boolean =
    resources.configuration.smallestScreenWidthDp >= SMALLEST_WIDTH_600

fun Context.isTablet(): Boolean {
    val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val metrics = DisplayMetrics()
    @Suppress("DEPRECATION")
    windowManager.defaultDisplay.getMetrics(metrics)
    val widthInches = metrics.widthPixels / metrics.xdpi
    val heightInches = metrics.heightPixels / metrics.ydpi
    val diagonalInches =
        sqrt(widthInches.toDouble().pow(2.0) + heightInches.toDouble().pow(2.0))
    if (diagonalInches >= 7.0) return true
    return false
}

fun Context.isPortraitSw600Config(): Boolean =
    resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT &&
            resources.configuration.smallestScreenWidthDp >= SMALLEST_WIDTH_600

fun Context.isLandscapeSw600Config(): Boolean =
    resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
            resources.configuration.smallestScreenWidthDp >= SMALLEST_WIDTH_600

fun Context.isLandscapeDisplayOrientation(): Boolean =
    resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

internal fun Context.addLifecycleObserver(observer: LifecycleObserver) {
    when (this) {
        is LifecycleOwner -> this.lifecycle.addObserver(observer)
        is ContextThemeWrapper -> this.baseContext.addLifecycleObserver(observer)
        is androidx.appcompat.view.ContextThemeWrapper -> this.baseContext.addLifecycleObserver(
            observer
        )
    }
}

fun Context.getMiddleScreenX(): Int {
    val screenEndX = this.resources.displayMetrics.widthPixels
    return (screenEndX / 2)
}

fun Context.getMiddleScreenY(): Int {
    val screenEndY = this.resources.displayMetrics.heightPixels
    return (screenEndY / 2)
}

const val SMALLEST_WIDTH_600: Int = 600
fun Context.createIconWithResourceCompat(
    @DrawableRes vectorIconId: Int,
    @DrawableRes adaptiveIconForegroundId: Int,
    @DrawableRes adaptiveIconBackgroundId: Int
): IconCompat {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val adaptiveIconDrawable = AdaptiveIconDrawable(
            ContextCompat.getDrawable(this, adaptiveIconBackgroundId),
            ContextCompat.getDrawable(this, adaptiveIconForegroundId)
        )

        IconCompat.createWithAdaptiveBitmap(
            adaptiveIconDrawable.toBitmap(
                config = Bitmap.Config.ARGB_8888
            )
        )
    } else {
        IconCompat.createWithResource(this, vectorIconId)
    }
}

fun Context.currentLanguage() = ConfigurationCompat.getLocales(resources.configuration)[0]?.language

fun Context.openBrowser(url: String, clearFromRecent: Boolean = true) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    browserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    if (clearFromRecent) browserIntent.flags =
        browserIntent.flags or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
    startActivity(browserIntent)
}

fun Context.inflate(resource: Int, root: ViewGroup? = null, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(this).inflate(resource, root, attachToRoot)
}

fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

fun Context.getDrawableCompat(@DrawableRes drawable: Int) =
    ContextCompat.getDrawable(this, drawable)

fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showShortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.openSearch(query: String? = null) {
    val intent = Intent(Intent.ACTION_WEB_SEARCH)
    intent.putExtra(SearchManager.QUERY, query ?: "")
    startActivity(intent)
}

fun Context.openUrl(url: String) {
    if (url.isEmpty()) return
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    startActivity(intent)
}

fun Context.resetDefaultLauncher() {
    val manufacturer = Build.MANUFACTURER.lowercase()
    when (manufacturer) {
        "google", "essential" -> runningStockAndroid()
        else -> notRunningStockAndroid()
    }
}

private fun Context.runningStockAndroid() {
    try {
        val packageManager = this.packageManager
        val componentName = ComponentName(this, FakeHomeActivity::class.java)

        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        val selector = Intent(Intent.ACTION_MAIN)
        selector.addCategory(Intent.CATEGORY_HOME)
        this.startActivity(selector)

        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun Context.notRunningStockAndroid() {
    try {
        val intent = Intent("android.settings.HOME_SETTINGS")
        this.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // Fallback to general settings if specific launcher settings are not found
        try {
            val intent = Intent(Settings.ACTION_SETTINGS)
            this.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun Context.searchView() {
    val intent = Intent(Intent.ACTION_WEB_SEARCH)
    intent.putExtra(SearchManager.QUERY, "")
    this.startActivity(intent)
}

fun Context.unInstallApp(appInfo: AppInfo) {
    val intent = Intent(Intent.ACTION_DELETE)
    intent.data = Uri.parse("package:${appInfo.packageName}")
    this.startActivity(intent)
}

fun Context.appInfo(appInfo: AppInfo) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.fromParts("package", appInfo.packageName, null)
    this.startActivity(intent)
}

fun Context.launchApp(appInfo: AppInfo) {
    val intent = this.packageManager.getLaunchIntentForPackage(appInfo.packageName)
    if (intent != null) {
        this.startActivity(intent)
    } else {
        showLongToast("Failed to open the application")
    }
}

fun Context.launchClock() {
    try {
        val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
        this.startActivity(intent)
    } catch (e: Exception) {
        Log.e("launchClock", "Error launching clock app: ${e.message}")
    }
}

fun Context.launchCalendar() {
    try {
        val cal: Calendar = Calendar.getInstance()
        cal.time = Date()
        val time = cal.time.time
        val builder: Uri.Builder = CalendarContract.CONTENT_URI.buildUpon()
        builder.appendPath("time")
        builder.appendPath(time.toString())
        this.startActivity(Intent(Intent.ACTION_VIEW, builder.build()))
    } catch (e: Exception) {
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_APP_CALENDAR)
            this.startActivity(intent)
        } catch (e: Exception) {
            Log.d("openCalendar", e.toString())
        }
    }
}

fun Context.openBatteryManager() {
    try {
        val intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // Battery manager settings cannot be opened
        // Handle this case as needed
        showLongToast("Battery manager settings are not available on this device.")
    }
}

fun Context.searchOnPlayStore(query: String? = null): Boolean {
    return try {
        val playStoreIntent = Intent(Intent.ACTION_VIEW)
        playStoreIntent.data = Uri.parse("${Constants.APP_GOOGLE_PLAY_STORE}=$query")

        // Check if the Play Store app is installed
        if (playStoreIntent.resolveActivity(packageManager) != null) {
            startActivity(playStoreIntent)
        } else {
            // If Play Store app is not installed, open Play Store website in browser
            playStoreIntent.data = Uri.parse("${Constants.URL_GOOGLE_PLAY_STORE}=$query")
            startActivity(playStoreIntent)
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun Context.searchCustomSearchEngine(searchQuery: String? = null): Boolean {
    val searchUrl = Constants.URL_GOOGLE_SEARCH
    val encodedQuery = Uri.encode(searchQuery)
    val fullUrl = "$searchUrl$encodedQuery"
    Log.d("fullUrl", fullUrl)
    openUrl(fullUrl)
    return true
}

fun Context.backupSharedPreferences(backupFileName: String) {
    val sharedPreferences: SharedPreferences =
        this.getSharedPreferences(Constants.PREFS_FILENAME, 0)
    val allPrefs = sharedPreferences.all
    val backupFile = File(filesDir, backupFileName)

    println("Backup SharedPreferences to: ${backupFile.absolutePath}")

    try {
        backupFile.bufferedWriter().use { writer ->
            for ((key, value) in allPrefs) {
                if (value != null) {
                    val line = when (value) {
                        is Boolean -> "$key=${value}\n"
                        is Int -> "$key=${value}\n"
                        is Float -> "$key=${value}\n"
                        is Long -> "$key=${value}\n"
                        is String -> "$key=${value}\n"
                        is Set<*> -> "$key=${value.joinToString(",")}\n"
                        else -> null
                    }
                    if (line != null) {
                        writer.write(line)
                        println("Writing: $line")
                    } else {
                        println("Skipping unsupported type for key: $key")
                    }
                } else {
                    println("Null value for key: $key")
                }
            }
        }
        println("Backup completed successfully.")
    } catch (e: IOException) {
        e.printStackTrace()
        println("Failed to backup SharedPreferences: ${e.message}")
    }
}

fun Context.restoreSharedPreferences(backupFileName: String) {
    val sharedPreferences: SharedPreferences =
        this.getSharedPreferences(Constants.PREFS_FILENAME, 0)
    val editor = sharedPreferences.edit()
    val backupFile = File(filesDir, backupFileName)

    println("Restoring SharedPreferences from: ${backupFile.absolutePath}")

    if (backupFile.exists()) {
        try {
            backupFile.forEachLine { line ->
                val (key, value) = line.split("=", limit = 2)
                when {
                    value.toBooleanStrictOrNull() != null -> editor.putBoolean(
                        key,
                        value.toBoolean()
                    )

                    value.toIntOrNull() != null -> editor.putInt(key, value.toInt())
                    value.toFloatOrNull() != null -> editor.putFloat(key, value.toFloat())
                    value.toLongOrNull() != null -> editor.putLong(key, value.toLong())
                    value.contains(",") -> editor.putStringSet(key, value.split(",").toSet())
                    else -> editor.putString(key, value)
                }
                println("Restoring: $key=$value")
            }
            editor.apply()
            println("Restore completed successfully.")
        } catch (e: IOException) {
            e.printStackTrace()
            println("Failed to restore SharedPreferences: ${e.message}")
        }
    } else {
        println("Backup file does not exist.")
    }
}

fun Context.isPackageInstalled(
    packageName: String,
    userHandle: UserHandle = android.os.Process.myUserHandle()
): Boolean {
    val launcher = getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    val activityInfo = launcher.getActivityList(packageName, userHandle)
    return activityInfo.size > 0
}