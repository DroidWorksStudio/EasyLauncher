package com.github.droidworksstudio.launcher.helper

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.droidworksstudio.common.showLongToast
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import javax.inject.Inject

class FingerprintHelper @Inject constructor(private val fragment: Fragment) {

    private lateinit var callback: Callback

    @Inject
    lateinit var appHelper: AppHelper

    interface Callback {
        fun onAuthenticationSucceeded(appInfo: AppInfo)
        fun onAuthenticationFailed()
        fun onAuthenticationError(errorCode: Int, errorMessage: CharSequence?)
    }

    fun startFingerprintAuth(appInfo: AppInfo, callback: Callback) {
        this.callback = callback

        val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                callback.onAuthenticationSucceeded(appInfo)
            }

            override fun onAuthenticationFailed() {
                callback.onAuthenticationFailed()
            }

            override fun onAuthenticationError(errorCode: Int, errorMessage: CharSequence) {
                callback.onAuthenticationError(errorCode, errorMessage)
            }
        }

        val executor = ContextCompat.getMainExecutor(fragment.requireContext())
        val biometricPrompt = BiometricPrompt(fragment, executor, authenticationCallback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(fragment.getString(R.string.authentication_title))
            .setSubtitle(fragment.getString(R.string.authentication_subtitle))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        val canAuthenticate = BiometricManager.from(fragment.requireContext())
            .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    fun startFingerprintSettingsAuth(runNavigation: Int) {
        val executor = ContextCompat.getMainExecutor(fragment.requireContext())

        val biometricPrompt =
            BiometricPrompt(fragment, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    fragment.findNavController().navigate(runNavigation)
                }

                override fun onAuthenticationFailed() {
                    fragment.requireContext()
                        .showLongToast(fragment.getString(R.string.authentication_failed))
                }

                override fun onAuthenticationError(errorCode: Int, errorMessage: CharSequence) {
                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED -> fragment.requireContext()
                            .showLongToast(fragment.getString(R.string.authentication_cancel))

                        else -> fragment.requireContext().showLongToast(
                            fragment.getString(R.string.authentication_error)
                                .format(errorMessage, errorCode)
                        )
                    }
                }
            })

        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(fragment.getString(R.string.authentication_title))
            .setSubtitle(fragment.getString(R.string.authentication_subtitle))

        val authenticators =
            BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        val canAuthenticate =
            BiometricManager.from(fragment.requireContext()).canAuthenticate(authenticators)

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            promptInfoBuilder.setAllowedAuthenticators(authenticators)
        } else {
            promptInfoBuilder.setNegativeButtonText(fragment.getString(R.string.authentication_cancel))
        }

        val promptInfo = promptInfoBuilder.build()

        when (canAuthenticate) {
            BiometricManager.BIOMETRIC_SUCCESS -> biometricPrompt.authenticate(promptInfo)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> sendToTarget(runNavigation)

            else -> fragment.requireContext()
                .showLongToast(fragment.getString(R.string.authentication_failed))
        }
    }

    private fun sendToTarget(runNavigation: Int) {
        try {
            fragment.findNavController().navigate(runNavigation)
        } catch (e: Exception) {
            fragment.requireContext()
                .showLongToast(fragment.getString(R.string.authentication_failed))
        }
    }
}
