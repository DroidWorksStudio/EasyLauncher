package com.github.droidworksstudio.launcher.ui.home

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.accessibility.MyAccessibilityService
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.databinding.FragmentHomeBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.FingerprintHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.helper.hideKeyboard
import com.github.droidworksstudio.launcher.listener.OnItemClickedListener
import com.github.droidworksstudio.launcher.listener.OnSwipeTouchListener
import com.github.droidworksstudio.launcher.listener.ScrollEventListener
import com.github.droidworksstudio.launcher.ui.activities.SettingsActivity
import com.github.droidworksstudio.launcher.ui.bottomsheetdialog.AppInfoBottomSheetFragment
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
class HomeFragment : Fragment(), OnItemClickedListener.OnAppsClickedListener,
    OnItemClickedListener.OnAppLongClickedListener,
    OnItemClickedListener.BottomSheetDismissListener,
    OnItemClickedListener.OnAppStateClickListener,
    FingerprintHelper.Callback, ScrollEventListener {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appHelper: AppHelper

    @Inject
    lateinit var fingerHelper: FingerprintHelper

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
        initSwipeTouchListener()
        setupBattery()
        setupRecyclerView()
        observeUserInterfaceSettings()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeInjectedDependencies() {
        context = requireContext()
        binding.mainView.hideKeyboard()

        binding.nestScrollView.scrollEventListener = this

        binding.nestScrollView.registerRecyclerView(binding.appListAdapter, this)

        preferenceViewModel.setShowTime(preferenceHelper.showTime)
        preferenceViewModel.setShowDate(preferenceHelper.showDate)
        preferenceViewModel.setShowDailyWord(preferenceHelper.showDailyWord)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSwipeTouchListener() {
        binding.nestScrollView.setOnTouchListener(getSwipeGestureListener(context))

        binding.clock.setOnClickListener { appHelper.launchClock(context) }
        binding.date.setOnClickListener { appHelper.launchCalendar(context) }
        binding.battery.setOnClickListener { appHelper.openBatteryManager(context) }
    }

    private fun setupBattery() {
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

                val batteryLevel = level * 100 / scale.toFloat()
                val batteryLevelText = getString(R.string.battery_level, batteryLevel.toString())
                binding.battery.text = batteryLevelText
            }
        }

        val batteryIntentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        requireActivity().registerReceiver(batteryReceiver, batteryIntentFilter)
    }

    private fun setupRecyclerView() {
        val marginTopInPixels = 128
        val params: ViewGroup.MarginLayoutParams =
            binding.appListAdapter.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = marginTopInPixels

        binding.appListAdapter.apply {
            adapter = homeAdapter
            layoutParams = params
            setHasFixedSize(false)
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            isNestedScrollingEnabled = false
        }
    }

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

    private fun observeUserInterfaceSettings() {
        binding.mainView.hideKeyboard()

        preferenceViewModel.setShowTime(preferenceHelper.showTime)
        preferenceViewModel.setShowDate(preferenceHelper.showDate)
        preferenceViewModel.setShowDailyWord(preferenceHelper.showDailyWord)
        preferenceViewModel.setShowBattery(preferenceHelper.showBattery)

        preferenceViewModel.showTimeLiveData.observe(viewLifecycleOwner) {
            Log.d("Tag", "ShowTime Home: $it")
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
        val is24HourFormat = DateFormat.is24HourFormat(requireContext())
        val localLocale = Locale.getDefault()
        val best12 = DateFormat.getBestDateTimePattern(localLocale, "hmma")
        val best24 = DateFormat.getBestDateTimePattern(localLocale, "HHmm")

        val timePattern = if (is24HourFormat) best24 else best12
        binding.clock.format12Hour = timePattern
        binding.clock.format24Hour = timePattern
        val datePattern = DateFormat.getBestDateTimePattern(localLocale, "eeeddMMM")
        binding.date.format12Hour = datePattern
        binding.date.format24Hour = datePattern

        binding.word.text = appHelper.wordOfTheDay(resources)
    }

    private fun observeBioAuthCheck(appInfo: AppInfo) {
        if (!appInfo.lock) appHelper.launchApp(
            context,
            appInfo
        ) else fingerHelper.startFingerprintAuth(appInfo, this)
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
                if (preferenceHelper.tapLockScreen) {
                    MyAccessibilityService.runAccessibilityMode(context)
                    MyAccessibilityService.instance()?.lockScreen()
                } else {
                    return
                }
            }

            override fun onSwipeLeft() {
                super.onSwipeLeft()
                findNavController().navigate(R.id.action_HomeFragment_to_DrawFragment)
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                findNavController().navigate(R.id.action_HomeFragment_to_FavoriteFragment)

            }

            override fun onSwipeDown() {
                super.onSwipeDown()
                if (preferenceHelper.swipeNotification) appHelper.expandNotificationDrawer(context)
            }

            override fun onSwipeUp() {
                super.onSwipeUp()
                if (preferenceHelper.swipeSearch) appHelper.searchView(context)
            }
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
                            BiometricPrompt.ERROR_USER_CANCELED -> appHelper.showToast(
                                requireContext(),
                                getString(R.string.authentication_cancel)
                            )

                            else -> appHelper.showToast(
                                requireContext(),
                                getString(R.string.authentication_error).format(
                                    errString,
                                    errorCode
                                )
                            )
                        }
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        findNavController().navigate(R.id.action_HomeFragment_to_SettingsFragment)
                    }

                    override fun onAuthenticationFailed() {
                        appHelper.showToast(
                            requireContext(),
                            getString(R.string.authentication_failed)
                        )
                    }
                })

            if (preferenceHelper.settingsLock) {
                fingerHelper.startFingerprintSettingsAuth(SettingsActivity::class.java)
            } else {
                findNavController().navigate(R.id.action_HomeFragment_to_SettingsFragment)
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
        binding.mainView.hideKeyboard()
    }

    override fun onResume() {
        super.onResume()
        binding.mainView.hideKeyboard()
        observeUserInterfaceSettings()
        observeFavoriteAppList()
    }

    override fun onAppClicked(appInfo: AppInfo) {
        observeBioAuthCheck(appInfo)
    }

    override fun onAppLongClicked(appInfo: AppInfo) {
        showSelectedApp(appInfo)
        Log.d("Tag", "Home LiveData Favorite : ${appInfo.favorite}")
    }

    override fun onAppStateClicked(appInfo: AppInfo) {
        viewModel.update(appInfo)
        Log.d("Tag", "${appInfo.appName} : Home Favorite: ${appInfo.favorite}")
    }

    override fun onAuthenticationSucceeded(appInfo: AppInfo) {
        appHelper.launchApp(context, appInfo)
        appHelper.showToast(context, getString(R.string.authentication_succeeded))
    }

    override fun onAuthenticationFailed() {
        appHelper.showToast(context, getString(R.string.authentication_failed))
    }

    override fun onAuthenticationError(errorCode: Int, errorMessage: CharSequence?) {
        when (errorCode) {
            BiometricPrompt.ERROR_USER_CANCELED -> appHelper.showToast(
                requireContext(),
                getString(R.string.authentication_cancel)
            )

            else -> appHelper.showToast(
                requireContext(),
                getString(R.string.authentication_error).format(errorMessage, errorCode)
            )
        }
    }
}