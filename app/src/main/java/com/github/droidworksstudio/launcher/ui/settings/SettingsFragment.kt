package com.github.droidworksstudio.launcher.ui.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.databinding.FragmentSettingsBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.helper.restartApp
import com.github.droidworksstudio.launcher.listener.ScrollEventListener
import com.github.droidworksstudio.launcher.ui.bottomsheetdialog.AlignmentBottomSheetDialogFragment
import com.github.droidworksstudio.launcher.ui.bottomsheetdialog.ColorBottomSheetDialogFragment
import com.github.droidworksstudio.launcher.ui.bottomsheetdialog.PaddingBottomSheetDialogFragment
import com.github.droidworksstudio.launcher.ui.bottomsheetdialog.TextBottomSheetDialogFragment
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(), ScrollEventListener {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val preferenceViewModel: PreferenceViewModel by viewModels()

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appHelper: AppHelper

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        _binding = binding

        return binding.root
    }

    // Called after the fragment view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = findNavController()
        // Set according to the system theme mode
        appHelper.dayNightMod(requireContext(), binding.nestScrollView)
        super.onViewCreated(view, savedInstanceState)

        initializeInjectedDependencies()
        observeClickListener()

        val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
        binding.versionInfo.text = getString(R.string.settings_version, getString(R.string.app_name), packageInfo.versionName)
    }

    @SuppressLint("SetTextI18n")
    private fun initializeInjectedDependencies() {
        binding.nestScrollView.scrollEventListener = this

        // Set initial values and listeners for switches
        binding.statueBarSwitchCompat.isChecked = preferenceHelper.showStatusBar
        binding.timeSwitchCompat.isChecked = preferenceHelper.showTime
        binding.dateSwitchCompat.isChecked = preferenceHelper.showDate
        binding.batterySwitchCompat.isChecked = preferenceHelper.showBattery
        binding.dailyWordSwitchCompat.isChecked = preferenceHelper.showDailyWord
        binding.appIconsSwitchCompat.isChecked = preferenceHelper.showAppIcon
        binding.automaticKeyboardSwitchCompat.isChecked = preferenceHelper.automaticKeyboard
        binding.automaticOpenAppSwitchCompat.isChecked = preferenceHelper.automaticOpenApp
        binding.gesturesLockSwitchCompat.isChecked = preferenceHelper.tapLockScreen
        binding.gesturesNotificationSwitchCompat.isChecked = preferenceHelper.swipeNotification
        binding.gesturesSearchSwitchCompat.isChecked = preferenceHelper.swipeSearch
        binding.lockSettingsSwitchCompat.isChecked = preferenceHelper.settingsLock
    }

    private fun observeClickListener() {
        setupSwitchListeners()

        // Click listener for reset default launcher
        binding.setLauncherSelector.setOnClickListener {
            appHelper.resetDefaultLauncher(requireContext())
        }

        binding.favoriteText.setOnClickListener {
            findNavController().navigate(R.id.action_SettingsFragment_to_FavoriteFragment)
        }

        binding.hiddenText.setOnClickListener {
            findNavController().navigate(R.id.action_SettingsFragment_to_HiddenFragment)
        }

        binding.setAppWallpaper.setOnClickListener {
            val intent = Intent(Intent.ACTION_SET_WALLPAPER)
            startActivity(Intent.createChooser(intent, "Select Wallpaper"))
        }

        binding.selectAppearanceTextSize.setOnClickListener {
            val bottomSheetFragment = TextBottomSheetDialogFragment()
            bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
        }

        binding.selectAppearanceAlignment.setOnClickListener {
            val bottomSheetFragment = AlignmentBottomSheetDialogFragment()
            bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
        }

        binding.selectAppearancePadding.setOnClickListener {
            val bottomSheetFragment = PaddingBottomSheetDialogFragment()
            bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
        }

        binding.selectAppearanceColor.setOnClickListener {
            val bottomSheetFragment = ColorBottomSheetDialogFragment()
            bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
        }

        binding.shareView.setOnClickListener {
            appHelper.shareAppButton(requireContext())
        }

        binding.githubView.setOnClickListener {
            appHelper.githubButton(requireContext())

        }

        binding.feedbackView.setOnClickListener {
            appHelper.feedbackButton(requireContext())
        }

        binding.backupView.setOnClickListener {
            appHelper.backupSharedPreferences(requireContext())
            Toast.makeText(requireContext(), getString(R.string.settings_reload_app_backup), Toast.LENGTH_SHORT).show()
        }

        binding.restoreView.setOnClickListener {
            appHelper.restoreSharedPreferences(requireContext())
            Toast.makeText(requireContext(), getString(R.string.settings_reload_app_restore), Toast.LENGTH_SHORT).show()
            restartApp()
        }
    }

    private fun setupSwitchListeners() {
        binding.statueBarSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setShowStatusBar(isChecked)
        }

        binding.timeSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setShowTime(isChecked)
        }

        binding.dateSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setShowDate(isChecked)
        }

        binding.batterySwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setShowBattery(isChecked)
        }

        binding.dailyWordSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setShowDailyWord(isChecked)
        }

        binding.appIconsSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setShowAppIcons(isChecked)
        }

        binding.gesturesLockSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            appHelper.enableAppAsAccessibilityService(requireContext(), preferenceHelper.tapLockScreen)
            preferenceViewModel.setDoubleTapLock(isChecked)
        }

        binding.gesturesNotificationSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setSwipeNotification(isChecked)
        }

        binding.gesturesSearchSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setSwipeSearch(isChecked)
        }

        binding.automaticKeyboardSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setAutoKeyboard(isChecked)
        }

        binding.automaticOpenAppSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setAutoOpenApp(isChecked)
        }

        binding.lockSettingsSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            preferenceViewModel.setLockSettings(isChecked)
        }
    }

    override fun onTopReached() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    override fun onBottomReached() {
        Log.d("Tag", "onBottomReached")
    }

    override fun onScroll(isTopReached: Boolean, isBottomReached: Boolean) {
        Log.d("Tag", "onScroll")
    }
}