package com.github.droidworksstudio.launcher.ui.home

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
    private val preferenceHelper: PreferenceHelper
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(appInfo: AppInfo) {

        binding.apply {
            val layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = preferenceHelper.homeAppAlignment
            }

            appHomeName.layoutParams = layoutParams
            appHomeName.text = appInfo.appName
            appHomeName.setTextColor(preferenceHelper.appColor)
            appHomeName.textSize = preferenceHelper.appTextSize
            Log.d("Tag", "Home Adapter Color: ${preferenceHelper.appColor}")
            appHomeIcon.visibility = View.GONE
        }

        itemView.setOnClickListener { onAppClickedListener.onAppClicked(appInfo) }

        itemView.setOnLongClickListener {
            onAppLongClickedListener.onAppLongClicked(appInfo)
            true
        }
    }
}
