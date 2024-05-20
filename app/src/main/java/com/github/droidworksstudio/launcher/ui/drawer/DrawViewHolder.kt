package com.github.droidworksstudio.launcher.ui.drawer

import android.util.Log
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.databinding.ItemDrawBinding
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnItemClickedListener

class DrawViewHolder(private val binding: ItemDrawBinding,
                     private val onAppClickedListener: OnItemClickedListener.OnAppsClickedListener,
                     private val onAppLongClickedListener: OnItemClickedListener.OnAppLongClickedListener,
                     private val preferenceHelper: PreferenceHelper):
    RecyclerView.ViewHolder(binding.root) {
    fun bind(appInfo: AppInfo) {
        binding.apply {
            val layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = preferenceHelper.homeAppAlignment
                topMargin = preferenceHelper.homeAppPadding.toInt()
                bottomMargin = preferenceHelper.homeAppPadding.toInt()
            }

            appDrawName.layoutParams = layoutParams
            appDrawName.text = appInfo.appName
            appDrawName.setTextColor(preferenceHelper.appColor)
            appDrawName.textSize = preferenceHelper.appTextSize
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