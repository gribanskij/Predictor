package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.Result
import java.util.*

interface Repository {

    suspend fun getStockData(stockName: String, date: Date): Result<String>
    suspend fun getPredict(stockName: String, inputData: List<Float>): Result<Float>

}