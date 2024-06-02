package com.github.droidworksstudio.launcher.ui.widgetmanager

import android.annotation.SuppressLint
import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.droidworksstudio.launcher.utils.Constants
import com.github.droidworksstudio.launcher.databinding.FragmentWidgetManagerBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.listener.OnSwipeTouchListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class WidgetManagerFragment : Fragment(),
    WidgetOptionsDialogFragment.WidgetOptionsListener {

    private var _binding: FragmentWidgetManagerBinding? = null

    private val binding get() = _binding!!

    private lateinit var pickWidgetLauncher: ActivityResultLauncher<Intent>
    private lateinit var createWidgetLauncher: ActivityResultLauncher<Intent>

    private lateinit var appWidgetManager: AppWidgetManager
    private lateinit var appWidgetHost: AppWidgetHost
    private lateinit var widgetAdapter: WidgetAdapter

    @Inject
    lateinit var appHelper: AppHelper

    private lateinit var context: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWidgetManagerBinding.inflate(inflater, container, false)

        return binding.root

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appHelper.dayNightMod(requireContext(), binding.drawBackground)
        super.onViewCreated(view, savedInstanceState)

        context = requireContext()

        appWidgetManager = AppWidgetManager.getInstance(requireContext())
        appWidgetHost = AppWidgetHost(requireContext(), Constants.APP_WIDGET_HOST_ID)

        widgetAdapter = WidgetAdapter(requireContext(), appWidgetHost) { appWidgetId ->
            showWidgetOptionsDialog(appWidgetId)
        }

        binding.widgetContainer.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = widgetAdapter
        }

        // Load saved widgets
        val savedWidgetIds = loadWidgetIds()
        for (widgetId in savedWidgetIds) {
            addWidget(widgetId)
        }

        // Initialize the ActivityResultLaunchers
        pickWidgetLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    data?.let {
                        val appWidgetId = it.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
                        if (appWidgetId != -1) {
                            val appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
                            if (appWidgetInfo?.configure != null) {
                                val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)
                                intent.component = appWidgetInfo.configure
                                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                                createWidgetLauncher.launch(intent)
                            } else {
                                addWidget(appWidgetId)
                            }
                        }
                    }
                } else if (result.resultCode == Activity.RESULT_CANCELED) {
                    val appWidgetId =
                        result.data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
                    if (appWidgetId != null && appWidgetId != -1) {
                        appWidgetHost.deleteAppWidgetId(appWidgetId)
                    }
                }
            }

        createWidgetLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    data?.let {
                        val appWidgetId = it.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
                        if (appWidgetId != -1) {
                            addWidget(appWidgetId)
                        }
                    }
                } else if (result.resultCode == Activity.RESULT_CANCELED) {
                    val appWidgetId =
                        result.data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
                    if (appWidgetId != null && appWidgetId != -1) {
                        appWidgetHost.deleteAppWidgetId(appWidgetId)
                    }
                }
            }

        binding.widgetParent.setOnTouchListener(getSwipeGestureListener(context))
    }

    private fun getSwipeGestureListener(context: Context): View.OnTouchListener {
        return object : OnSwipeTouchListener(context) {
            override fun onLongClick() {
                super.onLongClick()
                selectWidget()
            }

            override fun onSwipeLeft() {
                super.onSwipeLeft()
                findNavController().popBackStack()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                findNavController().popBackStack()
            }
        }
    }

    private fun selectWidget() {
        val appWidgetId = appWidgetHost.allocateAppWidgetId()
        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        pickWidgetLauncher.launch(intent)
    }


    private fun addWidget(appWidgetId: Int) {
        val appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
        appWidgetInfo?.let {
            val widgetItem = WidgetItem(appWidgetId, it)
            widgetAdapter.addWidget(widgetItem)
            saveWidgetIds(widgetAdapter.getWidgetIds()) // Save the updated widget IDs
        } ?: Log.e("WidgetManagerFragment", "Failed to get app widget info for ID: $appWidgetId")
    }

    override fun onRemoveWidget(appWidgetId: Int) {
        appWidgetHost.deleteAppWidgetId(appWidgetId)
        widgetAdapter.removeWidget(appWidgetId)
        saveWidgetIds(widgetAdapter.getWidgetIds())
    }

    private fun showWidgetOptionsDialog(appWidgetId: Int) {
        val dialog = WidgetOptionsDialogFragment.newInstance(appWidgetId)
        dialog.show(childFragmentManager, "WidgetOptionsDialog")
    }

    override fun onOpenAppWidget(appWidgetId: Int) {
        // Implement the logic to open the widget's configuration or app
        // This is typically dependent on the widget and its associated app
    }

    private fun saveWidgetIds(widgetIds: List<Int>) {
        val sharedPreferences =
            requireContext().getSharedPreferences(Constants.WIDGETS_PREFS, Activity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val widgetIdsSet = widgetIds.map { it.toString() }.toSet()
        editor.putStringSet(Constants.APP_WIDGETS_ID, widgetIdsSet)
        editor.apply()
    }

    private fun loadWidgetIds(): List<Int> {
        val sharedPreferences =
            requireContext().getSharedPreferences(Constants.WIDGETS_PREFS, Activity.MODE_PRIVATE)
        val widgetIdsSet = sharedPreferences.getStringSet(Constants.APP_WIDGETS_ID, emptySet())
        return widgetIdsSet?.map { it.toInt() } ?: emptyList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

