package com.gribanskij.predictor.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gribanskij.predictor.data.source.local.entities.Stock

@Database(
    entities = [Stock::class],
    version = 1,
    exportSchema = false
)
abstract class StockDatabase : RoomDatabase() {

    abstract fun stockDao(): StockDAO


}