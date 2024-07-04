package com.github.droidworksstudio.launcher.adapter.font

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.github.droidworksstudio.launcher.R
import com.github.droidworksstudio.launcher.utils.Constants

class FontAdapter(
    context: Context,
    private val items: Array<Constants.Fonts>,
    private val itemStrings: Array<String>
) : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, itemStrings) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)

        textView.text = itemStrings[position]

        // Set custom font based on the item
        val typeface = getCustomTypefaceForItem(context, items[position])
        textView.typeface = typeface

        return view
    }

    private fun getCustomTypefaceForItem(context: Context, item: Constants.Fonts): Typeface? {
        return when (item) {
            Constants.Fonts.Roboto -> ResourcesCompat.getFont(context, R.font.roboto)
            Constants.Fonts.OpenSans -> ResourcesCompat.getFont(context, R.font.open_sans)
            Constants.Fonts.Lato -> ResourcesCompat.getFont(context, R.font.lato)
            Constants.Fonts.Montserrat -> ResourcesCompat.getFont(context, R.font.montserrat)
            Constants.Fonts.Merriweather -> ResourcesCompat.getFont(context, R.font.montserrat)
            Constants.Fonts.DroidSans -> ResourcesCompat.getFont(context, R.font.droid_sans)
            Constants.Fonts.Raleway -> ResourcesCompat.getFont(context, R.font.open_sans)
            Constants.Fonts.SourceCodePro -> ResourcesCompat.getFont(context, R.font.open_sans)
            Constants.Fonts.Quicksand -> ResourcesCompat.getFont(context, R.font.quicksand)
            // Add other fonts as needed
            else -> Typeface.DEFAULT
        }
    }
}
