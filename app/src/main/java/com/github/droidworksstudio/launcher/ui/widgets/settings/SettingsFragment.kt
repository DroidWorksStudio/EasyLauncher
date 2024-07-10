package com.github.droidworksstudio.launcher.ui.widgets.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.droidworksstudio.common.hasInternetPermission
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.databinding.FragmentSettingsWidgetsBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnSwipeTouchListener
import com.github.droidworksstudio.launcher.listener.ScrollEventListener
import com.github.droidworksstudio.launcher.ui.bottomsheetdialog.ColorBottomSheetDialogFragment
import com.github.droidworksstudio.launcher.adapter.numberpicker.NumberPickerAdapter
import com.github.droidworksstudio.launcher.utils.Constants
import com.github.droidworksstudio.launcher.viewmodel.PreferenceViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(),
    ScrollEventListener {

    private var _binding: FragmentSettingsWidgetsBinding? = null
    private val binding get() = _binding!!

    private val preferenceViewModel: PreferenceViewModel by viewModels()

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
        _binding = FragmentSettingsWidgetsBinding.inflate(inflater, container, false)
        _binding = binding

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = findNavController()
        // Set according to the system theme mode
        appHelper.dayNightMod(requireContext(), binding.nestScrollView)
        super.onViewCreated(view, savedInstanceState)

        context = requireContext()

        initializeInjectedDependencies()
        observeClickListener()
        observeSwipeTouchListener()
    }

    private fun initializeInjectedDependencies() {
        binding.nestScrollView.scrollEventListener = this

        binding.apply {
            // Weather stuff here
            weatherSwitchCompat.isChecked = preferenceHelper.showWeatherWidget
            weatherSunsetSunriseSwitchCompat.isChecked = preferenceHelper.showWeatherWidgetSunSetRise

            weatherOrderControl.text = preferenceHelper.weatherOrderNumber.toString()

            if (!context.hasInternetPermission()) {
                weatherSettings.visibility = View.GONE
            }

            val weatherVisibility = if (weatherSwitchCompat.isChecked) View.VISIBLE else View.GONE
            weatherOrderMenu.visibility = weatherVisibility
            weatherSunsetSunriseMenu.visibility = weatherVisibility
            selectWeatherWidgetColor.visibility = View.GONE

            // Battery stuff here
            batterySwitchCompat.isChecked = preferenceHelper.showBatteryWidget

            batteryOrderControl.text = preferenceHelper.batteryOrderNumber.toString()

            val batteryVisibility = if (batterySwitchCompat.isChecked) View.VISIBLE else View.GONE
            batteryOrderMenu.visibility = batteryVisibility
            selectBatteryWidgetColor.visibility = View.GONE
        }
    }

    private fun observeClickListener() {
        setupSwitchListeners()
        binding.apply {
            weatherOrderControl.setOnClickListener {
                showOrderChangeDialog(binding.weatherOrderControl)
            }

            batteryOrderControl.setOnClickListener {
                showOrderChangeDialog(binding.batteryOrderControl)
            }

            selectBatteryWidgetColor.setOnClickListener {
                val bottomSheetFragment = ColorBottomSheetDialogFragment()
                bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
            }

            selectBatteryWidgetColor.setOnClickListener {
                val bottomSheetFragment = ColorBottomSheetDialogFragment()
                bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
            }
        }
    }

    private fun setupSwitchListeners() {
        binding.apply {
            weatherSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
                preferenceViewModel.setShowWeatherWidget(isChecked)
                binding.apply {
                    val weatherVisibility = if (isChecked) View.VISIBLE else View.GONE
                    weatherOrderMenu.visibility = weatherVisibility
                    weatherSunsetSunriseMenu.visibility = weatherVisibility
                    selectWeatherWidgetColor.visibility = View.GONE
                }
            }

            weatherSunsetSunriseSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
                preferenceViewModel.setShowWeatherWidgetSunSetRise(isChecked)
            }

            batterySwitchCompat.setOnCheckedChangeListener { _, isChecked ->
                preferenceViewModel.setShowBatteryWidget(isChecked)
                binding.apply {
                    val batteryVisibility = if (isChecked) View.VISIBLE else View.GONE
                    batteryOrderMenu.visibility = batteryVisibility
                    selectBatteryWidgetColor.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun observeSwipeTouchListener() {
        binding.touchArea.setOnTouchListener(getSwipeGestureListener(context))
    }

    private fun getSwipeGestureListener(context: Context): View.OnTouchListener {
        return object : OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                findNavController().navigateUp()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                findNavController().navigateUp()
            }

        }
    }

    @SuppressLint("InflateParams")
    private fun showOrderChangeDialog(view: View) {
        val numberPickerDialog = MaterialAlertDialogBuilder(context).create()

        val numberPickerView = layoutInflater.inflate(R.layout.item_number_picker, null)
        val recyclerView = numberPickerView.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val numbers = (1..Constants.WIDGETS_COUNT).toList() // Replace with your desired range

        when (view) {
            binding.weatherOrderControl -> {
                val adapter = NumberPickerAdapter(numbers) { selectedNumber ->
                    // Save the order number to SharedPreferences or ViewModel
                    preferenceViewModel.setWeatherOrderNumber(selectedNumber)
                    binding.weatherOrderControl.text = preferenceHelper.weatherOrderNumber.toString()
                    numberPickerDialog.dismiss()
                }

                recyclerView.adapter = adapter
            }

            binding.batteryOrderControl -> {
                val adapter = NumberPickerAdapter(numbers) { selectedNumber ->
                    // Save the order number to SharedPreferences or ViewModel
                    preferenceViewModel.setBatteryOrderNumber(selectedNumber)
                    binding.batteryOrderControl.text = preferenceHelper.batteryOrderNumber.toString()
                    numberPickerDialog.dismiss()
                }

                recyclerView.adapter = adapter
            }

            else -> {
                Log.d("showOrderChangeDialog", "else")
            }

        }

        numberPickerDialog.apply {
            setTitle("Change Order")
            setView(numberPickerView)
            setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        }

        numberPickerDialog.show()
    }

}