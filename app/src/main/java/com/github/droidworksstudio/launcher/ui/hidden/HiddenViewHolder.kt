package com.github.droidworksstudio.launcher.ui.hidden

import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
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
            // Get the current LayoutParams of appHiddenName
            val layoutParams = appHiddenName.layoutParams as LinearLayoutCompat.LayoutParams

            // Set the margins
            layoutParams.topMargin = preferenceHelper.homeAppPadding.toInt()
            layoutParams.bottomMargin = preferenceHelper.homeAppPadding.toInt()

            appHiddenName.layoutParams = layoutParams
            appHiddenName.text = appInfo.appName
            appHiddenName.setTextColor(preferenceHelper.appColor)
            appHiddenName.textSize = preferenceHelper.appTextSize
            appHiddenName.gravity = Gravity.START
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