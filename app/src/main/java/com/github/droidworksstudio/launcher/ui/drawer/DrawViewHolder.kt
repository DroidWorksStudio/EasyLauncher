package com.github.droidworksstudio.launcher.ui.drawer

import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.droidworksstudio.ktx.dpToPx
import com.github.droidworksstudio.ktx.isWorkProfileEnabled
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