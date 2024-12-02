package com.github.droidworksstudio.launcher.ui.drawer

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.droidworksstudio.common.hideKeyboard
import com.github.droidworksstudio.common.launchApp
import com.github.droidworksstudio.common.openSearch
import com.github.droidworksstudio.common.searchCustomSearchEngine
import com.github.droidworksstudio.common.searchOnPlayStore
import com.github.droidworksstudio.common.showKeyboard
import com.github.droidworksstudio.common.showLongToast
import com.github.droidworksstudio.fuzzywuzzy.FuzzyFinder
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.adapter.drawer.DrawAdapter
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.databinding.FragmentDrawBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.BiometricHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnItemClickedListener
import com.github.droidworksstudio.launcher.listener.OnSwipeTouchListener
import com.github.droidworksstudio.launcher.listener.ScrollEventListener
import com.github.droidworksstudio.launcher.ui.bottomsheetdialog.AppInfoBottomSheetFragment
import com.github.droidworksstudio.launcher.utils.Constants
import com.github.droidworksstudio.launcher.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class DrawFragment : Fragment(),
    OnItemClickedListener.OnAppsClickedListener,
    OnItemClickedListener.OnAppLongClickedListener,
    OnItemClickedListener.OnAppStateClickListener,
    BiometricHelper.Callback, ScrollEventListener {
    private var _binding: FragmentDrawBinding? = null

    private val binding get() = _binding!!

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var appHelper: AppHelper

    @Inject
    lateinit var fingerHelper: BiometricHelper

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
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentDrawBinding.inflate(inflater, container, false)

        return binding.root

    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appHelper.dayNightMod(requireContext(), binding.drawBackground)
        super.onViewCreated(view, savedInstanceState)

        context = requireContext()
        setupRecyclerView()
        setupSearch()
        observeClickListener()
        observeSwipeTouchListener()
        observeScrollTouchListener()

        // Initialize observation of drawer apps
        observeDrawerApps()
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
        // Start comparing installed app information
        viewModel.compareInstalledAppInfo()

        // Launch a coroutine tied to the lifecycle of the view
        viewLifecycleOwner.lifecycleScope.launch {
            // Use repeatOnLifecycle to manage the lifecycle state
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                // Collect the drawer apps from the ViewModel
                viewModel.drawApps.collect { apps ->
                    // Update the adapter with the new list of apps
                    drawAdapter.submitList(apps)
                    // Update the adapter's data with the new state flow
                    drawAdapter.updateDataWithStateFlow(apps)
                }
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
                            searchApp(trimmedQuery, false)
                            return true // Exit the function
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchApp(newText.toString(), true)
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
        binding.apply {
            mainView.setOnTouchListener(getSwipeGestureListener(context))
            drawAdapter.setOnTouchListener(getSwipeGestureListener(context))
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun observeScrollTouchListener() {
        binding.apply {
            drawAdapter.addOnScrollListener(getRecyclerViewOnScrollListener())
        }
    }

    private fun getRecyclerViewOnScrollListener(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            var onTop = false

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        onTop = !recyclerView.canScrollVertically(-1)
                        if (onTop) binding.searchViewText.hideKeyboard()
                        if (onTop && !recyclerView.canScrollVertically(1)) {
                            val actionTypeNavOptions: NavOptions? =
                                if (preferenceHelper.disableAnimations) null
                                else appHelper.getActionType(Constants.Swipe.Up)

                            Handler(Looper.getMainLooper()).post {
                                findNavController().navigate(
                                    R.id.HomeFragment,
                                    null,
                                    actionTypeNavOptions
                                )
                            }
                        }
                    }

                    RecyclerView.SCROLL_STATE_IDLE -> {
                        if (!recyclerView.canScrollVertically(1)) {
                            binding.searchViewText.hideKeyboard()
                        } else if (!recyclerView.canScrollVertically(-1)) {
                            if (onTop) {
                                val actionTypeNavOptions: NavOptions? =
                                    if (preferenceHelper.disableAnimations) null
                                    else appHelper.getActionType(Constants.Swipe.Up)

                                Handler(Looper.getMainLooper()).post {
                                    findNavController().navigate(
                                        R.id.HomeFragment,
                                        null,
                                        actionTypeNavOptions
                                    )
                                }
                            } else binding.searchViewText.showKeyboard()
                        }
                    }
                }
            }
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

    private fun searchApp(query: String, isSearching: Boolean) {
        // Launch a coroutine tied to the lifecycle of the view
        viewLifecycleOwner.lifecycleScope.launch {
            // Repeat the block when the lifecycle is at least CREATED
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                val trimmedQuery = query.trim()

                // Collect search results from the ViewModel
                viewModel.searchAppInfo().collect { searchResults ->
                    // Filter and score results using FuzzyFinder
                    val filteredResults = searchResults
                        .map { appInfo ->
                            val score = FuzzyFinder.scoreApp(appInfo, trimmedQuery, Constants.FILTER_STRENGTH_MAX)
                            appInfo to score // Pairing app info with its score
                        }
                        .filter { it.second > 25 } // Only keep results with a positive score
                        .sortedByDescending { it.second } // Sort results by score, descending

                    // Applying additional filtering based on preferences
                    val scoredApps = filteredResults.toMap()

                    val finalResults = if (preferenceHelper.filterStrength >= 1) {
                        // Filtering based on score strength
                        if (preferenceHelper.searchFromStart) {
                            // Filter apps that start with the search query and score higher than the filter strength
                            scoredApps.filter { (app, _) ->
                                app.appName.startsWith(trimmedQuery, ignoreCase = true)
                            }
                                .filter { (_, score) -> score > preferenceHelper.filterStrength }
                                .map { it.key }
                                .toMutableList()
                        } else {
                            // Filter based on score strength alone
                            scoredApps.filterValues { it > preferenceHelper.filterStrength }
                                .keys
                                .toMutableList()
                        }
                    } else {
                        // If filter strength is less than 1, normalize app names for both cases
                        searchResults.filter { app ->
                            FuzzyFinder.normalizeString(app.appName, trimmedQuery)
                        }.toMutableList()
                    }


                    val numberOfItemsLeft = finalResults.size
                    val appResults = finalResults.firstOrNull()

                    if (isSearching) {
                        when (numberOfItemsLeft) {
                            1 -> {
                                appResults?.let { appInfo ->
                                    if (preferenceHelper.automaticOpenApp) observeBioAuthCheck(appInfo)
                                }
                                drawAdapter.submitList(finalResults)
                            }

                            else -> {
                                drawAdapter.submitList(finalResults)
                            }
                        }
                        if (trimmedQuery.isEmpty()) {
                            drawAdapter.submitList(searchResults)
                        }
                    } else {
                        if (numberOfItemsLeft == 0 && !requireContext().searchOnPlayStore(trimmedQuery)) {
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
        }
    }

    private fun showSelectedApp(appInfo: AppInfo) {
        val bottomSheetFragment = AppInfoBottomSheetFragment(appInfo)
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
        if (preferenceHelper.automaticKeyboard) binding.searchViewText.showKeyboard()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStart() {
        super.onStart()
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
    }

    private fun observeBioAuthCheck(appInfo: AppInfo) {
        if (!appInfo.lock) {
            context.launchApp(appInfo)
        } else {
            fingerHelper.startBiometricAuth(appInfo, this)
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

