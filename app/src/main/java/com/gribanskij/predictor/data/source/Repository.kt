package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.local.entities.StockNoID
import java.util.*


interface Repository {

    suspend fun getStockData(stockName: String, date: Date): Result<List<StockNoID>>
    suspend fun getPredictData(stockName: String, inputData: List<Float>): Result<Float>

}