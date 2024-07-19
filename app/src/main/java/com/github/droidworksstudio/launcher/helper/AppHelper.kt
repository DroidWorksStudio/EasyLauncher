package com.github.droidworksstudio.launcher.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.navigation.NavOptions
import com.github.droidworksstudio.common.showLongToast
import com.github.droidworksstudio.launcher.BuildConfig
import com.github.droidworksstudio.launcher.utils.Constants
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.accessibility.ActionService
import com.github.droidworksstudio.launcher.helper.weather.WeatherResponse
import com.github.droidworksstudio.launcher.utils.WeatherApiService
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    fun dayNightMod(context: Context, view: View) {
        when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                view.setBackgroundColor(
                    context.resources.getColor(
                        R.color.blackTrans10,
                        context.theme
                    )
                )
            }

            Configuration.UI_MODE_NIGHT_NO -> {
                view.setBackgroundColor(
                    context.resources.getColor(
                        R.color.whiteTrans10,
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

    fun storeFile(activity: Activity) {
        // Generate a unique filename with a timestamp
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "backup_$timeStamp.json"

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
        longitude: Float
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
            .putLong("cachedDataTimestamp", cachedData.timestamp)
            .putString("weatherResponse", Gson().toJson(cachedData.weatherResponse))
            .apply()
    }

    // Function to retrieve weather data from cache
    private fun Context.getWeatherDataFromCache(): CachedWeatherData? {
        val sharedPreferences = getSharedPreferences(Constants.WEATHER_PREFS, Context.MODE_PRIVATE)
        val timestamp = sharedPreferences.getLong("cachedDataTimestamp", -1)
        val weatherResponseJson = sharedPreferences.getString("weatherResponse", null)
        if (timestamp != -1L && weatherResponseJson != null) {
            val weatherResponse = Gson().fromJson(weatherResponseJson, WeatherResponse::class.java)
            return CachedWeatherData(timestamp, weatherResponse)
        }
        return null
    }

    // Data class to hold cached weather data along with timestamp
    data class CachedWeatherData(val timestamp: Long, val weatherResponse: WeatherResponse)
}