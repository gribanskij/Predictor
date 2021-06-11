package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.Result
import kotlinx.coroutines.flow.Flow
import java.util.*

interface DataSource {
    suspend fun getStockData(stockName: String, date: Date): Result<List<StockNoID>>
    fun observeStockData(stockName: String, date: Date): Flow<Result<List<StockNoID>>>
    suspend fun saveData(stockName: String, date: Date)
    suspend fun getPredictStockData(stockName: String, inputData: List<Float>): Result<List<Float>>
}