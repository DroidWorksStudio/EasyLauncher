package com.github.droidworksstudio.launcher.ui.settings

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.databinding.FragmentSettingsBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.ScrollEventListener
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
import dagger.hilt.android.AndroidEntryPoint
import net.mm2d.color.chooser.ColorChooserDialog
import net.mm2d.color.chooser.ColorChooserDialog.TAB_HSV
import net.mm2d.color.chooser.ColorChooserDialog.TAB_PALETTE
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

    private var color: Int = Color.WHITE

    private lateinit var navController: NavController

    private var selectedAlignment: String = ""

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
        super.onViewCreated(view, savedInstanceState)

        initializeInjectedDependencies()
        observeView()
        observeClickListener()
    }

    private fun initializeInjectedDependencies() {
        navController = findNavController()
        binding.nestScrollView.scrollEventListener = this

        // Set initial values and listeners for switches
        binding.statueBarSwitchCompat.isChecked = preferenceHelper.showStatusBar
        binding.timeSwitchCompat.isChecked = preferenceHelper.showTime
        binding.dateSwitchCompat.isChecked = preferenceHelper.showDate
        binding.batterySwitchCompat.isChecked = preferenceHelper.showBattery
        binding.gesturesLockSwitchCompat1.isChecked = preferenceHelper.tapLockScreen
    }

    private fun observeView() {
        // Set background colors to text
        binding.dateColor.apply {
            text = getColorText(preferenceHelper.dateColor)
            setTextColor(preferenceHelper.dateColor)
        }

        binding.timeColor.apply {
            text = getColorText(preferenceHelper.timeColor)
            setTextColor(preferenceHelper.timeColor)
        }

        binding.batteryDisplayColor.apply {
            text = getColorText(preferenceHelper.batteryColor)
            setTextColor(preferenceHelper.batteryColor)
        }

        binding.appDisplayColor.apply {
            text = getColorText(preferenceHelper.appColor)
            setTextColor(preferenceHelper.appColor)
        }

        binding.dateAlignment.apply {
            text = appHelper.gravityToString(preferenceHelper.homeDateAlignment)
        }

        binding.timeAlignment.apply {
            text = appHelper.gravityToString(preferenceHelper.homeTimeAlignment)
        }

        binding.homeAppAlignment.apply {
            text = appHelper.gravityToString(preferenceHelper.homeAppAlignment)
        }
    }

    private fun observeClickListener() {
        setupSwitchListeners()
        setupColorPickers()
        setupAlignmentSelections()

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
        binding.gesturesLockSwitchCompat1.setOnCheckedChangeListener { _, isChecked ->
            appHelper.enableAppAsAccessibilityService(requireContext(), preferenceHelper.tapLockScreen)
            preferenceViewModel.setDoubleTapLock(isChecked)
        }
    }

    private fun setupColorPickers() {
        binding.selectDateColor.setOnClickListener {
            showColorPickerDialog(
                binding.dateColor,
                REQUEST_KEY_DATE_COLOR,
                preferenceHelper.dateColor
            )
        }

        binding.selectTimeColor.setOnClickListener {
            showColorPickerDialog(
                binding.timeColor,
                REQUEST_KEY_TIME_COLOR,
                preferenceHelper.timeColor
            )
        }

        binding.selectAppColor.setOnClickListener {
            showColorPickerDialog(
                binding.appDisplayColor,
                REQUEST_KEY_APP_COLOR,
                preferenceHelper.appColor
            )
        }
        binding.selectBatteryColor.setOnClickListener {
            showColorPickerDialog(
                binding.batteryDisplayColor,
                REQUEST_KEY_BATTERY_COLOR,
                preferenceHelper.batteryColor
            )
        }
    }

    private fun setupAlignmentSelections() {
        binding.selectDateAlignment.setOnClickListener {
            selectedAlignment = "DateAlignment"
            showListDialog(selectedAlignment)
        }

        binding.selectTimeAlignment.setOnClickListener {
            selectedAlignment = "TimeAlignment"
            showListDialog(selectedAlignment)
        }

        binding.selectHomeAppAlignment.setOnClickListener {
            selectedAlignment = "HomeAppAlignment"
            showListDialog(selectedAlignment)
        }
    }

    private fun showListDialog(selectedAlignment: String) {
        val items = resources.getStringArray(R.array.alignment_options).toList()

        val builder = MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialogTheme)

        builder.setTitle("Select Alignment")
        builder.setItems(items.toTypedArray()) { _, which ->
            val selectedItem = items[which]
            val gravity = appHelper.getGravityFromSelectedItem(selectedItem)

            when (selectedAlignment) {
                "HomeAppAlignment" -> {
                    setAlignment(selectedAlignment, selectedItem, gravity, binding.homeAppAlignment)
                }

                "TimeAlignment" -> {
                    setAlignment(selectedAlignment, selectedItem, gravity, binding.timeAlignment)
                }

                "DateAlignment" -> {
                    setAlignment(selectedAlignment, selectedItem, gravity, binding.dateAlignment)
                }
            }
        }
        builder.show()
    }

    private fun setAlignment(
        selectedAlignment: String,
        selectedItem: String,
        gravity: Int,
        textView: TextView
    ) {
        when (selectedAlignment) {
            "HomeAppAlignment" -> {
                preferenceViewModel.setHomeAppAlignment(gravity)
                textView.text = appHelper.gravityToString(preferenceHelper.homeAppAlignment)
            }

            "TimeAlignment" -> {
                preferenceViewModel.setHomeTimeAppAlignment(gravity)
                textView.text = appHelper.gravityToString(preferenceHelper.homeTimeAlignment)
            }

            "DateAlignment" -> {
                preferenceViewModel.setHomeDateAlignment(gravity)
                textView.text = appHelper.gravityToString(preferenceHelper.homeDateAlignment)
            }
        }
    }

    private fun getColorText(color: Int): SpannableString {
        val colorText = "#${Integer.toHexString(color)}"
        val spannableString = SpannableString(colorText)
        spannableString.setSpan(
            ForegroundColorSpan(color),
            0,
            colorText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    // Show color picker dialog and register listener
    private fun showColorPickerDialog(view: View, requestCode: String, color: Int) {
        ColorChooserDialog.show(
            this, requestCode, color, true, tabs = intArrayOf(TAB_HSV, TAB_PALETTE)
        )

        ColorChooserDialog.registerListener(this, requestCode, { pickedColor ->
            this.color = pickedColor
            (view as TextView).apply {
                text = getColorText(pickedColor)
                setTextColor(pickedColor)
            }
            when (requestCode) {
                REQUEST_KEY_DAILY_WORD_COLOR -> {
                    preferenceViewModel.setDailyWordColor(pickedColor)
                    Log.d("Tag", "Settings Daily Color: ${Integer.toHexString(color)}")
                }

                REQUEST_KEY_BATTERY_COLOR -> {
                    preferenceViewModel.setBatteryColor(pickedColor)
                    Log.d("Tag", "Settings Battery Color: ${Integer.toHexString(color)}")
                }

                REQUEST_KEY_APP_COLOR -> {
                    preferenceViewModel.setAppColor(pickedColor)
                    Log.d("Tag", "Settings Daily Color: ${Integer.toHexString(color)}")
                }

                REQUEST_KEY_DATE_COLOR -> {
                    preferenceViewModel.setDateColor(pickedColor)
                    Log.d("Tag", "Settings Date Color: ${Integer.toHexString(color)}")
                }

                REQUEST_KEY_TIME_COLOR -> {
                    preferenceViewModel.setTimeColor(pickedColor)
                    Log.d("Tag", "Settings Time Color: ${Integer.toHexString(color)}")
                }
            }
        }) {
            Toast.makeText(context, "onCancel", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_KEY_DATE_COLOR = "REQUEST_DATE_COLOR"
        private const val REQUEST_KEY_TIME_COLOR = "REQUEST_TIME_COLOR"
        private const val REQUEST_KEY_DAILY_WORD_COLOR = "REQUEST_DAILY_WORD_COLOR"
        private const val REQUEST_KEY_APP_COLOR = "REQUEST_APP_COLOR"
        private const val REQUEST_KEY_BATTERY_COLOR = "REQUEST_BATTERY_COLOR"
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
