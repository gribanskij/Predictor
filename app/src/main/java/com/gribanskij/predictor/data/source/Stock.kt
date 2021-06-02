package com.gribanskij.predictor.data.source

data class Stock(
    val shockName: String,
    val stockID: String,
    val date: String,
    val close: Double
)