package com.github.droidworksstudio.launcher.ui.bottomsheetdialog

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.viewModels
import com.github.droidworksstudio.launcher.databinding.BottomsheetdialogPaddingSettingsBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.BottomDialogHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.utils.Constants
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PaddingBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var _binding: BottomsheetdialogPaddingSettingsBinding? = null
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
        _binding = BottomsheetdialogPaddingSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    override fun onPause() {
        super.onPause()
        dismiss()  // Close the PaddingBottomSheetDialogFragment when the home button is pressed.
    }

    private fun initView() {
        bottomDialogHelper.setupDialogStyle(dialog)

        binding.apply {
            selectAppPaddingSize.setText("${preferenceHelper.homeAppPadding}")
            selectAppGroupPaddingSize.setText("${preferenceHelper.homeAppsPadding}")

            addMinMaxConstraint(selectAppPaddingSize, Constants.APP_PADDING_MIN, Constants.APP_PADDING_MAX)
            addMinMaxConstraint(selectAppGroupPaddingSize, Constants.APP_GROUP_PADDING_MIN, Constants.APP_GROUP_PADDING_MAX)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun observeValueChange() {
        binding.apply {
            val appPaddingValue = selectAppPaddingSize.text.toString()
            val appGroupPaddingValue = selectAppGroupPaddingSize.text.toString()

            val appPaddingFloatValue = parseFloatValue(appPaddingValue, preferenceHelper.homeAppPadding)
            val appGroupPaddingFloatValue = parseFloatValue(appGroupPaddingValue, preferenceHelper.homeAppsPadding)
            dismiss()

            preferenceViewModel.setAppPaddingSize(appPaddingFloatValue)
            preferenceViewModel.setAppGroupPaddingSize(appGroupPaddingFloatValue)

            val feedbackType = "select"
            appHelper.triggerHapticFeedback(context, feedbackType)
        }
    }

    fun addMinMaxConstraint(
        editText: AppCompatEditText,
        minValue: Double,
        maxValue: Double
    ) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    val input = s.toString()
                    if (input.isNotEmpty()) {
                        val value = input.toDouble()

                        // Check if the value is out of bounds
                        when {
                            value < minValue -> {
                                editText.setText(formatValue(minValue))
                                editText.setSelection(editText.text?.length ?: 0)
                            }

                            value > maxValue -> {
                                editText.setText(formatValue(maxValue))
                                editText.setSelection(editText.text?.length ?: 0)
                            }
                        }
                    }
                } catch (_: NumberFormatException) {
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed here
            }
        })
    }

    fun formatValue(value: Double): String {
        return if (value % 1 == 0.0) {
            value.toInt().toString() // Convert to Int if no decimal part
        } else {
            value.toString() // Keep as Double if there's a decimal part
        }
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