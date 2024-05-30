package com.github.droidworksstudio.launcher.ui.hidden

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.databinding.ItemHiddenBinding
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnItemClickedListener

class HiddenViewHolder(
    private val binding: ItemHiddenBinding,
    private val onAppClickedListener: OnItemClickedListener.OnAppsClickedListener,
    private val onAppLongClickedListener: OnItemClickedListener.OnAppLongClickedListener,
    private val preferenceHelper: PreferenceHelper,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(appInfo: AppInfo) {
        binding.apply {
            appHiddenName.text = appInfo.appName
            Log.d("Tag", "Draw Adapter: ${appInfo.appName}")

            if (preferenceHelper.showAppIcon) {
                val appIcon =
                    binding.root.context.packageManager.getApplicationIcon(appInfo.packageName)
                appHiddenLeftIcon.setImageDrawable(appIcon)
                appHiddenLeftIcon.layoutParams.width =
                    preferenceHelper.appTextSize.toInt() * 3
                appHiddenLeftIcon.layoutParams.height =
                    preferenceHelper.appTextSize.toInt() * 3
                appHiddenLeftIcon.visibility = View.VISIBLE
            }
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