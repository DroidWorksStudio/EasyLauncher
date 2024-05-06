package com.github.droidworksstudio.launcher.ui.home

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.databinding.ItemHomeBinding
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.listener.OnItemClickedListener
import javax.inject.Inject


class HomeViewHolder @Inject constructor(
    private val binding: ItemHomeBinding,
    private val onAppClickedListener: OnItemClickedListener.OnAppsClickedListener,
    private val onAppLongClickedListener: OnItemClickedListener.OnAppLongClickedListener,
    private val preferenceHelper: PreferenceHelper
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(appInfo: AppInfo) {

        binding.apply {
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = preferenceHelper.homeAppAlignment
            }

            appHomeName.layoutParams = layoutParams
            appHomeName.text = appInfo.appName
            appHomeName.setTextColor(preferenceHelper.appColor)
            Log.d("Tag", "Home Adapter Color: ${preferenceHelper.appColor}")
            appHomeIcon.visibility = View.GONE
        }

        itemView.setOnClickListener { onAppClickedListener.onAppClicked(appInfo) }

        itemView.setOnLongClickListener {
            onAppLongClickedListener.onAppLongClicked(appInfo)
            true
        }
    }

    /*private fun painTextView(view: View, strokeWidth: Float){
        val paint = (view as TextView).paint
        val width = paint.measureText(view.text.toString())
        val textShader: Shader = LinearGradient(
            0f, 0f, width, view.textSize, intArrayOf(
                preferenceHelper.appColor,
                Color.WHITE,
                //Color.parseColor("#DAA520"),
                /*Color.parseColor("#478AEA"),*/
            ), null, Shader.TileMode.MIRROR
        )

        //paint.style = Paint.Style.STROKE;
        //paint.strokeWidth = strokeWidth;

        view.paint.shader = textShader
    }*/
}
