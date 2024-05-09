package com.github.droidworksstudio.launcher.helper

import android.content.Context
import android.content.SharedPreferences
import android.view.Gravity
import com.github.droidworksstudio.launcher.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferenceHelper @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(Constants.PREFS_FILENAME, 0)

    var firstLaunch: Boolean
        get() = prefs.getBoolean(Constants.FIRST_LAUNCH, true)
        set(value) = prefs.edit().putBoolean(Constants.FIRST_LAUNCH, value).apply()

    var showStatusBar: Boolean
        get() = prefs.getBoolean(Constants.SHOW_STATUS_BAR, true)
        set(value) = prefs.edit().putBoolean(Constants.SHOW_STATUS_BAR, value).apply()
    var showTime: Boolean
        get() = prefs.getBoolean(Constants.SHOW_TIME, true)
        set(value) = prefs.edit().putBoolean(Constants.SHOW_TIME, value).apply()

    var showDate: Boolean
        get() = prefs.getBoolean(Constants.SHOW_DATE, true)
        set(value) = prefs.edit().putBoolean(Constants.SHOW_DATE, value).apply()

    var showBattery: Boolean
        get() = prefs.getBoolean(Constants.SHOW_BATTERY, true)
        set(value) = prefs.edit().putBoolean(Constants.SHOW_BATTERY, value).apply()

    var showDailyWord: Boolean
        get() = prefs.getBoolean(Constants.SHOW_DAILY_WORD, false)
        set(value) = prefs.edit().putBoolean(Constants.SHOW_DAILY_WORD, value).apply()

    var dateColor: Int
        get() = prefs.getInt(Constants.DATE_COLOR, 0xFFFFFFFF.toInt())
        set(value) = prefs.edit().putInt(Constants.DATE_COLOR, value).apply()

    var timeColor: Int
        get() = prefs.getInt(Constants.TIME_COLOR, 0xFFFFFFFF.toInt())
        set(value) = prefs.edit().putInt(Constants.TIME_COLOR, value).apply()

    var batteryColor: Int
        get() = prefs.getInt(Constants.BATTERY_COLOR, 0xFFFFFFFF.toInt())
        set(value) = prefs.edit().putInt(Constants.BATTERY_COLOR, value).apply()

    var dailyWordColor: Int
        get() = prefs.getInt(Constants.DAILY_WORD_COLOR, 0xFFFFFFFF.toInt())
        set(value) = prefs.edit().putInt(Constants.DAILY_WORD_COLOR, value).apply()

    var appColor: Int
        get() = prefs.getInt(Constants.APP_COLOR, 0xFFFFFFFF.toInt())
        set(value) = prefs.edit().putInt(Constants.APP_COLOR, value).apply()

    var showAppIcon: Boolean
        get() = prefs.getBoolean(Constants.SHOW_APP_ICON, false)
        set(value) = prefs.edit().putBoolean(Constants.SHOW_APP_ICON, value).apply()

    var automaticKeyboard: Boolean
        get() = prefs.getBoolean(Constants.AUTOMATIC_KEYBOARD, true)
        set(value) = prefs.edit().putBoolean(Constants.AUTOMATIC_KEYBOARD, value).apply()

    var automaticOpenApp: Boolean
        get() = prefs.getBoolean(Constants.AUTOMATIC_OPEN_APP, false)
        set(value) = prefs.edit().putBoolean(Constants.AUTOMATIC_OPEN_APP, value).apply()

    var homeAppAlignment: Int
        get() = prefs.getInt(Constants.HOME_APP_ALIGNMENT, Gravity.START)
        set(value) = prefs.edit().putInt(Constants.HOME_APP_ALIGNMENT, value).apply()

    var homeAppPadding: Float
        get() = prefs.getFloat(Constants.APP_TEXT_PADDING, 10f)
        set(value) = prefs.edit().putFloat(Constants.APP_TEXT_PADDING, value).apply()

    var homeDateAlignment: Int
        get() = prefs.getInt(Constants.HOME_DATE_ALIGNMENT, Gravity.START)
        set(value) = prefs.edit().putInt(Constants.HOME_DATE_ALIGNMENT, value).apply()

    var homeTimeAlignment: Int
        get() = prefs.getInt(Constants.HOME_TIME_ALIGNMENT, Gravity.START)
        set(value) = prefs.edit().putInt(Constants.HOME_TIME_ALIGNMENT, value).apply()

    var homeDailyWordAlignment: Int
        get() = prefs.getInt(Constants.HOME_DAILY_WORD_ALIGNMENT, Gravity.START)
        set(value) = prefs.edit().putInt(Constants.HOME_DAILY_WORD_ALIGNMENT,value).apply()

    var batteryTextSize: Float
        get() = prefs.getFloat(Constants.BATTERY_TEXT_SIZE, 12f)
        set(value) = prefs.edit().putFloat(Constants.BATTERY_TEXT_SIZE, value).apply()

    var dateTextSize: Float
        get() = prefs.getFloat(Constants.DATE_TEXT_SIZE, 32f)
        set(value) = prefs.edit().putFloat(Constants.DATE_TEXT_SIZE, value).apply()

    var timeTextSize: Float
        get() = prefs.getFloat(Constants.TIME_TEXT_SIZE, 48f)
        set(value) = prefs.edit().putFloat(Constants.TIME_TEXT_SIZE, value).apply()

    var appTextSize: Float
        get() = prefs.getFloat(Constants.APP_TEXT_SIZE, 24f)
        set(value) = prefs.edit().putFloat(Constants.APP_TEXT_SIZE, value).apply()

    var dailyWordTextSize: Float
        get() = prefs.getFloat(Constants.DAILY_WORD_TEXT_SIZE, 18f)
        set(value) = prefs.edit().putFloat(Constants.DAILY_WORD_TEXT_SIZE, value).apply()

    var tapLockScreen: Boolean
        get() = prefs.getBoolean(Constants.DOUBLE_TAP_LOCK, false)
        set(value) = prefs.edit().putBoolean(Constants.DOUBLE_TAP_LOCK, value).apply()

    var swipeNotification: Boolean
        get() = prefs.getBoolean(Constants.SWIPE_NOTIFICATION, true)
        set(value) = prefs.edit().putBoolean(Constants.SWIPE_NOTIFICATION, value).apply()

    var swipeSearch: Boolean
        get() = prefs.getBoolean(Constants.SWIPE_SEARCH, false)
        set(value) = prefs.edit().putBoolean(Constants.SWIPE_SEARCH, value).apply()
}