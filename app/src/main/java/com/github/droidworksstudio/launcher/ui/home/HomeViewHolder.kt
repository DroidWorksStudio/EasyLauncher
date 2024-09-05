package com.github.droidworksstudio.launcher.ui.home

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.databinding.ItemHomeBinding
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnItemClickedListener
import javax.inject.Inject
import kotlin.math.min

class HomeViewHolder @Inject constructor(
    private val binding: ItemHomeBinding,
    private val onAppClickedListener: OnItemClickedListener.OnAppsClickedListener,
    private val onAppLongClickedListener: OnItemClickedListener.OnAppLongClickedListener,
    private val preferenceHelper: PreferenceHelper,
) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("ClickableViewAccessibility")
    fun bind(appInfo: AppInfo) {
        binding.apply {
            // Get the current LayoutParams of appHiddenName
            val layoutParams = appHomeName.layoutParams as LinearLayoutCompat.LayoutParams

            // Set the margins
            layoutParams.topMargin = preferenceHelper.homeAppPadding.toInt()
            layoutParams.bottomMargin = preferenceHelper.homeAppPadding.toInt()

            appHomeName.layoutParams = layoutParams
            // Update appHomeName properties
            appHomeName.text = appInfo.appName
            appHomeName.setTextColor(preferenceHelper.appColor)
            appHomeName.textSize = preferenceHelper.appTextSize
            appHomeName.gravity = preferenceHelper.homeAppAlignment

            if (preferenceHelper.showAppIcon) {
                val pm: PackageManager = binding.root.context.packageManager
                val appIcon = pm.getApplicationIcon(appInfo.packageName)

                if (preferenceHelper.showAppIconAsDots) {
                    when (preferenceHelper.homeAppAlignment) {
                        Gravity.START -> {
                            val appNewIcon
                                    : Drawable = ContextCompat.getDrawable(itemView.context, R.drawable.app_dot_icon)!!

                            val bitmap = drawableToBitmap(appIcon)
                            val dominantColor = getDominantColor(bitmap)
                            val recoloredDrawable = recolorDrawable(appNewIcon, dominantColor)

                            val appIconSize = (preferenceHelper.appTextSize * 1.1f).toInt()
                            appHomeDotsIcon.setImageDrawable(recoloredDrawable)
                            appHomeDotsIcon.layoutParams.width = appIconSize
                            appHomeDotsIcon.layoutParams.height = appIconSize
                            appHomeDotsIcon.visibility = View.VISIBLE
                        }

                        else -> {
                            appHomeDotsIcon.visibility = View.GONE
                        }
                    }
                } else {
                    val appIconSize = (preferenceHelper.appTextSize * 2f).toInt()
                    when (preferenceHelper.homeAppAlignment) {
                        Gravity.START -> {
                            appHomeLeftIcon.setImageDrawable(appIcon)
                            appHomeLeftIcon.layoutParams.width = appIconSize
                            appHomeLeftIcon.layoutParams.height = appIconSize
                            appHomeLeftIcon.visibility = View.VISIBLE
                        }

                        Gravity.END -> {
                            appHomeRightIcon.setImageDrawable(appIcon)
                            appHomeRightIcon.layoutParams.width = appIconSize
                            appHomeRightIcon.layoutParams.height = appIconSize
                            appHomeRightIcon.visibility = View.VISIBLE
                        }

                        else -> {
                            appHomeLeftIcon.visibility = View.GONE
                            appHomeRightIcon.visibility = View.GONE
                        }
                    }
                }
            } else {
                // Hide icons if app icon is not shown
                appHomeLeftIcon.visibility = View.GONE
                appHomeRightIcon.visibility = View.GONE
                appHomeDotsIcon.visibility = View.GONE
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

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val width = drawable.intrinsicWidth.coerceAtLeast(1)
        val height = drawable.intrinsicHeight.coerceAtLeast(1)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun getDominantColor(bitmap: Bitmap): Int {
        // Scale the bitmap to a manageable size
        val scaledBitmap = Bitmap.createScaledBitmap(
            bitmap,
            min(bitmap.width / 4, 1280),
            min(bitmap.height / 4, 1280),
            true
        )

        // Generate a palette from the scaled bitmap
        val palette = Palette.from(scaledBitmap)
            .maximumColorCount(128)
            .generate()

        // Extract the colors from the palette
        val colors = palette.swatches.map { it.rgb }

        // Combine the colors into a single color
        return increaseColorVibrancy(combineColors(colors))
    }

    private fun combineColors(colors: List<Int>): Int {
        if (colors.isEmpty()) return Color.DKGRAY

        var red = 0
        var green = 0
        var blue = 0

        // Calculate the average color values
        for (color in colors) {
            red += Color.red(color)
            green += Color.green(color)
            blue += Color.blue(color)
        }

        val count = colors.size
        return Color.rgb(red / count, green / count, blue / count)
    }

    private fun increaseColorVibrancy(color: Int): Int {
        // Convert RGB to HSL
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(color, hsl)

        // Increase the saturation
        hsl[1] = (hsl[1] * 15f).coerceIn(0f, 1f)

        // Convert HSL back to RGB
        return ColorUtils.HSLToColor(hsl)
    }

    private fun recolorDrawable(drawable: Drawable, color: Int): Drawable {
        val wrappedDrawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(wrappedDrawable, color)
        return wrappedDrawable
    }
}
