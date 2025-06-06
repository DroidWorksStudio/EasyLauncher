package com.github.droidworksstudio.common

import android.Manifest
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.UserHandle
import android.os.UserManager
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
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.ui.activities.LauncherActivity
import com.github.droidworksstudio.launcher.utils.Constants
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
    @DrawableRes adaptiveIconBackgroundId: Int,
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

fun Context.openBrowser(url: String, clearFromRecent: Boolean = true) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
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
    val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
        putExtra(SearchManager.QUERY, query ?: "")
    }
    // Check if there is an app that can handle the web search intent
    val resolvedActivity = intent.resolveActivity(packageManager)

    if (resolvedActivity != null) {
        try {
            // Get the package name of the app that can handle the intent
            val packageName = resolvedActivity.packageName
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val appName = packageManager.getApplicationLabel(appInfo).toString()

            // Log the name of the app that will handle the search
            Log.d("WebSearchApp", "Search will be handled by: $appName ($packageName)")

            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    } else {
        val fallbackUrl = "${Constants.URL_GOOGLE_SEARCH}${Uri.encode(query ?: "")}"
        val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl))
        try {
            startActivity(fallbackIntent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }
}


fun Context.openUrl(url: String) {
    if (url.isEmpty()) return
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    startActivity(intent)
}

fun Context.resetDefaultLauncher() {
    try {
        val intent = Intent("android.settings.HOME_SETTINGS")
        this.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        // Fallback to general settings if specific launcher settings are not found
        try {
            val intent = Intent(Settings.ACTION_SETTINGS)
            this.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun Context.unInstallApp(appInfo: AppInfo) {
    val intent = Intent(Intent.ACTION_DELETE)
    intent.data = Uri.parse("package:${appInfo.packageName}")
    if (appInfo.userHandle > 0) this.showShortToast(getString(R.string.work_permission_message))
    else this.startActivity(intent)
}

fun Context.appInfo(appInfo: AppInfo) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.fromParts("package", appInfo.packageName, null)
//    this.test()
    if (appInfo.userHandle > 0) this.showShortToast(getString(R.string.work_permission_message))
    else this.startActivity(intent)
}

//fun Context.test() {
//    // Get the UserManager system service
//    val userManager = getSystemService(Context.USER_SERVICE) as? UserManager
//    val packageManager = packageManager
//
//    // Check if the UserManager is available
//    if (userManager == null) {
//        Log.e("Error", "UserManager is not available on this device")
//        return
//    }
//
//    // Log available user profiles
//    val users = userManager.userProfiles
//    Log.d("UserManager", "User profiles: $users")
//    // Iterate through users to check for work profiles
//    for (userHandle in users) {
//        try {
//            // Get the UserInfo object for each user
//            val userInfo = userManager.getUserInfo(userHandle)
//            Log.d("UserManager", "User info for handle $userHandle: ${userInfo.name}")
//
//            // Check if this user is a managed (work) profile
//            if (userInfo.isManagedProfile) {
//                Log.d("WorkProfile", "Work profile found for user: ${userInfo.name}")
//
//                // Get installed apps for this work profile
//                val appsInWorkProfile = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
//
//                // Log installed apps in the work profile
//                for (appInfo in appsInWorkProfile) {
//                    appInfo.applicationInfo?.let {
//                        if (it.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
//                            Log.d("App", "Possible app managing work profile: ${appInfo.packageName}")
//                        }
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("Error", "Failed to get user info for handle $userHandle: ${e.message}")
//        }
//    }
//}

fun Context.launchApp(appInfo: AppInfo) {
    val packageName = appInfo.packageName
    val primaryUserHandle = android.os.Process.myUserHandle()
    val userHandle = getUserHandleFromId(appInfo.userHandle) ?: primaryUserHandle  // Fallback to current user if not provided

    // Get the LauncherApps service
    val launcherApps = getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

    // Attempt to get the launch intent for the package
    val activityList = launcherApps.getActivityList(packageName, userHandle)

    Log.d("launchApp", "launchApp: $packageName - $activityList")
    if (activityList.isNotEmpty()) {
        val componentName = ComponentName(packageName, activityList[0].name)
        try {
            // Start the app's main activity
            launcherApps.startMainActivity(componentName, userHandle, null, null)
        } catch (e: SecurityException) {
            Log.e("launchApp", "SecurityException: ${e.message}")
            showLongToast("Unable to launch app due to security restrictions")
        } catch (e: Exception) {
            Log.e("launchApp", "Exception: ${e.message}")
            showLongToast("Unable to launch app")
        }
    } else {
        showLongToast("Failed to find the application activity")
    }
}

fun Context.getUserHandleFromId(userId: Int): UserHandle? {
    val userManager = getSystemService(Context.USER_SERVICE) as UserManager
    // Get all available UserHandles
    val userProfiles = userManager.userProfiles
    // Iterate over user profiles
    for (userProfile in userProfiles) {
        // Check if the UserHandle matches the provided user ID
        if (userProfile.hashCode() == userId) {
            return userProfile
        }
    }
    return null
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
            // Fallback: Open the Calendar app using category
            val fallbackIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_CALENDAR)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            this.startActivity(fallbackIntent)
        } catch (e: Exception) {
            // Log any error if both methods fail
            Log.e("openCalendar", "Failed to open Calendar: ${e.message}")
        }
}

fun Context.openBatteryManager() {
    try {
        val intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY)
        this.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
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

fun Context.searchCustomSearchEngine(
    preferenceHelper: PreferenceHelper,
    searchQuery: String? = null,
): Boolean {

    val searchUrl = when (preferenceHelper.searchEngines) {
        Constants.SearchEngines.Google -> {
            Constants.URL_GOOGLE_SEARCH
        }

        Constants.SearchEngines.Yahoo -> {
            Constants.URL_YAHOO_SEARCH
        }

        Constants.SearchEngines.DuckDuckGo -> {
            Constants.URL_DUCK_SEARCH
        }

        Constants.SearchEngines.Bing -> {
            Constants.URL_BING_SEARCH
        }

        Constants.SearchEngines.Brave -> {
            Constants.URL_BRAVE_SEARCH
        }

        Constants.SearchEngines.SwissCow -> {
            Constants.URL_SWISSCOW_SEARCH
        }

        else -> {
            openSearch(searchQuery)
            null // Returning null because we're launching the intent directly
        }
    }
    if (searchUrl != null) {
        val encodedQuery = Uri.encode(searchQuery)
        val fullUrl = "$searchUrl$encodedQuery"
        Log.d("fullUrl", fullUrl)
        openUrl(fullUrl)
        return true
    }
    return false
}

fun Context.isWorkProfileEnabled(): Boolean {
    val userManager = getSystemService(Context.USER_SERVICE) as? UserManager
    return if (userManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val profiles = userManager.userProfiles
        profiles.size > 1
    } else {
        false
    }
}

fun Context.getAllProfileAppIcons(): Map<Pair<Int?, String?>, Drawable?> {
    val launcherApps = this.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    val userManager = this.getSystemService(Context.USER_SERVICE) as UserManager
    val userHandles = userManager.userProfiles
    val appInfoMap = mutableMapOf<Pair<Int?, String?>, Drawable?>()

    try {
        for (userHandle in userHandles) {
            val apps = launcherApps.getActivityList(null, userHandle)
            apps.forEach { activityInfo ->
                val packageName = activityInfo.applicationInfo.packageName // App's package name
                val icon = activityInfo.getBadgedIcon(0) // Drawable for the app's icon
                val userHandle = activityInfo.user // UserHandle for the profile the app belongs to
                val userId = userHandle.hashCode() // Get the unique integer ID of the UserHandle

                // Construct the key as Pair(UserHandle, String)
                val key = Pair(userId, packageName)

                // Store the pair of UserHandle and label as the key, with the icon as the value
                appInfoMap[key] = icon
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return appInfoMap
}


fun Context.hasInternetPermission(): Boolean {
    val permission = Manifest.permission.INTERNET
    val result = ContextCompat.checkSelfPermission(this, permission)
    return result == PackageManager.PERMISSION_GRANTED
}

fun Context.isPackageInstalled(
    packageName: String,
    userHandle: UserHandle = android.os.Process.myUserHandle(),
): Boolean {
    val launcher = getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    val activityInfo = launcher.getActivityList(packageName, userHandle)
    return activityInfo.isNotEmpty()
}

fun Context.getAppNameFromPackageName(packageName: String): String? {
    val packageManager = this.packageManager
    return try {
        val appInfo = packageManager.getApplicationInfo(packageName, 0)
        packageManager.getApplicationLabel(appInfo) as String
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        null
    }
}