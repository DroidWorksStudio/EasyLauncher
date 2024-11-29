package com.github.droidworksstudio.launcher.helper

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.util.Log
import android.view.Gravity
import com.github.droidworksstudio.launcher.utils.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferenceHelper @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(Constants.PACKAGE_PREFS, 0)

    private val setColor = getColor(context)

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

    var showAlarmClock: Boolean
        get() = prefs.getBoolean(Constants.SHOW_ALARM_CLOCK, false)
        set(value) = prefs.edit().putBoolean(Constants.SHOW_ALARM_CLOCK, value).apply()

    var showWeatherWidget: Boolean
        get() = prefs.getBoolean(Constants.SHOW_WEATHER_WIDGET, false)
        set(value) = prefs.edit().putBoolean(Constants.SHOW_WEATHER_WIDGET, value).apply()

    var showWeatherWidgetSunSetRise: Boolean
        get() = prefs.getBoolean(Constants.SHOW_WEATHER_WIDGET_SUN_SET_RISE, true)
        set(value) = prefs.edit().putBoolean(Constants.SHOW_WEATHER_WIDGET_SUN_SET_RISE, value).apply()

    var showBatteryWidget: Boolean
        get() = prefs.getBoolean(Constants.SHOW_BATTERY_WIDGET, false)
        set(value) = prefs.edit().putBoolean(Constants.SHOW_BATTERY_WIDGET, value).apply()

    var dateColor: Int
        get() = prefs.getInt(Constants.DATE_COLOR, setColor.toInt())
        set(value) = prefs.edit().putInt(Constants.DATE_COLOR, value).apply()

    var alarmClockColor: Int
        get() = prefs.getInt(Constants.ALARM_CLOCK_COLOR, setColor.toInt())
        set(value) = prefs.edit().putInt(Constants.ALARM_CLOCK_COLOR, value).apply()

    var timeColor: Int
        get() = prefs.getInt(Constants.TIME_COLOR, setColor.toInt())
        set(value) = prefs.edit().putInt(Constants.TIME_COLOR, value).apply()

    var batteryColor: Int
        get() = prefs.getInt(Constants.BATTERY_COLOR, setColor.toInt())
        set(value) = prefs.edit().putInt(Constants.BATTERY_COLOR, value).apply()

    var dailyWordColor: Int
        get() = prefs.getInt(Constants.DAILY_WORD_COLOR, setColor.toInt())
        set(value) = prefs.edit().putInt(Constants.DAILY_WORD_COLOR, value).apply()

    var widgetBackgroundColor: Int
        get() = prefs.getInt(Constants.WIDGET_BACKGROUND_COLOR, 0xFF000000.toInt())
        set(value) = prefs.edit().putInt(Constants.WIDGET_BACKGROUND_COLOR, value).apply()

    var widgetTextColor: Int
        get() = prefs.getInt(Constants.WIDGET_TEXT_COLOR, setColor.toInt())
        set(value) = prefs.edit().putInt(Constants.WIDGET_TEXT_COLOR, value).apply()

    var appColor: Int
        get() = prefs.getInt(Constants.APP_COLOR, setColor.toInt())
        set(value) = prefs.edit().putInt(Constants.APP_COLOR, value).apply()

    var showAppIcon: Boolean
        get() = prefs.getBoolean(Constants.SHOW_APP_ICON, false)
        set(value) = prefs.edit().putBoolean(Constants.SHOW_APP_ICON, value).apply()

    var locationDenied: Boolean
        get() = prefs.getBoolean(Constants.LOCATION_DENIED, false)
        set(value) = prefs.edit().putBoolean(Constants.LOCATION_DENIED, value).apply()

    var automaticKeyboard: Boolean
        get() = prefs.getBoolean(Constants.AUTOMATIC_KEYBOARD, true)
        set(value) = prefs.edit().putBoolean(Constants.AUTOMATIC_KEYBOARD, value).apply()

    var automaticOpenApp: Boolean
        get() = prefs.getBoolean(Constants.AUTOMATIC_OPEN_APP, false)
        set(value) = prefs.edit().putBoolean(Constants.AUTOMATIC_OPEN_APP, value).apply()

    var searchFromStart: Boolean
        get() = prefs.getBoolean(Constants.SEARCH_FROM_START, true)
        set(value) = prefs.edit().putBoolean(Constants.SEARCH_FROM_START, value).apply()

    var filterStrength: Int
        get() = prefs.getInt(Constants.FILTER_STRENGTH, 25)
        set(value) = prefs.edit().putInt(Constants.FILTER_STRENGTH, value).apply()

    var swipeThreshold: Int
        get() = prefs.getInt(Constants.SWIPE_THRESHOLD, 100)
        set(value) = prefs.edit().putInt(Constants.SWIPE_THRESHOLD, value).apply()

    var homeAppAlignment: Int
        get() = prefs.getInt(Constants.HOME_APP_ALIGNMENT, Gravity.START)
        set(value) = prefs.edit().putInt(Constants.HOME_APP_ALIGNMENT, value).apply()

    var homeAppsPadding: Float
        get() = prefs.getFloat(Constants.APPS_PADDING, 128f)
        set(value) = prefs.edit().putFloat(Constants.APPS_PADDING, value).apply()

    var homeAppPadding: Float
        get() = prefs.getFloat(Constants.APP_TEXT_PADDING, 10f)
        set(value) = prefs.edit().putFloat(Constants.APP_TEXT_PADDING, value).apply()

    var homeDateAlignment: Int
        get() = prefs.getInt(Constants.HOME_DATE_ALIGNMENT, Gravity.START)
        set(value) = prefs.edit().putInt(Constants.HOME_DATE_ALIGNMENT, value).apply()

    var homeTimeAlignment: Int
        get() = prefs.getInt(Constants.HOME_TIME_ALIGNMENT, Gravity.START)
        set(value) = prefs.edit().putInt(Constants.HOME_TIME_ALIGNMENT, value).apply()

    var homeAlarmClockAlignment: Int
        get() = prefs.getInt(Constants.HOME_ALARM_CLOCK_ALIGNMENT, Gravity.START)
        set(value) = prefs.edit().putInt(Constants.HOME_ALARM_CLOCK_ALIGNMENT, value).apply()

    var homeDailyWordAlignment: Int
        get() = prefs.getInt(Constants.HOME_DAILY_WORD_ALIGNMENT, Gravity.START)
        set(value) = prefs.edit().putInt(Constants.HOME_DAILY_WORD_ALIGNMENT, value).apply()

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

    var alarmClockTextSize: Float
        get() = prefs.getFloat(Constants.ALARM_CLOCK_TEXT_SIZE, 22f)
        set(value) = prefs.edit().putFloat(Constants.ALARM_CLOCK_TEXT_SIZE, value).apply()

    var dailyWordTextSize: Float
        get() = prefs.getFloat(Constants.DAILY_WORD_TEXT_SIZE, 18f)
        set(value) = prefs.edit().putFloat(Constants.DAILY_WORD_TEXT_SIZE, value).apply()

    var weatherOrderNumber: Int
        get() = prefs.getInt(Constants.WIDGET_WEATHER, 1)
        set(value) = prefs.edit().putInt(Constants.WIDGET_WEATHER, value).apply()

    var batteryOrderNumber: Int
        get() = prefs.getInt(Constants.WIDGET_BATTERY, 2)
        set(value) = prefs.edit().putInt(Constants.WIDGET_BATTERY, value).apply()

    var homeAlignmentBottom: Boolean
        get() = prefs.getBoolean(Constants.HOME_ALLIGNMENT_BOTTOM, false)
        set(value) = prefs.edit().putBoolean(Constants.HOME_ALLIGNMENT_BOTTOM, value).apply()

    var settingsLock: Boolean
        get() = prefs.getBoolean(Constants.TOGGLE_SETTING_LOCK, false)
        set(value) = prefs.edit().putBoolean(Constants.TOGGLE_SETTING_LOCK, value).apply()

    var disableAnimations: Boolean
        get() = prefs.getBoolean(Constants.DISABLE_ANIMATIONS, false)
        set(value) = prefs.edit().putBoolean(Constants.DISABLE_ANIMATIONS, value).apply()

    var searchEngines: Constants.SearchEngines
        get() {
            return try {
                Constants.SearchEngines.valueOf(
                    prefs.getString(
                        Constants.SEARCH_ENGINE,
                        Constants.SearchEngines.Default.name
                    ).toString()
                )
            } catch (_: Exception) {
                Constants.SearchEngines.Default
            }
        }
        set(value) = prefs.edit().putString(Constants.SEARCH_ENGINE, value.name).apply()

    var iconPack: Constants.IconPacks
        get() {
            return try {
                Constants.IconPacks.valueOf(
                    prefs.getString(
                        Constants.ICONS_PACK,
                        Constants.IconPacks.System.name
                    ).toString()
                )
            } catch (_: Exception) {
                Constants.IconPacks.System
            }
        }
        set(value) = prefs.edit().putString(Constants.ICONS_PACK, value.name).apply()

    var launcherFont: Constants.Fonts
        get() {
            return try {
                Constants.Fonts.valueOf(
                    prefs.getString(
                        Constants.LAUNCHER_FONT,
                        Constants.Fonts.System.name
                    ).toString()
                )
            } catch (_: Exception) {
                Constants.Fonts.System
            }
        }
        set(value) = prefs.edit().putString(Constants.LAUNCHER_FONT, value.name).apply()

    var swipeUpAction: Constants.Action
        get() = loadAction(Constants.SWIPE_UP_ACTION, Constants.Action.ShowRecents)
        set(value) = storeAction(Constants.SWIPE_UP_ACTION, value)

    var swipeDownAction: Constants.Action
        get() = loadAction(Constants.SWIPE_DOWN_ACTION, Constants.Action.ShowNotification)
        set(value) = storeAction(Constants.SWIPE_DOWN_ACTION, value)

    var swipeLeftAction: Constants.Action
        get() = loadAction(Constants.SWIPE_LEFT_ACTION, Constants.Action.ShowAppList)
        set(value) = storeAction(Constants.SWIPE_LEFT_ACTION, value)

    var swipeRightAction: Constants.Action
        get() = loadAction(Constants.SWIPE_RIGHT_ACTION, Constants.Action.ShowFavoriteList)
        set(value) = storeAction(Constants.SWIPE_RIGHT_ACTION, value)

    var doubleTapAction: Constants.Action
        get() = loadAction(Constants.DOUBLE_TAP_ACTION, Constants.Action.LockScreen)
        set(value) = storeAction(Constants.DOUBLE_TAP_ACTION, value)

    var swipeUpApp: String
        get() = prefs.getString(Constants.SWIPE_UP_APP, "").toString()
        set(value) = prefs.edit().putString(Constants.SWIPE_UP_APP, value).apply()

    var swipeDownApp: String
        get() = prefs.getString(Constants.SWIPE_DOWN_APP, "").toString()
        set(value) = prefs.edit().putString(Constants.SWIPE_DOWN_APP, value).apply()

    var swipeLeftApp: String
        get() = prefs.getString(Constants.SWIPE_LEFT_APP, "").toString()
        set(value) = prefs.edit().putString(Constants.SWIPE_LEFT_APP, value).apply()

    var swipeRightApp: String
        get() = prefs.getString(Constants.SWIPE_RIGHT_APP, "").toString()
        set(value) = prefs.edit().putString(Constants.SWIPE_RIGHT_APP, value).apply()

    var doubleTapApp: String
        get() = prefs.getString(Constants.DOUBLE_TAP_APP, "").toString()
        set(value) = prefs.edit().putString(Constants.DOUBLE_TAP_APP, value).apply()

    var appLanguage: Constants.Language
        get() {
            return try {
                Constants.Language.valueOf(
                    prefs.getString(
                        Constants.PACKAGE_LANGUAGE,
                        Constants.Language.System.name
                    ).toString()
                )
            } catch (_: Exception) {
                Constants.Language.System
            }
        }
        set(value) = prefs.edit().putString(Constants.PACKAGE_LANGUAGE, value.name).apply()


    var weatherUnits: Constants.Units
        get() {
            return try {
                Constants.Units.valueOf(
                    prefs.getString(
                        Constants.WEATHER_UNITS,
                        Constants.Units.Metric.name
                    ).toString()
                )
            } catch (_: Exception) {
                Constants.Units.Metric
            }
        }
        set(value) = prefs.edit().putString(Constants.WEATHER_UNITS, value.name).apply()

    private fun loadAction(prefString: String, default: Constants.Action): Constants.Action {
        val string = prefs.getString(
            prefString,
            default.toString()
        ).toString()
        return Constants.Action.valueOf(string)
    }

    private fun storeAction(prefString: String, value: Constants.Action) {
        prefs.edit().putString(prefString, value.name).apply()
    }

    private fun getColor(context: Context): Long {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                0xFFFFFFFF
            }

            Configuration.UI_MODE_NIGHT_NO -> {
                0xFF000000
            }

            else -> {
                0xFFFFFFFF
            }
        }
    }

    fun saveToString(): String {
        val all: HashMap<String, Any?> = HashMap(prefs.all)
        return Gson().toJson(all)
    }

    fun loadFromString(json: String) {
        val editor = prefs.edit()
        // Custom deserializer to handle numbers properly
        val gson = GsonBuilder().registerTypeAdapter(
            object : TypeToken<HashMap<String, Any?>>() {}.type,
            JsonDeserializer { jsonElement, _, _ ->
                val map = HashMap<String, Any?>()
                val jsonObject = jsonElement.asJsonObject
                for (entry in jsonObject.entrySet()) {
                    val key = entry.key
                    val value = entry.value
                    when {
                        value.isJsonPrimitive -> {
                            val primitive = value.asJsonPrimitive
                            when {
                                primitive.isBoolean -> map[key] = primitive.asBoolean
                                primitive.isString -> map[key] = primitive.asString
                                primitive.isNumber -> {
                                    val numStr = primitive.toString()
                                    map[key] = if (numStr.contains(".")) {
                                        numStr.toFloat()
                                    } else {
                                        numStr.toInt()
                                    }
                                }
                            }
                        }

                        value.isJsonArray -> {
                            val jsonArray = value.asJsonArray
                            val set = mutableSetOf<String>()
                            for (element in jsonArray) {
                                if (element.isJsonPrimitive && element.asJsonPrimitive.isString) {
                                    set.add(element.asString)
                                }
                            }
                            map[key] = set
                        }

                        else -> {
                            Log.d("backup error", "$key | $value")
                        }
                    }
                }
                map
            }).create()

        val all: HashMap<String, Any?> = gson.fromJson(json, object : TypeToken<HashMap<String, Any?>>() {}.type)

        for ((key, value) in all) {
            when (value) {
                is String -> editor.putString(key, value)
                is Boolean -> editor.putBoolean(key, value)
                is Int -> editor.putInt(key, value)
                is Float -> editor.putFloat(key, value)
                is MutableSet<*> -> {
                    val list = value.filterIsInstance<String>().toSet()
                    editor.putStringSet(key, list)
                }

                else -> {
                    Log.d("backup error", "$key | $value")
                }
            }
        }
        editor.apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun clearAll(context: Context) {
        val prefsLauncher: SharedPreferences = context.getSharedPreferences(Constants.PACKAGE_PREFS, 0)
        val prefsWidgets: SharedPreferences = context.getSharedPreferences(Constants.WEATHER_PREFS, 0)

        prefsLauncher.edit().clear().apply()
        prefsWidgets.edit().clear().apply()
    }
}