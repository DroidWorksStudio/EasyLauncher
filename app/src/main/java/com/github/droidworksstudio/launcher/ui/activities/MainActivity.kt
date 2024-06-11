package com.github.droidworksstudio.launcher.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.github.droidworksstudio.common.hasInternetPermission
import com.github.droidworksstudio.common.isTablet
import com.github.droidworksstudio.common.showLongToast
import com.github.droidworksstudio.launcher.BuildConfig
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.databinding.ActivityMainBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.utils.Constants
import com.github.droidworksstudio.launcher.viewmodel.AppViewModel
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager

    companion object {
        private const val REQUEST_LOCATION_PERMISSION_CODE = 1001
    }

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
    private val saveLocationRunnable = object : Runnable {
        override fun run() {
            lastKnownLocation?.let { location ->
                saveLocation(location.latitude.toFloat(), location.longitude.toFloat())
            }
            handler.postDelayed(this, 30000) // Run every 30 seconds
        }
    }

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

        if (applicationContext.hasInternetPermission()) {
            checkLocationPermission()
            checkForUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        if (applicationContext.hasInternetPermission()) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                locationManager.removeUpdates(this)
            }
            handler.removeCallbacks(saveLocationRunnable)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (applicationContext.hasInternetPermission()) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                locationManager.removeUpdates(this)
            }
            handler.removeCallbacks(saveLocationRunnable)
        }
    }

    private fun initializeDependencies() {

        preferenceViewModel.setShowStatusBar(preferenceHelper.showStatusBar)
        preferenceViewModel.setFirstLaunch(preferenceHelper.firstLaunch)

        window.addFlags(FLAG_LAYOUT_NO_LIMITS)

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

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION_CODE
            )
        } else {
            // Permission already granted, proceed with getting location
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with getting location
                getLocation()
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLocation() {
        try {
            // Check if permission is granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    this
                )
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0f,
                    this
                )
                // Start the handler to save location every 30 seconds
                handler.post(saveLocationRunnable)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun onLocationChanged(location: Location) {
        // Get latitude and longitude
        val latitude = location.latitude.toFloat()
        val longitude = location.longitude.toFloat()

        // Save the location data immediately
        saveLocation(latitude, longitude)
        lastKnownLocation = location
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
            navController.popBackStack(R.id.HomeFragment, false)
    }

    private fun checkForUpdates() {
        val currentVersion = BuildConfig.VERSION_NAME
        val url = "https://api.github.com/repos/DroidWorksStudio/EasyLauncher/releases/latest"

        val request = Request.Builder().url(url).build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle the error
            }

            @SuppressLint("NewApi")
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    val jsonObject = JSONObject(json.toString())
                    val tagName = jsonObject.getString("tag_name")
                    val latestVersion = tagName.replace("v", "")
                    val assets = jsonObject.getJSONArray("assets")
                    val apkUrl = (assets.get(1) as JSONObject).getString("browser_download_url")

                    if (latestVersion > currentVersion) {
                        val sharedPreferences = getSharedPreferences("update_prefs", Context.MODE_PRIVATE)
                        val declinedVersion = sharedPreferences.getString("declined_version", "")

                        if (latestVersion != declinedVersion) {
                            // Ask the user if they want to update
                            runOnUiThread {
                                showUpdateDialog(latestVersion, apkUrl)
                            }
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("NewApi")
    fun showUpdateDialog(latestVersion: String, apkUrl: String) {
        MaterialAlertDialogBuilder(this).apply {
            setTitle("Update Available")
            setMessage("A new version of the app is available. Do you want to update?")
            setPositiveButton("Update") { _, _ ->
                downloadApk(apkUrl)
            }
            setNegativeButton("Later") { _, _ ->
                // Save the declined version
                val sharedPreferences = getSharedPreferences("update_prefs", Context.MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putString("declined_version", latestVersion)
                    apply()
                }
            }
            setCancelable(false)
            show()
        }
    }

    @SuppressLint("NewApi")
    private fun downloadApk(apkUrl: String) {
        val request = DownloadManager.Request(Uri.parse(apkUrl)).apply {
            setTitle("Downloading update")
            setDescription("Your app is downloading the latest update")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${Constants.PACKAGE_NAME}.apk")
        }

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = manager.enqueue(request)

        // Register a BroadcastReceiver to listen for completion of the download
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = manager.query(query)
                    if (cursor != null && cursor.moveToFirst()) {
                        val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        if (statusIndex != -1) {
                            val status = cursor.getInt(statusIndex)
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                // Download completed successfully, now call installApk()
                                requestInstallPermission()
                            }
                        }
                    }
                    cursor?.close()
                }
            }
        }

        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), RECEIVER_EXPORTED)
    }

    private fun requestInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!packageManager.canRequestPackageInstalls()) {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:$packageName")
                }
                @Suppress("DEPRECATION")
                startActivityForResult(intent, Constants.REQUEST_INSTALL_PERMISSION)
                return
            } else {
                // Permission already granted, proceed with installation
                installApk()
            }
        } else {
            // For devices below Android Oreo, installation permission is granted by default
            installApk()
        }
    }

    private fun installApk() {
        val apkFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "${Constants.PACKAGE_NAME}.apk")
        val apkUri = FileProvider.getUriForFile(applicationContext, "$packageName.provider", apkFile)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        startActivity(intent)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_INSTALL_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val canInstallPackages = packageManager.canRequestPackageInstalls()
                if (canInstallPackages) {
                    // Permission granted, proceed with installation
                    installApk()
                } else {
                    // Permission still not granted, handle accordingly
                    applicationContext.showLongToast("Please allow install permission to install.")
                }
            }
        }
    }
}