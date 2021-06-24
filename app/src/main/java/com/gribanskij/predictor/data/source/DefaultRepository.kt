package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.DateMaker
import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.local.entities.Stock
import com.gribanskij.predictor.di.ViewModelModule
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

//количество точек на вход модели
const val INPUT_NUM_DAYS = 7

@ViewModelScoped
class DefaultRepository @Inject constructor(
    @ViewModelModule.RemoteData
    private val remoteDataS: DataSource,
    @ViewModelModule.LocalData
    private val localDataS: DataSource,
    private val ioDispatcher: CoroutineDispatcher
) : Repository {

    @Inject
    lateinit var dateMaker: DateMaker


    override suspend fun getStockData(stockName: String, date: Date): Result<List<Stock>> {
        return Result.Success(listOf())
    }

    override suspend fun getPredictData(stockName: String, inputData: List<Float>): Result<Float> {
        return Result.Success(0.0f)
    }

    override fun observeStockData(stockName: String, date: Date): Flow<List<Stock>> {
        val interval = dateMaker.getListDate(INPUT_NUM_DAYS, date)
        val sDate = interval.last()
        val eDate = interval.first()
        return localDataS.observeStockData(stockName, sDate, eDate)
    }

    override fun checkNewData(stockName: String, date: Date) {
        GlobalScope.launch {
            val interval = dateMaker.getListDate(INPUT_NUM_DAYS, date)
            val sDate = interval.last()
            val eDate = interval.first()
            val data = localDataS.getStockDataFromDB(stockName, sDate, eDate)

            if (data.size == INPUT_NUM_DAYS) return@launch

            when (val result = remoteDataS.getStockData(stockName, sDate, eDate)) {

                is Result.Success -> {
                    localDataS.saveData(result.data)
                }
                is Result.Error -> {
                }

                is Result.Loading -> {
                }
            }
        }
    }
}