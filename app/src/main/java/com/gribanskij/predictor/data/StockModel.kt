package com.gribanskij.predictor.data

 data class StockModel(
    val NAME: String,
    val URL: String,
    val MODEL_INPUT: Int,
    val MODEL_NAME: String,
    val MODEL_MAX_VALUE: Float,
    val MODEL_MIN_VALUE: Float
)