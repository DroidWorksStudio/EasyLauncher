package com.github.droidworksstudio.launcher.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.droidworksstudio.common.getAppNameFromPackageName
import com.github.droidworksstudio.common.resetDefaultLauncher
import com.github.droidworksstudio.launcher.utils.Constants
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.adapter.font.FontAdapter
import com.github.droidworksstudio.launcher.databinding.FragmentSettingsBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.AppReloader
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnSwipeTouchListener
import com.github.droidworksstudio.launcher.listener.ScrollEventListener
import com.github.droidworksstudio.launcher.repository.AppInfoRepository
import com.github.droidworksstudio.launcher.ui.bottomsheetdialog.AlignmentBottomSheetDialogFragment
import com.github.droidworksstudio.launcher.ui.bottomsheetdialog.ColorBottomSheetDialogFragment
import com.github.droidworksstudio.launcher.ui.bottomsheetdialog.PaddingBottomSheetDialogFragment
import com.github.droidworksstudio.launcher.ui.bottomsheetdialog.TextBottomSheetDialogFragment
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(),
    ScrollEventListener {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val preferenceViewModel: PreferenceViewModel by viewModels()

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appInfoRepository: AppInfoRepository

    @Inject
    lateinit var appHelper: AppHelper

    private lateinit var navController: NavController

    private lateinit var context: Context

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

        context = requireContext()

        initializeInjectedDependencies()
        observeClickListener()
        observeSwipeTouchListener()

        val packageInfo =
            requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
        binding.versionInfo.text = getString(R.string.settings_version).format(
            getString(R.string.app_name),
            packageInfo.versionName
        )

        binding.miscellaneousSearchEngineControl.text = preferenceHelper.searchEngines.getString(context)
        binding.miscellaneousLauncherFontsControl.text = preferenceHelper.launcherFont.getString(context)

        updateGestureControlText(context, preferenceHelper.doubleTapAction, preferenceHelper.doubleTapApp, binding.gesturesDoubleTapControl)
        updateGestureControlText(context, preferenceHelper.swipeUpAction, preferenceHelper.swipeUpApp, binding.gesturesSwipeUpControl)
        updateGestureControlText(context, preferenceHelper.swipeDownAction, preferenceHelper.swipeDownApp, binding.gesturesSwipeDownControl)
        updateGestureControlText(context, preferenceHelper.swipeLeftAction, preferenceHelper.swipeLeftApp, binding.gesturesSwipeLeftControl)
        updateGestureControlText(context, preferenceHelper.swipeRightAction, preferenceHelper.swipeRightApp, binding.gesturesSwipeRightControl)
    }

    // Function to update UI text based on action and app name
    private fun updateGestureControlText(
        context: Context,
        action: Constants.Action,
        appPackageName: String?,
        textView: TextView
    ) {
        val appName = appPackageName?.let { context.getAppNameFromPackageName(it) }
        val actionText = if (action == Constants.Action.OpenApp) {
            context.getString(R.string.settings_actions_open_app_run, appName)
        } else {
            action.getString(context)
        }
        textView.text = actionText
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
        binding.lockSettingsSwitchCompat.isChecked = preferenceHelper.settingsLock
    }

    private fun observeClickListener() {
        setupSwitchListeners()

        // Click listener for reset default launcher
        binding.setLauncherSelector.setOnClickListener {
            requireContext().resetDefaultLauncher()
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
        }

        binding.restoreView.setOnClickListener {
            appHelper.restoreSharedPreferences(requireContext())
            AppReloader.restartApp(context)
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

    @SuppressLint("ClickableViewAccessibility")
    private fun observeSwipeTouchListener() {
        binding.touchArea.setOnTouchListener(getSwipeGestureListener(context))

        binding.miscellaneousSearchEngineControl.setOnClickListener {
            showSearchEngineDialog()
        }

        binding.miscellaneousLauncherFontsControl.setOnClickListener {
            showLauncherFontDialog()
        }

        binding.gesturesDoubleTapControl.setOnClickListener {
            swipeActionClickEvent(Constants.Swipe.DoubleTap)
        }

        binding.gesturesSwipeUpControl.setOnClickListener {
            swipeActionClickEvent(Constants.Swipe.Up)
        }

        binding.gesturesSwipeDownControl.setOnClickListener {
            swipeActionClickEvent(Constants.Swipe.Down)
        }

        binding.gesturesSwipeLeftControl.setOnClickListener {
            swipeActionClickEvent(Constants.Swipe.Left)
        }

        binding.gesturesSwipeRightControl.setOnClickListener {
            swipeActionClickEvent(Constants.Swipe.Right)
        }
    }

    private fun getSwipeGestureListener(context: Context): View.OnTouchListener {
        return object : OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                findNavController().navigateUp()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                findNavController().navigateUp()
            }

        }
    }

    private var searchEngineDialog: AlertDialog? = null

    private fun showSearchEngineDialog() {
        // Dismiss any existing dialog to prevent multiple dialogs open simultaneously
        searchEngineDialog?.dismiss()
        // Get the array of SearchEngines enum values
        val items = Constants.SearchEngines.entries.toTypedArray()

        // Map the enum values to their string representations
        val itemStrings = items.map { it.getString(context) }.toTypedArray()

        val dialogBuilder = MaterialAlertDialogBuilder(context)

        dialogBuilder.setTitle(getString(R.string.settings_select_search_engine))
        dialogBuilder.setItems(itemStrings) { _, which ->
            val selectedItem = items[which]
            preferenceViewModel.setSearchEngine(selectedItem)
            binding.miscellaneousSearchEngineControl.text = preferenceHelper.searchEngines.name
        }
        // Assign the created dialog to launcherFontDialog
        searchEngineDialog = dialogBuilder.create()
        searchEngineDialog?.show()
    }

    private var launcherFontDialog: AlertDialog? = null

    private fun showLauncherFontDialog() {
        // Dismiss any existing dialog to prevent multiple dialogs open simultaneously
        launcherFontDialog?.dismiss()

        // Get the array of SearchEngines enum values
        val items = Constants.Fonts.entries.toTypedArray()

        // Map the enum values to their string representations
        val itemStrings = items.map { it.getString(context) }.toTypedArray()

        val dialogBuilder = MaterialAlertDialogBuilder(context)
        dialogBuilder.setTitle(getString(R.string.settings_select_launcher_font))
        dialogBuilder.setAdapter(FontAdapter(context, items, itemStrings)) { _, which ->
            val selectedItem = items[which]
            preferenceViewModel.setLauncherFont(selectedItem)
            binding.miscellaneousLauncherFontsControl.text = preferenceHelper.launcherFont.name

            // Delay the restart slightly to ensure preferences are saved
            Handler(Looper.getMainLooper()).postDelayed({
                AppReloader.restartApp(context)
            }, 500) // Delay in milliseconds (e.g., 500ms)
        }

        // Assign the created dialog to launcherFontDialog
        launcherFontDialog = dialogBuilder.create()
        launcherFontDialog?.show()
    }

    private var appSelectionDialog: AlertDialog? = null

    private fun showAppSelectionDialog(swipeType: Constants.Swipe) {
        // Make sure this method is called within a lifecycle owner scope
        lifecycleScope.launch(Dispatchers.Main) {
            // Dismiss any existing dialog to prevent multiple dialogs open simultaneously
            appSelectionDialog?.dismiss()

            // Collect the flow of installed apps
            appInfoRepository.getDrawApps().collect { installedApps ->
                // Extract app names and package names
                val appNames = installedApps.map { it.appName }.toTypedArray()
                val packageNames = installedApps.map { it.packageName }

                // Build and display the dialog
                val dialogBuilder = MaterialAlertDialogBuilder(context)
                dialogBuilder.setTitle("Select an App")
                dialogBuilder.setItems(appNames) { _, which ->
                    val selectedPackageName = packageNames[which]
                    when (swipeType) {
                        Constants.Swipe.DoubleTap,
                        Constants.Swipe.Up,
                        Constants.Swipe.Down,
                        Constants.Swipe.Left,
                        Constants.Swipe.Right -> handleSwipeAction(swipeType, selectedPackageName)
                    }

                }

                // Assign the created dialog to launcherFontDialog
                appSelectionDialog = dialogBuilder.create()
                appSelectionDialog?.show()
            }
        }
    }

    // Add this function to dismiss the dialog if it's showing
    private fun dismissDialogs() {
        launcherFontDialog?.dismiss()
        searchEngineDialog?.dismiss()
        appSelectionDialog?.dismiss()
    }

    private fun handleSwipeAction(swipeType: Constants.Swipe, selectedPackageName: String) {
        val selectedApp = context.getAppNameFromPackageName(selectedPackageName)

        when (swipeType) {
            Constants.Swipe.DoubleTap -> {
                binding.gesturesDoubleTapControl.text = "Open $selectedApp"
                preferenceHelper.doubleTapApp = selectedPackageName
            }

            Constants.Swipe.Up -> {
                binding.gesturesSwipeUpControl.text = "Open $selectedApp"
                preferenceHelper.swipeUpApp = selectedPackageName
            }

            Constants.Swipe.Down -> {
                binding.gesturesSwipeDownControl.text = "Open $selectedApp"
                preferenceHelper.swipeDownApp = selectedPackageName
            }

            Constants.Swipe.Left -> {
                binding.gesturesSwipeLeftControl.text = "Open $selectedApp"
                preferenceHelper.swipeLeftApp = selectedPackageName
            }

            Constants.Swipe.Right -> {
                binding.gesturesSwipeRightControl.text = "Open $selectedApp"
                preferenceHelper.swipeRightApp = selectedPackageName
            }
        }
    }


    private fun swipeActionClickEvent(swipe: Constants.Swipe) {
        // Get the array of Action enum values
        val actions = Constants.Action.entries.toTypedArray()
        // Map the enum values to their string representations
        val actionStrings = actions.map { it.getString(context) }.toTypedArray()

        val dialog = MaterialAlertDialogBuilder(context)

        dialog.setTitle("Select a Action")
        dialog.setItems(actionStrings) { _, which ->
            val selectedAction = actions[which]
            when (swipe) {
                Constants.Swipe.DoubleTap -> handleSwipeAction(context, Constants.Swipe.DoubleTap, selectedAction, binding)
                Constants.Swipe.Up -> handleSwipeAction(context, Constants.Swipe.Up, selectedAction, binding)
                Constants.Swipe.Down -> handleSwipeAction(context, Constants.Swipe.Down, selectedAction, binding)
                Constants.Swipe.Left -> handleSwipeAction(context, Constants.Swipe.Left, selectedAction, binding)
                Constants.Swipe.Right -> handleSwipeAction(context, Constants.Swipe.Right, selectedAction, binding)
            }
        }
        dialog.show()
    }

    // Function to handle setting action and updating UI
    private fun handleSwipeAction(context: Context, swipe: Constants.Swipe, action: Constants.Action, binding: FragmentSettingsBinding) {
        preferenceViewModel.setSwipeAction(swipe, action)

        when (action) {
            Constants.Action.OpenApp -> {
                val selectedApp = when (swipe) {
                    Constants.Swipe.DoubleTap -> context.getAppNameFromPackageName(preferenceHelper.doubleTapApp)
                    Constants.Swipe.Up -> context.getAppNameFromPackageName(preferenceHelper.swipeUpApp)
                    Constants.Swipe.Down -> context.getAppNameFromPackageName(preferenceHelper.swipeDownApp)
                    Constants.Swipe.Left -> context.getAppNameFromPackageName(preferenceHelper.swipeLeftApp)
                    Constants.Swipe.Right -> context.getAppNameFromPackageName(preferenceHelper.swipeRightApp)
                }
                binding.apply {
                    when (swipe) {
                        Constants.Swipe.DoubleTap -> gesturesDoubleTapControl.text = "Open $selectedApp"
                        Constants.Swipe.Up -> gesturesSwipeUpControl.text = "Open $selectedApp"
                        Constants.Swipe.Down -> gesturesSwipeDownControl.text = "Open $selectedApp"
                        Constants.Swipe.Left -> gesturesSwipeLeftControl.text = "Open $selectedApp"
                        Constants.Swipe.Right -> gesturesSwipeRightControl.text = "Open $selectedApp"
                    }
                }
                showAppSelectionDialog(swipe)
            }

            else -> {
                binding.apply {
                    when (swipe) {
                        Constants.Swipe.DoubleTap -> gesturesDoubleTapControl.text = preferenceHelper.doubleTapAction.getString(context)
                        Constants.Swipe.Up -> gesturesSwipeUpControl.text = preferenceHelper.swipeUpAction.getString(context)
                        Constants.Swipe.Down -> gesturesSwipeDownControl.text = preferenceHelper.swipeDownAction.getString(context)
                        Constants.Swipe.Left -> gesturesSwipeLeftControl.text = preferenceHelper.swipeLeftAction.getString(context)
                        Constants.Swipe.Right -> gesturesSwipeRightControl.text = preferenceHelper.swipeRightAction.getString(context)
                    }
                }
            }
        }
    }

    // Extension function to set swipe action in ViewModel
    private fun PreferenceViewModel.setSwipeAction(swipe: Constants.Swipe, action: Constants.Action) {
        when (swipe) {
            Constants.Swipe.DoubleTap -> setDoubleTap(action)
            Constants.Swipe.Up -> setSwipeUp(action)
            Constants.Swipe.Down -> setSwipeDown(action)
            Constants.Swipe.Left -> setSwipeLeft(action)
            Constants.Swipe.Right -> setSwipeRight(action)
        }
    }

    override fun onStop() {
        super.onStop()
        dismissDialogs()
    }

}