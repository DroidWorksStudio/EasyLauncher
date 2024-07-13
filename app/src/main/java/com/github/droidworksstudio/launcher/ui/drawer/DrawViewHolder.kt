package com.github.droidworksstudio.launcher.ui.drawer

import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.droidworksstudio.common.dpToPx
import com.github.droidworksstudio.common.isWorkProfileEnabled
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.databinding.ItemDrawBinding
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnItemClickedListener

class DrawViewHolder(
    private val binding: ItemDrawBinding,
    private val onAppClickedListener: OnItemClickedListener.OnAppsClickedListener,
    private val onAppLongClickedListener: OnItemClickedListener.OnAppLongClickedListener,
    private val preferenceHelper: PreferenceHelper
) :

    RecyclerView.ViewHolder(binding.root) {

    fun bind(appInfo: AppInfo) {
        binding.apply {
            // Get the current LayoutParams of appHiddenName
            val layoutParams = appDrawName.layoutParams as LinearLayoutCompat.LayoutParams

            // Set the margins
            layoutParams.topMargin = preferenceHelper.homeAppPadding.toInt()
            layoutParams.bottomMargin = preferenceHelper.homeAppPadding.toInt()

            appDrawName.layoutParams = layoutParams
            appDrawName.text = appInfo.appName
            appDrawName.setTextColor(preferenceHelper.appColor)
            appDrawName.textSize = preferenceHelper.appTextSize
            appDrawName.gravity = preferenceHelper.homeAppAlignment
            Log.d("Tag", "Draw Adapter: ${appInfo.appName + appInfo.id} | ${appInfo.work}")
            val icon = AppCompatResources.getDrawable(appDrawName.context, R.drawable.work_profile)
            val px = preferenceHelper.appTextSize.toInt().dpToPx()
            icon?.setBounds(0, 0, px, px)
            if (appInfo.work) {
                val appLabelGravity = preferenceHelper.homeAppAlignment

                if (appLabelGravity == Gravity.START) {
                    appDrawName.setCompoundDrawables(icon, null, null, null)
                } else {
                    appDrawName.setCompoundDrawables(null, null, icon, null)
                }
                appDrawName.compoundDrawablePadding = 20
                if (!appDrawName.context.isWorkProfileEnabled()) {
                    appDrawName.visibility = View.GONE
                }
            } else {

                // If appInfo.work is false, remove the drawable
                appDrawName.setCompoundDrawables(null, null, null, null)
                appDrawName.compoundDrawablePadding = 0
            }

            if (preferenceHelper.showAppIcon) {
                val appIcon =
                    binding.root.context.packageManager.getApplicationIcon(appInfo.packageName)
                appDrawLeftIcon.setImageDrawable(appIcon)
                appDrawLeftIcon.layoutParams.width =
                    preferenceHelper.appTextSize.toInt() * 3
                appDrawLeftIcon.layoutParams.height =
                    preferenceHelper.appTextSize.toInt() * 3
                appDrawLeftIcon.visibility = View.VISIBLE
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