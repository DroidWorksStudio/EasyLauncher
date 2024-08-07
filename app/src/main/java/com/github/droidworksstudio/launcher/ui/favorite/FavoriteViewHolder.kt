package com.github.droidworksstudio.launcher.ui.favorite

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.databinding.ItemFavoriteBinding
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnItemClickedListener

@SuppressLint("ClickableViewAccessibility")
class FavoriteViewHolder(
    private val binding: ItemFavoriteBinding,
    private val onAppClickedListener: OnItemClickedListener.OnAppsClickedListener,
    private val preferenceHelper: PreferenceHelper,
    private val touchHelper: ItemTouchHelper,
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.appFavoriteDragIcon.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                touchHelper.startDrag(this)
            }
            false
        }
    }

    fun bind(appInfo: AppInfo) {
        binding.apply {
            // Get the current LayoutParams of appFavoriteName
            val layoutParams = appFavoriteName.layoutParams as LinearLayoutCompat.LayoutParams

            // Set the margins
            layoutParams.topMargin = preferenceHelper.homeAppPadding.toInt()
            layoutParams.bottomMargin = preferenceHelper.homeAppPadding.toInt()

            appFavoriteName.layoutParams = layoutParams
            appFavoriteName.text = appInfo.appName
            appFavoriteName.setTextColor(preferenceHelper.appColor)
            appFavoriteName.textSize = preferenceHelper.appTextSize
            Log.d("Tag", "Draw Adapter: ${appInfo.appName}")

            if (preferenceHelper.showAppIcon) {
                val appIcon =
                    binding.root.context.packageManager.getApplicationIcon(appInfo.packageName)
                appFavoriteLeftIcon.setImageDrawable(appIcon)
                appFavoriteLeftIcon.layoutParams.width =
                    preferenceHelper.appTextSize.toInt() * 3
                appFavoriteLeftIcon.layoutParams.height =
                    preferenceHelper.appTextSize.toInt() * 3
                appFavoriteLeftIcon.visibility = View.VISIBLE
            }
        }

        itemView.setOnClickListener {
            onAppClickedListener.onAppClicked(appInfo)
        }
    }
}