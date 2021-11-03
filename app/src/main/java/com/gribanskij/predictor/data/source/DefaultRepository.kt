package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.DateMaker
import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.StockModel
import com.gribanskij.predictor.data.source.local.entities.Stock
import com.gribanskij.predictor.data.source.ml.MlPredictor
import com.gribanskij.predictor.di.ViewModelModule
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject


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
        stock: StockModel,
        date: Long
    ): Flow<Result<List<SimpleStock>>> {
        val lastWorkDates = dateMaker.getPrevWorkDate(stock.MODEL_INPUT, date)
        val sDate = lastWorkDates.last()
        val eDate = lastWorkDates.first()
        val numDates = lastWorkDates.size

        predictor.init(stock)

        return localDataS.observeStockData(stock, sDate, eDate).distinctUntilChanged()
            .map { data ->

                //начинаем расчет только когда достаточно входных данных
                if (data.size == numDates) {

                    val input = mutableListOf<Float>()
                    input.addAll(data.map { it.priceClose })

                    val futureWorkDates = dateMaker.getFutureWorkDate(stock.MODEL_INPUT, date)
                    val predictData = mutableListOf<SimpleStock>()

                    futureWorkDates.forEach {
                        val res = predictor.doInference(input.toFloatArray())
                        input.removeFirst()
                        input.add(res)
                        predictData.add(SimpleStock(stock.NAME, it, res))
                    }

                    Result.Success(predictData)
                } else {
                    Result.Loading
                }
            }.flowOn(ioDispatcher)

    }

    override fun observeStockData(stock: StockModel, date: Long): Flow<Result<List<Stock>>> {

        val lastWorkDates = dateMaker.getPrevWorkDate(stock.MODEL_INPUT, date)
        val sDate = lastWorkDates.last()
        val eDate = lastWorkDates.first()
        val numDates = lastWorkDates.size

        return localDataS.observeStockData(stock, sDate, eDate).distinctUntilChanged().map {
            if (it.size == numDates) Result.Success(it)
            else {
                Result.Loading
            }
        }
    }


    override fun observeUpdateStatus(stock: StockModel, date: Long): Flow<Result<List<Stock>>> {

        return flow {
            val lastWorkDates = dateMaker.getPrevWorkDate(stock.MODEL_INPUT, date)
            val sDate = lastWorkDates.last()
            val eDate = lastWorkDates.first()
            val numDates = lastWorkDates.size

            when (val localData = localDataS.getStockData(stock, sDate, eDate)) {

                is Result.Success -> {
                    if (localData.data.size != numDates) {
                        val status = loadUpdate(stock, sDate, eDate, numDates)
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
        stock: StockModel,
        startDate: String,
        stopDate: String,
        needDataSize: Int
    ): Result<List<Stock>> {


        return when (val resultFromWeb = remoteDataS.getStockData(stock, startDate, stopDate)) {

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