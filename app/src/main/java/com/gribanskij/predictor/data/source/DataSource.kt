package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.local.entities.Stock
import kotlinx.coroutines.flow.Flow

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
    ): Flow<List<Stock>>

    suspend fun saveData(stock: List<Stock>)
    suspend fun getPredictStockData(stockName: String, inputData: List<Float>): Result<List<Float>>
    suspend fun getStockDataFromDB(stockName: String, sDate: String, eDate: String): List<Stock>
}