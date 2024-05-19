package com.github.droidworksstudio.launcher.ui.drawer

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.databinding.ItemDrawBinding
import com.github.droidworksstudio.launcher.listener.OnItemClickedListener

class DrawViewHolder(private val binding: ItemDrawBinding,
                     private val onAppClickedListener: OnItemClickedListener.OnAppsClickedListener,
                     private val onAppLongClickedListener: OnItemClickedListener.OnAppLongClickedListener) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(appInfo: AppInfo) {
        binding.apply {
            appDrawName.text = appInfo.appName
            Log.d("Tag", "Draw Adapter: ${appInfo.appName + appInfo.id}")
        }

        itemView.setOnClickListener {
            onAppClickedListener.onAppClicked(appInfo)
        }

        itemView.setOnLongClickListener {
            onAppLongClickedListener.onAppLongClicked(appInfo)
            true
        }
    }
}