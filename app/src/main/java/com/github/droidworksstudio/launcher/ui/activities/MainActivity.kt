package com.github.droidworksstudio.launcher.ui.activities

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.github.droidworksstudio.launcher.Constants
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.databinding.ActivityMainBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.viewmodel.AppViewModel
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val viewModel: AppViewModel by viewModels()
    private val preferenceViewModel: PreferenceViewModel by viewModels()

    private lateinit var navController: NavController

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appHelper: AppHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeDependencies()
        setupNavController()
        setupOrientation()
    }

    private fun initializeDependencies() {

        preferenceViewModel.setShowStatusBar(preferenceHelper.showStatusBar)
        preferenceViewModel.setFirstLaunch(preferenceHelper.firstLaunch)

        window.addFlags(FLAG_LAYOUT_NO_LIMITS)

    }

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
        if (appHelper.isTablet(this)) return
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
}