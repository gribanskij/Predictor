package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.DateMaker
import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.local.entities.Stock
import com.gribanskij.predictor.data.source.ml.MlPredictor
import com.gribanskij.predictor.di.ViewModelModule
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
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

    @Inject
    lateinit var predictor: MlPredictor


    override fun observePredictData(
        stockName: String,
        date: Date
    ): Flow<Result<List<SimpleStock>>> {
        val interval = dateMaker.getListDate(INPUT_NUM_DAYS, date)
        val sDate = interval.last()
        val eDate = interval.first()
        predictor.init(stockName)

        return localDataS.observeStockData(stockName, sDate, eDate).distinctUntilChanged()
            .map { data ->


                if (data.size == INPUT_NUM_DAYS) {

                    val predictData = mutableListOf<SimpleStock>()
                    val input = mutableListOf<Float>()
                    input.addAll(data.map { it.priceClose })

                    data.forEach {
                        val res = predictor.doInference(input.toFloatArray())
                        input.removeFirst()
                        input.add(res)
                        predictData.add(SimpleStock(stockName, it.tradeDate, res))
                    }

                    Result.Success(predictData)
                } else {
                    Result.Loading
                }
            }.flowOn(ioDispatcher)

        /*
        return flow {
            val interval = dateMaker.getListDate(INPUT_NUM_DAYS, date)
            val sDate = interval.last()
            val eDate = interval.first()
            val res = localDataS.getStockData(stockName, sDate, eDate) as Result.Success


            if (res.data.size == INPUT_NUM_DAYS) {
                predictor.init(stockName)

                val input = res.data.map { it.priceClose }.toFloatArray()

                val res = predictor.doInference(input)

                val d = 1

                emit(res)
            }
        }

         */
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

                        /*
                        if(res.data.size == INPUT_NUM_DAYS){
                            localDataS.saveData(result.data)
                            emit(result)
                        } else {
                            emit(Result.Error(Exception("Данные не доступны")))
                        }

                         */

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