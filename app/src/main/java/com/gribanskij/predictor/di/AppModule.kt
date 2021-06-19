package com.gribanskij.predictor.di

import android.content.Context
import androidx.room.Room
import com.gribanskij.predictor.data.source.local.StockDAO
import com.gribanskij.predictor.data.source.local.StockDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): StockDatabase {
        return Room.databaseBuilder(
            appContext,
            StockDatabase::class.java,
            "stock.db"
        ).build()
    }

    @Provides
    fun provideLogDao(database: StockDatabase): StockDAO {
        return database.stockDao()
    }

}