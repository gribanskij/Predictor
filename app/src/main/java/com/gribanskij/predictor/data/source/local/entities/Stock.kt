package com.gribanskij.predictor.data.source.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["stockId", "tradeDate"])
data class Stock(
    val name: String,
    val stockId: String,
    val tradeDate: String,
    val priceClose: Float,
    val priceOpen: Float,
    val priceLow: Float,
    val priceHigh: Float,
)
