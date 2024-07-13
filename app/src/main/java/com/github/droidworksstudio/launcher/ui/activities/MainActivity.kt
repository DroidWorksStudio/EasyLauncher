package com.github.droidworksstudio.launcher.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.github.droidworksstudio.common.hasInternetPermission
import com.github.droidworksstudio.common.isTablet
import com.github.droidworksstudio.common.showLongToast
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.databinding.ActivityMainBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.AppReloader
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.utils.Constants
import com.github.droidworksstudio.launcher.viewmodel.AppViewModel
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var locationManager: LocationManager

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val viewModel: AppViewModel by viewModels()
    private val preferenceViewModel: PreferenceViewModel by viewModels()

    private lateinit var navController: NavController

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appHelper: AppHelper

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var handler: Handler

    private var lastKnownLocation: Location? = null

    private fun saveLocation(latitude: Float = 0f, longitude: Float = 0f) {
        with(sharedPreferences.edit()) {
            putFloat(Constants.LATITUDE, latitude)
            putFloat(Constants.LONGITUDE, longitude)
            apply()
        }
    }

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        sharedPreferences = getSharedPreferences(Constants.WEATHER_PREFS, Context.MODE_PRIVATE)
        handler = Handler(Looper.getMainLooper())

        initializeDependencies()
        setupNavController()
        setupOrientation()
        setupLocationManager()
    }

    private fun initializeDependencies() {
        setLocationPermissionDenied(false)
        preferenceViewModel.setShowStatusBar(preferenceHelper.showStatusBar)
        preferenceViewModel.setFirstLaunch(preferenceHelper.firstLaunch)

        window.addFlags(FLAG_LAYOUT_NO_LIMITS)
    }

    private fun setupLocationManager() {
        if (applicationContext.hasInternetPermission()) {
            if (!isLocationPermissionDenied()) {
                checkLocationPermission()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.REQUEST_LOCATION_PERMISSION_CODE
            )
        }
    }

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            1000L,
            1f,
            object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    // Get latitude and longitude
                    val latitude = location.latitude.toFloat()
                    val longitude = location.longitude.toFloat()

                    // Save the location data immediately
                    saveLocation(latitude, longitude)
                    lastKnownLocation = location
                }

                override fun onProviderEnabled(provider: String) {
                    // Handle provider enabled
                    when (provider) {
                        LocationManager.GPS_PROVIDER -> applicationContext.showLongToast("GPS Provider Enabled")
                        LocationManager.NETWORK_PROVIDER -> applicationContext.showLongToast("Network Provider Enabled")
                    }
                }

                override fun onProviderDisabled(provider: String) {
                    // Handle provider disabled
                    when (provider) {
                        LocationManager.GPS_PROVIDER -> applicationContext.showLongToast("GPS Provider Disabled")
                        LocationManager.NETWORK_PROVIDER -> applicationContext.showLongToast("Network Provider Disabled")
                    }
                }
            }
        )
    }

    private fun setLocationPermissionDenied(status: Boolean) {
        preferenceHelper.locationDenied = status
    }

    private fun isLocationPermissionDenied(): Boolean {
        return preferenceHelper.locationDenied
    }

    private fun getLocation() {
        if (isLocationEnabled()) {
            requestLocationUpdates()
        } else {
            checkLocationPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setupDataBase() {
        lifecycleScope.launch {
            viewModel.initializeInstalledAppInfo(this@MainActivity)
        }
        preferenceHelper.firstLaunch = false
    }

    private fun observeUI() {
        preferenceViewModel.setShowStatusBar(preferenceHelper.showStatusBar)
        preferenceViewModel.showStatusBarLiveData.observe(this) {
            if (it) appHelper.showStatusBar(this.window)
            else appHelper.hideStatusBar(this.window)
        }
    }

    private fun setupNavController() {
        // Find the NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment

        // Retrieve the NavController
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun setupOrientation() {
        if (this.isTablet()) return
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        backToHomeScreen()
        setupDataBase()
        observeUI()
    }

    override fun onStop() {
        backToHomeScreen()
        super.onStop()
    }

    override fun onUserLeaveHint() {
        backToHomeScreen()
        super.onUserLeaveHint()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        navController = findNavController(R.id.nav_host_fragment_content_main)
        @Suppress("DEPRECATION")
        if (navController.currentDestination?.id != R.id.HomeFragment)
            super.onBackPressed()
    }

    private fun backToHomeScreen() {
        navController = findNavController(R.id.nav_host_fragment_content_main)
        if (navController.currentDestination?.id != R.id.HomeFragment)
            navController.navigate(R.id.HomeFragment)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.REQUEST_LOCATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with getting location
                    getLocation()
                } else {
                    // Permission denied, show a message to the user
                    setLocationPermissionDenied(true)
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            // showToastLong(applicationContext, "Intent Error")
            return
        }

        when (requestCode) {
            Constants.BACKUP_READ -> {
                data?.data?.also { uri ->
                    applicationContext.contentResolver.openInputStream(uri).use { inputStream ->
                        val stringBuilder = StringBuilder()
                        BufferedReader(InputStreamReader(inputStream)).use { reader ->
                            var line: String? = reader.readLine()
                            while (line != null) {
                                stringBuilder.append(line)
                                line = reader.readLine()
                            }
                        }

                        val string = stringBuilder.toString()
                        val prefs = PreferenceHelper(applicationContext)
                        prefs.clear()
                        prefs.loadFromString(string)
                    }
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    AppReloader.restartApp(applicationContext)
                }, 500)
            }

            Constants.BACKUP_WRITE -> {
                data?.data?.also { uri ->
                    applicationContext.contentResolver.openFileDescriptor(uri, "w")?.use { file ->
                        FileOutputStream(file.fileDescriptor).use { stream ->
                            val text = PreferenceHelper(applicationContext).saveToString()
                            stream.channel.truncate(0)
                            stream.write(text.toByteArray())
                        }
                    }
                }
            }
        }
    }
}