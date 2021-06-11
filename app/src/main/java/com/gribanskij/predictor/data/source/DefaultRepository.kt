package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.local.entities.StockNoID
import com.gribanskij.predictor.data.source.remote.RemoteDataSource
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@ViewModelScoped
class DefaultRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher
) : Repository {

    override suspend fun getStockData(stockName: String, date: Date): Result<List<StockNoID>> {
        return withContext(ioDispatcher) {
            remoteDataSource.getStockData(stockName, date)
        }
    }

    override suspend fun getPredictData(stockName: String, inputData: List<Float>): Result<Float> {
        return withContext(ioDispatcher) {
            Result.Success(0.0f)
        }
    }
}