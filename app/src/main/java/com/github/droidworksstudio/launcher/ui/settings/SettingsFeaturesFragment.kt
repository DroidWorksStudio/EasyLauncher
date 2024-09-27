package com.github.droidworksstudio.launcher.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.droidworksstudio.launcher.databinding.FragmentSettingsLookFeelBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.ScrollEventListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFeaturesFragment : Fragment(),
    ScrollEventListener {

    private var _binding: FragmentSettingsLookFeelBinding? = null
    private val binding get() = _binding!!

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
    }

    override fun onStop() {
        super.onStop()
        dismissDialogs()
    }

    private fun initializeInjectedDependencies() {
        binding.nestScrollView.scrollEventListener = this
    }

    private fun observeClickListener() {
        binding.apply {
        }
    }

    private fun dismissDialogs() {
//        backupRestoreDialog?.dismiss()
    }
}