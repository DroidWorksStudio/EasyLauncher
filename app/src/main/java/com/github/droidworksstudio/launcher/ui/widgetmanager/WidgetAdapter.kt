package com.github.droidworksstudio.launcher.ui.widgetmanager

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.github.droidworksstudio.launcher.R

class WidgetAdapter(
    private val context: Context,
    private val appWidgetHost: AppWidgetHost,
    private val onWidgetClick: (Int) -> Unit
) : RecyclerView.Adapter<WidgetAdapter.WidgetViewHolder>() {

    private val widgetItems = mutableListOf<WidgetItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_widget, parent, false)
        return WidgetViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        val widgetItem = widgetItems[position]
        holder.bind(widgetItem)
    }

    override fun getItemCount(): Int = widgetItems.size

    fun addWidget(widgetItem: WidgetItem) {
        widgetItems.add(widgetItem)
        notifyItemInserted(widgetItems.size - 1)
    }

    fun removeWidget(appWidgetId: Int) {
        val position = widgetItems.indexOfFirst { it.appWidgetId == appWidgetId }
        if (position != -1) {
            widgetItems.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getWidgetIds(): List<Int> = widgetItems.map { it.appWidgetId }

    inner class WidgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(widgetItem: WidgetItem) {
            val hostView = appWidgetHost.createView(context, widgetItem.appWidgetId, widgetItem.appWidgetInfo)
            itemView.findViewById<FrameLayout>(R.id.widget_frame).addView(hostView)

            itemView.setOnClickListener {
                onWidgetClick(widgetItem.appWidgetId)
            }
        }
    }
}
