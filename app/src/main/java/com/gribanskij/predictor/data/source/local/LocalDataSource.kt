package com.gribanskij.predictor.data.source.local

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.DataSource
import com.gribanskij.predictor.data.source.local.entities.StockNoID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class LocalDataSource internal constructor(
    private val stockDAO: StockDAO
) : DataSource {

    override suspend fun getStockData(stockName: String, date: Date): Result<List<StockNoID>> {
        return Result.Success(listOf())
    }

    override fun observeStockData(stockName: String, date: Date): Flow<Result<List<StockNoID>>> =
        stockDAO.getStockBeforeDate(stockName, date.toString()).map {
            Result.Success(it)
        }


    override suspend fun saveData(stockName: String, date: Date) {
    }

    override suspend fun getPredictStockData(
        stockName: String,
        inputData: List<Float>
    ): Result<List<Float>> {
        return Result.Success(listOf())
    }
}