package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.local.entities.Stock
import kotlinx.coroutines.flow.Flow
import java.util.*


interface Repository {

    fun observePredictData(stockName: String, date: Date): Flow<Result<List<Stock>>>
    fun observeStockData(stockName: String, date: Date): Flow<Result<List<Stock>>>
    fun observeUpdateStatus(stockName: String, date: Date): Flow<Result<List<Stock>>>

}