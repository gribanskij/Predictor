package com.gribanskij.predictor.data.source.local

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.DataSource
import com.gribanskij.predictor.data.source.local.entities.StockNoID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val stockDAO: StockDAO
) : DataSource {

    override suspend fun getStockData(
        stockName: String,
        sDate: String,
        eDate: String
    ): Result<List<StockNoID>> {
        return Result.Success(listOf())
    }

    override fun observeStockData(
        stockName: String,
        sDate: String,
        eDate: String
    ): Flow<Result<List<StockNoID>>> =
        stockDAO.getStockBeforeDate(stockName, sDate, eDate).map {
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