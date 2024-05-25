package com.github.droidworksstudio.ktx

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import androidx.core.text.getSpans

/**
 * Returns [Spannable] where the term is
 * highlighted as a bold text.
 */
fun String.highlightTerm(term: String): SpannableString {
    val regex = Regex(term.lowercase())
    val matches = regex.findAll(this.lowercase())
    val ranges = matches.map { it.range }
    val spannable = SpannableString(this)
    ranges.forEach {
        spannable.setSpan(
            ForegroundColorSpan(Color.BLACK),
            it.first,
            it.last + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    return spannable
}

/**
 * Returns [Spannable] where all <em> (e.g. italic) tags are replaced
 * with <strong> (e.g. bold)
 *
 * Important: works only with <em> tags replacing it only with <strong> tags.
 * Using deprecated [Html.fromHtml] as it's deprecated only from API 24.
 */
fun String.replaceItalicWithBold(): SpannableString {
    val spannable = SpannableString(Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY))

    spannable.getSpans<StyleSpan>(0, spannable.length)
        .replaceItalicInSpannable(spannable)

    return spannable
}

fun Array<out StyleSpan>.replaceItalicInSpannable(spannable: SpannableString) = forEach {
    if (it.style == Typeface.ITALIC) {
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            spannable.getSpanStart(it),
            spannable.getSpanEnd(it),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(Color.BLACK),
            spannable.getSpanStart(it),
            spannable.getSpanEnd(it),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.removeSpan(it)
    }
}

fun CharSequence.makeTextClickable(
    clickableText: String,
    clickableTextColor: Int,
    clickListener: () -> (Unit)
): SpannableString {
    val regex = Regex(clickableText)
    val matches = regex.findAll(this)
    val ranges = matches.map { it.range }
    val spannable = SpannableString(this)
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            clickListener.invoke()
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = clickableTextColor
            ds.isUnderlineText = false
        }
    }
    ranges.forEach {
        spannable.setSpan(clickableSpan, it.first, it.last + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    return spannable
}

fun String.showLongToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_LONG).show()
}

fun String.showShortToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}