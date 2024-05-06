package com.github.droidworksstudio.launcher.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.github.droidworksstudio.launcher.data.AppDatabase
import com.github.droidworksstudio.launcher.data.dao.AppInfoDAO
import com.github.droidworksstudio.launcher.helper.AppHelper
import com.github.droidworksstudio.launcher.helper.FingerprintHelper
import com.github.droidworksstudio.launcher.helper.PreferenceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object DatabaseModule {
    @Provides
    @ViewModelScoped
    fun provideLocalDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration()
            .build()

    @Provides
    @ViewModelScoped
    fun provideAppDao(appDatabase: AppDatabase): AppInfoDAO = appDatabase.appDao()

    @Provides
    @ViewModelScoped
    fun providePackageManager(@ApplicationContext context: Context): PackageManager {
        return context.packageManager
    }

    @Provides
    @ViewModelScoped
    fun providePreferenceHelper(@ApplicationContext context: Context): PreferenceHelper {
        return PreferenceHelper(context)
    }

    @Provides
    @ViewModelScoped
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @ViewModelScoped
    fun providerFingerPrint(fragment: Fragment): FingerprintHelper{
        return FingerprintHelper(fragment)
    }

    @Provides
    @ViewModelScoped
    fun providerUtils(): AppHelper{
        return AppHelper()
    }
}