package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.StockModel
import com.gribanskij.predictor.data.source.local.entities.Stock
import kotlinx.coroutines.flow.Flow

interface DataSource {
    suspend fun getStockData(
        stock: StockModel,
        sDate: String,
        eDate: String
    ): List<Stock>

    fun observeStockData(
        stock: StockModel,
        sDate: String,
        eDate: String
    ): Flow<List<Stock>>

    suspend fun saveData(stock: List<Stock>) = Unit
}