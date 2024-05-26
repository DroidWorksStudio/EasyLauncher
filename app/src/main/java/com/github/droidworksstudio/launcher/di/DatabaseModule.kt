package com.github.droidworksstudio.launcher.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Room
import com.github.droidworksstudio.launcher.data.AppDatabase
import com.github.droidworksstudio.launcher.data.dao.AppInfoDAO
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.BottomDialogHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideLocalDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()

    @Provides
    @Singleton
    fun provideAppDao(appDatabase: AppDatabase): AppInfoDAO = appDatabase.appDao()

    @Provides
    fun providePackageManager(@ApplicationContext context: Context): PackageManager {
        return context.packageManager
    }

    @Provides
    @Singleton
    fun providePreferenceHelper(@ApplicationContext context: Context): PreferenceHelper {
        return PreferenceHelper(context)
    }

    @Provides
    fun provideAppHelper(): AppHelper {
        return AppHelper()
    }

    @Provides
    fun provideBottomDialogHelper(): BottomDialogHelper {
        return BottomDialogHelper()
    }
}
