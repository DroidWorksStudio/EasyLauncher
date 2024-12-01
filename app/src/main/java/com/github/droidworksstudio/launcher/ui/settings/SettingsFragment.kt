package com.github.droidworksstudio.launcher.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.databinding.FragmentSettingsBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnSwipeTouchListener
import com.github.droidworksstudio.launcher.listener.ScrollEventListener
import com.github.droidworksstudio.launcher.repository.AppInfoRepository
import com.github.droidworksstudio.launcher.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(),
    ScrollEventListener {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appInfoRepository: AppInfoRepository

    @Inject
    lateinit var appHelper: AppHelper

    private lateinit var navController: NavController

    private lateinit var context: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
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

        observeClickListener()
        observeSwipeTouchListener()
    }

    private fun observeClickListener() {
        binding.apply {
            featuresSettings.setOnClickListener {
                navController.navigate(R.id.SettingsFeaturesFragment)
            }

            lookFeelSettings.setOnClickListener {
                navController.navigate(R.id.SettingsLookFeelFragment)
            }

            widgetsSettings.setOnClickListener {
                navController.navigate(R.id.SettingsWidgetFragment)
            }

            favoriteApps.setOnClickListener {
                navController.navigate(R.id.FavoriteFragment)
            }

            hiddenApps.setOnClickListener {
                navController.navigate(R.id.HiddenFragment)
            }

            advancedSettings.setOnClickListener {
                navController.navigate(R.id.SettingsAdvancedFragment)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun observeSwipeTouchListener() {
        binding.apply {
            nestedScrollView.setOnTouchListener(getSwipeGestureListener(context))
        }
    }

    private fun getSwipeGestureListener(context: Context): View.OnTouchListener {
        return object : OnSwipeTouchListener(context, preferenceHelper) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                val actionTypeNavOptions: NavOptions? =
                    if (preferenceHelper.disableAnimations) null
                    else appHelper.getActionType(Constants.Swipe.Left)

                Handler(Looper.getMainLooper()).post {
                    findNavController().navigate(
                        R.id.HomeFragment,
                        null,
                        actionTypeNavOptions
                    )
                }
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                val actionTypeNavOptions: NavOptions? =
                    if (preferenceHelper.disableAnimations) null
                    else appHelper.getActionType(Constants.Swipe.Right)

                Handler(Looper.getMainLooper()).post {
                    findNavController().navigate(
                        R.id.HomeFragment,
                        null,
                        actionTypeNavOptions
                    )
                }
            }
        }
    }
}