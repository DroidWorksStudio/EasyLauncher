package com.github.droidworksstudio.launcher.data.dao

import android.util.Log
import androidx.room.*
import com.github.droidworksstudio.launcher.data.entities.AppInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface AppInfoDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(app: AppInfo)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(apps: List<AppInfo>)

    @Update
    suspend fun update(app: AppInfo)

    @Delete
    fun delete(app: AppInfo)

    @Query("SELECT * FROM app ORDER BY app_name COLLATE NOCASE ASC")
    fun getAllApps(): List<AppInfo>

    @Query("SELECT * FROM app ORDER BY app_name COLLATE NOCASE ASC")
    fun getAllAppsFlow(): Flow<List<AppInfo>>

    @Query("SELECT * FROM app WHERE is_hidden = 0 ORDER BY app_name COLLATE NOCASE ASC")
    fun getDrawAppsFlow(): Flow<List<AppInfo>>

    @Query("SELECT * FROM app WHERE is_favorite = 1 ORDER BY app_order ASC, id ASC")
    fun getFavoriteAppsFlow(): Flow<List<AppInfo>>

    @Query("SELECT * FROM app WHERE is_hidden = 1 ORDER BY id ASC")
    fun getHiddenAppsFlow(): Flow<List<AppInfo>>

    @Query("SELECT * FROM app WHERE is_lock = 1 ORDER BY app_order ASC")
    fun getLockAppsFlow(): Flow<List<AppInfo>>

    @Query("SELECT * FROM app WHERE app_name LIKE :query COLLATE NOCASE AND is_hidden = 0")
    fun searchApps(query: String?): Flow<List<AppInfo>>

    @Update
    suspend fun updateAppInfo(appInfo: AppInfo)

    @Update
    suspend fun updateAppOrder(appInfo: List<AppInfo>)

    @Transaction
    suspend fun updateAppName(appInfo: AppInfo, newAppName: String) {
        appInfo.appName = newAppName
        update(appInfo)
        logUpdate("App name updated", appInfo)
    }

    @Transaction
    suspend fun updateAppHidden(appInfo: AppInfo, appHidden: Boolean) {
        appInfo.hidden = appHidden
        update(appInfo)
        logUpdate("App hidden status updated", appInfo)
    }

    @Transaction
    suspend fun updateLockApp(appInfo: AppInfo, appLock: Boolean) {
        appInfo.lock = appLock
        update(appInfo)
        logUpdate("App lock status updated", appInfo)
    }

    @Query("SELECT MAX(`app_order`) FROM app")
    fun getMaxOrder(): Int

    @Query("SELECT * FROM app WHERE is_favorite = 1")
    fun getFavoriteAppInfo(): List<AppInfo>

    @Query("SELECT * FROM app WHERE package_name = :packageName AND is_work = 0")
    suspend fun getAppByPackageName(packageName: String): AppInfo?

    @Query("SELECT * FROM app WHERE package_name = :packageName AND is_work = 1")
    suspend fun getAppByPackageNameWork(packageName: String): AppInfo?

    private fun logUpdate(message: String, appInfo: AppInfo) {
        // You can replace this with a logging library like Timber for more advanced logging capabilities.
        Log.d(
            "AppInfoDAO",
            "$message: ${appInfo.appName} (Order: ${appInfo.appOrder}, Hidden: ${appInfo.hidden}, Lock: ${appInfo.lock})"
        )
    }
}
