package com.github.droidworksstudio.launcher.ui.bottomsheetdialog

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.github.droidworksstudio.launcher.databinding.BottomsheetdialogTextSettingsBinding
import com.github.droidworksstudio.launcher.helper.BottomDialogHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class TextBottomSheetDialogFragment(context: Context) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetdialogTextSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView(){
        bottomDialogHelper.setupDialogStyle(dialog)

        binding.selectDateTextSize.setText(preferenceHelper.dateTextSize.toString())
        binding.selectTimeTextSize.setText(preferenceHelper.timeTextSize.toString())
        binding.selectAppTextSize.setText(preferenceHelper.appTextSize.toString())
    }

    private fun observeValueChange(){
        val dateValue = binding.selectDateTextSize.text.toString()
        val timeValue = binding.selectTimeTextSize.text.toString()
        val appValue = binding.selectAppTextSize.text.toString()

        val dateFloatValue = parseFloatValue(dateValue, preferenceHelper.dateTextSize)
        val timeFloatValue = parseFloatValue(timeValue, preferenceHelper.timeTextSize)
        val appFloatValue = parseFloatValue(appValue, preferenceHelper.appTextSize)
        dismiss()

        preferenceViewModel.setDateTextSize(dateFloatValue)
        preferenceViewModel.setTimeTextSize(timeFloatValue)
        preferenceViewModel.setAppTextSize(appFloatValue)
    }

    private fun parseFloatValue(text: String, defaultValue: Float): Float {
        if (text.isEmpty() || text == "0") {
            return defaultValue
        }
        return text.toFloat()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        observeValueChange()
    }
}