package com.github.droidworksstudio.launcher.utils

import android.content.Context
import androidx.compose.ui.res.stringResource
import com.github.droidworksstudio.launcher.R

object Constants {
    const val PACKAGE_NAME = "app.easy.launcher"
    const val PACKAGE_NAME_DEBUG = "$PACKAGE_NAME.debug"

    const val WIDGETS_PREFS = "EasyLauncherWidgets.pref"
    const val APP_WIDGETS_ID = "APP_WIDGETS_ID"

    const val PREFS_FILENAME = "EasyLauncher.pref"
    const val FIRST_LAUNCH = "FIRST_LAUNCH"
    const val SHOW_DATE = "SHOW_DATE"
    const val SHOW_TIME = "SHOW_TIME"
    const val SHOW_DAILY_WORD = "SHOW_DAILY_WORD"
    const val SHOW_BATTERY = "SHOW_BATTERY"
    const val SHOW_STATUS_BAR = "SHOW_STATUS_BAR"

    const val DATE_COLOR = "DATE_COLOR"
    const val TIME_COLOR = "TIME_COLOR"
    const val BATTERY_COLOR = "BATTERY_COLOR"
    const val DAILY_WORD_COLOR = "DAILY_WORD_COLOR"
    const val APP_COLOR = "APP_COLOR"

    const val BATTERY_TEXT_SIZE = "BATTERY_TEXT_SIZE"
    const val DATE_TEXT_SIZE = "DATE_TEXT_SIZE"
    const val TIME_TEXT_SIZE = "TIME_TEXT_SIZE"
    const val APP_TEXT_SIZE = "APP_TEXT_SIZE"
    const val DAILY_WORD_TEXT_SIZE = "DAILY_WORD_TEXT_SIZE"

    const val APP_TEXT_PADDING = "APP_TEXT_PADDING"

    const val SHOW_APP_ICON = "SHOW_APP_ICON"

    const val AUTOMATIC_KEYBOARD = "AUTOMATIC_KEYBOARD"
    const val AUTOMATIC_OPEN_APP = "AUTOMATIC_OPEN_APP"

    const val HOME_DATE_ALIGNMENT = "HOME_DATE_ALIGNMENT"
    const val HOME_TIME_ALIGNMENT = "HOME_TIME_ALIGNMENT"
    const val HOME_APP_ALIGNMENT = "HOME_APP_ALIGNMENT"
    const val HOME_DAILY_WORD_ALIGNMENT = "HOME_DAILY_WORD_ALIGNMENT"

    const val NOTIFICATION_SERVICE = "statusbar"
    const val NOTIFICATION_MANAGER = "android.app.StatusBarManager"
    const val NOTIFICATION_METHOD = "expandNotificationsPanel"

    const val QUICKSETTINGS_SERVICE = "statusbar"
    const val QUICKSETTINGS_MANAGER = "android.app.StatusBarManager"
    const val QUICKSETTINGS_METHOD = "expandSettingsPanel"

    const val TOGGLE_SETTING_LOCK = "TOGGLE_SETTING_LOCK"

    const val SEARCH_ENGINE = "SEARCH_ENGINE"
    const val URL_DUCK_SEARCH = "https://duckduckgo.com/?q="
    const val URL_GOOGLE_SEARCH = "https://google.com/search?q="
    const val URL_YAHOO_SEARCH = "https://search.yahoo.com/search?p="
    const val URL_BING_SEARCH = "https://bing.com/search?q="
    const val URL_BRAVE_SEARCH = "https://search.brave.com/search?q="
    const val URL_SWISSCOW_SEARCH = "https://swisscows.com/web?query="
    const val URL_GOOGLE_PLAY_STORE = "https://play.google.com/store/search?c=apps&q"
    const val APP_GOOGLE_PLAY_STORE = "market://search?c=apps&q"

    const val APP_WIDGET_HOST_ID = 1024
    const val TRIPLE_TAP_DELAY_MS = 300
    const val LONG_PRESS_DELAY_MS = 500

    enum class SearchEngines {
        Google,
        Yahoo,
        DuckDuckGo,
        Bing,
        Brave,
        SwissCow;

        fun getString(context: Context): String {
            return when (this) {
                Google -> context.getString(R.string.search_google)
                Yahoo -> context.getString(R.string.search_yahoo)
                DuckDuckGo -> context.getString(R.string.search_duckduckgo)
                Bing -> context.getString(R.string.search_bing)
                Brave -> context.getString(R.string.search_brave)
                SwissCow -> context.getString(R.string.search_swisscow)
            }
        }
    }

    const val SWIPE_UP_ACTION = "SWIPE_UP_ACTION"
    const val SWIPE_DOWN_ACTION = "SWIPE_DOWN_ACTION"
    const val SWIPE_LEFT_ACTION = "SWIPE_LEFT_ACTION"
    const val SWIPE_RIGHT_ACTION = "SWIPE_RIGHT_ACTION"
    const val DOUBLE_TAP_ACTION = "DOUBLE_TAP_ACTION"

    enum class Action {
        //        OpenApp,
        LockScreen,
        ShowNotification,
        ShowAppList,
        ShowFavoriteList,
        OpenQuickSettings,
        ShowRecents,
        ShowWidgets,
        OpenPowerDialog,
        TakeScreenShot,
        Disabled;

        fun getString(context: Context): String {
            return when (this) {
//                OpenApp -> context.getString(R.string.settings_actions_open_app)
                LockScreen -> context.getString(R.string.settings_actions_lock_screen)
                ShowNotification -> context.getString(R.string.settings_actions_show_notifications)
                ShowAppList -> context.getString(R.string.settings_actions_show_app_list)
                ShowFavoriteList -> context.getString(R.string.settings_actions_show_favorite_list)
                OpenQuickSettings -> context.getString(R.string.settings_actions_open_quick_settings)
                ShowRecents -> context.getString(R.string.settings_actions_show_recents)
                ShowWidgets -> context.getString(R.string.settings_actions_show_widgets)
                OpenPowerDialog -> context.getString(R.string.settings_actions_open_power_dialog)
                TakeScreenShot -> context.getString(R.string.settings_actions_take_a_screenshot)
                Disabled -> context.getString(R.string.settings_actions_disabled)
            }
        }
    }

    enum class Swipe {
        DoubleTap,
        Left,
        Right,
        Up,
        Down;
    }
}