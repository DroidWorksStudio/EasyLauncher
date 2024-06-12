package com.github.droidworksstudio.launcher.ui.favorite

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.droidworksstudio.common.launchApp
import com.github.droidworksstudio.common.showLongToast
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.databinding.FragmentFavoriteBinding
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.FingerprintHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnItemClickedListener
import com.github.droidworksstudio.launcher.listener.OnItemMoveListener
import com.github.droidworksstudio.launcher.listener.OnSwipeTouchListener
import com.github.droidworksstudio.launcher.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject


@AndroidEntryPoint
class FavoriteFragment : Fragment(),
    OnItemClickedListener.OnAppsClickedListener,
    OnItemClickedListener.BottomSheetDismissListener,
    OnItemClickedListener.OnAppStateClickListener,
    OnItemMoveListener.OnItemActionListener,
    FingerprintHelper.Callback {
    private var _binding: FragmentFavoriteBinding? = null

    private val binding get() = _binding!!

    private val viewModel: AppViewModel by viewModels()

    private lateinit var context: Context

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var fingerHelper: FingerprintHelper

    @Inject
    lateinit var appHelper: AppHelper

    private val favoriteAdapter: FavoriteAdapter by lazy { FavoriteAdapter(this, preferenceHelper) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appHelper.dayNightMod(requireContext(), binding.favoriteView)
        super.onViewCreated(view, savedInstanceState)

        context = requireContext()

        setupRecyclerView()
        observeFavorite()
        observeHomeAppOrder()
        observeSwipeTouchListener()
    }

    private fun setupRecyclerView() {

        binding.favoriteAdapter.apply {
            adapter = favoriteAdapter
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(false)
        }
    }

    private fun handleDragAndDrop(oldPosition: Int, newPosition: Int) {
        val items = favoriteAdapter.currentList.toMutableList()
        Collections.swap(items, oldPosition, newPosition)

        items.forEachIndexed { index, appInfo ->
            appInfo.appOrder = index
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateAppOrder(items)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun observeFavorite() {
        viewModel.compareInstalledAppInfo()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteApps.collect {
                favoriteAdapter.submitList(it)
            }
        }
    }

    private fun observeHomeAppOrder() {
        binding.favoriteAdapter.adapter = favoriteAdapter
        val listener: OnItemMoveListener.OnItemActionListener = favoriteAdapter

        val simpleItemTouchCallback = object : ItemTouchHelper.Callback() {

            override fun onChildDraw(
                canvas: Canvas, recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder, dX: Float,
                dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                if (isCurrentlyActive) {
                    viewHolder.itemView.alpha = 0.5f
                } else {
                    viewHolder.itemView.alpha = 1f
                }
                super.onChildDraw(
                    canvas, recyclerView, viewHolder,
                    dX, dY,
                    actionState, isCurrentlyActive
                )
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = 0
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                val oldPosition = viewHolder.bindingAdapterPosition
                val newPosition = target.bindingAdapterPosition

                handleDragAndDrop(oldPosition, newPosition)

                return listener.onViewMoved(
                    viewHolder.bindingAdapterPosition,
                    target.bindingAdapterPosition
                )

            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                @Suppress("DEPRECATION")
                listener.onViewSwiped(viewHolder.adapterPosition)
            }

            override fun isLongPressDragEnabled() = false
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)

        favoriteAdapter.setItemTouchHelper(itemTouchHelper)
        itemTouchHelper.attachToRecyclerView(binding.favoriteAdapter)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun observeSwipeTouchListener() {
        binding.fragmentContainer.setOnTouchListener(getSwipeGestureListener(context))
        binding.favoriteAdapter.setOnTouchListener(getSwipeGestureListener(context))
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

    private fun observeBioAuthCheck(appInfo: AppInfo) {
        if (!appInfo.lock) {
            context.launchApp(appInfo)
        } else {
            fingerHelper.startFingerprintAuth(appInfo, this)
        }
    }

    override fun onAppStateClicked(appInfo: AppInfo) {
        viewModel.update(appInfo)
    }

    override fun onAppClicked(appInfo: AppInfo) {
        observeBioAuthCheck(appInfo)
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

    override fun onViewMoved(oldPosition: Int, newPosition: Int): Boolean {
        return true
    }
}