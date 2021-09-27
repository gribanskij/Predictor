package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.DateMaker
import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.local.entities.Stock
import com.gribanskij.predictor.data.source.ml.MlPredictor
import com.gribanskij.predictor.di.ViewModelModule
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
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
        date: Long
    ): Flow<Result<List<SimpleStock>>> {
        val lastWorkDates = dateMaker.getPrevWorkDate(INPUT_NUM_DAYS, date)
        val sDate = lastWorkDates.last()
        val eDate = lastWorkDates.first()
        val numDates = lastWorkDates.size

        predictor.init(stockName)

        return localDataS.observeStockData(stockName, sDate, eDate).distinctUntilChanged()
            .map { data ->

                //начинаем расчет только когда достаточно входных данных
                if (data.size == numDates) {

                    val input = mutableListOf<Float>()
                    input.addAll(data.map { it.priceClose })

                    val futureWorkDates = dateMaker.getFutureWorkDate(INPUT_NUM_DAYS, date)
                    val predictData = mutableListOf<SimpleStock>()

                    futureWorkDates.forEach {
                        val res = predictor.doInference(input.toFloatArray())
                        input.removeFirst()
                        input.add(res)
                        predictData.add(SimpleStock(stockName, it, res))
                    }

                    Result.Success(predictData)
                } else {
                    Result.Loading
                }
            }.flowOn(ioDispatcher)

    }

    override fun observeStockData(stockName: String, date: Long): Flow<Result<List<Stock>>> {

        val lastWorkDates = dateMaker.getPrevWorkDate(INPUT_NUM_DAYS, date)
        val sDate = lastWorkDates.last()
        val eDate = lastWorkDates.first()
        val numDates = lastWorkDates.size

        return localDataS.observeStockData(stockName, sDate, eDate).distinctUntilChanged().map {
            if (it.size == numDates) Result.Success(it)
            else {
                Result.Loading
            }
        }
    }


    override fun observeUpdateStatus(stockName: String, date: Long): Flow<Result<List<Stock>>> {

        return flow {
            val lastWorkDates = dateMaker.getPrevWorkDate(INPUT_NUM_DAYS, date)
            val sDate = lastWorkDates.last()
            val eDate = lastWorkDates.first()
            val numDates = lastWorkDates.size

            when (val localData = localDataS.getStockData(stockName, sDate, eDate)) {

                is Result.Success -> {
                    if (localData.data.size != numDates) {
                        val status = loadUpdate(stockName, sDate, eDate, numDates)
                        emit(status)
                    } else {
                        emit(localData)
                    }
                }

                is Result.Error -> {
                    emit(localData)
                }

                else -> {
                    emit(Result.Error(Exception("Ошибка загрузки данных!")))
                }
            }
        }
    }

    private suspend fun loadUpdate(
        stockName: String,
        startDate: String,
        stopDate: String,
        needDataSize: Int
    ): Result<List<Stock>> {


        return when (val resultFromWeb = remoteDataS.getStockData(stockName, startDate, stopDate)) {

            is Result.Success -> {

                if (resultFromWeb.data.size == needDataSize) {
                    localDataS.saveData(resultFromWeb.data)
                    Result.Success(listOf())
                } else {
                    Result.Error(Exception("Данные не доступны в данный момент"))
                }
            }
            is Result.Error -> {
                resultFromWeb
            }

            else -> {
                Result.Error(Exception("Не допустимый статус!"))
            }
        }
    }
}