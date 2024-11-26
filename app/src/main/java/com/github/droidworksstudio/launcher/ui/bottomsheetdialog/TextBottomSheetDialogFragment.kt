package com.github.droidworksstudio.launcher.ui.bottomsheetdialog

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.github.droidworksstudio.launcher.databinding.BottomsheetdialogTextSettingsBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.BottomDialogHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TextBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var _binding: BottomsheetdialogTextSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appHelper: AppHelper

    @Inject
    lateinit var bottomDialogHelper: BottomDialogHelper

    private val preferenceViewModel: PreferenceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetdialogTextSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    override fun onPause() {
        super.onPause()
        dismiss()  // Close the TextBottomSheetDialogFragment when the home button is pressed.
    }

    private fun initView() {
        bottomDialogHelper.setupDialogStyle(dialog)

        binding.apply {
            selectDateTextSize.setText("${preferenceHelper.dateTextSize}")
            selectTimeTextSize.setText("${preferenceHelper.timeTextSize}")
            selectAppTextSize.setText("${preferenceHelper.appTextSize}")
            selectBatteryTextSize.setText("${preferenceHelper.batteryTextSize}")
            selectAlarmClockTextSize.setText("${preferenceHelper.alarmClockTextSize}")
            selectDailyWordTextSize.setText("${preferenceHelper.dailyWordTextSize}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun observeValueChange() {
        binding.apply {
            val dateValue = selectDateTextSize.text.toString()
            val timeValue = selectTimeTextSize.text.toString()
            val appValue = selectAppTextSize.text.toString()
            val batteryValue = selectBatteryTextSize.text.toString()
            val alarmValue = selectAlarmClockTextSize.text.toString()
            val wordValue = selectDailyWordTextSize.text.toString()

            val dateFloatValue = parseFloatValue(dateValue, preferenceHelper.dateTextSize)
            val timeFloatValue = parseFloatValue(timeValue, preferenceHelper.timeTextSize)
            val appFloatValue = parseFloatValue(appValue, preferenceHelper.appTextSize)
            val batteryFloatValue = parseFloatValue(batteryValue, preferenceHelper.batteryTextSize)
            val alarmFloatValue = parseFloatValue(alarmValue, preferenceHelper.alarmClockTextSize)
            val wordFloatValue = parseFloatValue(wordValue, preferenceHelper.dailyWordTextSize)
            dismiss()

            preferenceViewModel.setDateTextSize(dateFloatValue)
            preferenceViewModel.setTimeTextSize(timeFloatValue)
            preferenceViewModel.setAppTextSize(appFloatValue)
            preferenceViewModel.setBatteryTextSize(batteryFloatValue)
            preferenceViewModel.setAlarmClockTextSize(alarmFloatValue)
            preferenceViewModel.setDailyWordTextSize(wordFloatValue)
        }

        val feedbackType = "save"
        appHelper.triggerHapticFeedback(context, feedbackType)
    }

    private fun parseFloatValue(text: String, defaultValue: Float): Float {
        if (text.isEmpty() || text == "0") {
            return defaultValue
        }
        return text.toFloat()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        observeValueChange()
    }
}