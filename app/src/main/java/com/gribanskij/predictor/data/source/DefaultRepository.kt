package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.DateMaker
import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.local.entities.Stock
import com.gribanskij.predictor.di.ViewModelModule
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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


    override fun observePredictData(stockName: String, date: Date): Flow<Result<List<Stock>>> {
        return flow {
            val interval = dateMaker.getListDate(INPUT_NUM_DAYS, date)
            val sDate = interval.last()
            val eDate = interval.first()
            val res = localDataS.getStockData(stockName, sDate, eDate) as Result.Success

            if (res.data.size == INPUT_NUM_DAYS) {
                when (val result = remoteDataS.getStockData(stockName, sDate, eDate)) {

                    is Result.Success -> {
                        localDataS.saveData(result.data)
                        emit(result)
                    }
                    is Result.Error -> {
                        emit(result)
                    }

                    is Result.Loading -> {
                        emit(result)
                    }
                }
            }


        }
    }

    override fun observeStockData(stockName: String, date: Date): Flow<Result<List<Stock>>> {

        val interval = dateMaker.getListDate(INPUT_NUM_DAYS, date)
        val sDate = interval.last()
        val eDate = interval.first()

        return localDataS.observeStockData(stockName, sDate, eDate).distinctUntilChanged().map {
            if (it.size == INPUT_NUM_DAYS) Result.Success(it)
            else {
                Result.Loading
            }
        }
    }


    override fun observeUpdateStatus(stockName: String, date: Date): Flow<Result<List<Stock>>> {

        return flow {
            val interval = dateMaker.getListDate(INPUT_NUM_DAYS, date)
            val sDate = interval.last()
            val eDate = interval.first()
            val res = localDataS.getStockData(stockName, sDate, eDate) as Result.Success

            if (res.data.size != INPUT_NUM_DAYS) {
                when (val result = remoteDataS.getStockData(stockName, sDate, eDate)) {

                    is Result.Success -> {
                        localDataS.saveData(result.data)
                        emit(result)
                    }
                    is Result.Error -> {
                        emit(result)
                    }

                    is Result.Loading -> {
                        emit(result)
                    }
                }
            }
        }
    }
}