package com.gribanskij.predictor.data.source.local.entities

data class StockNoID(
    val name: String,
    val stockId: String,
    val tradeDate: String,
    val priceClose: Float,
    val priceOpen: Float,
    val priceLow: Float,
    val priceHigh: Float
)
