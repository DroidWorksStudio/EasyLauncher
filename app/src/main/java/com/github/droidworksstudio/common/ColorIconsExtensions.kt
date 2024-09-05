package com.github.droidworksstudio.common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.palette.graphics.Palette
import kotlin.math.min

class ColorIconsExtensions {
    companion object {
        fun drawableToBitmap(drawable: Drawable): Bitmap {
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

        fun getDominantColor(bitmap: Bitmap): Int {
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

        fun recolorDrawable(drawable: Drawable, color: Int): Drawable {
            val wrappedDrawable = DrawableCompat.wrap(drawable)
            DrawableCompat.setTint(wrappedDrawable, color)
            return wrappedDrawable
        }
    }
}