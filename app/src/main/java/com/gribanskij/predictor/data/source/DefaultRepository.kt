package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.Result
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@ViewModelScoped
class DefaultRepository @Inject constructor(
        private val remoteDataSource: DataSource,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : Repository {

    override suspend fun getStockData(stockName: String, date: Date): Result<List<String>> {
        return withContext(ioDispatcher) {
            remoteDataSource.getData(stockName, date)
        }
    }

    override suspend fun getPredict(stockName: String, inputData: List<Float>): Result<Float> {
        return withContext(ioDispatcher) {
            Result.Success(0.0f)
        }
    }
}