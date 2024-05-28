package com.github.droidworksstudio.launcher.ui.drawer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.databinding.ItemDrawBinding
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnItemClickedListener

class DrawAdapter(
    private val onAppClickedListener: OnItemClickedListener.OnAppsClickedListener,
    private val onAppLongClickedListener: OnItemClickedListener.OnAppLongClickedListener,
    private val preferenceHelperProvider: PreferenceHelper
) :
    ListAdapter<AppInfo, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: android.view.ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val binding = ItemDrawBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val preferenceHelper = preferenceHelperProvider

        return DrawViewHolder(
            binding,
            onAppClickedListener,
            onAppLongClickedListener,
            preferenceHelper
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val appInfo = getItem(position)
        (holder as DrawViewHolder).bind(appInfo)
    }

    class DiffCallback : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo) =
            oldItem == newItem
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDataWithStateFlow(newData: List<AppInfo>) {
        submitList(newData.toMutableList())
        notifyDataSetChanged()
    }
}