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
import com.github.droidworksstudio.launcher.databinding.BottomsheetdialogPaddingSettingsBinding
import com.github.droidworksstudio.launcher.helper.BottomDialogHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class PaddingBottomSheetDialogFragment(context: Context) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetdialogPaddingSettingsBinding? = null
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
        _binding = BottomsheetdialogPaddingSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView(){
        bottomDialogHelper.setupDialogStyle(dialog)

        binding.selectAppPaddingSize.setText(preferenceHelper.homeAppPadding.toString())
    }

    private fun observeValueChange(){
        val appValue = binding.selectAppPaddingSize.text.toString()

        val appFloatValue = parseFloatValue(appValue, preferenceHelper.homeAppPadding)
        dismiss()

        preferenceViewModel.setAppPaddingSize(appFloatValue)
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