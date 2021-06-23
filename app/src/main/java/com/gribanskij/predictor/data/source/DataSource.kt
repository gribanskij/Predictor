package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.local.entities.Stock
import kotlinx.coroutines.flow.Flow
import java.util.*

interface DataSource {
    suspend fun getStockData(
        stockName: String,
        sDate: String,
        eDate: String
    ): Result<List<Stock>>

    fun observeStockData(
        stockName: String,
        sDate: String,
        eDate: String
    ): Flow<Result<List<Stock>>>

    suspend fun saveData(stockName: String, date: Date)
    suspend fun getPredictStockData(stockName: String, inputData: List<Float>): Result<List<Float>>
}