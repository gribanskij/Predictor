package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.local.entities.StockNoID
import com.gribanskij.predictor.di.ViewModelModule
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import java.util.*
import javax.inject.Inject

@ViewModelScoped
class DefaultRepository @Inject constructor(
    @ViewModelModule.RemoteData
    private val remoteDataS: DataSource,
    @ViewModelModule.LocalData
    private val localDataS: DataSource,
    private val ioDispatcher: CoroutineDispatcher
) : Repository {

    override suspend fun getStockData(stockName: String, date: Date): Result<List<StockNoID>> {
        return remoteDataS.getStockData(stockName, "2021-06-01", "2021-06-19")
    }

    override suspend fun getPredictData(stockName: String, inputData: List<Float>): Result<Float> {
        return Result.Success(0.0f)
    }
}