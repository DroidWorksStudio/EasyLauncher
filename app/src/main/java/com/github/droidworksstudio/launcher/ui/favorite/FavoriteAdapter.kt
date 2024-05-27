package com.github.droidworksstudio.launcher.ui.favorite

import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.databinding.ItemFavoriteBinding
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnItemClickedListener
import com.github.droidworksstudio.launcher.listener.OnItemMoveListener

class FavoriteAdapter(
    private val onAppClickedListener:
    OnItemClickedListener.OnAppsClickedListener,
    private val preferenceHelperProvider: PreferenceHelper
) : ListAdapter<AppInfo, RecyclerView.ViewHolder>(DiffCallback()),
    OnItemMoveListener.OnItemActionListener {

    private lateinit var touchHelper: ItemTouchHelper

    override fun onCreateViewHolder(
        parent: android.view.ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val binding = ItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val preferenceHelper = preferenceHelperProvider
        return FavoriteViewHolder(
            binding,
            onAppClickedListener,
            preferenceHelper,
            touchHelper
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)
        when (holder) {
            is FavoriteViewHolder -> {
                holder.bind(currentItem)
            }
        }
    }

    class DiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo) =
            oldItem == newItem
    }

    fun setItemTouchHelper(touchHelper: ItemTouchHelper) {
        this.touchHelper = touchHelper
    }

    override fun onViewMoved(oldPosition: Int, newPosition: Int): Boolean {
        Log.d("Tag", "List Adapter$newPosition")
        return false
    }

    override fun onViewSwiped(position: Int) {
        Log.d("Tag", "onViewMoved")
    }

}