package com.github.droidworksstudio.launcher.ui.hidden

import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.databinding.ItemHiddenBinding
import com.github.droidworksstudio.launcher.listener.OnItemClickedListener

class HiddenAdapter(
    private val onAppClickedListener: OnItemClickedListener.OnAppsClickedListener,
    private val onAppLongClickedListener: OnItemClickedListener.OnAppLongClickedListener
) : ListAdapter<AppInfo, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: android.view.ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val binding = ItemHiddenBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HiddenViewHolder(binding, onAppClickedListener, onAppLongClickedListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)
        when (holder) {
            is HiddenViewHolder -> {

                val appInfo = getItem(position) as AppInfo
                holder.bind(appInfo)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo) =
            oldItem == newItem
    }

    fun updateData(newData: List<AppInfo>) {
        notifyItemChanged(newData.size)
        submitList(newData)
        Log.d("Tag", "Collected Hidden Adapter : $newData")
    }
}