package com.gribanskij.predictor.data.source.local

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.DataSource
import com.gribanskij.predictor.data.source.local.entities.Stock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val stockDAO: StockDAO
) : DataSource {

    override suspend fun getStockData(
        stockName: String,
        sDate: String,
        eDate: String
    ): Result<List<Stock>> {
        return Result.Success(stockDAO.getStockData(stockName, sDate, eDate))
    }

    override fun observeStockData(
        stockName: String,
        sDate: String,
        eDate: String
    ): Flow<List<Stock>> =
        stockDAO.observeStockData(stockName, sDate, eDate)


    override suspend fun saveData(stock: List<Stock>) = withContext(Dispatchers.IO) {
        stock.forEach {
            stockDAO.saveStock(it)
        }
    }
}