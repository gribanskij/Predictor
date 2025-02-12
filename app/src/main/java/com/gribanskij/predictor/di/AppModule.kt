package com.gribanskij.predictor.di

import android.content.Context
import androidx.room.Room
import com.gribanskij.predictor.data.StockModel
import com.gribanskij.predictor.data.source.local.StockDAO
import com.gribanskij.predictor.data.source.local.StockDatabase
import com.gribanskij.predictor.data.source.ml.MlPredictor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO

    @Singleton
    @Provides
    fun provideStock():List<StockModel> {
        return listOf(StockModel(
            NAME = "SBER",
            URL = "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/SBER.json?",
            MODEL_INPUT = 6,
            MODEL_NAME = "sber.tflite",
            MODEL_MAX_VALUE = 387.6f,
            MODEL_MIN_VALUE = 53.5f
        ), StockModel(
            NAME = "GAZP",
            URL = "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/GAZP.json?",
            MODEL_INPUT = 6,
            MODEL_NAME = "gazp.tflite",
            MODEL_MAX_VALUE = 389.82f,
            MODEL_MIN_VALUE = 115.35f
        ), StockModel(
            NAME = "LKOH",
            URL = "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/LKOH.json?",
            MODEL_INPUT = 6,
            MODEL_NAME = "lkoh.tflite",
            MODEL_MAX_VALUE = 7441.5f,
            MODEL_MIN_VALUE = 1790.4f
        ), StockModel(
            NAME = "ROSN",
            URL = "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/ROSN.json?",
            MODEL_INPUT = 6,
            MODEL_NAME = "rosn.tflite",
            MODEL_MAX_VALUE = 655.25f,
            MODEL_MIN_VALUE = 193.2f
        ))
    }

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

    @Provides
    fun getPredictor(
        @ApplicationContext appContext: Context,
        dispatcher: CoroutineDispatcher
    ): MlPredictor {
        return MlPredictor(appContext, dispatcher)
    }

/*
    StockModel(
    NAME = "YNDX",
    URL = "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/YNDX.json?",
    MODEL_INPUT = 6,
    MODEL_NAME = "yand.tflite",
    MODEL_MAX_VALUE = 5973f,
    MODEL_MIN_VALUE = 694f

 */

}