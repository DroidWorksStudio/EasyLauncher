package com.github.droidworksstudio.launcher.ui.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.databinding.ActivitySettingsBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val preferenceViewModel: PreferenceViewModel by viewModels()

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appHelper: AppHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Find the NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_settings) as NavHostFragment

        // Retrieve the NavController
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)

        initializeDependencies()

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_settings)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun initializeDependencies() {

        preferenceViewModel.setShowStatusBar(preferenceHelper.showStatusBar)
    }
    private fun observeUI(){
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        preferenceViewModel.setShowStatusBar(preferenceHelper.showStatusBar)
        preferenceViewModel.showStatusBarLiveData.observe(this) {
            if (it) appHelper.showStatusBar(this.window)
            else appHelper.hideStatusBar(this.window) }
    }

    override fun onResume() {
        super.onResume()
        observeUI()
    }
}