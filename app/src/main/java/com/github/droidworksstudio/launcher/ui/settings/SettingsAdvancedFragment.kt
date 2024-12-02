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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.droidworksstudio.common.resetDefaultLauncher
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.data.dao.AppInfoDAO
import com.github.droidworksstudio.launcher.databinding.FragmentSettingsAdvancedBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.AppReloader
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.ScrollEventListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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

    @Inject
    lateinit var appDAO: AppInfoDAO

    private lateinit var navController: NavController

    private lateinit var context: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
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
        appHelper.dayNightMod(requireContext(), binding.mainLayout)
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

            helpFeedback.setOnClickListener {
                appHelper.helpFeedbackButton(requireContext())
            }

            communitySupport.setOnClickListener {
                appHelper.communitySupportButton(requireContext())
            }

            shareApplication.setOnClickListener {
                appHelper.shareApplicationButton(requireContext())
            }
        }
    }

    private var backupRestoreDialog: AlertDialog? = null

    private fun showBackupRestoreDialog() {
        // Dismiss any existing dialog to prevent multiple dialogs open simultaneously
        backupRestoreDialog?.dismiss()

        // Define the items for the dialog (Backup, Restore, Clear Data)
        val items = arrayOf(
            getString(R.string.advanced_settings_backup_restore_backup_prefs),
            getString(R.string.advanced_settings_backup_restore_restore_prefs),
            getString(R.string.advanced_settings_backup_restore_backup_apps),
            getString(R.string.advanced_settings_backup_restore_restore_apps),
            getString(R.string.advanced_settings_backup_restore_clear)
        )

        val dialogBuilder = MaterialAlertDialogBuilder(context)
        dialogBuilder.setTitle(getString(R.string.advanced_settings_backup_restore_title))
        dialogBuilder.setItems(items) { _, which ->
            when (which) {
                0 -> appHelper.storeFile(requireActivity())
                1 -> appHelper.loadFile(requireActivity())
                2 -> appHelper.storeFileApps(requireActivity())
                3 -> appHelper.loadFileApps(requireActivity())
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
        lifecycleScope.launch {
            appHelper.resetDatabase(appDAO)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            AppReloader.restartApp(context)
        }, 500)
    }

    private fun dismissDialogs() {
        backupRestoreDialog?.dismiss()
    }

}