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
    suspend fun delete(app: AppInfo)

    @Query("SELECT * FROM app ORDER BY app_name ASC")
    fun getAllApps(): List<AppInfo>
    
    @Query("SELECT * FROM app ORDER BY app_name ASC")
    fun getAllAppsFlow(): Flow<List<AppInfo>>

    @Query("SELECT * FROM app WHERE is_hidden = 0 ORDER BY app_name ASC")
    fun getDrawAppsFlow(): Flow<List<AppInfo>>

    @Query("SELECT * FROM app WHERE is_favorite = 1 ORDER BY app_order ASC, id ASC")
    fun getFavoriteAppsFlow(): Flow<List<AppInfo>>

    @Query("SELECT * FROM app WHERE is_hidden = 1 ORDER BY id ASC")
    fun getHiddenAppsFlow(): Flow<List<AppInfo>>

    @Query("SELECT * FROM app WHERE is_lock = 1 ORDER BY app_order ASC")
    fun getLockAppsFlow(): Flow<List<AppInfo>>

    @Query("SELECT * FROM app WHERE app_name LIKE :query AND is_hidden = 0")
    fun searchApps(query: String?): Flow<List<AppInfo>>

    @Update
    fun updateAppInfo(appInfo: AppInfo)

    @Update
    suspend fun updateAppOrder(appInfo: List<AppInfo>)

    @Transaction
    suspend fun updateAppName(appInfo: AppInfo, newAppName: String) {
        appInfo.appName = newAppName
        update(appInfo)
        Log.d("Tag", "${appInfo.appName} : Repo Order: ${appInfo.appOrder}")
    }

    @Transaction
    suspend fun updateAppHidden(appInfo: AppInfo, appHidden: Boolean) {
        appInfo.hidden = appHidden
        update(appInfo)
        Log.d("Tag", "${appInfo.appName} : Repo Order: ${appInfo.hidden}")
    }

    @Transaction
    suspend fun updateLockApp(appInfo: AppInfo, appLock: Boolean) {
        appInfo.lock = appLock
        update(appInfo)
        Log.d("Tag", "${appInfo.appName} : Repo Order: ${appInfo.lock}")
    }

    @Query("SELECT MAX(`app_order`) FROM app")
    fun getMaxOrder(): Int

    @Query("SELECT * FROM app WHERE is_favorite = 1")
    fun getFavoriteAppInfo(): List<AppInfo>

    @Query("SELECT * FROM app WHERE package_name = :packageName")
    suspend fun getAppByPackageName(packageName: String): AppInfo?
}
