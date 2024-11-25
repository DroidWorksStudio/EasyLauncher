package com.github.droidworksstudio.launcher.ui.home

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.droidworksstudio.common.hideKeyboard
import com.github.droidworksstudio.common.isPackageInstalled
import com.github.droidworksstudio.common.launchApp
import com.github.droidworksstudio.common.launchCalendar
import com.github.droidworksstudio.common.launchClock
import com.github.droidworksstudio.common.openBatteryManager
import com.github.droidworksstudio.common.showLongToast
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.accessibility.ActionService
import com.github.droidworksstudio.launcher.adapter.home.HomeAdapter
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.databinding.FragmentHomeBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.BiometricHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnItemClickedListener
import com.github.droidworksstudio.launcher.listener.OnSwipeTouchListener
import com.github.droidworksstudio.launcher.listener.ScrollEventListener
import com.github.droidworksstudio.launcher.ui.bottomsheetdialog.AppInfoBottomSheetFragment
import com.github.droidworksstudio.launcher.utils.Constants
import com.github.droidworksstudio.launcher.viewmodel.AppViewModel
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class HomeFragment : Fragment(),
    OnItemClickedListener.OnAppsClickedListener,
    OnItemClickedListener.OnAppLongClickedListener,
    OnItemClickedListener.BottomSheetDismissListener,
    OnItemClickedListener.OnAppStateClickListener,
    BiometricHelper.Callback, ScrollEventListener {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appHelper: AppHelper


    @Inject
    lateinit var fingerHelper: BiometricHelper

    private val viewModel: AppViewModel by viewModels()
    private val preferenceViewModel: PreferenceViewModel by viewModels()

    private val homeAdapter: HomeAdapter by lazy { HomeAdapter(this, this, preferenceHelper) }

    private lateinit var batteryReceiver: BroadcastReceiver
    private lateinit var biometricPrompt: BiometricPrompt

    private lateinit var context: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeInjectedDependencies()
        setupBattery()
        setupRecyclerView()
        observeSwipeTouchListener()
        observeUserInterfaceSettings()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeInjectedDependencies() {
        context = requireContext()
        binding.nestScrollView.hideKeyboard()

        binding.nestScrollView.scrollEventListener = this

        binding.nestScrollView.registerRecyclerView(binding.appListAdapter, this)

        preferenceViewModel.setShowTime(preferenceHelper.showTime)
        preferenceViewModel.setShowDate(preferenceHelper.showDate)
        preferenceViewModel.setShowAlarmClock(preferenceHelper.showAlarmClock)
        preferenceViewModel.setShowDailyWord(preferenceHelper.showDailyWord)
    }

    private fun setupBattery() {
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

                // Get the TextView by its ID
                val batteryTextView: AppCompatTextView = binding.battery

                val batteryLevel = level * 100 / scale.toFloat()

                val batteryDrawable = when {
                    batteryLevel >= 76 -> ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.app_battery100
                    )

                    batteryLevel >= 51 -> ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.app_battery75
                    )

                    batteryLevel >= 26 -> ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.app_battery50
                    )

                    else -> ContextCompat.getDrawable(requireContext(), R.drawable.app_battery25)
                }

                batteryDrawable?.let {
                    // Resize the drawable to match the text size
                    val textSize = batteryTextView.textSize.toInt()
                    if (preferenceHelper.showBattery) {
                        it.setBounds(0, 0, textSize, textSize)
                        batteryTextView.setCompoundDrawables(it, null, null, null)
                    } else {
                        it.setBounds(0, 0, 0, 0)
                        batteryTextView.setCompoundDrawables(null, null, null, null)
                    }
                }

                val batteryLevelText = getString(R.string.battery_level, batteryLevel.toString())
                binding.battery.text = batteryLevelText
            }
        }

        val batteryIntentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        requireActivity().registerReceiver(batteryReceiver, batteryIntentFilter)
    }

    private fun setupRecyclerView() {
        val marginInPixels = 128

        // Ensure correct type for layout params
        val layoutParams = (binding.appListAdapter.layoutParams as? LinearLayout.LayoutParams)
            ?: LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        // Set the bottom margin instead of top
        layoutParams.bottomMargin = marginInPixels
        layoutParams.topMargin = marginInPixels

        // Set gravity to align RecyclerView to the bottom
        layoutParams.gravity = when (preferenceHelper.homeAppAlignment) {
            Gravity.START -> Gravity.START or Gravity.BOTTOM
            Gravity.CENTER -> Gravity.CENTER or Gravity.BOTTOM
            Gravity.END -> Gravity.END or Gravity.BOTTOM
            else -> Gravity.BOTTOM
        }

        // Apply configurations to RecyclerView
        binding.appListAdapter.apply {
            adapter = homeAdapter
            this.layoutParams = layoutParams
            setHasFixedSize(false)
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            isNestedScrollingEnabled = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun observeFavoriteAppList() {
        viewModel.compareInstalledAppInfo()

        @Suppress("DEPRECATION")
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.favoriteApps.flowOn(Dispatchers.Main).collect {
                homeAdapter.submitList(it)
                homeAdapter.updateDataWithStateFlow(it)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun observeSwipeTouchListener() {
        binding.apply {
            touchArea.setOnTouchListener(getSwipeGestureListener(context))
            nestScrollView.setOnTouchListener(getSwipeGestureListener(context))
            appListTouchArea.setOnTouchListener(getSwipeGestureListener(context))

            clock.setOnClickListener { context.launchClock() }
            date.setOnClickListener { context.launchCalendar() }
            battery.setOnClickListener { context.openBatteryManager() }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun observeUserInterfaceSettings() {
        preferenceViewModel.setShowTime(preferenceHelper.showTime)
        preferenceViewModel.setShowDate(preferenceHelper.showDate)
        preferenceViewModel.setShowAlarmClock(preferenceHelper.showAlarmClock)
        preferenceViewModel.setShowDailyWord(preferenceHelper.showDailyWord)
        preferenceViewModel.setShowBattery(preferenceHelper.showBattery)

        preferenceViewModel.showTimeLiveData.observe(viewLifecycleOwner) {
            appHelper.updateUI(
                binding.clock,
                preferenceHelper.homeTimeAlignment,
                preferenceHelper.timeColor,
                preferenceHelper.timeTextSize,
                preferenceHelper.showTime
            )
        }

        preferenceViewModel.showDateLiveData.observe(viewLifecycleOwner) {
            appHelper.updateUI(
                binding.date,
                preferenceHelper.homeDateAlignment,
                preferenceHelper.dateColor,
                preferenceHelper.dateTextSize,
                preferenceHelper.showDate
            )
        }
        preferenceViewModel.showBatteryLiveData.observe(viewLifecycleOwner) {
            appHelper.updateUI(
                binding.battery,
                Gravity.END,
                preferenceHelper.batteryColor,
                preferenceHelper.batteryTextSize,
                preferenceHelper.showBattery
            )
        }

        preferenceViewModel.showDailyWordLiveData.observe(viewLifecycleOwner) {
            appHelper.updateUI(
                binding.word,
                preferenceHelper.homeDailyWordAlignment,
                preferenceHelper.dailyWordColor,
                preferenceHelper.dailyWordTextSize,
                preferenceHelper.showDailyWord
            )
        }

        preferenceViewModel.showDailyWordLiveData.observe(viewLifecycleOwner) {
            appHelper.updateUI(
                binding.alarm,
                preferenceHelper.homeAlarmClockAlignment,
                preferenceHelper.alarmClockColor,
                preferenceHelper.alarmClockTextSize,
                preferenceHelper.showAlarmClock
            )
        }

        binding.apply {
            nestScrollView.hideKeyboard()

            val is24HourFormat = DateFormat.is24HourFormat(requireContext())
            val localLocale = Locale.getDefault()
            val best12 = DateFormat.getBestDateTimePattern(localLocale, "hmma")
            val best24 = DateFormat.getBestDateTimePattern(localLocale, "HHmm")

            val timePattern = if (is24HourFormat) best24 else best12
            clock.format12Hour = timePattern
            clock.format24Hour = timePattern
            val datePattern = DateFormat.getBestDateTimePattern(localLocale, "eeeddMMM")
            date.format12Hour = datePattern
            date.format24Hour = datePattern

            alarm.text = appHelper.getNextAlarm(context, preferenceHelper)
            word.text = appHelper.wordOfTheDay(resources)
        }
    }

    private fun observeBioAuthCheck(appInfo: AppInfo) {
        if (!appInfo.lock)
            context.launchApp(appInfo)
        else
            fingerHelper.startBiometricAuth(appInfo, this)
    }

    private fun showSelectedApp(appInfo: AppInfo) {
        val bottomSheetFragment = AppInfoBottomSheetFragment(appInfo)
        bottomSheetFragment.setOnBottomSheetDismissedListener(this)
        bottomSheetFragment.setOnAppStateClickListener(this)
        bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")

    }

    private fun getSwipeGestureListener(context: Context): View.OnTouchListener {
        return object : OnSwipeTouchListener(context) {
            override fun onLongClick() {
                super.onLongClick()
                trySettings()
                return
            }

            @RequiresApi(Build.VERSION_CODES.P)
            override fun onDoubleClick() {
                super.onDoubleClick()
                handleOtherAction(preferenceHelper.doubleTapAction, Constants.Swipe.DoubleTap)
            }

            @RequiresApi(Build.VERSION_CODES.P)
            override fun onSwipeUp() {
                super.onSwipeUp()
                handleOtherAction(preferenceHelper.swipeUpAction, Constants.Swipe.Up)
            }

            @RequiresApi(Build.VERSION_CODES.P)
            override fun onSwipeDown() {
                super.onSwipeDown()
                handleOtherAction(preferenceHelper.swipeDownAction, Constants.Swipe.Down)
            }

            @RequiresApi(Build.VERSION_CODES.P)
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                handleOtherAction(preferenceHelper.swipeLeftAction, Constants.Swipe.Left)
            }

            @RequiresApi(Build.VERSION_CODES.P)
            override fun onSwipeRight() {
                super.onSwipeRight()
                handleOtherAction(preferenceHelper.swipeRightAction, Constants.Swipe.Right)
            }
        }
    }

    private fun openApp(packageName: String) {
        val context = binding.root.context
        val pm: PackageManager = context.packageManager
        val intent = pm.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            context.startActivity(intent)
        } else {
            Log.e("HomeViewHolder", "Unable to find app with package name: $packageName")
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun handleOtherAction(action: Constants.Action, actionType: Constants.Swipe) {
        when (action) {
            Constants.Action.OpenApp -> {
                when (actionType) {
                    Constants.Swipe.DoubleTap,
                    Constants.Swipe.Up,
                    Constants.Swipe.Down,
                    Constants.Swipe.Left,
                    Constants.Swipe.Right -> {
                        val packageName = when (actionType) {
                            Constants.Swipe.DoubleTap -> preferenceHelper.doubleTapApp
                            Constants.Swipe.Up -> preferenceHelper.swipeUpApp
                            Constants.Swipe.Down -> preferenceHelper.swipeDownApp
                            Constants.Swipe.Left -> preferenceHelper.swipeLeftApp
                            Constants.Swipe.Right -> preferenceHelper.swipeRightApp
                        }

                        if (packageName.isNotEmpty()) {
                            if (context.isPackageInstalled(packageName)) {
                                openApp(packageName)
                            } else {
                                Log.e("HomeViewHolder", "App $packageName is not installed")
                                context.showLongToast("App $packageName is not installed")
                            }
                        } else {
                            Log.e("HomeViewHolder", "No package name found in preferences")
                        }
                    }
                }
            }

            Constants.Action.LockScreen -> {
                ActionService.runAccessibilityMode(context)
                ActionService.instance()?.lockScreen()
            }

            Constants.Action.ShowNotification -> {
                appHelper.expandNotificationDrawer(context)
            }

            Constants.Action.ShowAppList -> {
                val actionTypeNavOptions: NavOptions = appHelper.getActionType(actionType)
                Handler(Looper.getMainLooper()).post {
                    findNavController().navigate(
                        R.id.action_HomeFragment_to_DrawFragment,
                        null,
                        actionTypeNavOptions
                    )
                }
            }


            Constants.Action.ShowFavoriteList -> {
                val actionTypeNavOptions: NavOptions = appHelper.getActionType(actionType)
                Handler(Looper.getMainLooper()).post {
                    findNavController().navigate(
                        R.id.action_HomeFragment_to_FavoriteFragment,
                        null,
                        actionTypeNavOptions
                    )
                }
            }

            Constants.Action.ShowHiddenList -> {
                val actionTypeNavOptions: NavOptions = appHelper.getActionType(actionType)
                Handler(Looper.getMainLooper()).post {
                    findNavController().navigate(
                        R.id.action_HomeFragment_to_HiddenFragment,
                        null,
                        actionTypeNavOptions
                    )
                }
            }

            Constants.Action.OpenQuickSettings -> {
                appHelper.expandQuickSettings(context)
            }

            Constants.Action.ShowRecents -> {
                ActionService.runAccessibilityMode(context)
                ActionService.instance()?.showRecents()
            }

            Constants.Action.ShowWidgets -> {
                val actionTypeNavOptions: NavOptions = appHelper.getActionType(actionType)
                Handler(Looper.getMainLooper()).post {
                    findNavController().navigate(
                        R.id.action_HomeFragment_to_WidgetsFragment,
                        null,
                        actionTypeNavOptions
                    )
                }
            }

            Constants.Action.OpenPowerDialog -> {
                ActionService.runAccessibilityMode(context)
                ActionService.instance()?.openPowerDialog()
            }

            Constants.Action.TakeScreenShot -> {
                ActionService.runAccessibilityMode(context)
                ActionService.instance()?.takeScreenShot()
            }

            Constants.Action.Disabled -> {}
        }
    }

    private fun trySettings() {
        lifecycleScope.launch(Dispatchers.Main) {
            biometricPrompt = BiometricPrompt(this@HomeFragment,
                ContextCompat.getMainExecutor(requireContext()),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        when (errorCode) {
                            BiometricPrompt.ERROR_USER_CANCELED -> requireContext().showLongToast(
                                getString(R.string.authentication_cancel)
                            )

                            else -> requireContext().showLongToast(
                                getString(R.string.authentication_error).format(
                                    errString,
                                    errorCode
                                )
                            )
                        }
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        val actionTypeNavOptions: NavOptions =
                            appHelper.getActionType(Constants.Swipe.DoubleTap)
                        findNavController().navigate(
                            R.id.action_HomeFragment_to_SettingsFragment,
                            null,
                            actionTypeNavOptions
                        )
                    }

                    override fun onAuthenticationFailed() {
                        requireContext().showLongToast(getString(R.string.authentication_failed))
                    }
                })

            if (preferenceHelper.settingsLock) {
                fingerHelper.startBiometricSettingsAuth(R.id.action_HomeFragment_to_SettingsFragment)
            } else {
                val actionTypeNavOptions: NavOptions =
                    appHelper.getActionType(Constants.Swipe.DoubleTap)
                findNavController().navigate(
                    R.id.action_HomeFragment_to_SettingsFragment,
                    null,
                    actionTypeNavOptions
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        requireActivity().unregisterReceiver(batteryReceiver)
    }

    override fun onPause() {
        super.onPause()
        binding.nestScrollView.hideKeyboard()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        binding.nestScrollView.hideKeyboard()
        observeUserInterfaceSettings()
        observeFavoriteAppList()
    }

    override fun onAppClicked(appInfo: AppInfo) {
        observeBioAuthCheck(appInfo)
    }

    override fun onAppLongClicked(appInfo: AppInfo) {
        showSelectedApp(appInfo)
    }

    override fun onAppStateClicked(appInfo: AppInfo) {
        viewModel.update(appInfo)
    }

    override fun onAuthenticationSucceeded(appInfo: AppInfo) {
        context.launchApp(appInfo)
        requireContext().showLongToast(getString(R.string.authentication_succeeded))
    }

    override fun onAuthenticationFailed() {
        requireContext().showLongToast(getString(R.string.authentication_failed))
    }

    override fun onAuthenticationError(errorCode: Int, errorMessage: CharSequence?) {
        when (errorCode) {
            BiometricPrompt.ERROR_USER_CANCELED -> requireContext().showLongToast(getString(R.string.authentication_cancel))

            else -> requireContext().showLongToast(
                getString(R.string.authentication_error).format(
                    errorMessage,
                    errorCode
                )
            )
        }
    }
}