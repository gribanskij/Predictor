package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.local.entities.Stock
import kotlinx.coroutines.flow.Flow


interface Repository {

    fun observePredictData(stockName: String, date: Long): Flow<Result<List<SimpleStock>>>
    fun observeStockData(stockName: String, date: Long): Flow<Result<List<Stock>>>
    fun observeUpdateStatus(stockName: String, date: Long): Flow<Result<List<Stock>>>

}