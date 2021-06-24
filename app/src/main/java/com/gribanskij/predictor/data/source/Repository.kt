package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.local.entities.Stock
import kotlinx.coroutines.flow.Flow
import java.util.*


interface Repository {

    suspend fun getStockData(stockName: String, date: Date): Result<List<Stock>>
    suspend fun getPredictData(stockName: String, inputData: List<Float>): Result<Float>
    fun observeStockData(stockName: String, date: Date): Flow<List<Stock>>
    fun checkNewData(stockName: String, date: Date)

}