package com.github.droidworksstudio.launcher.ui.hidden

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.droidworksstudio.ktx.showLongToast
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.databinding.FragmentHiddenBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.FingerprintHelper
import com.github.droidworksstudio.launcher.listener.OnItemClickedListener
import com.github.droidworksstudio.launcher.listener.OnSwipeTouchListener
import com.github.droidworksstudio.launcher.ui.bottomsheetdialog.AppInfoBottomSheetFragment
import com.github.droidworksstudio.launcher.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HiddenFragment : Fragment(),
    OnItemClickedListener.OnAppsClickedListener,
    OnItemClickedListener.OnAppLongClickedListener,
    OnItemClickedListener.BottomSheetDismissListener,
    OnItemClickedListener.OnAppStateClickListener,
    FingerprintHelper.Callback {
    private var _binding: FragmentHiddenBinding? = null

    private val binding get() = _binding!!

    private val hiddenAdapter: HiddenAdapter by lazy { HiddenAdapter(this, this) }

    private val viewModel: AppViewModel by viewModels()

    private lateinit var context: Context

    @Inject
    lateinit var fingerHelper: FingerprintHelper

    @Inject
    lateinit var appHelper: AppHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHiddenBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appHelper.dayNightMod(requireContext(), binding.hiddenView)
        super.onViewCreated(view, savedInstanceState)

        context = requireContext()

        setupRecyclerView()
        observeHiddenApps()
        observeSwipeTouchListener()
    }

    private fun setupRecyclerView() {
        binding.hiddenAdapter.apply {
            adapter = hiddenAdapter
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(false)
        }

    }

    private fun observeHiddenApps() {
        viewModel.compareInstalledAppInfo()
        @Suppress("DEPRECATION")
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.hiddenApps.collect {
                hiddenAdapter.updateData(it)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun observeSwipeTouchListener() {
        binding.touchArea.setOnTouchListener(getSwipeGestureListener(context))
        binding.hiddenAdapter.setOnTouchListener(getSwipeGestureListener(context))
    }

    private fun getSwipeGestureListener(context: Context): View.OnTouchListener {
        return object : OnSwipeTouchListener(context) {
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

    private fun observeBioAuthCheck(appInfo: AppInfo) {
        if (!appInfo.lock) {
            appHelper.launchApp(context, appInfo)
        } else {
            fingerHelper.startFingerprintAuth(appInfo, this)
        }
    }

    private fun showSelectedApp(appInfo: AppInfo) {
        val bottomSheetFragment = AppInfoBottomSheetFragment(appInfo)
        bottomSheetFragment.setOnBottomSheetDismissedListener(this)
        bottomSheetFragment.setOnAppStateClickListener(this)
        bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        binding.hiddenAdapter.scrollToPosition(0)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        observeHiddenApps()
    }

    override fun onAppLongClicked(appInfo: AppInfo) {
        showSelectedApp(appInfo)
    }

    override fun onAppStateClicked(appInfo: AppInfo) {
        viewModel.update(appInfo)
    }

    override fun onAppClicked(appInfo: AppInfo) {
        observeBioAuthCheck(appInfo)
    }

    override fun onAuthenticationSucceeded(appInfo: AppInfo) {
        context.showLongToast(getString(R.string.authentication_succeeded))
        appHelper.launchApp(context, appInfo)
    }

    override fun onAuthenticationFailed() {
        context.showLongToast(getString(R.string.authentication_failed))
    }

    override fun onAuthenticationError(errorCode: Int, errorMessage: CharSequence?) {
        context.showLongToast(
            getString(R.string.authentication_error).format(
                errorMessage,
                errorCode
            )
        )
    }
}