package com.gribanskij.predictor.data.source

data class Stock(
    val stockName: String,
    val stockID: String,
    val date: String,
    val close: Double
)