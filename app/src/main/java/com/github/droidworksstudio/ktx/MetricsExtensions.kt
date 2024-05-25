package com.github.droidworksstudio.ktx

import android.content.res.Resources
import android.util.TypedValue

/**
 * Converts value in pixels (px) into value in device-independent pixels (dp)
 */
fun Int.pxToDp(): Int {
    val metrics = Resources.getSystem().displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, this.toFloat(), metrics).toInt()
}

/**
 * Converts value in pixels (px) into value in scaled pixels (sp)
 */
fun Int.pxToSp(): Int {
    val metrics = Resources.getSystem().displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, this.toFloat(), metrics).toInt()
}

/**
 * Converts value in device-independent pixels (dp) into value in pixels (px)
 */
fun Int.dpToPx(): Int {
    val metrics = Resources.getSystem().displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics).toInt()
}

/**
 * Converts value in density-independent pixels (dp) into value in scaled pixels (sp)
 */
fun Int.dpToSp(): Int {
    val metrics = Resources.getSystem().displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics).toInt()
}

/**
 * Converts value in scaled pixels (sp) into value in device-independent pixels (dp)
 */
fun Int.spToDp(): Int {
    val metrics = Resources.getSystem().displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), metrics).toInt()
}

/**
 * Converts value in scaled pixels (sp) into value in pixels (px)
 */
fun Int.spToPx(): Int {
    val metrics = Resources.getSystem().displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), metrics).toInt()
}

