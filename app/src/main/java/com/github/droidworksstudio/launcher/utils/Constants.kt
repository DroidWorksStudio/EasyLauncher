package com.github.droidworksstudio.launcher.utils

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.github.droidworksstudio.launcher.R

object Constants {
    const val PACKAGE_NAME = "app.easy.launcher"
    const val PACKAGE_NAME_DEBUG = "$PACKAGE_NAME.debug"

    const val WIDGETS_COUNT = 2
    const val WIDGET_WEATHER = "WIDGET_WEATHER"
    const val WIDGET_BATTERY = "WIDGET_BATTERY"

    const val WEATHER_UNITS = "WEATHER_UNITS"

    const val WEATHER_PREFS = "EasyWeather.pref"
    const val LATITUDE = "LATITUDE"
    const val LONGITUDE = "LONGITUDE"

    const val PREFS_FILENAME = "EasyLauncher.pref"
    const val FIRST_LAUNCH = "FIRST_LAUNCH"
    const val SHOW_DATE = "SHOW_DATE"
    const val SHOW_TIME = "SHOW_TIME"
    const val SHOW_DAILY_WORD = "SHOW_DAILY_WORD"
    const val SHOW_BATTERY = "SHOW_BATTERY"
    const val SHOW_STATUS_BAR = "SHOW_STATUS_BAR"

    const val SHOW_WEATHER_WIDGET = "SHOW_WEATHER_WIDGET"
    const val SHOW_WEATHER_WIDGET_SUN_SET_RISE = "SHOW_WEATHER_WIDGET_SUN_SET_RISE"
    const val SHOW_BATTERY_WIDGET = "SHOW_BATTERY_WIDGET"

    const val DATE_COLOR = "DATE_COLOR"
    const val TIME_COLOR = "TIME_COLOR"
    const val BATTERY_COLOR = "BATTERY_COLOR"
    const val DAILY_WORD_COLOR = "DAILY_WORD_COLOR"
    const val WIDGET_BACKGROUND_COLOR = "WIDGET_BACKGROUND_COLOR"
    const val WIDGET_TEXT_COLOR = "WIDGET_TEXT_COLOR"
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

    const val LAUNCHER_FONT = "LAUNCHER_FONT"

    const val REQUEST_INSTALL_PERMISSION = 123
    const val REQUEST_LOCATION_PERMISSION_CODE = 246

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
        ShowHiddenList,
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
                ShowHiddenList -> context.getString(R.string.settings_actions_show_hidden_list)
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

    enum class Units {
        Metric,
        Imperial;

        fun getString(context: Context): String {
            return when (this) {
                Metric -> context.getString(R.string.settings_units_metric)
                Imperial -> context.getString(R.string.settings_units_imperial)
            }
        }
    }

    enum class Fonts {
        System,
        Bitter,
        DroidSans,
        GreatVibes,
        Lato,
        Lobster,
        Merriweather,
        Montserrat,
        OpenSans,
        Pacifico,
        Quicksand,
        Raleway,
        Roboto,
        SourceCodePro;

        fun getFont(context: Context): Typeface? {
            return when (this) {
                System -> Typeface.DEFAULT
                Bitter -> ResourcesCompat.getFont(context, R.font.bitter)
                DroidSans -> ResourcesCompat.getFont(context, R.font.open_sans)
                GreatVibes -> ResourcesCompat.getFont(context, R.font.great_vibes)
                Lato -> ResourcesCompat.getFont(context, R.font.lato)
                Lobster -> ResourcesCompat.getFont(context, R.font.lobster)
                Merriweather -> ResourcesCompat.getFont(context, R.font.merriweather)
                Montserrat -> ResourcesCompat.getFont(context, R.font.montserrat)
                OpenSans -> ResourcesCompat.getFont(context, R.font.open_sans)
                Pacifico -> ResourcesCompat.getFont(context, R.font.pacifico)
                Quicksand -> ResourcesCompat.getFont(context, R.font.quicksand)
                Raleway -> ResourcesCompat.getFont(context, R.font.raleway)
                Roboto -> ResourcesCompat.getFont(context, R.font.roboto)
                SourceCodePro -> ResourcesCompat.getFont(context, R.font.source_code_pro)
            }
        }

        fun getString(context: Context): String {
            return when (this) {
                System -> context.getString(R.string.settings_font_system)
                Bitter -> context.getString(R.string.settings_font_bitter)
                DroidSans -> context.getString(R.string.settings_font_droidsans)
                GreatVibes -> context.getString(R.string.settings_font_greatvibes)
                Lato -> context.getString(R.string.settings_font_lato)
                Lobster -> context.getString(R.string.settings_font_lobster)
                Merriweather -> context.getString(R.string.settings_font_merriweather)
                Montserrat -> context.getString(R.string.settings_font_montserrat)
                OpenSans -> context.getString(R.string.settings_font_opensans)
                Pacifico -> context.getString(R.string.settings_font_pacifico)
                Quicksand -> context.getString(R.string.settings_font_quicksand)
                Raleway -> context.getString(R.string.settings_font_raleway)
                Roboto -> context.getString(R.string.settings_font_roboto)
                SourceCodePro -> context.getString(R.string.settings_font_sourcecodepro)
            }
        }
    }
}