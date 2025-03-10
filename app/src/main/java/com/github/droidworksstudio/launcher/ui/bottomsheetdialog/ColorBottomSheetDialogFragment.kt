package com.github.droidworksstudio.launcher.ui.bottomsheetdialog

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.github.droidworksstudio.common.showLongToast
import com.github.droidworksstudio.launcher.databinding.BottomsheetdialogColorSettingsBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.BottomDialogHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import net.mm2d.color.chooser.ColorChooserDialog
import javax.inject.Inject

@AndroidEntryPoint
class ColorBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var _binding: BottomsheetdialogColorSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appHelper: AppHelper

    @Inject
    lateinit var bottomDialogHelper: BottomDialogHelper

    private val preferenceViewModel: PreferenceViewModel by viewModels()

    private var color: Int = Color.WHITE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetdialogColorSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observeClickListener()
    }

    override fun onPause() {
        super.onPause()
        dismiss()  // Close the ColorBottomSheetDialogFragment when the home button is pressed.
    }

    private fun initView() {
        bottomDialogHelper.setupDialogStyle(dialog)

        binding.apply {
            selectDateTextColor.apply {
                text = bottomDialogHelper.getColorText(preferenceHelper.dateColor)
                setTextColor(preferenceHelper.dateColor)
            }

            selectTimeTextColor.apply {
                text = bottomDialogHelper.getColorText(preferenceHelper.timeColor)
                setTextColor(preferenceHelper.timeColor)
            }

            selectAppTextColor.apply {
                text = bottomDialogHelper.getColorText(preferenceHelper.appColor)
                setTextColor(preferenceHelper.appColor)
            }

            selectBatteryTextColor.apply {
                text = bottomDialogHelper.getColorText(preferenceHelper.batteryColor)
                setTextColor(preferenceHelper.batteryColor)
            }

            selectAlarmClockTextColor.apply {
                text = bottomDialogHelper.getColorText(preferenceHelper.alarmClockColor)
                setTextColor(preferenceHelper.alarmClockColor)
            }

            selectWordTextColor.apply {
                text = bottomDialogHelper.getColorText(preferenceHelper.dailyWordColor)
                setTextColor(preferenceHelper.dailyWordColor)
            }

            selectWidgetBackgroundColor.apply {
                text = bottomDialogHelper.getColorText(preferenceHelper.widgetBackgroundColor)
                setTextColor(preferenceHelper.widgetBackgroundColor)
            }

            selectWidgetTextColor.apply {
                text = bottomDialogHelper.getColorText(preferenceHelper.widgetTextColor)
                setTextColor(preferenceHelper.widgetTextColor)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun observeClickListener() {
        binding.apply {
            bottomColorDateView.setOnClickListener {
                showColorPickerDialog(
                    binding.selectDateTextColor,
                    REQUEST_KEY_DATE_COLOR,
                    preferenceHelper.dateColor
                )
            }

            bottomColorTimeView.setOnClickListener {
                showColorPickerDialog(
                    binding.selectTimeTextColor,
                    REQUEST_KEY_TIME_COLOR,
                    preferenceHelper.timeColor
                )
            }

            bottomColorAppView.setOnClickListener {
                showColorPickerDialog(
                    binding.selectAppTextColor,
                    REQUEST_KEY_APP_COLOR,
                    preferenceHelper.appColor
                )
            }

            bottomColorBatteryView.setOnClickListener {
                showColorPickerDialog(
                    binding.selectBatteryTextColor,
                    REQUEST_KEY_BATTERY_COLOR,
                    preferenceHelper.batteryColor
                )
            }

            bottomColorWordView.setOnClickListener {
                showColorPickerDialog(
                    binding.selectWordTextColor,
                    REQUEST_KEY_DAILY_WORD_COLOR,
                    preferenceHelper.dailyWordColor
                )
            }

            bottomColorAlarmClockView.setOnClickListener {
                showColorPickerDialog(
                    binding.selectAlarmClockTextColor,
                    REQUEST_KEY_DAILY_ALARM_CLOCK_COLOR,
                    preferenceHelper.alarmClockColor
                )
            }

            bottomColorWidgetBackgroundView.setOnClickListener {
                showColorPickerDialog(
                    binding.selectWidgetBackgroundColor,
                    REQUEST_KEY_WIDGET_BACKGROUND_COLOR,
                    preferenceHelper.widgetBackgroundColor
                )
            }

            bottomColorWidgetTextView.setOnClickListener {
                showColorPickerDialog(
                    binding.selectWidgetTextColor,
                    REQUEST_KEY_WIDGET_TEXT_COLOR,
                    preferenceHelper.widgetTextColor
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showColorPickerDialog(view: View, requestCode: String, color: Int) {
        ColorChooserDialog.show(
            this, requestCode, color, true, tabs = intArrayOf(
                ColorChooserDialog.TAB_HSV,
                ColorChooserDialog.TAB_PALETTE
            )
        )

        ColorChooserDialog.registerListener(this, requestCode, { pickedColor ->
            this.color = pickedColor
            (view as TextView).apply {
                text = bottomDialogHelper.getColorText(pickedColor)
                setTextColor(pickedColor)
            }
            when (requestCode) {
                REQUEST_KEY_BATTERY_COLOR -> {
                    preferenceViewModel.setBatteryColor(pickedColor)
                    Log.d("Tag", "Settings Battery Color: ${Integer.toHexString(pickedColor)}")
                }

                REQUEST_KEY_APP_COLOR -> {
                    preferenceViewModel.setAppColor(pickedColor)
                    Log.d("Tag", "Settings Daily Color: ${Integer.toHexString(pickedColor)}")
                }

                REQUEST_KEY_DATE_COLOR -> {
                    preferenceViewModel.setDateColor(pickedColor)
                    Log.d("Tag", "Settings Date Color: ${Integer.toHexString(pickedColor)}")
                }

                REQUEST_KEY_TIME_COLOR -> {
                    preferenceViewModel.setTimeColor(pickedColor)
                    Log.d("Tag", "Settings Time Color: ${Integer.toHexString(color)}")
                }

                REQUEST_KEY_DAILY_ALARM_CLOCK_COLOR -> {
                    preferenceViewModel.setAlarmClockColor(pickedColor)
                    Log.d("Tag", "Settings Alarm Clock Color: ${Integer.toHexString(pickedColor)}")
                }

                REQUEST_KEY_DAILY_WORD_COLOR -> {
                    preferenceViewModel.setDailyWordColor(pickedColor)
                    Log.d("Tag", "Settings Daily Word Color: ${Integer.toHexString(pickedColor)}")
                }

                REQUEST_KEY_WIDGET_BACKGROUND_COLOR -> {
                    preferenceViewModel.setWidgetBackgroundColor(pickedColor)
                    Log.d("Tag", "Settings Widget Background Color: ${Integer.toHexString(color)}")
                }

                REQUEST_KEY_WIDGET_TEXT_COLOR -> {
                    preferenceViewModel.setWidgetTextColor(pickedColor)
                    Log.d("Tag", "Settings Widget Text Color: ${Integer.toHexString(color)}")
                }
            }

            val feedbackType = "select"
            appHelper.triggerHapticFeedback(context, feedbackType)
        }) {
            context?.showLongToast("onCancel")
        }
    }

    companion object {
        private const val REQUEST_KEY_DATE_COLOR = "REQUEST_DATE_COLOR"
        private const val REQUEST_KEY_TIME_COLOR = "REQUEST_TIME_COLOR"
        private const val REQUEST_KEY_DAILY_ALARM_CLOCK_COLOR = "REQUEST_KEY_DAILY_ALARM_CLOCK_COLOR"
        private const val REQUEST_KEY_DAILY_WORD_COLOR = "REQUEST_DAILY_WORD_COLOR"
        private const val REQUEST_KEY_APP_COLOR = "REQUEST_APP_COLOR"
        private const val REQUEST_KEY_BATTERY_COLOR = "REQUEST_BATTERY_COLOR"
        private const val REQUEST_KEY_WIDGET_BACKGROUND_COLOR = "REQUEST_WIDGET_BACKGROUND_COLOR"
        private const val REQUEST_KEY_WIDGET_TEXT_COLOR = "REQUEST_WIDGET_TEXT_COLOR"
    }
}