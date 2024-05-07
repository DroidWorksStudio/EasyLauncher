package com.github.droidworksstudio.launcher.data


import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.droidworksstudio.launcher.data.dao.AppInfoDAO
import com.github.droidworksstudio.launcher.data.entities.AppInfo

@Database(entities = [AppInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppInfoDAO
}