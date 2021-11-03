package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.StockModel
import com.gribanskij.predictor.data.source.local.entities.Stock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface DataSource {
    suspend fun getStockData(
        stock: StockModel,
        sDate: String,
        eDate: String
    ): Result<List<Stock>>

    fun observeStockData(
        stock: StockModel,
        sDate: String,
        eDate: String
    ): Flow<List<Stock>> {
        return flow {
            emit(listOf())
        }
    }

    suspend fun saveData(stock: List<Stock>) = Unit
}