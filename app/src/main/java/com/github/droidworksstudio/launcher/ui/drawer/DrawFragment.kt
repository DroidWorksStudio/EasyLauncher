package com.github.droidworksstudio.launcher.ui.drawer

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.droidworksstudio.common.hideKeyboard
import com.github.droidworksstudio.common.launchApp
import com.github.droidworksstudio.common.openSearch
import com.github.droidworksstudio.common.searchCustomSearchEngine
import com.github.droidworksstudio.common.searchOnPlayStore
import com.github.droidworksstudio.common.showKeyboard
import com.github.droidworksstudio.common.showLongToast
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.databinding.FragmentDrawBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.FingerprintHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnItemClickedListener
import com.github.droidworksstudio.launcher.listener.OnSwipeTouchListener
import com.github.droidworksstudio.launcher.listener.ScrollEventListener
import com.github.droidworksstudio.launcher.ui.bottomsheetdialog.AppInfoBottomSheetFragment
import com.github.droidworksstudio.launcher.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class DrawFragment : Fragment(),
    OnItemClickedListener.OnAppsClickedListener,
    OnItemClickedListener.OnAppLongClickedListener,
    OnItemClickedListener.BottomSheetDismissListener,
    OnItemClickedListener.OnAppStateClickListener,
    FingerprintHelper.Callback, ScrollEventListener {
    private var _binding: FragmentDrawBinding? = null

    private val binding get() = _binding!!

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appHelper: AppHelper

    @Inject
    lateinit var fingerHelper: FingerprintHelper

    private val viewModel: AppViewModel by viewModels()

    private val drawAdapter: DrawAdapter by lazy {
        DrawAdapter(
            this,
            this,
            preferenceHelper
        )
    }

    private lateinit var context: Context
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDrawBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appHelper.dayNightMod(requireContext(), binding.drawBackground)
        super.onViewCreated(view, savedInstanceState)

        context = requireContext()
        setupRecyclerView()
        setupSearch()
        observeClickListener()
        observeSwipeTouchListener()
    }

    private fun setupRecyclerView() {

        binding.drawAdapter.apply {
            adapter = drawAdapter
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun observeDrawerApps() {
        viewModel.compareInstalledAppInfo()

        @Suppress("DEPRECATION")
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.drawApps.collect {
                drawAdapter.submitList(it)
                drawAdapter.updateDataWithStateFlow(it)
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun setupSearch() {
        binding.searchViewText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    val trimmedQuery = it.trim()
                    if (trimmedQuery.isNotEmpty()) {
                        if (trimmedQuery.startsWith("!")) {
                            val searchQuery = trimmedQuery.substringAfter("!")
                            requireContext().searchCustomSearchEngine(preferenceHelper, searchQuery)
                        } else {
                            checkAppThenRun(trimmedQuery)
                            return true // Exit the function
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchApp(newText.toString())
                return true
            }
        })

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun observeClickListener() {
        binding.drawSearchButton.setOnClickListener {
            binding.searchViewText.showKeyboard()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun observeSwipeTouchListener() {
        binding.touchArea.setOnTouchListener(getSwipeGestureListener(context))
        binding.drawAdapter.setOnTouchListener(getSwipeGestureListener(context))
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

    private fun checkAppThenRun(query: String) {
        val searchQuery = "%$query%"
        @Suppress("DEPRECATION")
        viewLifecycleOwner.lifecycle.coroutineScope.launchWhenCreated {
            val trimmedQuery = searchQuery.trim()
            viewModel.searchAppInfo(trimmedQuery).collect { searchResults ->
                val numberOfItemsLeft = searchResults.size
                val appResults = searchResults.firstOrNull()
                if (numberOfItemsLeft == 0 && !requireContext().searchOnPlayStore(
                        trimmedQuery
                    )
                ) {
                    requireContext().openSearch(trimmedQuery)
                } else {
                    appResults?.let { appInfo ->
                        observeBioAuthCheck(appInfo)
                    }
                    drawAdapter.submitList(searchResults)
                }
            }
        }
    }


    private fun searchApp(query: String) {
        val searchQuery = "%$query%"
        @Suppress("DEPRECATION")
        viewLifecycleOwner.lifecycle.coroutineScope.launchWhenCreated {
            viewModel.searchAppInfo(searchQuery).collect { searchResults ->
                val numberOfItemsLeft = searchResults.size
                val appResults = searchResults.firstOrNull()
                when (numberOfItemsLeft) {
                    1 -> {
                        appResults?.let { appInfo ->
                            if (preferenceHelper.automaticOpenApp) observeBioAuthCheck(appInfo)
                        }
                        drawAdapter.submitList(searchResults)
                    }

                    else -> {
                        drawAdapter.submitList(searchResults)
                    }
                }
            }
        }
    }


    private fun showSelectedApp(appInfo: AppInfo) {
        binding.searchViewText.setQuery("", false)

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
        binding.searchViewText.setQuery("", false)
        binding.searchViewText.hideKeyboard()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        observeDrawerApps()
        if (preferenceHelper.automaticKeyboard) binding.searchViewText.showKeyboard()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStart() {
        super.onStart()
        observeDrawerApps()
    }

    override fun onStop() {
        super.onStop()
        binding.searchViewText.hideKeyboard()
    }

    override fun onAppClicked(appInfo: AppInfo) {
        observeBioAuthCheck(appInfo)
    }

    override fun onAppLongClicked(appInfo: AppInfo) {
        showSelectedApp(appInfo)
    }

    override fun onAppStateClicked(appInfo: AppInfo) {
        viewModel.update(appInfo)
        Log.d("Tag", "${appInfo.appName} : Draw Favorite: ${appInfo.favorite}")
    }

    private fun observeBioAuthCheck(appInfo: AppInfo) {
        if (!appInfo.lock) {
            context.launchApp(appInfo)
        } else {
            fingerHelper.startFingerprintAuth(appInfo, this)
        }
    }

    override fun onAuthenticationSucceeded(appInfo: AppInfo) {
        context.showLongToast(getString(R.string.authentication_succeeded))
        context.launchApp(appInfo)
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

