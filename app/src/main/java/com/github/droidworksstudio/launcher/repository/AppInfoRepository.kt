package com.github.droidworksstudio.launcher.repository

import android.content.Context
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.os.Build
import android.os.UserHandle
import android.os.UserManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.github.droidworksstudio.launcher.utils.Constants
import com.github.droidworksstudio.launcher.data.dao.AppInfoDAO
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.lang.reflect.Method
import java.time.LocalDateTime
import javax.inject.Inject


class AppInfoRepository @Inject constructor(
    private val appDao: AppInfoDAO,
) {

    @Inject
    lateinit var packageManager: PackageManager

    fun getDrawApps(): Flow<List<AppInfo>> {
        return appDao.getDrawAppsFlow()
    }

    fun getFavoriteApps(): Flow<List<AppInfo>> {
        return appDao.getFavoriteAppsFlow()
    }

    fun getHiddenApps(): Flow<List<AppInfo>> {
        return appDao.getHiddenAppsFlow()
    }

    suspend fun updateInfo(appInfo: AppInfo) {
        appDao.update(appInfo)
    }

    suspend fun updateAppOrder(appInfoList: List<AppInfo>) {
        withContext(Dispatchers.IO) {
            appDao.updateAppOrder(appInfoList)
        }
    }

    fun searchNote(query: String?): Flow<List<AppInfo>> {
        return appDao.searchApps(query)
    }

    suspend fun updateFavoriteAppInfo(appInfo: AppInfo) = withContext(Dispatchers.IO) {

        if (appInfo.favorite) {
            val maxOrder = appDao.getMaxOrder()
            val newOrder = maxOrder + 1
            appInfo.appOrder = newOrder
            appDao.updateAppInfo(appInfo)
            Log.d("Tag", "${appInfo.appName} : DAO Order: ${appInfo.appOrder}")
            Log.d("Tag", "${appInfo.appName} : DAO Favorite: ${appInfo.favorite}")
        } else {
            appInfo.appOrder = -1
            appDao.updateAppInfo(appInfo)
            Log.d("Tag", "${appInfo.appName} : DAO Order Remove: ${appInfo.appOrder}")
            Log.d("Tag", "${appInfo.appName} : DAO Favorite Remove: ${appInfo.favorite}")
        }

        val favoriteAppInfos = appDao.getFavoriteAppInfo().sortedBy { it.appOrder }

        for ((index, info) in favoriteAppInfos.withIndex()) {
            info.appOrder = index
            appDao.updateAppInfo(info)
        }
    }

    suspend fun updateAppName(appInfo: AppInfo, newAppName: String) = withContext(Dispatchers.IO) {
        appInfo.appName = newAppName
        appDao.updateAppName(appInfo, newAppName)
    }

    suspend fun updateAppHidden(appInfo: AppInfo, appHidden: Boolean) =
        withContext(Dispatchers.IO) {
            appInfo.hidden = appHidden
            appDao.updateAppHidden(appInfo, appHidden)
        }

    suspend fun updateAppLock(appInfo: AppInfo, appLock: Boolean) = withContext(Dispatchers.IO) {
        appInfo.lock = appLock
        appDao.updateLockApp(appInfo, appLock)
    }

    private suspend fun getInstalledPackages(): Set<String> = withContext(Dispatchers.IO) {

        val packages = HashSet<String>()
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in apps) {
            packages.add(app.packageName)
        }
        packages
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun initInstalledAppInfo(context: Context): List<AppInfo> =
        withContext(Dispatchers.IO) {
            val appList: MutableList<AppInfo> = mutableListOf()

            val allApps = appDao.getAllAppsFlow().firstOrNull()

            val existingPackageNames = allApps?.map { it.packageName } ?: emptyList()

            val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
            val launcherApps =
                context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

            val excludedPackageNames =
                mutableListOf(Constants.PACKAGE_NAME, Constants.PACKAGE_NAME_DEBUG)

            val getIdentifierMethod: Method =
                UserHandle::class.java.getDeclaredMethod("getIdentifier")

            val newAppList: List<AppInfo> = userManager.userProfiles
                .flatMap { profile ->
                    // Invoke the getIdentifier method on the UserHandle instance
                    val userId = getIdentifierMethod.invoke(profile) as Int

                    when (userId) {
                        0 -> {
                            // Handle the case when profile is UserHandle{0}
                            launcherApps.getActivityList(null, profile)
                                .mapNotNull { app ->
                                    val packageName = app.applicationInfo.packageName
                                    val currentDateTime = LocalDateTime.now()
                                    if (packageName !in existingPackageNames && packageName !in excludedPackageNames) {
                                        AppInfo(
                                            appName = app.label.toString(),
                                            packageName = packageName,
                                            favorite = false,
                                            hidden = false,
                                            lock = false,
                                            createTime = currentDateTime.toString(),
                                            work = false,
                                        )
                                    } else {
                                        val existingApp = getAppByPackageName(packageName)
                                        existingApp?.let { appList.add(it) }
                                        existingApp
                                    }
                                }
                        }

                        else -> {
                            // Handle other profiles
                            launcherApps.getActivityList(null, profile)
                                .mapNotNull { app ->
                                    val packageName = app.applicationInfo.packageName
                                    val currentDateTime = LocalDateTime.now()
                                    if (packageName !in existingPackageNames && packageName !in excludedPackageNames) {
                                        AppInfo(
                                            appName = app.label.toString(),
                                            packageName = packageName,
                                            favorite = false,
                                            hidden = false,
                                            lock = false,
                                            createTime = currentDateTime.toString(),
                                            work = true,
                                        )
                                    } else {
                                        val existingApp = getAppByPackageNameWork(packageName)
                                        existingApp?.let { appList.add(it) }
                                        existingApp
                                    }
                                }
                        }
                    }
                }


            appDao.insertAll(newAppList.sortedBy { it.appName })
            Log.d("Tag", "State: ${newAppList.sortedBy { it.appName }}")

            val deletedApps = allApps?.filter { it.packageName !in existingPackageNames }
            deletedApps?.forEach { appDao.delete(it) }

            appList.sortBy { it.appName }
            appList
        }

    @RequiresApi(Build.VERSION_CODES.R)
    suspend fun compareInstalledApp(): List<AppInfo> = withContext(Dispatchers.IO) {
        val installedPackages = getInstalledPackages()
        val uninstalledApps = mutableListOf<AppInfo>()

        val allApps = appDao.getAllApps()

        val newApps = mutableListOf<AppInfo>()

        for (app in allApps) {
            val packageName = app.packageName
            if (!installedPackages.contains(packageName)) {
                appDao.delete(app)
                uninstalledApps.add(app)
            }
        }

        val newPackageNames = installedPackages.filterNot { packageName ->
            allApps.any { app -> app.packageName == packageName }
        }

        for (packageName in newPackageNames) {
            val app = appDao.getAppByPackageName(packageName)
            app?.let {
                newApps.add(app)
            }
        }

        appDao.insertAll(newApps)
        uninstalledApps
    }

    private suspend fun getAppByPackageName(packageName: String): AppInfo? {
        return appDao.getAppByPackageName(packageName)
    }

    private suspend fun getAppByPackageNameWork(packageName: String): AppInfo? {
        return appDao.getAppByPackageNameWork(packageName)
    }
}