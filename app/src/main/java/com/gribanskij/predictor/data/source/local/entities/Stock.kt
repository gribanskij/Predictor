package com.gribanskij.predictor.data.source.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "STOCK")
data class Stock(
    @PrimaryKey val id: Long,
    val name: String,
    val stockId: String,
    val tradeDate: String,
    val priceClose: Float,
    val priceOpen: Float,
    val priceLow: Float,
    val priceHigh: Float,
    val sysDate: String
)
