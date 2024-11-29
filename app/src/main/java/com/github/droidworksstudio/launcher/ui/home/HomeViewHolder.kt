package com.github.droidworksstudio.launcher.ui.home

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
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
import com.github.droidworksstudio.launcher.databinding.ItemHomeBinding
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnItemClickedListener
import com.github.droidworksstudio.launcher.utils.Constants
import javax.inject.Inject

class HomeViewHolder @Inject constructor(
    private val binding: ItemHomeBinding,
    private val onAppClickedListener: OnItemClickedListener.OnAppsClickedListener,
    private val onAppLongClickedListener: OnItemClickedListener.OnAppLongClickedListener,
    private val preferenceHelper: PreferenceHelper,
) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("ClickableViewAccessibility")
    fun bind(appInfo: AppInfo) {
        binding.apply {
            itemView.setBackgroundColor(Color.parseColor("#0000FF"))
            // Get the current LayoutParams of appHiddenName
            val layoutAppNameParams = appHomeName.layoutParams as LinearLayoutCompat.LayoutParams

            // Set the margins
            layoutAppNameParams.topMargin = preferenceHelper.homeAppPadding.toInt()
            layoutAppNameParams.bottomMargin = preferenceHelper.homeAppPadding.toInt()

            appHomeName.layoutParams = layoutAppNameParams

            // Update appHomeName properties
            appHomeName.text = appInfo.appName
            appHomeName.setTextColor(preferenceHelper.appColor)
            appHomeName.textSize = preferenceHelper.appTextSize
            appHomeName.gravity = preferenceHelper.homeAppAlignment

            if (preferenceHelper.showAppIcon) {
                val pm: PackageManager = binding.root.context.packageManager
                val appIcon = pm.getApplicationIcon(appInfo.packageName)
                val appIconSize = (preferenceHelper.appTextSize * if (preferenceHelper.iconPack == Constants.IconPacks.System) 2f else 1.1f).toInt()

                val layoutParams = LinearLayoutCompat.LayoutParams(appIconSize, appIconSize)
                val appNewIcon: Drawable? = if (preferenceHelper.iconPack == Constants.IconPacks.EasyDots) {
                    val newIcon = ContextCompat.getDrawable(itemView.context, R.drawable.app_easy_dot_icon)!!
                    val bitmap = ColorIconsExtensions.drawableToBitmap(appIcon)
                    val dominantColor = ColorIconsExtensions.getDominantColor(bitmap)
                    ColorIconsExtensions.recolorDrawable(newIcon, dominantColor)
                } else if (preferenceHelper.iconPack == Constants.IconPacks.NiagaraDots) {
                    val newIcon = ContextCompat.getDrawable(itemView.context, R.drawable.app_niagara_dot_icon)!!
                    val bitmap = ColorIconsExtensions.drawableToBitmap(appIcon)
                    val dominantColor = ColorIconsExtensions.getDominantColor(bitmap)
                    ColorIconsExtensions.recolorDrawable(newIcon, dominantColor)
                } else {
                    null
                }

                appHomeIcon.layoutParams = layoutParams
                appHomeIcon.setImageDrawable(appNewIcon ?: appIcon)
                appHomeIcon.visibility = View.VISIBLE

                val parentLayout = appHomeName.parent as LinearLayoutCompat
                parentLayout.orientation = LinearLayoutCompat.HORIZONTAL
                parentLayout.removeAllViews()

                when (preferenceHelper.homeAppAlignment) {
                    Gravity.START -> {
                        layoutParams.marginEnd = 10.dpToPx()
                        parentLayout.addView(appHomeIcon)
                        parentLayout.addView(appHomeName)
                    }

                    Gravity.END -> {
                        layoutParams.marginStart = 10.dpToPx()
                        parentLayout.addView(appHomeName)
                        parentLayout.addView(appHomeIcon)
                    }

                    else -> {
                        parentLayout.addView(appHomeName)
                    }
                }
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
