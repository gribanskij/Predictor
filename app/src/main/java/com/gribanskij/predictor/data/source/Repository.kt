package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.StockModel
import com.gribanskij.predictor.data.source.local.entities.Stock
import kotlinx.coroutines.flow.Flow


interface Repository {

    fun observePredictData(stock: StockModel): Flow<Result<List<SimpleStock>>>
    fun observeStockData(stock: StockModel): Flow<Result<List<Stock>>>
    fun observeUpdateStatus(stock: StockModel): Flow<Result<List<Stock>>>

}