package com.gribanskij.predictor.data.source.local

import com.gribanskij.predictor.data.StockModel
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
        stock: StockModel,
        sDate: String,
        eDate: String
    ): List<Stock> =
        stockDAO.getStockData(stock.NAME, sDate, eDate)


    override fun observeStockData(
        stock: StockModel,
        sDate: String,
        eDate: String
    ): Flow<List<Stock>> =
        stockDAO.observeStockData(stock.NAME, sDate, eDate)


    override suspend fun saveData(stock: List<Stock>) = withContext(Dispatchers.IO) {
        stock.forEach {
            stockDAO.saveStock(it)
        }
    }
}