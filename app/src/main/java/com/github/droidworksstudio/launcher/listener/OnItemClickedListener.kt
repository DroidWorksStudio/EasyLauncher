package com.github.droidworksstudio.launcher.listener

import com.github.droidworksstudio.launcher.data.entities.AppInfo

class OnItemClickedListener {

    interface OnAppsClickedListener {
        fun onAppClicked(appInfo: AppInfo) {}
    }

    interface OnAppLongClickedListener {
        fun onAppLongClicked(appInfo: AppInfo) {}
    }

    interface OnAppStateClickListener {
        fun onAppStateClicked(appInfo: AppInfo) {}
    }
}