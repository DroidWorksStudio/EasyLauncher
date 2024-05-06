package com.github.droidworksstudio.launcher.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreferenceViewModel @Inject constructor(
    private val preferenceHelper: PreferenceHelper)
    : ViewModel() {

    val firstLaunchLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val showStatusBarLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val showTimeLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val showDateLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val showDailyWordLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val showBatteryLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val homeAppAlignmentLiveData: MutableLiveData<Int> = MutableLiveData()
    val homeDateAlignmentLiveData: MutableLiveData<Int> = MutableLiveData()
    val homeTimeAlignmentLiveData: MutableLiveData<Int> = MutableLiveData()
    val homeDailyWordAlignmentLiveData: MutableLiveData<Int> = MutableLiveData()
    val dateColorLiveData: MutableLiveData<Int> = MutableLiveData()
    val timeColorLiveData: MutableLiveData<Int> = MutableLiveData()
    val batteryColorLiveData: MutableLiveData<Int> = MutableLiveData()
    val dailyWordColorLiveData: MutableLiveData<Int> = MutableLiveData()
    val appColorLiveData: MutableLiveData<Int> = MutableLiveData()
    val tapLockScreenLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun setFirstLaunch(firstLaunch: Boolean) {
        preferenceHelper.firstLaunch = firstLaunch
        firstLaunchLiveData.postValue(preferenceHelper.firstLaunch)
    }

    fun setShowStatusBar(showStatusBar: Boolean) {
        preferenceHelper.showStatusBar = showStatusBar
        showStatusBarLiveData.postValue(preferenceHelper.showStatusBar)
    }

    fun setShowTime(showTime: Boolean) {
        preferenceHelper.showTime = showTime
        showTimeLiveData.postValue(preferenceHelper.showTime)
    }

    fun setShowDate(showDate: Boolean) {
        preferenceHelper.showDate = showDate
        showDateLiveData.postValue(preferenceHelper.showDate)
    }

    fun setShowBattery(showBattery: Boolean){
        preferenceHelper.showBattery = showBattery
        showBatteryLiveData.postValue(preferenceHelper.showBattery)
    }

    fun setShowDailyWord(showDailyWord: Boolean) {
        preferenceHelper.showDailyWord = showDailyWord
        showDailyWordLiveData.postValue(preferenceHelper.showDailyWord)
    }

    fun setDailyWordColor(dailyWordColor: Int) {
        preferenceHelper.dailyWordColor = dailyWordColor
        dailyWordColorLiveData.postValue(preferenceHelper.dailyWordColor)
    }

    fun setAppColor(appColor: Int) {
        preferenceHelper.appColor = appColor
        appColorLiveData.postValue(preferenceHelper.appColor)
    }

    fun setDateColor(dateColor: Int) {
        preferenceHelper.dateColor = dateColor
        dateColorLiveData.postValue(preferenceHelper.dateColor)
    }

    fun setTimeColor(timeColor: Int) {
        preferenceHelper.timeColor = timeColor
        timeColorLiveData.postValue(preferenceHelper.timeColor)
    }

    fun setBatteryColor(batteryColor: Int){
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

    fun setHomeDailyWordAlignment(dailyWordAlignment: Int){
        preferenceHelper.homeDailyWordAlignment = dailyWordAlignment
        homeDailyWordAlignmentLiveData.postValue(preferenceHelper.homeDailyWordAlignment)
    }

    fun setDoubleTapLock(tapLockScreen: Boolean){
        preferenceHelper.tapLockScreen = tapLockScreen
        tapLockScreenLiveData.postValue((preferenceHelper.tapLockScreen))
    }
}