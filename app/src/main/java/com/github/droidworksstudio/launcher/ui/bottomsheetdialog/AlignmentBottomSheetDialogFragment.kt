package com.github.droidworksstudio.launcher.ui.bottomsheetdialog

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.databinding.BottomsheetdialogAlignmentSettingsBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.BottomDialogHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlignmentBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var _binding: BottomsheetdialogAlignmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appHelper: AppHelper

    @Inject
    lateinit var bottomDialogHelper: BottomDialogHelper

    private val preferenceViewModel: PreferenceViewModel by viewModels()

    private var selectedAlignment: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetdialogAlignmentSettingsBinding.inflate(inflater, container, false)
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
        dismiss()  // Close the AlignmentBottomSheetDialogFragment when the home button is pressed.
    }

    private fun initView() {
        bottomDialogHelper.setupDialogStyle(dialog)

        binding.apply {
            selectDateTextSize.apply {
                text = appHelper.gravityToString(preferenceHelper.homeDateAlignment)
            }

            selectTimeTextSize.apply {
                text = appHelper.gravityToString(preferenceHelper.homeTimeAlignment)
            }

            selectAppTextSize.apply {
                text = appHelper.gravityToString(preferenceHelper.homeAppAlignment)
            }

            selectWordTextSize.apply {
                text = appHelper.gravityToString(preferenceHelper.homeDailyWordAlignment)
            }

            selectAlarmClockTextSize.apply {
                text = appHelper.gravityToString(preferenceHelper.homeAlarmClockAlignment)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun observeClickListener() {
        binding.apply {
            bottomAlignmentDateView.setOnClickListener {
                selectedAlignment = REQUEST_KEY_DATE_ALIGNMENT
                showListDialog(selectedAlignment)
            }

            bottomAlignmentTimeView.setOnClickListener {
                selectedAlignment = REQUEST_KEY_TIME_ALIGNMENT
                showListDialog(selectedAlignment)
            }

            bottomAlignmentAppView.setOnClickListener {
                selectedAlignment = REQUEST_KEY_APP_ALIGNMENT
                showListDialog(selectedAlignment)
            }

            bottomAlignmentWordView.setOnClickListener {
                selectedAlignment = REQUEST_KEY_WORD_ALIGNMENT
                showListDialog(selectedAlignment)
            }

            bottomAlignmentAlarmClockView.setOnClickListener {
                selectedAlignment = REQUEST_KEY_ALARM_CLOCK_ALIGNMENT
                showListDialog(selectedAlignment)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showListDialog(selectedAlignment: String) {
        val items = resources.getStringArray(R.array.alignment_options)

        val dialog = MaterialAlertDialogBuilder(requireContext())

        dialog.setTitle(DIALOG_TITLE)
        dialog.setItems(items) { _, index ->
            val selectedItem = index
            val gravity = appHelper.getGravityFromSelectedItem(selectedItem)

            binding.apply {
                when (selectedAlignment) {
                    REQUEST_KEY_APP_ALIGNMENT -> {
                        setAlignment(
                            selectedAlignment,
                            gravity,
                            selectAppTextSize
                        )
                    }

                    REQUEST_KEY_TIME_ALIGNMENT -> {
                        setAlignment(
                            selectedAlignment,
                            gravity,
                            selectTimeTextSize
                        )
                    }

                    REQUEST_KEY_DATE_ALIGNMENT -> {
                        setAlignment(
                            selectedAlignment,
                            gravity,
                            selectDateTextSize
                        )
                    }

                    REQUEST_KEY_WORD_ALIGNMENT -> {
                        setAlignment(
                            selectedAlignment,
                            gravity,
                            selectWordTextSize
                        )
                    }

                    REQUEST_KEY_ALARM_CLOCK_ALIGNMENT -> {
                        setAlignment(
                            selectedAlignment,
                            gravity,
                            selectAlarmClockTextSize
                        )
                    }
                }
            }
        }
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setAlignment(
        alignmentType: String,
        gravity: Int,
        textView: TextView
    ) {
        val alignmentPreference: (Int) -> Unit
        val alignmentGetter: () -> Int

        when (alignmentType) {
            REQUEST_KEY_APP_ALIGNMENT -> {
                alignmentPreference = { preferenceViewModel.setHomeAppAlignment(it) }
                alignmentGetter = { preferenceHelper.homeAppAlignment }
            }

            REQUEST_KEY_TIME_ALIGNMENT -> {
                alignmentPreference = { preferenceViewModel.setHomeTimeAppAlignment(it) }
                alignmentGetter = { preferenceHelper.homeTimeAlignment }
            }

            REQUEST_KEY_DATE_ALIGNMENT -> {
                alignmentPreference = { preferenceViewModel.setHomeDateAlignment(it) }
                alignmentGetter = { preferenceHelper.homeDateAlignment }
            }

            REQUEST_KEY_WORD_ALIGNMENT -> {
                alignmentPreference = { preferenceViewModel.setHomeDailyWordAppAlignment(it) }
                alignmentGetter = { preferenceHelper.homeDailyWordAlignment }
            }

            REQUEST_KEY_ALARM_CLOCK_ALIGNMENT -> {
                alignmentPreference = { preferenceViewModel.setHomeAlarmClockAppAlignment(it) }
                alignmentGetter = { preferenceHelper.homeAlarmClockAlignment }
            }

            else -> return
        }

        alignmentPreference(gravity)
        textView.text = appHelper.gravityToString(alignmentGetter())
        val feedbackType = "select"
        appHelper.triggerHapticFeedback(context, feedbackType)
    }

    companion object {
        private const val DIALOG_TITLE = "Select Alignment"
        private const val REQUEST_KEY_DATE_ALIGNMENT = "REQUEST_KEY_DATE_ALIGNMENT"
        private const val REQUEST_KEY_TIME_ALIGNMENT = "REQUEST_KEY_TIME_ALIGNMENT"
        private const val REQUEST_KEY_APP_ALIGNMENT = "REQUEST_KEY_APP_ALIGNMENT"
        private const val REQUEST_KEY_ALARM_CLOCK_ALIGNMENT = "REQUEST_KEY_ALARM_CLOCK_ALIGNMENT"
        private const val REQUEST_KEY_WORD_ALIGNMENT = "REQUEST_KEY_WORD_ALIGNMENT"
    }
}
