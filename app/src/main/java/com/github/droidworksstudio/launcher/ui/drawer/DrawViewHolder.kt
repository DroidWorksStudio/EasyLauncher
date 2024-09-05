package com.github.droidworksstudio.launcher.ui.drawer

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.droidworksstudio.common.ColorIconsExtensions
import com.github.droidworksstudio.common.dpToPx
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
            val layoutAppNameParams = appDrawName.layoutParams as LinearLayoutCompat.LayoutParams

            // Set the margins
            layoutAppNameParams.topMargin = preferenceHelper.homeAppPadding.toInt()
            layoutAppNameParams.bottomMargin = preferenceHelper.homeAppPadding.toInt()

            appDrawName.layoutParams = layoutAppNameParams

            // Update appDrawName properties
            appDrawName.text = appInfo.appName
            appDrawName.setTextColor(preferenceHelper.appColor)
            appDrawName.textSize = preferenceHelper.appTextSize
            appDrawName.gravity = preferenceHelper.homeAppAlignment

            if (preferenceHelper.showAppIcon) {
                val pm: PackageManager = binding.root.context.packageManager
                val appIcon = pm.getApplicationIcon(appInfo.packageName)
                val appIconSize = (preferenceHelper.appTextSize * if (preferenceHelper.showAppIconAsDots) 1.1f else 2f).toInt()

                val layoutParams = LinearLayoutCompat.LayoutParams(appIconSize, appIconSize)
                val appNewIcon: Drawable? = if (preferenceHelper.showAppIconAsDots) {
                    val newIcon = ContextCompat.getDrawable(itemView.context, R.drawable.app_dot_icon)!!
                    val bitmap = ColorIconsExtensions.drawableToBitmap(appIcon)
                    val dominantColor = ColorIconsExtensions.getDominantColor(bitmap)
                    ColorIconsExtensions.recolorDrawable(newIcon, dominantColor)
                } else {
                    null
                }

                appDrawIcon.layoutParams = layoutParams
                appDrawIcon.setImageDrawable(appNewIcon ?: appIcon)
                appDrawIcon.visibility = View.VISIBLE

                val parentLayout = appDrawName.parent as LinearLayoutCompat
                parentLayout.orientation = LinearLayoutCompat.HORIZONTAL
                parentLayout.removeAllViews()

                when (preferenceHelper.homeAppAlignment) {
                    Gravity.START -> {
                        layoutParams.marginEnd = 10.dpToPx()
                        parentLayout.addView(appDrawIcon)
                        parentLayout.addView(appDrawName)
                    }

                    Gravity.END -> {
                        layoutParams.marginStart = 10.dpToPx()
                        parentLayout.addView(appDrawName)
                        parentLayout.addView(appDrawIcon)
                    }

                    else -> {
                        appDrawIcon.visibility = View.GONE
                    }
                }
            } else {
                appDrawIcon.visibility = View.GONE
            }
        }

        itemView.setOnClickListener {
            onAppClickedListener.onAppClicked(appInfo)
        }

        itemView.setOnLongClickListener {
            if (appInfo.userHandle <= 0) {
                onAppLongClickedListener.onAppLongClicked(appInfo)
            }
            true
        }
    }
}