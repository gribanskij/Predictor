package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.local.entities.Stock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
    ): Flow<List<Stock>> {
        return flow {
            emit(listOf())
        }
    }

    suspend fun saveData(stock: List<Stock>) = Unit
}