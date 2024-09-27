package com.github.droidworksstudio.launcher.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.adapter.font.FontAdapter
import com.github.droidworksstudio.launcher.databinding.FragmentSettingsLookFeelBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.AppReloader
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.ScrollEventListener
import com.github.droidworksstudio.launcher.ui.bottomsheetdialog.AlignmentBottomSheetDialogFragment
import com.github.droidworksstudio.launcher.ui.bottomsheetdialog.ColorBottomSheetDialogFragment
import com.github.droidworksstudio.launcher.ui.bottomsheetdialog.PaddingBottomSheetDialogFragment
import com.github.droidworksstudio.launcher.ui.bottomsheetdialog.TextBottomSheetDialogFragment
import com.github.droidworksstudio.launcher.utils.Constants
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsLookFeelFragment : Fragment(),
    ScrollEventListener {

    private var _binding: FragmentSettingsLookFeelBinding? = null
    private val binding get() = _binding!!

    private val preferenceViewModel: PreferenceViewModel by viewModels()

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appHelper: AppHelper

    private lateinit var navController: NavController

    private lateinit var context: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsLookFeelBinding.inflate(inflater, container, false)
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

        binding.apply {
            miscellaneousLauncherFontsControl.text = preferenceHelper.launcherFont.getString(context)
        }
    }

    override fun onStop() {
        super.onStop()
        dismissDialogs()
    }

    private fun initializeInjectedDependencies() {
        binding.nestScrollView.scrollEventListener = this

        // Set initial values and listeners for switches
        binding.apply {
            statueBarSwitchCompat.isChecked = preferenceHelper.showStatusBar
            timeSwitchCompat.isChecked = preferenceHelper.showTime
            dateSwitchCompat.isChecked = preferenceHelper.showDate
            batterySwitchCompat.isChecked = preferenceHelper.showBattery
            dailyWordSwitchCompat.isChecked = preferenceHelper.showDailyWord
            appIconsSwitchCompat.isChecked = preferenceHelper.showAppIcon
            appIconDotsSwitchCompat.isChecked = preferenceHelper.showAppIconAsDots
        }

        if (!binding.appIconsSwitchCompat.isChecked) {
            // Disable and gray out the other setting if appIconsSwitchCompat is checked
            binding.appIconDotsSwitchCompat.apply {
                isEnabled = binding.appIconsSwitchCompat.isChecked
                isChecked = false
            }
        }
    }

    private fun observeClickListener() {
        binding.apply {
            selectAppearanceTextSize.setOnClickListener {
                val bottomSheetFragment = TextBottomSheetDialogFragment()
                bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
            }

            selectAppearanceColor.setOnClickListener {
                val bottomSheetFragment = ColorBottomSheetDialogFragment()
                bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
            }

            selectAppearanceAlignment.setOnClickListener {
                val bottomSheetFragment = AlignmentBottomSheetDialogFragment()
                bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
            }

            selectAppearancePadding.setOnClickListener {
                val bottomSheetFragment = PaddingBottomSheetDialogFragment()
                bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
            }

            setAppWallpaper.setOnClickListener {
                val intent = Intent(Intent.ACTION_SET_WALLPAPER)
                startActivity(Intent.createChooser(intent, "Select Wallpaper"))
            }

            miscellaneousLauncherFontsControl.setOnClickListener {
                showLauncherFontDialog()
            }
        }
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

    private fun dismissDialogs() {
        launcherFontDialog?.dismiss()
    }
}