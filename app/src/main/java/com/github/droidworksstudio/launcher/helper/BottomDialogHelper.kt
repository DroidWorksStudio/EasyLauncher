package com.github.droidworksstudio.launcher.helper

import android.app.Dialog
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.WindowManager
import javax.inject.Inject

class BottomDialogHelper @Inject constructor() {

    fun setupDialogStyle(dialog: Dialog?) {

        val window = dialog?.window
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            @Suppress("DEPRECATION")
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    fun getColorText(color: Int): SpannableString {
        val colorText = "#${Integer.toHexString(color)}"
        val spannableString = SpannableString(colorText)
        spannableString.setSpan(
            ForegroundColorSpan(color),
            0,
            colorText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }
}