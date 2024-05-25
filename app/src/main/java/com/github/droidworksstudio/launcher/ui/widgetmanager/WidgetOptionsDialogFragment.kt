package com.github.droidworksstudio.launcher.ui.widgetmanager

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.github.droidworksstudio.launcher.Constants

class WidgetOptionsDialogFragment : DialogFragment() {

    interface WidgetOptionsListener {
        fun onRemoveWidget(appWidgetId: Int)
        fun onOpenAppWidget(appWidgetId: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val appWidgetId =
            arguments?.getInt(ARG_APP_WIDGET_ID) ?: return super.onCreateDialog(savedInstanceState)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Widget Options")
            .setItems(arrayOf("Remove Widget", "Open Widget")) { _, which ->
                val listener = parentFragment as? WidgetOptionsListener
                when (which) {
                    0 -> listener?.onRemoveWidget(appWidgetId)
                    1 -> listener?.onOpenAppWidget(appWidgetId)
                }
            }
        return builder.create()
    }

    companion object {
        private const val ARG_APP_WIDGET_ID = Constants.APP_WIDGETS_ID

        fun newInstance(appWidgetId: Int): WidgetOptionsDialogFragment {
            val fragment = WidgetOptionsDialogFragment()
            val args = Bundle().apply {
                putInt(ARG_APP_WIDGET_ID, appWidgetId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
