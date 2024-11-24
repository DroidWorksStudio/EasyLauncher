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

    @RequiresApi(Build.VERSION_CODES.O)
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

        binding.selectDateTextSize.apply {
            text = appHelper.gravityToString(preferenceHelper.homeDateAlignment)
        }

        binding.selectTimeTextSize.apply {
            text = appHelper.gravityToString(preferenceHelper.homeTimeAlignment)
        }

        binding.selectAppTextSize.apply {
            text = appHelper.gravityToString(preferenceHelper.homeAppAlignment)
        }

        binding.selectWordTextSize.apply {
            text = appHelper.gravityToString(preferenceHelper.homeDailyWordAlignment)
        }

        binding.selectAlarmClockTextSize.apply {
            text = appHelper.gravityToString(preferenceHelper.homeAlarmClockAlignment)
        }
    }

    private fun observeClickListener() {
        binding.bottomAlignmentDateView.setOnClickListener {
            selectedAlignment = REQUEST_KEY_DATE_ALIGNMENT
            showListDialog(selectedAlignment)
        }

        binding.bottomAlignmentTimeView.setOnClickListener {
            selectedAlignment = REQUEST_KEY_TIME_ALIGNMENT
            showListDialog(selectedAlignment)
        }

        binding.bottomAlignmentAppView.setOnClickListener {
            selectedAlignment = REQUEST_KEY_APP_ALIGNMENT
            showListDialog(selectedAlignment)
        }

        binding.bottomAlignmentWordView.setOnClickListener {
            selectedAlignment = REQUEST_KEY_WORD_ALIGNMENT
            showListDialog(selectedAlignment)
        }

        binding.bottomAlignmentAlarmClockView.setOnClickListener {
            selectedAlignment = REQUEST_KEY_ALARM_CLOCK_ALIGNMENT
            showListDialog(selectedAlignment)
        }
    }


    private fun showListDialog(selectedAlignment: String) {
        val items = resources.getStringArray(R.array.alignment_options)

        val dialog = MaterialAlertDialogBuilder(requireContext())

        dialog.setTitle(DIALOG_TITLE)
        dialog.setItems(items) { _, which ->
            val selectedItem = items[which]
            val gravity = appHelper.getGravityFromSelectedItem(selectedItem)

            when (selectedAlignment) {
                REQUEST_KEY_APP_ALIGNMENT -> {
                    setAlignment(
                        selectedAlignment,
                        gravity,
                        binding.selectAppTextSize
                    )
                }

                REQUEST_KEY_TIME_ALIGNMENT -> {
                    setAlignment(
                        selectedAlignment,
                        gravity,
                        binding.selectTimeTextSize
                    )
                }

                REQUEST_KEY_DATE_ALIGNMENT -> {
                    setAlignment(
                        selectedAlignment,
                        gravity,
                        binding.selectDateTextSize
                    )
                }

                REQUEST_KEY_WORD_ALIGNMENT -> {
                    setAlignment(
                        selectedAlignment,
                        gravity,
                        binding.selectWordTextSize
                    )
                }

                REQUEST_KEY_ALARM_CLOCK_ALIGNMENT -> {
                    setAlignment(
                        selectedAlignment,
                        gravity,
                        binding.selectAlarmClockTextSize
                    )
                }
            }
        }
        dialog.show()
    }

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