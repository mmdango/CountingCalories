package com.michaeldang.countingcalories.feat.entries

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.michaeldang.countingcalories.database.CaloriesDao
import com.michaeldang.countingcalories.database.CaloriesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class CaloriesEntriesModule {
    @Provides
    @Singleton
    fun provideCaloriesDao(@ApplicationContext context: Context): CaloriesDao {
        return Room.databaseBuilder(
            context = context,
            klass = CaloriesDatabase::class.java,
            name = "database-1"
        )
            .build()
            .caloriesDao()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("total_calories", Context.MODE_PRIVATE)
    }
}