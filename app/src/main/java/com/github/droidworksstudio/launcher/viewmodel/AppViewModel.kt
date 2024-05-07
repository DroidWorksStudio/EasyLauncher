package com.github.droidworksstudio.launcher.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import com.github.droidworksstudio.launcher.repository.AppInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val appInfoRepository: AppInfoRepository
) : ViewModel() {

    val drawApps: Flow<List<AppInfo>> = appInfoRepository.getDrawApps().conflate()
    val favoriteApps: Flow<List<AppInfo>> = appInfoRepository.getFavoriteApps().conflate()
    val hiddenApps: Flow<List<AppInfo>> = appInfoRepository.getHiddenApps().conflate()
    fun initializeInstalledAppInfo(context: Context) {
        viewModelScope.launch {
            appInfoRepository.initInstalledAppInfo(context)
        }
    }
    fun updateAppInfoFavorite(appInfo: AppInfo) {
        viewModelScope.launch {
            appInfoRepository.updateFavoriteAppInfo(appInfo)
            Log.d("Tag", "ViewModel Home Order : ${appInfo.appOrder}")
        }
    }
    fun updateAppInfoAppName(appInfo: AppInfo, newAppName: String) {
        viewModelScope.launch {
            appInfoRepository.updateAppName(appInfo, newAppName)
            Log.d("Tag", "ViewModel Home Name : ${appInfo.appName}")
        }
    }
    fun updateAppHidden(appInfo: AppInfo, appHidden: Boolean) {
        viewModelScope.launch {
            appInfoRepository.updateAppHidden(appInfo, appHidden)
        }
    }
    fun updateAppLock(appInfo: AppInfo, appLock: Boolean) {
        viewModelScope.launch {
            appInfoRepository.updateAppLock(appInfo, appLock)
        }
    }
    fun compareInstalledAppInfo() {
        viewModelScope.launch {
            appInfoRepository.compareInstalledApp()
        }
    }
    suspend fun updateAppOrder(appInfoList: List<AppInfo>) {
        withContext(Dispatchers.IO) {
            appInfoRepository.updateAppOrder(appInfoList)
        }
    }
    fun update(appInfo: AppInfo) {
        viewModelScope.launch {
            appInfoRepository.updateInfo(appInfo)
        }
    }
    fun searchAppInfo(query: String?) = appInfoRepository.searchNote(query)
}
