package com.gribanskij.predictor.data.source.local

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.DataSource
import com.gribanskij.predictor.data.source.local.entities.Stock
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val stockDAO: StockDAO
) : DataSource {

    override suspend fun getStockData(
        stockName: String,
        sDate: String,
        eDate: String
    ): Result<List<Stock>> {
        return Result.Success(listOf())
    }

    override fun observeStockData(
        stockName: String,
        sDate: String,
        eDate: String
    ): Flow<List<Stock>> =
        stockDAO.subscribeStockData(stockName, sDate, eDate)


    override suspend fun saveData(stock: List<Stock>) {
        stock.forEach {
            stockDAO.saveStock(it)
        }
    }

    override suspend fun getPredictStockData(
        stockName: String,
        inputData: List<Float>
    ): Result<List<Float>> {
        return Result.Success(listOf())
    }

    override suspend fun getStockDataFromDB(
        stockName: String,
        sDate: String,
        eDate: String
    ): List<Stock> {
        return stockDAO.getStockInDB(stockName, sDate, eDate)
    }
}