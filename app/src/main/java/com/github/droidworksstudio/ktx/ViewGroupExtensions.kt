package com.github.droidworksstudio.ktx

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.view.SupportMenuInflater
import androidx.appcompat.widget.ActionMenuView
import androidx.lifecycle.MutableLiveData
import kotlin.math.min

fun ViewGroup.LayoutParams.calculateAspectRatio(
    maxWidth: Float,
    maxHeight: Float,
    imageWidth: Float,
    imageHeight: Float
): ViewGroup.LayoutParams = apply {
    val widthRatio = maxWidth / imageWidth
    val heightRatio = maxHeight / imageHeight
    val bestRatio = min(widthRatio, heightRatio)

    this.width = (imageWidth * bestRatio).toInt()
    this.height = (imageHeight * bestRatio).toInt()
}

fun ViewGroup.LayoutParams.calculateProportionalHeight(
    maxWidth: Float,
    imageWidth: Float,
    imageHeight: Float
): ViewGroup.LayoutParams = apply {
    val aspectRatio = maxWidth / imageWidth
    this.height = (imageHeight * aspectRatio).toInt()
}

/**
 * Attaches Live data to Radio Group.
 * This will update Live Data data with selected radio button text.
 */
fun RadioGroup.attachLiveDataForValue(data: MutableLiveData<String>) {
    data.value = getCheckedView()?.text.toString()
    setOnCheckedChangeListener { group, checkedId ->
        val checkedView = group.findViewById<RadioButton>(checkedId)
        data.value = checkedView.text.toString()
    }
}

/**
 * Attaches Live data to Radio Group.
 * This will update Live Data data with selected radio button text.
 */
fun RadioGroup.attachLiveDataForId(data: MutableLiveData<Int>) {
    data.value = checkedRadioButtonId
    setOnCheckedChangeListener { _, checkedId ->
        data.value = checkedId
    }
}

fun RadioGroup.getCheckedView() = findViewById<RadioButton>(checkedRadioButtonId) ?: null

@SuppressLint("RestrictedApi")
fun ActionMenuView.inflateMenu(menuId: Int) = SupportMenuInflater(context).inflate(menuId, menu)