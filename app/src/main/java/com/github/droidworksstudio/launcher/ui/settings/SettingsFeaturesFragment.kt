package com.github.droidworksstudio.launcher.ui.settings

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.droidworksstudio.common.getAppNameFromPackageName
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.databinding.FragmentSettingsFeaturesBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.ScrollEventListener
import com.github.droidworksstudio.launcher.repository.AppInfoRepository
import com.github.droidworksstudio.launcher.utils.Constants
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFeaturesFragment : Fragment(),
    ScrollEventListener {

    private var _binding: FragmentSettingsFeaturesBinding? = null
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
        _binding = FragmentSettingsFeaturesBinding.inflate(inflater, container, false)
        _binding = binding

        return binding.root
    }

    // Called after the fragment view is created
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = findNavController()
        // Set according to the system theme mode
        appHelper.dayNightMod(requireContext(), binding.nestScrollView)
        super.onViewCreated(view, savedInstanceState)

        context = requireContext()

        initializeInjectedDependencies()
        observeClickListener()

        binding.apply {
            miscellaneousSearchEngineControl.text = preferenceHelper.searchEngines.getString(context)
            miscellaneousFilterStrengthControl.text = "${preferenceHelper.filterStrength}"
        }

        val actions = listOf(
            Triple(preferenceHelper.doubleTapAction, preferenceHelper.doubleTapApp, binding.gesturesDoubleTapControl),
            Triple(preferenceHelper.swipeUpAction, preferenceHelper.swipeUpApp, binding.gesturesSwipeUpControl),
            Triple(preferenceHelper.swipeDownAction, preferenceHelper.swipeDownApp, binding.gesturesSwipeDownControl),
            Triple(preferenceHelper.swipeLeftAction, preferenceHelper.swipeLeftApp, binding.gesturesSwipeLeftControl),
            Triple(preferenceHelper.swipeRightAction, preferenceHelper.swipeRightApp, binding.gesturesSwipeRightControl)
        )

        actions.forEach { (action, app, control) ->
            updateGestureControlText(context, action, app, control)
        }
    }

    // Function to update UI text based on action and app name
    private fun updateGestureControlText(
        context: Context,
        action: Constants.Action,
        appPackageName: String?,
        textView: TextView
    ) {
        val actionText = if (action == Constants.Action.OpenApp) {
            val appName = appPackageName?.let { context.getAppNameFromPackageName(it) }
            context.getString(R.string.settings_actions_open_app_run, appName)
        } else {
            action.getString(context)
        }
        textView.text = actionText
    }

    override fun onStop() {
        super.onStop()
        dismissDialogs()
    }

    private fun initializeInjectedDependencies() {
        binding.nestScrollView.scrollEventListener = this

        // Set initial values and listeners for switches
        binding.apply {
            automaticKeyboardSwitchCompat.isChecked = preferenceHelper.automaticKeyboard
            automaticOpenAppSwitchCompat.isChecked = preferenceHelper.automaticOpenApp
            searchFromStartSwitchCompat.isChecked = preferenceHelper.searchFromStart
            lockSettingsSwitchCompat.isChecked = preferenceHelper.settingsLock
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun observeClickListener() {
        setupSwitchListeners()

        binding.apply {
            gesturesDoubleTapControl.setOnClickListener {
                swipeActionClickEvent(Constants.Swipe.DoubleTap)
            }

            gesturesSwipeUpControl.setOnClickListener {
                swipeActionClickEvent(Constants.Swipe.Up)
            }

            gesturesSwipeDownControl.setOnClickListener {
                swipeActionClickEvent(Constants.Swipe.Down)
            }

            gesturesSwipeLeftControl.setOnClickListener {
                swipeActionClickEvent(Constants.Swipe.Left)
            }

            gesturesSwipeRightControl.setOnClickListener {
                swipeActionClickEvent(Constants.Swipe.Right)
            }

            miscellaneousSearchEngineControl.setOnClickListener {
                showSearchEngineDialog()
            }

            miscellaneousFilterStrengthControl.setOnClickListener {
                showFilterStrengthDialog()
            }
        }
    }

    private var searchEngineDialog: AlertDialog? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showSearchEngineDialog() {
        // Dismiss any existing dialog to prevent multiple dialogs open simultaneously
        searchEngineDialog?.dismiss()
        // Get the array of SearchEngines enum values
        val items = Constants.SearchEngines.entries.toTypedArray()

        // Map the enum values to their string representations
        val itemStrings = items.map { it.getString(context) }.toTypedArray()

        val dialogBuilder = MaterialAlertDialogBuilder(context).apply {
            setTitle(getString(R.string.settings_select_search_engine))
            setItems(itemStrings) { _, which ->
                val selectedItem = items[which]
                preferenceViewModel.setSearchEngine(selectedItem)
                binding.miscellaneousSearchEngineControl.text = preferenceHelper.searchEngines.name
                val feedbackType = "select"
                appHelper.triggerHapticFeedback(context, feedbackType)
            }
        }

        // Assign the created dialog to launcherFontDialog
        searchEngineDialog = dialogBuilder.create()
        searchEngineDialog?.show()
    }

    private var filterStrengthDialog: AlertDialog? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showFilterStrengthDialog() {
        // Dismiss any existing dialog to prevent multiple dialogs open simultaneously
        filterStrengthDialog?.dismiss()

        var currentValue = preferenceHelper.filterStrength

        // Create a layout to hold the SeekBar and the value display
        val seekBarLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(16, 16, 16, 16)

            // TextView to display the current value
            val valueText = TextView(context).apply {
                text = "$currentValue"
                textSize = 16f
                gravity = Gravity.CENTER
            }

            // SeekBar for horizontal number selection
            val seekBar = SeekBar(context).apply {
                min = Constants.FILTER_STRENGTH_MIN // Maximum value
                max = Constants.FILTER_STRENGTH_MAX // Maximum value
                progress = currentValue // Default value
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        currentValue = progress
                        valueText.text = "$currentValue"
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                        // Not used
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        // Not used
                    }
                })
            }

            // Add TextView and SeekBar to the layout
            addView(valueText)
            addView(seekBar)
        }

        // Create the dialog
        val dialogBuilder = MaterialAlertDialogBuilder(context).apply {
            setTitle(getString(R.string.settings_select_filter_strength))
            setView(seekBarLayout) // Add the slider directly to the dialog
            setPositiveButton("ok") { _, _ ->
                // Save the slider value when OK is pressed
                preferenceViewModel.setFilterStrength(currentValue)
                binding.miscellaneousFilterStrengthControl.text = "$currentValue"

                val feedbackType = "select"
                appHelper.triggerHapticFeedback(context, feedbackType)
            }
            setNegativeButton("cancel", null)
        }

        // Assign the created dialog to launcherFontDialog
        filterStrengthDialog = dialogBuilder.create()
        filterStrengthDialog?.show()
    }

    private var appSelectionDialog: AlertDialog? = null

    @RequiresApi(Build.VERSION_CODES.Q)
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
                val dialogBuilder = MaterialAlertDialogBuilder(context).apply {
                    setTitle("Select an App")
                    setItems(appNames) { _, which ->
                        val selectedPackageName = packageNames[which]
                        when (swipeType) {
                            Constants.Swipe.DoubleTap,
                            Constants.Swipe.Up,
                            Constants.Swipe.Down,
                            Constants.Swipe.Left,
                            Constants.Swipe.Right -> handleSwipeAction(swipeType, selectedPackageName)
                        }
                        val feedbackType = "select"
                        appHelper.triggerHapticFeedback(context, feedbackType)
                    }
                }

                // Assign the created dialog to launcherFontDialog
                appSelectionDialog = dialogBuilder.create()
                appSelectionDialog?.show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setupSwitchListeners() {
        binding.apply {
            automaticKeyboardSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
                preferenceViewModel.setAutoKeyboard(isChecked)
                val feedbackType = if (isChecked) "short" else "long"
                appHelper.triggerHapticFeedback(context, feedbackType)
            }

            automaticOpenAppSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
                preferenceViewModel.setAutoOpenApp(isChecked)
                val feedbackType = if (isChecked) "on" else "off"
                appHelper.triggerHapticFeedback(context, feedbackType)
            }

            searchFromStartSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
                preferenceViewModel.setSearchFromStart(isChecked)
                val feedbackType = if (isChecked) "on" else "off"
                appHelper.triggerHapticFeedback(context, feedbackType)
            }

            lockSettingsSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
                preferenceViewModel.setLockSettings(isChecked)
                val feedbackType = if (isChecked) "on" else "off"
                appHelper.triggerHapticFeedback(context, feedbackType)
            }
        }

    }

    private var swipeActionDialog: AlertDialog? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun swipeActionClickEvent(swipe: Constants.Swipe) {
        // Dismiss any existing dialog to prevent multiple dialogs open simultaneously
        swipeActionDialog?.dismiss()
        // Get the array of Action enum values
        val actions = Constants.Action.entries.toTypedArray()
        // Map the enum values to their string representations
        val actionStrings = actions.map { it.getString(context) }.toTypedArray()

        val dialogBuilder = MaterialAlertDialogBuilder(context).apply {
            setTitle("Select a Action")
            setItems(actionStrings) { _, which ->
                val selectedAction = actions[which]
                when (swipe) {
                    Constants.Swipe.DoubleTap -> handleSwipeAction(context, Constants.Swipe.DoubleTap, selectedAction, binding)
                    Constants.Swipe.Up -> handleSwipeAction(context, Constants.Swipe.Up, selectedAction, binding)
                    Constants.Swipe.Down -> handleSwipeAction(context, Constants.Swipe.Down, selectedAction, binding)
                    Constants.Swipe.Left -> handleSwipeAction(context, Constants.Swipe.Left, selectedAction, binding)
                    Constants.Swipe.Right -> handleSwipeAction(context, Constants.Swipe.Right, selectedAction, binding)
                }
                val feedbackType = "select"
                appHelper.triggerHapticFeedback(context, feedbackType)
            }
        }

        // Assign the created dialog to launcherFontDialog
        swipeActionDialog = dialogBuilder.create()
        swipeActionDialog?.show()
    }

    private fun handleSwipeAction(swipeType: Constants.Swipe, selectedPackageName: String) {
        val selectedApp = context.getAppNameFromPackageName(selectedPackageName)

        when (swipeType) {
            Constants.Swipe.DoubleTap -> {
                binding.gesturesDoubleTapControl.text = getString(R.string.settings_actions_open_app_run, selectedApp)
                preferenceHelper.doubleTapApp = selectedPackageName
            }

            Constants.Swipe.Up -> {
                binding.gesturesSwipeUpControl.text = getString(R.string.settings_actions_open_app_run, selectedApp)
                preferenceHelper.swipeUpApp = selectedPackageName
            }

            Constants.Swipe.Down -> {
                binding.gesturesSwipeDownControl.text = getString(R.string.settings_actions_open_app_run, selectedApp)
                preferenceHelper.swipeDownApp = selectedPackageName
            }

            Constants.Swipe.Left -> {
                binding.gesturesSwipeLeftControl.text = getString(R.string.settings_actions_open_app_run, selectedApp)
                preferenceHelper.swipeLeftApp = selectedPackageName
            }

            Constants.Swipe.Right -> {
                binding.gesturesSwipeRightControl.text = getString(R.string.settings_actions_open_app_run, selectedApp)
                preferenceHelper.swipeRightApp = selectedPackageName
            }
        }
    }

    // Function to handle setting action and updating UI
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun handleSwipeAction(context: Context, swipe: Constants.Swipe, action: Constants.Action, binding: FragmentSettingsFeaturesBinding) {
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
                        Constants.Swipe.DoubleTap -> gesturesDoubleTapControl.text = getString(R.string.settings_actions_open_app_run, selectedApp)
                        Constants.Swipe.Up -> gesturesSwipeUpControl.text = getString(R.string.settings_actions_open_app_run, selectedApp)
                        Constants.Swipe.Down -> gesturesSwipeDownControl.text = getString(R.string.settings_actions_open_app_run, selectedApp)
                        Constants.Swipe.Left -> gesturesSwipeLeftControl.text = getString(R.string.settings_actions_open_app_run, selectedApp)
                        Constants.Swipe.Right -> gesturesSwipeRightControl.text = getString(R.string.settings_actions_open_app_run, selectedApp)
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

    private fun dismissDialogs() {
        swipeActionDialog?.dismiss()
        searchEngineDialog?.dismiss()
        filterStrengthDialog?.dismiss()
        appSelectionDialog?.dismiss()
    }
}