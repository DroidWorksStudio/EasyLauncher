package com.github.droidworksstudio.launcher.ui.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.databinding.FragmentSettingsBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
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

    //private val viewModel: AppViewModel by viewModels()
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
        binding.gesturesLockSwitchCompat1.isChecked = preferenceHelper.tapLockScreen
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
            val bottomSheetFragment = TextBottomSheetDialogFragment(this.requireContext())
            bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
        }

        binding.selectAppearanceAlignment.setOnClickListener {
            val bottomSheetFragment = AlignmentBottomSheetDialogFragment(this.requireContext())
            bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
        }

        binding.selectAppearancePadding.setOnClickListener {
            val bottomSheetFragment = PaddingBottomSheetDialogFragment(this.requireContext())
            bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
        }

        binding.selectAppearanceColor.setOnClickListener {
            val bottomSheetFragment = ColorBottomSheetDialogFragment(this.requireContext())
            bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
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

        binding.gesturesLockSwitchCompat1.setOnCheckedChangeListener { _, isChecked ->
            appHelper.enableAppAsAccessibilityService(requireContext(), preferenceHelper.tapLockScreen)
            preferenceViewModel.setDoubleTapLock(isChecked)
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