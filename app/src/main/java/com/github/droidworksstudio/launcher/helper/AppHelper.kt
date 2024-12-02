package com.github.droidworksstudio.launcher.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.JsonReader
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.navigation.NavOptions
import com.github.droidworksstudio.common.showLongToast
import com.github.droidworksstudio.launcher.BuildConfig
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.accessibility.ActionService
import com.github.droidworksstudio.launcher.data.dao.AppInfoDAO
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.helper.weather.WeatherResponse
import com.github.droidworksstudio.launcher.utils.Constants
import com.github.droidworksstudio.launcher.utils.WeatherApiService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.StringReader
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
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
    fun expandQuickSettings(context: Context) {
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

    @RequiresApi(Build.VERSION_CODES.Q)
    fun triggerHapticFeedback(context: Context?, effectType: String) {
        val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Use VibratorManager for API 31 and above
            val vibratorManager = context?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            // Use Vibrator directly for older versions
            @Suppress("DEPRECATION")
            context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Define vibration effects based on the effectType
                val vibrationEffect = when (effectType.lowercase()) {
                    "on" -> VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE) // 100ms vibration
                    "off" -> VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE) // 200ms vibration
                    "save" -> VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 100), -1) // Two quick vibrations
                    "select" -> VibrationEffect.createWaveform(longArrayOf(0, 100, 100, 300, 200, 100), -1) // Patterned vibration
                    "click" -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK) // Predefined click effect
                    else -> VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE) // Default effect
                }
                vibrator.vibrate(vibrationEffect)
            } else {
                @Suppress("DEPRECATION")
                // For older APIs, approximate effects
                when (effectType.lowercase()) {
                    "on" -> vibrator.vibrate(100)
                    "off" -> vibrator.vibrate(200)
                    "save" -> vibrator.vibrate(longArrayOf(0, 100, 50, 100), -1)
                    "select" -> vibrator.vibrate(longArrayOf(0, 100, 100, 300, 200, 100), -1)
                    "click" -> vibrator.vibrate(50) // Approximation for click
                    else -> vibrator.vibrate(50) // Default effect
                }
            }
        } else {
            // Handle cases where the device does not support vibration
            Log.w("HapticFeedback", "Device does not support vibration")
        }
    }

    fun dayNightMod(context: Context, view: View) {
        when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                view.setBackgroundColor(
                    context.resources.getColor(
                        R.color.blackTrans80,
                        context.theme
                    )
                )
            }

            Configuration.UI_MODE_NIGHT_NO -> {
                view.setBackgroundColor(
                    context.resources.getColor(
                        R.color.whiteTrans80,
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
            view.visibility = if (isVisible) View.VISIBLE else View.GONE
            view.isClickable = if (isVisible) view.isClickable else !view.isClickable
        }
    }

    fun openDigitalWellbeing(context: Context) {
        try {
            val packageName = "com.google.android.apps.wellbeing"
            val className = "com.google.android.apps.wellbeing.settings.TopLevelSettingsActivity"

            val intent = Intent()
            intent.component = ComponentName(packageName, className)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Digital Wellbeing app is not installed or cannot be opened
            // Handle this case as needed
            context.showLongToast("Digital Wellbeing is not available on this device.")
            Log.e("AppHelper", "Digital Wellbeing app not found or cannot be opened.", e)
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

    fun getGravityFromSelectedItem(selectedItem: Int): Int {
        return when (selectedItem) {
            0 -> Gravity.START
            1 -> Gravity.CENTER
            2 -> Gravity.END
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

    fun showNavigationBar(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 (API 30) and above, use WindowInsetsController to show the navigation bar
            window.insetsController?.show(WindowInsets.Type.navigationBars())
        } else {
            @Suppress("DEPRECATION", "InlinedApi")
            // For older versions, show the navigation bar using systemUiVisibility
            window.decorView.apply {
                systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            }
        }
    }

    fun hideNavigationBar(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 (API 30) and above, use WindowInsetsController to hide the navigation bar
            window.insetsController?.hide(WindowInsets.Type.navigationBars())
        } else {
            @Suppress("DEPRECATION")
            // For older versions, hide the navigation bar using systemUiVisibility
            window.decorView.apply {
                systemUiVisibility =
                    View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
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

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getNextAlarm(context: Context, preferenceHelper: PreferenceHelper): CharSequence {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val nextAlarmClock = alarmManager.nextAlarmClock

        if (nextAlarmClock == null) return "No alarm is set."

        val alarmTime = nextAlarmClock.triggerTime
        val formattedTime = SimpleDateFormat("EEE, MMM d hh:mm a", Locale.getDefault()).format(alarmTime)

        val drawable = AppCompatResources.getDrawable(context, R.drawable.ic_alarm_clock)
        val fontSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            (preferenceHelper.alarmClockTextSize / 1.5).toFloat(),
            context.resources.displayMetrics
        ).toInt()

        drawable?.setBounds(0, 0, fontSize, fontSize)

        return SpannableStringBuilder(" ").apply {
            drawable?.let {
                setSpan(
                    ImageSpan(it, ImageSpan.ALIGN_CENTER),
                    0, 1,
                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            append(" $formattedTime")
        }
    }


    fun shareApplicationButton(context: Context) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        val description = context.getString(R.string.advanced_settings_share_application_description, context.getString(R.string.app_name))
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Application")
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "$description https://f-droid.org/packages/${context.packageName}"
        )
        context.startActivity(Intent.createChooser(shareIntent, "Share Application"))
    }

    fun helpFeedbackButton(context: Context) {
        val uri = Uri.parse("https://github.com/DroidWorksStudio/EasyLauncher")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    fun communitySupportButton(context: Context) {
        val uri = Uri.parse("https://t.me/DroidWorksStudio/")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    fun emailButton(context: Context) {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:droidworksstuido@063240.xyz")
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
        context.startActivity(Intent.createChooser(emailIntent, "Choose Mail Application"))
    }

    fun storeFile(activity: Activity) {
        // Generate a unique filename with a timestamp
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Settings_backup_$timeStamp.json"

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        activity.startActivityForResult(intent, Constants.BACKUP_WRITE, null)
    }

    fun loadFile(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
        activity.startActivityForResult(intent, Constants.BACKUP_READ, null)
    }

    fun storeFileApps(activity: Activity) {
        // Generate a unique filename with a timestamp
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Apps_backup_$timeStamp.json"

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        activity.startActivityForResult(intent, Constants.BACKUP_WRITE_APPS, null)
    }

    fun loadFileApps(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
        activity.startActivityForResult(intent, Constants.BACKUP_READ_APPS, null)
    }

    suspend fun backupAppInfo(context: Context, dao: AppInfoDAO, uri: Uri) {
        try {
            dao.getAllAppsFlow()
                .first() // Get the first emission from the Flow
                .let { allApps ->
                    val gson = Gson()
                    val jsonString = gson.toJson(allApps)

                    // Write JSON to the selected URI
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(jsonString.toByteArray())
                    } ?: throw Exception("Failed to open output stream")
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun restoreAppInfo(context: Context, dao: AppInfoDAO, uri: Uri) {
        try {
            // Open an InputStream from the selected Uri
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                // Read the content from the InputStream
                val jsonString = inputStream.bufferedReader().use { it.readText() }

                // Create a JsonReader with lenient parsing
                val jsonReader = JsonReader(StringReader(jsonString))
                jsonReader.isLenient = true // Enable lenient mode

                // Convert JSON to List<AppInfo>
                val gson = Gson()
                val type = object : TypeToken<List<AppInfo>>() {}.type
                val appInfoList: List<AppInfo> = gson.fromJson(jsonString, type)

                // Clear all apps first
                resetDatabase(dao)
                // Reinsert data into the database
                dao.restoreAll(appInfoList)
            } ?: throw Exception("Failed to open input stream from URI")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun resetDatabase(dao: AppInfoDAO) {
        dao.clearAll()                // Clears all rows in the table
        dao.resetAutoIncrement()      // Resets the ID counter
    }


    fun getActionType(actionType: Constants.Swipe): NavOptions {
        return when (actionType) {
            Constants.Swipe.DoubleTap -> {
                NavOptions.Builder()
                    .setEnterAnim(R.anim.zoom_in)
                    .setExitAnim(R.anim.zoom_out)
                    .setPopEnterAnim(R.anim.zoom_in)
                    .setPopExitAnim(R.anim.zoom_out)
                    .build()
            }

            Constants.Swipe.Up -> {
                NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_top)
                    .setExitAnim(R.anim.slide_out_top)
                    .setPopEnterAnim(R.anim.slide_in_bottom)
                    .setPopExitAnim(R.anim.slide_out_bottom)
                    .build()
            }

            Constants.Swipe.Down -> {
                NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_bottom)
                    .setExitAnim(R.anim.slide_out_bottom)
                    .setPopEnterAnim(R.anim.slide_in_top)
                    .setPopExitAnim(R.anim.slide_out_top)
                    .build()
            }

            Constants.Swipe.Left -> {
                NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_right)
                    .setExitAnim(R.anim.slide_out_right)
                    .setPopEnterAnim(R.anim.slide_in_left)
                    .setPopExitAnim(R.anim.slide_out_left)
                    .build()
            }

            Constants.Swipe.Right -> {
                NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_left)
                    .setExitAnim(R.anim.slide_out_left)
                    .setPopEnterAnim(R.anim.slide_in_right)
                    .setPopExitAnim(R.anim.slide_out_right)
                    .build()
            }
        }
    }

    sealed class WeatherResult {
        data class Success(val weatherResponse: WeatherResponse) : WeatherResult()
        data class Failure(val errorMessage: String) : WeatherResult()
    }

    fun fetchWeatherData(
        context: Context,
        latitude: Float,
        longitude: Float,
    ): WeatherResult {
        // Check if cached data is available and not expired
        val cachedWeatherData = context.getWeatherDataFromCache()
        if (cachedWeatherData?.let { System.currentTimeMillis() - it.timestamp < TimeUnit.MINUTES.toMillis(15) } == true) {
            return WeatherResult.Success(cachedWeatherData.weatherResponse)
        }

        // Fetch weather data from the network
        val apiKey = BuildConfig.API_KEY
        val baseURL = "api.openweathermap.org"
        val units = "metric"

        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://$baseURL/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(WeatherApiService::class.java)

            val response = service.getWeather("$latitude", "$longitude", units, apiKey).execute()
            if (response.isSuccessful) {
                val weatherResponse = response.body()
                if (weatherResponse != null) {
                    // Cache the fetched weather data
                    context.cacheWeatherData(weatherResponse)
                    return WeatherResult.Success(weatherResponse)
                } else {
                    return WeatherResult.Failure("Weather response body is null")
                }
            } else {
                return WeatherResult.Failure("Failed to fetch weather data: ${response.errorBody()}")
            }
        } catch (e: UnknownHostException) {
            Log.e("AppHelper", "Unknown Host.", e)
            return WeatherResult.Failure("Unknown Host : $baseURL")
        } catch (e: Exception) {
            return WeatherResult.Failure("${e.message}")
        }
    }

    // Function to cache weather data
    private fun Context.cacheWeatherData(weatherResponse: WeatherResponse) {
        val timestamp = System.currentTimeMillis()
        val cachedData = CachedWeatherData(timestamp, weatherResponse)
        // Save cached data to SharedPreferences
        val sharedPreferences = getSharedPreferences(Constants.WEATHER_PREFS, Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putLong(Constants.CACHED_DATA_TIMESTAMP, cachedData.timestamp)
            .putString(Constants.WEATHER_RESPONSE, Gson().toJson(cachedData.weatherResponse))
            .apply()
    }

    // Function to retrieve weather data from cache
    private fun Context.getWeatherDataFromCache(): CachedWeatherData? {
        val sharedPreferences = getSharedPreferences(Constants.TIMERS_PREFS, Context.MODE_PRIVATE)
        val timestamp = sharedPreferences.getLong(Constants.CACHED_DATA_TIMESTAMP, -1)
        val weatherResponseJson = sharedPreferences.getString(Constants.WEATHER_RESPONSE, null)
        if (timestamp != -1L && weatherResponseJson != null) {
            val weatherResponse = Gson().fromJson(weatherResponseJson, WeatherResponse::class.java)
            return CachedWeatherData(timestamp, weatherResponse)
        }
        return null
    }

    // Data class to hold cached weather data along with timestamp
    data class CachedWeatherData(val timestamp: Long, val weatherResponse: WeatherResponse)
}
