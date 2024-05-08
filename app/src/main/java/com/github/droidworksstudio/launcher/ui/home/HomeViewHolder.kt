package com.github.droidworksstudio.launcher.ui.home

import android.content.Context
import android.util.Log
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.databinding.ItemHomeBinding
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnItemClickedListener
import javax.inject.Inject


class HomeViewHolder @Inject constructor(
    private val binding: ItemHomeBinding,
    private val onAppClickedListener: OnItemClickedListener.OnAppsClickedListener,
    private val onAppLongClickedListener: OnItemClickedListener.OnAppLongClickedListener,
    private val preferenceHelper: PreferenceHelper,
) : RecyclerView.ViewHolder(binding.root) {
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

            appHomeName.layoutParams = layoutParams
            appHomeName.text = appInfo.appName
            appHomeName.setTextColor(preferenceHelper.appColor)
            appHomeName.textSize = preferenceHelper.appTextSize
            Log.d("Tag", "Home Adapter Color: ${preferenceHelper.appColor}")

            val appIcon = binding.root.context.packageManager.getApplicationIcon(appInfo.packageName)
            appHomeIcon.setImageDrawable(appIcon)
            appHomeIcon.layoutParams.width = preferenceHelper.appTextSize.toInt() * 3
            appHomeIcon.layoutParams.height = preferenceHelper.appTextSize.toInt() * 3
            appHomeIcon.visibility = View.GONE
        }

        itemView.setOnClickListener { onAppClickedListener.onAppClicked(appInfo) }

        itemView.setOnLongClickListener {
            onAppLongClickedListener.onAppLongClicked(appInfo)
            true
        }
    }
}
