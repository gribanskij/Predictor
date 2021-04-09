package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.Result
import java.util.*

interface DataSource {

    suspend fun getData(stockName: String, date: Date): Result<String>
    suspend fun saveData(stockName: String, date: Date)
    suspend fun getPredict(stockName: String, inputData: List<Float>): Result<Float>
}