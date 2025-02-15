package com.github.droidworksstudio.launcher.ui.widgets

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.github.droidworksstudio.common.capitalizeEachWord
import com.github.droidworksstudio.common.hasInternetPermission
import com.github.droidworksstudio.common.hideKeyboard
import com.github.droidworksstudio.common.showLongToast
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.databinding.FragmentWidgetsBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnSwipeTouchListener
import com.github.droidworksstudio.launcher.listener.ScrollEventListener
import com.github.droidworksstudio.launcher.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class WidgetFragment : Fragment(),
    ScrollEventListener {
    private var _binding: FragmentWidgetsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appHelper: AppHelper

    private lateinit var navController: NavController

    private lateinit var context: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentWidgetsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = findNavController()
        // Set according to the system theme mode
        appHelper.dayNightMod(requireContext(), binding.mainLayout)
        super.onViewCreated(view, savedInstanceState)

        initializeInjectedDependencies()
        orderWidgetsBySettings()
        setupWeatherWidget()
        setupBatteryWidget()
        observeClickListener()
        observeSwipeTouchListener()
    }

    private fun initializeInjectedDependencies() {
        context = requireContext()
        binding.mainLayout.hideKeyboard()
    }

    private fun orderWidgetsBySettings() {
        val linearLayout = binding.linearLayoutContainer

        // Find RelativeLayouts by their IDs
        val weatherRoot = binding.weatherRoot
        val batteryRoot = binding.batteryRoot

        // Order the list of layouts
        val orderList = listOf(
            Pair(weatherRoot, preferenceHelper.weatherOrderNumber),
            Pair(batteryRoot, preferenceHelper.batteryOrderNumber)
        )

        // Sort the list based on the second value of the pairs (the order number)
        val sortedOrderList = orderList.sortedBy { it.second }

        // Remove all views from the LinearLayout
        linearLayout.removeAllViews()

        // Add the RelativeLayouts back in the sorted order
        for ((relativeLayout, _) in sortedOrderList) {
            linearLayout.addView(relativeLayout)
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun setupWeatherWidget() {
        val sharedPreferences =
            context.getSharedPreferences(Constants.WEATHER_PREFS, Context.MODE_PRIVATE)
        val latitude = sharedPreferences.getFloat(Constants.LATITUDE, 0f)
        val longitude = sharedPreferences.getFloat(Constants.LONGITUDE, 0f)

        // Pre-fetch preferences
        val showWeatherWidget = preferenceHelper.showWeatherWidget
        val temperatureScale = if (preferenceHelper.weatherUnits == Constants.Units.Metric) getString(R.string.widget_c) else getString(
            R.string.widget_f
        )
        val speedScale = if (preferenceHelper.weatherUnits == Constants.Units.Metric) getString(R.string.widget_weather_mps) else getString(R.string.widget_weather_mph)
        val widgetTextColor = preferenceHelper.widgetTextColor
        val widgetBackgroundColor = preferenceHelper.widgetBackgroundColor

        lifecycleScope.launch {
            if (!showWeatherWidget || !context.hasInternetPermission()) return@launch

            try {
                val weatherDeferred = GlobalScope.async {
                    appHelper.fetchWeatherData(context, latitude, longitude)
                }

                // Prepare UI elements concurrently
                withContext(Dispatchers.Main) {
                    binding.apply {
                        val weatherWidgetDrawable = weatherRoot.background
                        if (weatherWidgetDrawable is GradientDrawable) {
                            weatherWidgetDrawable.setColor(widgetBackgroundColor)
                        }

                        if (preferenceHelper.showWeatherWidgetSunSetRise) {
                            binding.weatherSunsetSunrise.visibility = View.VISIBLE
                        }

                        weatherCity.setTextColor(widgetTextColor)
                        weatherTemperature.setTextColor(widgetTextColor)
                        weatherDescription.setTextColor(widgetTextColor)
                        weatherWind.setTextColor(widgetTextColor)
                        weatherHumidity.setTextColor(widgetTextColor)
                        weatherRefresh.setTextColor(widgetTextColor)
                        weatherLastRun.setTextColor(widgetTextColor)

                        sunsetText.setTextColor(widgetTextColor)
                        sunriseText.setTextColor(widgetTextColor)
                        weatherRefresh.typeface = ResourcesCompat.getFont(requireActivity(), R.font.weather)
                    }
                }

                val result = weatherDeferred.await()
                Log.d("weatherResponse", "$result")

                when (result) {
                    is AppHelper.WeatherResult.Success -> {
                        val weatherResponse = result.weatherResponse

                        withContext(Dispatchers.Main) {
                            val timestamp = convertTimestampToReadableDate(weatherResponse.dt)
                            binding.apply {
                                weatherCity.text = getString(R.string.widget_weather_location, weatherResponse.name, weatherResponse.sys.country)
                                weatherTemperature.text = getString(R.string.widget_weather_temp, weatherResponse.main.temp, temperatureScale)
                                weatherDescription.text = getString(R.string.widget_weather_description, weatherResponse.weather[0].description).capitalizeEachWord()
                                weatherWind.text = getString(R.string.widget_weather_wind, weatherResponse.wind.speed, speedScale)
                                weatherHumidity.text = getString(R.string.widget_weather_humidity, weatherResponse.main.humidity)
                                weatherLastRun.text = timestamp
                                weatherRefresh.text = getString(R.string.widget_weather_refresh, getString(R.string.refresh_icon))

                                val weatherIconBitmap = createWeatherIcon(context, setWeatherIcon(context, weatherResponse.weather[0].id))
                                weatherIcon.setImageBitmap(weatherIconBitmap) // Ensure this matches your ImageView ID
                                weatherIcon.setColorFilter(widgetTextColor)

                                val sunriseIconBitmap = createSunIcon(context, getString(R.string.sunrise_icon))
                                sunriseIcon.setImageBitmap(sunriseIconBitmap)
                                sunriseIcon.setColorFilter(widgetTextColor)

                                val sunsetIconBitmap = createSunIcon(context, getString(R.string.sunset_icon))
                                sunsetIcon.setImageBitmap(sunsetIconBitmap)
                                sunsetIcon.setColorFilter(widgetTextColor)

                                val sunriseTime = convertTimestampToReadableDate(weatherResponse.sys.sunrise)
                                sunriseText.text = getString(R.string.widget_sunrise_time, sunriseTime)

                                val sunsetTime = convertTimestampToReadableDate(weatherResponse.sys.sunset)
                                sunsetText.text = getString(R.string.widget_sunset_time, sunsetTime)

                                weatherRoot.visibility = View.VISIBLE
                            }
                        }
                    }

                    is AppHelper.WeatherResult.Failure -> {
                        val errorMessage = result.errorMessage
                        context.showLongToast(errorMessage)
                    }
                }
            } catch (e: Exception) {
                Log.e("Weather", "Failed to fetch weather data: ${e.message}")
            }
        }
    }


    private fun createWeatherIcon(context: Context, text: String): Bitmap {
        val bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        val weatherFont = ResourcesCompat.getFont(requireActivity(), R.font.weather)
        paint.isAntiAlias = true
        paint.isSubpixelText = true
        paint.typeface = weatherFont
        paint.style = Paint.Style.FILL
        paint.color = ContextCompat.getColor(context, R.color.white)
        paint.textSize = 170f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(text, 128f, 200f, paint)
        return bitmap
    }

    private fun createSunIcon(context: Context, text: String): Bitmap {
        val bitmap = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        val weatherFont = ResourcesCompat.getFont(requireActivity(), R.font.weather)
        paint.isAntiAlias = true
        paint.isSubpixelText = true
        paint.typeface = weatherFont
        paint.style = Paint.Style.FILL
        paint.color = ContextCompat.getColor(context, R.color.white)
        paint.textSize = 96f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(text, 64f, 96f, paint)
        return bitmap
    }

    private fun setWeatherIcon(context: Context, id: Int): String {
        val icon: String
        val idDivided = id / 100
        val hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val isDayTime = hourOfDay in 7..19

        icon = when {
            id == 800 -> if (isDayTime) context.getString(R.string.wi_day_sunny) else context.getString(R.string.wi_night_clear)
            idDivided == 2 -> if (isDayTime) context.getString(R.string.wi_day_thunderstorm) else context.getString(R.string.wi_night_alt_thunderstorm)
            idDivided == 3 -> if (isDayTime) context.getString(R.string.wi_day_sprinkle) else context.getString(R.string.wi_night_alt_sprinkle)
            idDivided == 5 -> when (id) {
                500 -> if (isDayTime) context.getString(R.string.wi_day_showers) else context.getString(R.string.wi_night_showers)
                501 -> if (isDayTime) context.getString(R.string.wi_day_rain) else context.getString(R.string.wi_night_rain)
                502 -> if (isDayTime) context.getString(R.string.wi_day_rain_wind) else context.getString(R.string.wi_night_rain_wind)
                503 -> if (isDayTime) context.getString(R.string.wi_day_rain_mix) else context.getString(R.string.wi_night_rain_mix)
                504 -> if (isDayTime) context.getString(R.string.wi_day_rain) else context.getString(R.string.wi_night_rain)
                511 -> if (isDayTime) context.getString(R.string.wi_day_sleet) else context.getString(R.string.wi_night_sleet)
                520 -> if (isDayTime) context.getString(R.string.wi_day_showers) else context.getString(R.string.wi_night_showers)
                521 -> if (isDayTime) context.getString(R.string.wi_day_showers) else context.getString(R.string.wi_night_showers)
                522 -> if (isDayTime) context.getString(R.string.wi_day_storm_showers) else context.getString(R.string.wi_night_storm_showers)
                else -> if (isDayTime) context.getString(R.string.wi_day_rain) else context.getString(R.string.wi_night_rain)
            }

            idDivided == 6 -> if (isDayTime) context.getString(R.string.wi_day_snow) else context.getString(R.string.wi_night_alt_snow)
            idDivided == 7 -> if (isDayTime) context.getString(R.string.wi_day_fog) else context.getString(R.string.wi_night_fog)
            idDivided == 8 -> when (id) {
                801 -> if (isDayTime) context.getString(R.string.wi_day_cloudy) else context.getString(R.string.wi_night_cloudy)
                802 -> if (isDayTime) context.getString(R.string.wi_day_cloudy_gusts) else context.getString(R.string.wi_night_alt_cloudy_gusts)
                803 -> if (isDayTime) context.getString(R.string.wi_day_cloudy_windy) else context.getString(R.string.wi_night_alt_cloudy_windy)
                804 -> if (isDayTime) context.getString(R.string.wi_day_cloudy) else context.getString(R.string.wi_night_cloudy)
                else -> if (isDayTime) context.getString(R.string.wi_day_cloudy) else context.getString(R.string.wi_night_cloudy)
            }

            else -> context.getString(R.string.wi_na)
        }
        return icon
    }


    private fun convertTimestampToReadableDate(timestamp: Long): String {
        // Multiply by 1000 to convert seconds to milliseconds if the timestamp is in seconds
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("hh:mm aa", Locale.getDefault())
        return format.format(date)
    }


    private fun setupBatteryWidget() {
        lifecycleScope.launch {
            if (!preferenceHelper.showBatteryWidget) return@launch
            try {
                val weatherBatteryDrawable = binding.batteryRoot.background
                if (weatherBatteryDrawable is GradientDrawable) {
                    weatherBatteryDrawable.setColor(preferenceHelper.widgetBackgroundColor)
                }

                binding.batteryLevel.setTextColor(preferenceHelper.widgetTextColor)
                binding.batteryCount.setTextColor(preferenceHelper.widgetTextColor)
                binding.chargingStatus.setTextColor(preferenceHelper.widgetTextColor)
                binding.batteryHealth.setTextColor(preferenceHelper.widgetTextColor)
                binding.batteryCurrent.setTextColor(preferenceHelper.widgetTextColor)
                binding.batteryVoltage.setTextColor(preferenceHelper.widgetTextColor)
                binding.batteryTemperature.setTextColor(preferenceHelper.widgetTextColor)

                binding.batteryRoot.visibility = View.VISIBLE
            } catch (e: Exception) {
                Log.e("Battery", "Failed to fetch battery data: ${e.message}")
            }
        }
    }

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val batteryManager = requireContext().getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val isCharging = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
                val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
                val count = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    intent.getIntExtra(BatteryManager.EXTRA_CYCLE_COUNT, 0)
                } else {
                    0
                }
                val currentMicroAmps = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
                val current = (currentMicroAmps / 1000.0)
                val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
                val voltageVolts = (voltage / 1.0)
                val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                val temperatureCelsius = (temperature / 10.0)

                val voltageScale = getString(R.string.widget_mv)
                val currentScale = getString(R.string.widget_ma)
                val temperatureScale = getString(R.string.widget_c)

                val batteryPct = (level / scale.toFloat() * 100).toInt()
                val chargingStatusText = when (isCharging) {
                    BatteryManager.BATTERY_PLUGGED_AC, BatteryManager.BATTERY_PLUGGED_USB, BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Charging"
                    else -> "Not Charging"
                }
                val healthStatus = when (health) {
                    BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                    BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
                    BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                    BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
                    BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
                    BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
                    else -> "Unknown"
                }

                binding.batteryLevel.text = getString(R.string.widgets_battery_level, batteryPct)
                binding.batteryCount.text = getString(R.string.widgets_battery_count, count)
                binding.chargingStatus.text = getString(R.string.widgets_battery_status, chargingStatusText)
                binding.batteryHealth.text = getString(R.string.widgets_battery_health, healthStatus)
                binding.batteryVoltage.text = getString(R.string.widgets_battery_voltage, voltageVolts, voltageScale)
                binding.batteryCurrent.text = getString(R.string.widgets_battery_current, current, currentScale)
                binding.batteryTemperature.text = getString(R.string.widgets_battery_temperature, temperatureCelsius, temperatureScale)
            }
        }
    }

    private fun observeClickListener() {
        binding.weatherRefresh.setOnClickListener {
            setupWeatherWidget()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun observeSwipeTouchListener() {
        binding.apply {
            nestedScrollView.setOnTouchListener(getSwipeGestureListener(context))
        }
    }

    private fun getSwipeGestureListener(context: Context): View.OnTouchListener {
        return object : OnSwipeTouchListener(context, preferenceHelper) {
            override fun onSwipeLeft() {
                println("getSwipeGestureListener")
                super.onSwipeLeft()
                val actionTypeNavOptions: NavOptions? =
                    if (preferenceHelper.disableAnimations) null
                    else appHelper.getActionType(Constants.Swipe.Left)

                Handler(Looper.getMainLooper()).post {
                    findNavController().navigate(
                        R.id.HomeFragment,
                        null,
                        actionTypeNavOptions
                    )
                }
            }

            override fun onSwipeRight() {
                println("getSwipeGestureListener")
                super.onSwipeRight()
                val actionTypeNavOptions: NavOptions? =
                    if (preferenceHelper.disableAnimations) null
                    else appHelper.getActionType(Constants.Swipe.Right)

                Handler(Looper.getMainLooper()).post {
                    findNavController().navigate(
                        R.id.HomeFragment,
                        null,
                        actionTypeNavOptions
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        context.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        setupWeatherWidget()
    }

    override fun onPause() {
        super.onPause()
        context.unregisterReceiver(batteryReceiver)
    }
}

