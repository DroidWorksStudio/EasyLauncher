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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.github.droidworksstudio.launcher.databinding.BottomsheetdialogColorSettingsBinding
import com.github.droidworksstudio.launcher.helper.BottomDialogHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observeClickListener()
    }

    private fun initView() {
        bottomDialogHelper.setupDialogStyle(dialog)

        binding.selectDateTextColor.apply {
            text = bottomDialogHelper.getColorText(preferenceHelper.dateColor)
            setTextColor(preferenceHelper.dateColor)
        }

        binding.selectTimeTextColor.apply {
            text = bottomDialogHelper.getColorText(preferenceHelper.timeColor)
            setTextColor(preferenceHelper.timeColor)
        }

        binding.selectAppTextColor.apply {
            text = bottomDialogHelper.getColorText(preferenceHelper.appColor)
            setTextColor(preferenceHelper.appColor)
        }

        binding.selectBatteryTextColor.apply {
            text = bottomDialogHelper.getColorText(preferenceHelper.batteryColor)
            setTextColor(preferenceHelper.batteryColor)
        }

        binding.selectWordTextColor.apply {
            text = bottomDialogHelper.getColorText(preferenceHelper.dailyWordColor)
            setTextColor(preferenceHelper.dailyWordColor)
        }

        binding.selectWidgetBackgroundColor.apply {
            text = bottomDialogHelper.getColorText(preferenceHelper.widgetBackgroundColor)
            setTextColor(preferenceHelper.widgetBackgroundColor)
        }

        binding.selectWidgetTextColor.apply {
            text = bottomDialogHelper.getColorText(preferenceHelper.widgetTextColor)
            setTextColor(preferenceHelper.widgetTextColor)
        }
    }


    private fun observeClickListener() {
        binding.bottomColorDateView.setOnClickListener {
            showColorPickerDialog(
                binding.selectDateTextColor,
                REQUEST_KEY_DATE_COLOR,
                preferenceHelper.dateColor
            )
        }

        binding.bottomColorTimeView.setOnClickListener {
            showColorPickerDialog(
                binding.selectTimeTextColor,
                REQUEST_KEY_TIME_COLOR,
                preferenceHelper.timeColor
            )
        }

        binding.bottomColorAppView.setOnClickListener {
            showColorPickerDialog(
                binding.selectAppTextColor,
                REQUEST_KEY_APP_COLOR,
                preferenceHelper.appColor
            )
        }

        binding.bottomColorBatteryView.setOnClickListener {
            showColorPickerDialog(
                binding.selectBatteryTextColor,
                REQUEST_KEY_BATTERY_COLOR,
                preferenceHelper.batteryColor
            )
        }

        binding.bottomColorWordView.setOnClickListener {
            showColorPickerDialog(
                binding.selectWordTextColor,
                REQUEST_KEY_DAILY_WORD_COLOR,
                preferenceHelper.dailyWordColor
            )
        }

        binding.bottomColorWidgetBackgroundView.setOnClickListener {
            showColorPickerDialog(
                binding.selectWidgetBackgroundColor,
                REQUEST_KEY_WIDGET_BACKGROUND_COLOR,
                preferenceHelper.widgetBackgroundColor
            )
        }

        binding.bottomColorWidgetTextView.setOnClickListener {
            showColorPickerDialog(
                binding.selectWidgetTextColor,
                REQUEST_KEY_WIDGET_TEXT_COLOR,
                preferenceHelper.widgetTextColor
            )
        }
    }

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
                REQUEST_KEY_DAILY_WORD_COLOR -> {
                    preferenceViewModel.setDailyWordColor(pickedColor)
                    Log.d("Tag", "Settings Daily Color: ${Integer.toHexString(pickedColor)}")
                }

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

                REQUEST_KEY_WIDGET_BACKGROUND_COLOR -> {
                    preferenceViewModel.setWidgetBackgroundColor(pickedColor)
                    Log.d("Tag", "Settings Widget Background Color: ${Integer.toHexString(color)}")
                }

                REQUEST_KEY_WIDGET_TEXT_COLOR -> {
                    preferenceViewModel.setWidgetTextColor(pickedColor)
                    Log.d("Tag", "Settings Widget Text Color: ${Integer.toHexString(color)}")
                }
            }
        }) {
            context?.showLongToast("onCancel")
        }
    }

    companion object {
        private const val REQUEST_KEY_DATE_COLOR = "REQUEST_DATE_COLOR"
        private const val REQUEST_KEY_TIME_COLOR = "REQUEST_TIME_COLOR"
        private const val REQUEST_KEY_DAILY_WORD_COLOR = "REQUEST_DAILY_WORD_COLOR"
        private const val REQUEST_KEY_APP_COLOR = "REQUEST_APP_COLOR"
        private const val REQUEST_KEY_BATTERY_COLOR = "REQUEST_BATTERY_COLOR"
        private const val REQUEST_KEY_WIDGET_BACKGROUND_COLOR = "REQUEST_WIDGET_BACKGROUND_COLOR"
        private const val REQUEST_KEY_WIDGET_TEXT_COLOR = "REQUEST_WIDGET_TEXT_COLOR"
    }
}