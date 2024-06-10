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
import androidx.annotation.RequiresApi
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
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.databinding.FragmentWidgetsBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnSwipeTouchListener
import com.github.droidworksstudio.launcher.listener.ScrollEventListener
import com.github.droidworksstudio.launcher.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.absoluteValue


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
        savedInstanceState: Bundle?
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
        observeSwipeTouchListener()
        observeClickListener()
    }

    private fun initializeInjectedDependencies() {
        context = requireContext()
        binding.nestScrollView.hideKeyboard()

        binding.nestScrollView.scrollEventListener = this
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

    private fun setupWeatherWidget() {
        val sharedPreferences =
            context.getSharedPreferences(Constants.WEATHER_PREFS, Context.MODE_PRIVATE)
        val latitude = sharedPreferences.getFloat(Constants.LATITUDE, 0f)
        val longitude = sharedPreferences.getFloat(Constants.LONGITUDE, 0f)
        val timestamp = convertTimestampToReadableDate(sharedPreferences.getLong("cachedDataTimestamp", 0))

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
                val weatherDeferred = async { appHelper.fetchWeatherData(context, latitude, longitude) }

                // Prepare UI elements concurrently
                withContext(Dispatchers.Main) {
                    binding.apply {
                        weatherCity.setTextColor(widgetTextColor)
                        weatherTemperature.setTextColor(widgetTextColor)
                        weatherDescription.setTextColor(widgetTextColor)
                        weatherWind.setTextColor(widgetTextColor)
                        weatherHumidity.setTextColor(widgetTextColor)
                        weatherRefresh.setTextColor(widgetTextColor)
                        weatherLastRun.setTextColor(widgetTextColor)
                        weatherRefresh.typeface = ResourcesCompat.getFont(requireActivity(), R.font.weather)
                    }
                }

                val weatherResponse = weatherDeferred.await()
                Log.d("weatherResponse", "$weatherResponse")

                withContext(Dispatchers.Main) {
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

                        val weatherWidgetDrawable = weatherRoot.background
                        if (weatherWidgetDrawable is GradientDrawable) {
                            weatherWidgetDrawable.setColor(widgetBackgroundColor)
                        }
                        weatherRoot.visibility = View.VISIBLE
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
        paint.textSize = 180f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(text, 128f, 200f, paint)
        return bitmap
    }

    private fun setWeatherIcon(context: Context, id: Int): String {
        val icon: String
        val idDivided = id / 100
        val hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (idDivided * 100 == 800) {
            icon = if (hourOfDay in 7..19) {
                context.getString(R.string.weather_sunny)
            } else {
                context.getString(R.string.weather_clear_night)
            }
        } else {
            icon = when (idDivided) {
                2 -> context.getString(R.string.weather_thunder)
                3 -> context.getString(R.string.weather_drizzle)
                7 -> context.getString(R.string.weather_foggy)
                8 -> context.getString(R.string.weather_cloudy)
                6 -> context.getString(R.string.weather_snowy)
                5 -> context.getString(R.string.weather_rainy)
                else -> ""
            }
        }
        return icon
    }

    private fun convertTimestampToReadableDate(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("hh:mm aa", Locale.getDefault())
        return format.format(date)
    }

    private fun setupBatteryWidget() {
        lifecycleScope.launch {
            if (!preferenceHelper.showBatteryWidget) return@launch
            try {
                binding.batteryLevel.setTextColor(preferenceHelper.widgetTextColor)
                binding.batteryCount.setTextColor(preferenceHelper.widgetTextColor)
                binding.chargingStatus.setTextColor(preferenceHelper.widgetTextColor)
                binding.batteryHealth.setTextColor(preferenceHelper.widgetTextColor)
                binding.batteryCurrent.setTextColor(preferenceHelper.widgetTextColor)
                binding.batteryVoltage.setTextColor(preferenceHelper.widgetTextColor)
                binding.batteryTemperature.setTextColor(preferenceHelper.widgetTextColor)

                val weatherBatteryDrawable = binding.batteryRoot.background
                if (weatherBatteryDrawable is GradientDrawable) {
                    weatherBatteryDrawable.setColor(preferenceHelper.widgetBackgroundColor)
                }
                binding.batteryRoot.visibility = View.VISIBLE
            } catch (e: Exception) {
                Log.e("Battery", "Failed to fetch battery data: ${e.message}")
            }
        }
    }

    private val batteryReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        override fun onReceive(context: Context?, intent: Intent?) {
            val batteryManager = requireContext().getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val isCharging = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
                val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
                val count = intent.getIntExtra(BatteryManager.EXTRA_CYCLE_COUNT, 0)
                val current = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
                val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
                val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                val temperatureCelsius = temperature / 10.0

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
                binding.batteryVoltage.text = getString(R.string.widgets_battery_voltage, voltage, voltageScale)
                binding.batteryCurrent.text = getString(R.string.widgets_battery_current, current, currentScale)
                binding.batteryTemperature.text = getString(R.string.widgets_battery_temperature, temperatureCelsius, temperatureScale)
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun observeSwipeTouchListener() {
        binding.touchArea.setOnTouchListener(getSwipeGestureListener(context))
        binding.nestScrollView.setOnTouchListener(getSwipeGestureListener(context))
    }

    private fun getSwipeGestureListener(context: Context): View.OnTouchListener {
        return object : OnSwipeTouchListener(context) {
            override fun onLongClick() {
                super.onLongClick()
                val actionTypeNavOptions: NavOptions =
                    appHelper.getActionType(Constants.Swipe.DoubleTap)
                Handler(Looper.getMainLooper()).post {
                    findNavController().navigate(
                        R.id.action_WidgetsFragment_to_WidgetsSettingsFragment,
                        null,
                        actionTypeNavOptions
                    )
                }
                return
            }

            override fun onSwipeLeft() {
                super.onSwipeLeft()
                findNavController().popBackStack()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                findNavController().popBackStack()
            }
        }
    }

    private fun observeClickListener() {
        binding.weatherRefresh.setOnClickListener {
            setupWeatherWidget()
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

