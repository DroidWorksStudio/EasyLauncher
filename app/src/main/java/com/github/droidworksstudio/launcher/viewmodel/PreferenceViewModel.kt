package com.github.droidworksstudio.launcher.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import com.github.droidworksstudio.launcher.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreferenceViewModel @Inject constructor(
    private val preferenceHelper: PreferenceHelper,
) : ViewModel() {

    private val firstLaunchLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val showStatusBarLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val showNavigationBarLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val showTimeLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val showDateLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val showDailyWordLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val showAlarmClockLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val showBatteryLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val showWeatherWidgetLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val showWeatherWidgetSunSetRiseLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val showBatteryWidgetLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val showAppIconLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val homeAppAlignmentLiveData: MutableLiveData<Int> = MutableLiveData()
    private val homeDateAlignmentLiveData: MutableLiveData<Int> = MutableLiveData()
    private val homeTimeAlignmentLiveData: MutableLiveData<Int> = MutableLiveData()
    private val homeAlarmClockAlignmentLiveData: MutableLiveData<Int> = MutableLiveData()
    private val homeDailyWordAlignmentLiveData: MutableLiveData<Int> = MutableLiveData()
    private val dateColorLiveData: MutableLiveData<Int> = MutableLiveData()
    private val timeColorLiveData: MutableLiveData<Int> = MutableLiveData()
    private val batteryColorLiveData: MutableLiveData<Int> = MutableLiveData()
    private val dailyWordColorLiveData: MutableLiveData<Int> = MutableLiveData()
    private val alarmClockColorLiveData: MutableLiveData<Int> = MutableLiveData()
    private val widgetBackgroundColorLiveData: MutableLiveData<Int> = MutableLiveData()
    private val widgetTextColorLiveData: MutableLiveData<Int> = MutableLiveData()
    private val appColorLiveData: MutableLiveData<Int> = MutableLiveData()
    private val dateTextSizeLiveData: MutableLiveData<Float> = MutableLiveData()
    private val timeTextSizeLiveData: MutableLiveData<Float> = MutableLiveData()
    private val appTextSizeLiveData: MutableLiveData<Float> = MutableLiveData()
    private val batteryTextSizeLiveData: MutableLiveData<Float> = MutableLiveData()
    private val alarmClockTextSizeLiveData: MutableLiveData<Float> = MutableLiveData()
    private val dailyWordTextSizeLiveData: MutableLiveData<Float> = MutableLiveData()
    private val autoOpenAppsLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val searchFromStartLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val homeAlignmentBottomLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val autoKeyboardLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val lockSettingsLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val disableAnimationsLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val appGroupPaddingSizeLiveData: MutableLiveData<Float> = MutableLiveData()
    private val appPaddingSizeLiveData: MutableLiveData<Float> = MutableLiveData()

    private val weatherOrderNumberLiveData: MutableLiveData<Int> = MutableLiveData()
    private val batteryOrderNumberLiveData: MutableLiveData<Int> = MutableLiveData()
    private val filterStrengthLiveData: MutableLiveData<Int> = MutableLiveData()
    private val swipeThresholdLiveData: MutableLiveData<Int> = MutableLiveData()

    private val appLanguageLiveData: MutableLiveData<Constants.Language> = MutableLiveData()
    private val searchEngineLiveData: MutableLiveData<Constants.SearchEngines> = MutableLiveData()
    private val iconPackLiveData: MutableLiveData<Constants.IconPacks> = MutableLiveData()
    private val launcherFontLiveData: MutableLiveData<Constants.Fonts> = MutableLiveData()
    private val doubleTapActionLiveData: MutableLiveData<Constants.Action> = MutableLiveData()
    private val swipeUpActionLiveData: MutableLiveData<Constants.Action> = MutableLiveData()
    private val swipeDownActionLiveData: MutableLiveData<Constants.Action> = MutableLiveData()
    private val swipeLeftActionLiveData: MutableLiveData<Constants.Action> = MutableLiveData()
    private val swipeRightActionLiveData: MutableLiveData<Constants.Action> = MutableLiveData()

    fun setFirstLaunch(firstLaunch: Boolean) {
        preferenceHelper.firstLaunch = firstLaunch
        firstLaunchLiveData.postValue(preferenceHelper.firstLaunch)
    }

    fun setShowStatusBar(showStatusBar: Boolean) {
        preferenceHelper.showStatusBar = showStatusBar
        showStatusBarLiveData.postValue(preferenceHelper.showStatusBar)
    }

    fun setShowNavigationBar(showNavigationBar: Boolean) {
        preferenceHelper.showNavigationBar = showNavigationBar
        showNavigationBarLiveData.postValue(preferenceHelper.showNavigationBar)
    }

    fun setShowTime(showTime: Boolean) {
        preferenceHelper.showTime = showTime
        showTimeLiveData.postValue(preferenceHelper.showTime)
    }

    fun setShowDate(showDate: Boolean) {
        preferenceHelper.showDate = showDate
        showDateLiveData.postValue(preferenceHelper.showDate)
    }

    fun setShowBattery(showBattery: Boolean) {
        preferenceHelper.showBattery = showBattery
        showBatteryLiveData.postValue(preferenceHelper.showBattery)
    }

    fun setShowDailyWord(showDailyWord: Boolean) {
        preferenceHelper.showDailyWord = showDailyWord
        showDailyWordLiveData.postValue(preferenceHelper.showDailyWord)
    }

    fun setShowAlarmClock(showAlarmClock: Boolean) {
        preferenceHelper.showAlarmClock = showAlarmClock
        showAlarmClockLiveData.postValue(preferenceHelper.showAlarmClock)
    }

    fun setShowAppIcons(showAppIcons: Boolean) {
        preferenceHelper.showAppIcon = showAppIcons
        showAppIconLiveData.postValue(preferenceHelper.showAppIcon)
    }

    fun setAlarmClockColor(alarmClockColor: Int) {
        preferenceHelper.alarmClockColor = alarmClockColor
        alarmClockColorLiveData.postValue(preferenceHelper.alarmClockColor)
    }

    fun setDailyWordColor(dailyWordColor: Int) {
        preferenceHelper.dailyWordColor = dailyWordColor
        dailyWordColorLiveData.postValue(preferenceHelper.dailyWordColor)
    }

    fun setShowWeatherWidget(showWeather: Boolean) {
        preferenceHelper.showWeatherWidget = showWeather
        showWeatherWidgetLiveData.postValue(preferenceHelper.showWeatherWidget)
    }

    fun setShowWeatherWidgetSunSetRise(showWeatherSunSetRise: Boolean) {
        preferenceHelper.showWeatherWidgetSunSetRise = showWeatherSunSetRise
        showWeatherWidgetSunSetRiseLiveData.postValue(preferenceHelper.showWeatherWidgetSunSetRise)
    }

    fun setShowBatteryWidget(showBattery: Boolean) {
        preferenceHelper.showBatteryWidget = showBattery
        showBatteryWidgetLiveData.postValue(preferenceHelper.showBatteryWidget)
    }

    fun setAppColor(appColor: Int) {
        preferenceHelper.appColor = appColor
        appColorLiveData.postValue(preferenceHelper.appColor)
    }

    fun setWeatherOrderNumber(orderNumber: Int) {
        preferenceHelper.weatherOrderNumber = orderNumber
        weatherOrderNumberLiveData.postValue(preferenceHelper.weatherOrderNumber)
    }

    fun setBatteryOrderNumber(orderNumber: Int) {
        preferenceHelper.batteryOrderNumber = orderNumber
        batteryOrderNumberLiveData.postValue(preferenceHelper.batteryOrderNumber)
    }

    fun setDateColor(dateColor: Int) {
        preferenceHelper.dateColor = dateColor
        dateColorLiveData.postValue(preferenceHelper.dateColor)
    }

    fun setTimeColor(timeColor: Int) {
        preferenceHelper.timeColor = timeColor
        timeColorLiveData.postValue(preferenceHelper.timeColor)
    }

    fun setWidgetBackgroundColor(timeColor: Int) {
        preferenceHelper.widgetBackgroundColor = timeColor
        widgetBackgroundColorLiveData.postValue(preferenceHelper.widgetBackgroundColor)
    }

    fun setWidgetTextColor(timeColor: Int) {
        preferenceHelper.widgetTextColor = timeColor
        widgetTextColorLiveData.postValue(preferenceHelper.widgetTextColor)
    }

    fun setBatteryColor(batteryColor: Int) {
        preferenceHelper.batteryColor = batteryColor
        batteryColorLiveData.postValue(preferenceHelper.batteryColor)
    }

    fun setHomeAppAlignment(homeAppAlignment: Int) {
        preferenceHelper.homeAppAlignment = homeAppAlignment
        homeAppAlignmentLiveData.postValue(preferenceHelper.homeAppAlignment)
    }

    fun setHomeDateAlignment(homeDateAlignment: Int) {
        preferenceHelper.homeDateAlignment = homeDateAlignment
        homeDateAlignmentLiveData.postValue(preferenceHelper.homeDateAlignment)
    }

    fun setHomeTimeAppAlignment(homeTimeAlignment: Int) {
        preferenceHelper.homeTimeAlignment = homeTimeAlignment
        homeTimeAlignmentLiveData.postValue(preferenceHelper.homeTimeAlignment)
    }

    fun setHomeDailyWordAppAlignment(homeDailyWordAlignment: Int) {
        preferenceHelper.homeDailyWordAlignment = homeDailyWordAlignment
        homeDailyWordAlignmentLiveData.postValue(preferenceHelper.homeDailyWordAlignment)
    }

    fun setHomeAlarmClockAppAlignment(homeAlarmClockAlignment: Int) {
        preferenceHelper.homeAlarmClockAlignment = homeAlarmClockAlignment
        homeAlarmClockAlignmentLiveData.postValue(preferenceHelper.homeAlarmClockAlignment)
    }

    fun setDateTextSize(dateTextSize: Float) {
        preferenceHelper.dateTextSize = dateTextSize
        dateTextSizeLiveData.postValue(preferenceHelper.dateTextSize)
    }

    fun setTimeTextSize(timeTextSize: Float) {
        preferenceHelper.timeTextSize = timeTextSize
        timeTextSizeLiveData.postValue(preferenceHelper.timeTextSize)
    }

    fun setAppTextSize(appTextSize: Float) {
        preferenceHelper.appTextSize = appTextSize
        appTextSizeLiveData.postValue(preferenceHelper.appTextSize)
    }

    fun setBatteryTextSize(batteryTextSize: Float) {
        preferenceHelper.batteryTextSize = batteryTextSize
        batteryTextSizeLiveData.postValue(preferenceHelper.batteryTextSize)
    }

    fun setAlarmClockTextSize(alarmTextSize: Float) {
        preferenceHelper.alarmClockTextSize = alarmTextSize
        alarmClockTextSizeLiveData.postValue(preferenceHelper.alarmClockTextSize)
    }

    fun setDailyWordTextSize(dailyWordTextSize: Float) {
        preferenceHelper.dailyWordTextSize = dailyWordTextSize
        dailyWordTextSizeLiveData.postValue(preferenceHelper.dailyWordTextSize)
    }

    fun setAppPaddingSize(appPaddingSize: Float) {
        preferenceHelper.homeAppPadding = appPaddingSize
        appPaddingSizeLiveData.postValue(preferenceHelper.homeAppPadding)
    }

    fun setAppGroupPaddingSize(appPaddingSize: Float) {
        preferenceHelper.homeAppsPadding = appPaddingSize
        appGroupPaddingSizeLiveData.postValue(preferenceHelper.homeAppsPadding)
    }

    fun setDoubleTap(action: Constants.Action) {
        preferenceHelper.doubleTapAction = action
        doubleTapActionLiveData.postValue((preferenceHelper.doubleTapAction))
    }

    fun setSwipeUp(action: Constants.Action) {
        preferenceHelper.swipeUpAction = action
        swipeUpActionLiveData.postValue((preferenceHelper.swipeUpAction))
    }

    fun setSwipeDown(action: Constants.Action) {
        preferenceHelper.swipeDownAction = action
        swipeDownActionLiveData.postValue((preferenceHelper.swipeDownAction))
    }

    fun setSwipeLeft(action: Constants.Action) {
        preferenceHelper.swipeLeftAction = action
        swipeLeftActionLiveData.postValue((preferenceHelper.swipeLeftAction))
    }

    fun setSwipeRight(action: Constants.Action) {
        preferenceHelper.swipeRightAction = action
        swipeRightActionLiveData.postValue((preferenceHelper.swipeRightAction))
    }

    fun setAutoKeyboard(autoKeyboard: Boolean) {
        preferenceHelper.automaticKeyboard = autoKeyboard
        autoKeyboardLiveData.postValue((preferenceHelper.automaticKeyboard))
    }

    fun setAutoOpenApp(autoOpenApp: Boolean) {
        preferenceHelper.automaticOpenApp = autoOpenApp
        autoOpenAppsLiveData.postValue((preferenceHelper.automaticOpenApp))
    }

    fun setHomeAlignmentBottom(homeAlignmentBottom: Boolean) {
        preferenceHelper.homeAlignmentBottom = homeAlignmentBottom
        homeAlignmentBottomLiveData.postValue((preferenceHelper.homeAlignmentBottom))
    }

    fun setSearchFromStart(searchFromStart: Boolean) {
        preferenceHelper.searchFromStart = searchFromStart
        searchFromStartLiveData.postValue((preferenceHelper.searchFromStart))
    }

    fun setLockSettings(lockSettings: Boolean) {
        preferenceHelper.settingsLock = lockSettings
        lockSettingsLiveData.postValue((preferenceHelper.settingsLock))
    }

    fun setDisableAnimations(disableAnimations: Boolean) {
        preferenceHelper.disableAnimations = disableAnimations
        disableAnimationsLiveData.postValue((preferenceHelper.disableAnimations))
    }

    fun setAppLanguage(appLanguage: Constants.Language) {
        preferenceHelper.appLanguage = appLanguage
        appLanguageLiveData.postValue((preferenceHelper.appLanguage))
    }

    fun setSearchEngine(searchEngine: Constants.SearchEngines) {
        preferenceHelper.searchEngines = searchEngine
        searchEngineLiveData.postValue((preferenceHelper.searchEngines))
    }

    fun setIconsPack(iconPack: Constants.IconPacks) {
        preferenceHelper.iconPack = iconPack
        iconPackLiveData.postValue((preferenceHelper.iconPack))
    }

    fun setFilterStrength(filterStrength: Int) {
        preferenceHelper.filterStrength = filterStrength
        filterStrengthLiveData.postValue((preferenceHelper.filterStrength))
    }

    fun setSwipeThreshold(swipeThreshold: Int) {
        preferenceHelper.swipeThreshold = swipeThreshold
        swipeThresholdLiveData.postValue((preferenceHelper.swipeThreshold))
    }

    fun setLauncherFont(launcherFont: Constants.Fonts) {
        preferenceHelper.launcherFont = launcherFont
        launcherFontLiveData.postValue((preferenceHelper.launcherFont))
    }
}