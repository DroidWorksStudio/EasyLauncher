package com.github.droidworksstudio.launcher.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.droidworksstudio.common.hideKeyboard
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.databinding.FragmentWidgetsBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnSwipeTouchListener
import com.github.droidworksstudio.launcher.listener.ScrollEventListener
import com.github.droidworksstudio.launcher.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentWidgetsBinding.inflate(inflater, container, false)
        _binding = binding

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = findNavController()
        // Set according to the system theme mode
        appHelper.dayNightMod(requireContext(), binding.mainLayout)
        super.onViewCreated(view, savedInstanceState)

        initializeInjectedDependencies()
        setupWeatherWidget()
        observeSwipeTouchListener()
        observeClickListener()
    }

    private fun initializeInjectedDependencies() {
        context = requireContext()
        binding.nestScrollView.hideKeyboard()

        binding.nestScrollView.scrollEventListener = this
    }

    private fun setupWeatherWidget() {
        val sharedPreferences =
            context.getSharedPreferences(Constants.WEATHER_PREFS, Context.MODE_PRIVATE)
        val latitude = sharedPreferences.getFloat(Constants.LATITUDE, 0f)
        val longitude = sharedPreferences.getFloat(Constants.LONGITUDE, 0f)
        lifecycleScope.launch {
            try {
                binding.weatherRoot.visibility = View.VISIBLE
                val weatherResponse = appHelper.fetchWeatherData(context, latitude, longitude)
                val temperatureScale =
                    if (preferenceHelper.weatherUnits == Constants.Units.Metric) getString(R.string.weather_c) else getString(
                        R.string.weather_f
                    )
                val speedScale =
                    if (preferenceHelper.weatherUnits == Constants.Units.Metric) getString(R.string.weather_mps) else getString(
                        R.string.weather_mph
                    )

                binding.weatherCity.setTextColor(preferenceHelper.widgetTextColor)
                binding.weatherTemperature.setTextColor(preferenceHelper.widgetTextColor)
                binding.weatherDescription.setTextColor(preferenceHelper.widgetTextColor)
                binding.weatherWind.setTextColor(preferenceHelper.widgetTextColor)
                binding.weatherHumidity.setTextColor(preferenceHelper.widgetTextColor)
                binding.weatherPressure.setTextColor(preferenceHelper.widgetTextColor)

                binding.weatherCity.text =
                    getString(R.string.widget_weather_location, weatherResponse.name)
                binding.weatherTemperature.text =
                    getString(
                        R.string.widget_weather_temp,
                        weatherResponse.main.temp,
                        temperatureScale
                    )
                binding.weatherDescription.text =
                    getString(
                        R.string.widget_weather_description,
                        weatherResponse.weather[0].description
                    )
                binding.weatherWind.text =
                    getString(R.string.widget_weather_wind, weatherResponse.wind.speed, speedScale)
                binding.weatherHumidity.text =
                    getString(R.string.widget_weather_humidity, weatherResponse.main.humidity)
                binding.weatherPressure.text =
                    getString(R.string.widget_weather_pressure, weatherResponse.main.pressure)

                val weatherWidgetDrawable = binding.weatherRoot.background
                if (weatherWidgetDrawable is GradientDrawable) {
                    weatherWidgetDrawable.setColor(preferenceHelper.widgetBackgroundColor)
                }

                binding.weatherButtonRefresh.setColorFilter(preferenceHelper.widgetTextColor)
            } catch (e: Exception) {
                binding.weatherRoot.visibility = View.GONE
                Log.e("Weather", "Failed to fetch weather data: ${e.message}")
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
        binding.weatherButtonRefresh.setOnClickListener {
            setupWeatherWidget()
        }
    }
}

