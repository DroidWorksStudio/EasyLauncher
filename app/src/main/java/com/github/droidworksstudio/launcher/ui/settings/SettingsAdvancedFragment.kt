package com.github.droidworksstudio.launcher.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.droidworksstudio.common.resetDefaultLauncher
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.databinding.FragmentSettingsAdvancedBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.AppReloader
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.ScrollEventListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsAdvancedFragment : Fragment(),
    ScrollEventListener {

    private var _binding: FragmentSettingsAdvancedBinding? = null
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
        _binding = FragmentSettingsAdvancedBinding.inflate(inflater, container, false)
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

    private fun initializeInjectedDependencies() {
        binding.nestScrollView.scrollEventListener = this

        val packageInfo =
            requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)

        binding.apply {
            appInfoDescription.text = getString(R.string.advanced_settings_app_info_description).format(packageInfo.versionName)
        }
    }

    private fun observeClickListener() {
        binding.apply {
            appInfo.setOnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            }

            setDefaultLauncher.setOnClickListener {
                requireContext().resetDefaultLauncher()
            }

            restartLauncher.setOnClickListener {
                Handler(Looper.getMainLooper()).postDelayed({
                    AppReloader.restartApp(context)
                }, 500)
            }

            backupRestore.setOnClickListener {
                showBackupRestoreDialog()
            }

            shareView.setOnClickListener {
                appHelper.shareAppButton(requireContext())
            }

            githubView.setOnClickListener {
                appHelper.githubButton(requireContext())
            }

            feedbackView.setOnClickListener {
                appHelper.feedbackButton(requireContext())
            }
        }
    }

    private var backupRestoreDialog: AlertDialog? = null

    private fun showBackupRestoreDialog() {
        // Dismiss any existing dialog to prevent multiple dialogs open simultaneously
        backupRestoreDialog?.dismiss()

        // Define the items for the dialog (Backup, Restore, Clear Data)
        val items = arrayOf(
            getString(R.string.advanced_settings_backup_restore_backup),
            getString(R.string.advanced_settings_backup_restore_restore),
            getString(R.string.advanced_settings_backup_restore_clear)
        )

        val dialogBuilder = MaterialAlertDialogBuilder(context)
        dialogBuilder.setTitle(getString(R.string.advanced_settings_backup_restore_title))
        dialogBuilder.setItems(items) { _, which ->
            when (which) {
                0 -> appHelper.storeFile(requireActivity())
                1 -> appHelper.loadFile(requireActivity())
                else -> confirmClearData()
            }
        }

        // Assign the created dialog to backupRestoreDialog
        backupRestoreDialog = dialogBuilder.create()
        backupRestoreDialog?.show()
    }

    // Function to handle the Clear Data action, with a confirmation dialog
    private fun confirmClearData() {
        MaterialAlertDialogBuilder(context)
            .setTitle(getString(R.string.advanced_settings_backup_restore_clear_title))
            .setMessage(getString(R.string.advanced_settings_backup_restore_clear_description))
            .setPositiveButton(getString(R.string.advanced_settings_backup_restore_clear_yes)) { _, _ ->
                clearData()
            }
            .setNegativeButton(getString(R.string.advanced_settings_backup_restore_clear_no), null)
            .show()
    }

    private fun clearData() {
        preferenceHelper.clearAll(context)
        Handler(Looper.getMainLooper()).postDelayed({
            AppReloader.restartApp(context)
        }, 500)
    }

}