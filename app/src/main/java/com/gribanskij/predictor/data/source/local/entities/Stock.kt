package com.gribanskij.predictor.data.source.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "STOCK")
data class Stock(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val stockId: String,
    val tradeDate: String,
    val priceClose: Float,
    val priceOpen: Float,
    val priceLow: Float,
    val priceHigh: Float,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val sysDate: String
)
