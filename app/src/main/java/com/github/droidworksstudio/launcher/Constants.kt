package com.github.droidworksstudio.launcher

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
    const val DOUBLE_TAP_LOCK = "DOUBLE_TAP_LOCK"

    const val SWIPE_NOTIFICATION = "SWIPE_NOTIFICATION"
    const val SWIPE_SEARCH = "SWIPE_SEARCH"

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
}