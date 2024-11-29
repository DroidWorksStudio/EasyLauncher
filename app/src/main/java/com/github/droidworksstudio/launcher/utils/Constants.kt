package com.github.droidworksstudio.launcher.utils

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.github.droidworksstudio.launcher.R
import java.util.Locale

object Constants {
    const val PACKAGE_NAME = "app.easy.launcher"
    const val PACKAGE_NAME_DEBUG = "$PACKAGE_NAME.debug"
    const val PACKAGE_PREFS = "EasyLauncher.pref"
    const val PACKAGE_LANGUAGE = "PACKAGE_LANGUAGE"

    const val TIMERS_PREFS = "Timers.pref"
    const val CACHED_DATA_TIMESTAMP = "CACHED_DATA_TIMESTAMP"

    const val WIDGETS_COUNT = 2
    const val WIDGET_WEATHER = "WIDGET_WEATHER"
    const val WIDGET_BATTERY = "WIDGET_BATTERY"

    const val WEATHER_PREFS = "EasyWeather.pref"
    const val WEATHER_RESPONSE = "WEATHER_RESPONSE"
    const val WEATHER_UNITS = "WEATHER_UNITS"
    const val LATITUDE = "LATITUDE"
    const val LONGITUDE = "LONGITUDE"

    const val FIRST_LAUNCH = "FIRST_LAUNCH"
    const val SHOW_DATE = "SHOW_DATE"
    const val SHOW_TIME = "SHOW_TIME"
    const val SHOW_DAILY_WORD = "SHOW_DAILY_WORD"
    const val SHOW_ALARM_CLOCK = "SHOW_ALARM_CLOCK"
    const val SHOW_BATTERY = "SHOW_BATTERY"
    const val SHOW_STATUS_BAR = "SHOW_STATUS_BAR"

    const val SHOW_WEATHER_WIDGET = "SHOW_WEATHER_WIDGET"
    const val SHOW_WEATHER_WIDGET_SUN_SET_RISE = "SHOW_WEATHER_WIDGET_SUN_SET_RISE"
    const val SHOW_BATTERY_WIDGET = "SHOW_BATTERY_WIDGET"

    const val DATE_COLOR = "DATE_COLOR"
    const val TIME_COLOR = "TIME_COLOR"
    const val BATTERY_COLOR = "BATTERY_COLOR"
    const val ALARM_CLOCK_COLOR = "ALARM_CLOCK_COLOR"
    const val DAILY_WORD_COLOR = "DAILY_WORD_COLOR"
    const val WIDGET_BACKGROUND_COLOR = "WIDGET_BACKGROUND_COLOR"
    const val WIDGET_TEXT_COLOR = "WIDGET_TEXT_COLOR"
    const val APP_COLOR = "APP_COLOR"

    const val BATTERY_TEXT_SIZE = "BATTERY_TEXT_SIZE"
    const val DATE_TEXT_SIZE = "DATE_TEXT_SIZE"
    const val TIME_TEXT_SIZE = "TIME_TEXT_SIZE"
    const val APP_TEXT_SIZE = "APP_TEXT_SIZE"
    const val ALARM_CLOCK_TEXT_SIZE = "ALARM_CLOCK_TEXT_SIZE"
    const val DAILY_WORD_TEXT_SIZE = "DAILY_WORD_TEXT_SIZE"

    const val APPS_PADDING = "APPS_PADDING"
    const val APP_TEXT_PADDING = "APP_TEXT_PADDING"

    const val SHOW_APP_ICON = "SHOW_APP_ICON"

    const val AUTOMATIC_KEYBOARD = "AUTOMATIC_KEYBOARD"
    const val AUTOMATIC_OPEN_APP = "AUTOMATIC_OPEN_APP"
    const val SEARCH_FROM_START = "SEARCH_FROM_START"
    const val FILTER_STRENGTH = "FILTER_STRENGTH"
    const val SWIPE_THRESHOLD = "SWIPE_THRESHOLD"
    const val HOME_ALLIGNMENT_BOTTOM = "HOME_ALLIGNMENT_BOTTOM"
    const val TOGGLE_SETTING_LOCK = "TOGGLE_SETTING_LOCK"
    const val DISABLE_ANIMATIONS = "DISABLE_ANIMATIONS"

    const val HOME_DATE_ALIGNMENT = "HOME_DATE_ALIGNMENT"
    const val HOME_TIME_ALIGNMENT = "HOME_TIME_ALIGNMENT"
    const val HOME_APP_ALIGNMENT = "HOME_APP_ALIGNMENT"
    const val HOME_ALARM_CLOCK_ALIGNMENT = "HOME_ALARM_CLOCK_ALIGNMENT"
    const val HOME_DAILY_WORD_ALIGNMENT = "HOME_DAILY_WORD_ALIGNMENT"

    const val NOTIFICATION_SERVICE = "statusbar"
    const val NOTIFICATION_MANAGER = "android.app.StatusBarManager"
    const val NOTIFICATION_METHOD = "expandNotificationsPanel"

    const val QUICKSETTINGS_SERVICE = "statusbar"
    const val QUICKSETTINGS_MANAGER = "android.app.StatusBarManager"
    const val QUICKSETTINGS_METHOD = "expandSettingsPanel"

    const val SEARCH_ENGINE = "SEARCH_ENGINE"
    const val URL_DUCK_SEARCH = "https://duckduckgo.com/?q="
    const val URL_GOOGLE_SEARCH = "https://google.com/search?q="
    const val URL_YAHOO_SEARCH = "https://search.yahoo.com/search?p="
    const val URL_BING_SEARCH = "https://bing.com/search?q="
    const val URL_BRAVE_SEARCH = "https://search.brave.com/search?q="
    const val URL_SWISSCOW_SEARCH = "https://swisscows.com/web?query="
    const val URL_GOOGLE_PLAY_STORE = "https://play.google.com/store/search?c=apps&q"
    const val APP_GOOGLE_PLAY_STORE = "market://search?c=apps&q"

    const val ICONS_PACK = "ICONS_PACK"
    const val LAUNCHER_FONT = "LAUNCHER_FONT"

    const val REQUEST_INSTALL_PERMISSION = 123
    const val REQUEST_LOCATION_PERMISSION_CODE = 234

    const val FILTER_STRENGTH_MIN = 0
    const val FILTER_STRENGTH_MAX = 100

    const val SWIPE_THRESHOLD_MIN = 10
    const val SWIPE_THRESHOLD_MAX = 255

    const val APP_GROUP_PADDING_MIN = 0.0
    const val APP_GROUP_PADDING_MAX = 1000.0

    const val APP_PADDING_MIN = 0.0
    const val APP_PADDING_MAX = 100.0

    const val BACKUP_WRITE = 987
    const val BACKUP_READ = 876

    const val LOCATION_DENIED = "LOCATION_DENIED"

    const val TRIPLE_TAP_DELAY_MS = 300
    const val LONG_PRESS_DELAY_MS = 500

    enum class SearchEngines {
        Default,
        Google,
        Yahoo,
        DuckDuckGo,
        Bing,
        Brave,
        SwissCow;

        fun getString(context: Context): String {
            return when (this) {
                Default -> context.getString(R.string.search_default)
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
    const val SWIPE_UP_APP = "SWIPE_UP_APP"
    const val SWIPE_DOWN_ACTION = "SWIPE_DOWN_ACTION"
    const val SWIPE_DOWN_APP = "SWIPE_DOWN_APP"
    const val SWIPE_LEFT_ACTION = "SWIPE_LEFT_ACTION"
    const val SWIPE_LEFT_APP = "SWIPE_LEFT_APP"
    const val SWIPE_RIGHT_ACTION = "SWIPE_RIGHT_ACTION"
    const val SWIPE_RIGHT_APP = "SWIPE_RIGHT_APP"
    const val DOUBLE_TAP_ACTION = "DOUBLE_TAP_ACTION"
    const val DOUBLE_TAP_APP = "DOUBLE_TAP_APP"

    enum class Action {
        OpenApp,
        LockScreen,
        ShowNotification,
        ShowAppList,
        ShowFavoriteList,
        ShowHiddenList,
        OpenQuickSettings,
        OpenAppSettings,
        ShowRecents,
        ShowWidgets,
        OpenPowerDialog,
        TakeScreenShot,
        OpenDigitalWellbing,
        Disabled;

        fun getString(context: Context): String {
            return when (this) {
                OpenApp -> context.getString(R.string.settings_actions_open_app)
                LockScreen -> context.getString(R.string.settings_actions_lock_screen)
                ShowNotification -> context.getString(R.string.settings_actions_show_notifications)
                ShowAppList -> context.getString(R.string.settings_actions_show_app_list)
                ShowFavoriteList -> context.getString(R.string.settings_actions_show_favorite_list)
                ShowHiddenList -> context.getString(R.string.settings_actions_show_hidden_list)
                OpenQuickSettings -> context.getString(R.string.settings_actions_open_quick_settings)
                OpenAppSettings -> context.getString(R.string.settings_actions_open_launcher_settings)
                ShowRecents -> context.getString(R.string.settings_actions_show_recents)
                ShowWidgets -> context.getString(R.string.settings_actions_show_widgets)
                OpenPowerDialog -> context.getString(R.string.settings_actions_open_power_dialog)
                TakeScreenShot -> context.getString(R.string.settings_actions_take_a_screenshot)
                OpenDigitalWellbing -> context.getString(R.string.settings_actions_open_digital_wellbeing)
                Disabled -> context.getString(R.string.settings_actions_disabled)
            }
        }
    }

    enum class Swipe {
        DoubleTap,
        Up,
        Down,
        Left,
        Right;
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

    enum class IconPacks {
        System,
        EasyDots,
        NiagaraDots;

        fun getString(context: Context): String {
            return when (this) {
                System -> context.getString(R.string.settings_system)
                EasyDots -> context.getString(R.string.settings_icons_easy_dots)
                NiagaraDots -> context.getString(R.string.settings_icons_niagara_dots)
            }
        }
    }

    enum class Fonts {
        System,
        Bitter,
        Dotness,
        DroidSans,
        Lato,
        Merriweather,
        Montserrat,
        OpenSans,
        Quicksand,
        Raleway,
        Roboto,
        SourceCodePro;

        fun getFont(context: Context): Typeface? {
            return when (this) {
                System -> Typeface.DEFAULT
                Bitter -> ResourcesCompat.getFont(context, R.font.bitter)
                Dotness -> ResourcesCompat.getFont(context, R.font.dotness)
                DroidSans -> ResourcesCompat.getFont(context, R.font.open_sans)
                Lato -> ResourcesCompat.getFont(context, R.font.lato)
                Merriweather -> ResourcesCompat.getFont(context, R.font.merriweather)
                Montserrat -> ResourcesCompat.getFont(context, R.font.montserrat)
                OpenSans -> ResourcesCompat.getFont(context, R.font.open_sans)
                Quicksand -> ResourcesCompat.getFont(context, R.font.quicksand)
                Raleway -> ResourcesCompat.getFont(context, R.font.raleway)
                Roboto -> ResourcesCompat.getFont(context, R.font.roboto)
                SourceCodePro -> ResourcesCompat.getFont(context, R.font.source_code_pro)
            }
        }

        fun getString(context: Context): String {
            return when (this) {
                System -> context.getString(R.string.settings_system)
                Bitter -> context.getString(R.string.settings_font_bitter)
                Dotness -> context.getString(R.string.settings_font_dotness)
                DroidSans -> context.getString(R.string.settings_font_droidsans)
                Lato -> context.getString(R.string.settings_font_lato)
                Merriweather -> context.getString(R.string.settings_font_merriweather)
                Montserrat -> context.getString(R.string.settings_font_montserrat)
                OpenSans -> context.getString(R.string.settings_font_opensans)
                Quicksand -> context.getString(R.string.settings_font_quicksand)
                Raleway -> context.getString(R.string.settings_font_raleway)
                Roboto -> context.getString(R.string.settings_font_roboto)
                SourceCodePro -> context.getString(R.string.settings_font_sourcecodepro)
            }
        }
    }

    enum class Language {
        System,
        Czech,
        Danish,
        Dutch,
        English,
        German,
        Hebrew,
        Italian,
        Lithuanian,
        Slovak,
        Turkish,
        Ukrainian;

        fun string(context: Context): String {
            return when (this) {
                System -> context.getString(R.string.settings_system)
                Czech -> "Czech"
                Danish -> "Danish"
                Dutch -> "Dutch"
                English -> "English"
                German -> "German"
                Hebrew -> "Hebrew"
                Italian -> "Italian"
                Lithuanian -> "Lithuanian"
                Slovak -> "Slovak"
                Turkish -> "Turkish"
                Ukrainian -> "Ukrainian"
            }
        }


        fun locale(): Locale {
            return Locale(value())
        }

        private fun value(): String {
            return when (this) {
                System -> Locale.getDefault().language
                Czech -> "cs"
                Danish -> "da"
                Dutch -> "nl"
                English -> "en"
                German -> "de"
                Hebrew -> "iw"
                Italian -> "it"
                Lithuanian -> "lt"
                Slovak -> "sk"
                Turkish -> "tr"
                Ukrainian -> "uk"
            }
        }

        fun timezone(): Locale {
            return Locale(zone())
        }

        private fun zone(): String {
            return when (this) {
                System -> Locale.getDefault().toLanguageTag()
                Czech -> "cs-CZ"
                Danish -> "da-DK"
                Dutch -> "nl-NL"
                English -> "en-US"
                German -> "de-DE"
                Hebrew -> "he-IL"
                Italian -> "it-IT"
                Lithuanian -> "lt-LT"
                Slovak -> "sk-SK"
                Turkish -> "tr-TR"
                Ukrainian -> "uk-UA"
            }
        }
    }
}