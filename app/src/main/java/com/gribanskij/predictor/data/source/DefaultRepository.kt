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
import java.util.Calendar
import java.util.Date
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


    private val dateNow = Calendar.getInstance().apply {
        time = Date()
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    //предсказанные данные
    override fun observePredictData(
        stock: StockModel
    ): Flow<Result<List<SimpleStock>>> {
        val lastWorkDates = dateMaker.getPrevWorkDate(stock.MODEL_INPUT, dateNow.time.time)
        val sDate = lastWorkDates.last()
        val eDate = lastWorkDates.first()

        predictor.init(stock)

        return localDataS.observeStockData(stock, sDate, eDate).distinctUntilChanged()
            .map { data ->

                //начинаем расчет только когда достаточно входных данных
                if (data.size >= stock.MODEL_INPUT) {

                    val firstIndex = data.size - stock.MODEL_INPUT
                    val lastIndex = data.size
                    val lastData = data.subList(firstIndex, lastIndex)

                    val input = mutableListOf<Float>()
                    input.addAll(lastData.map { it.priceClose })

                    val futureWorkDates = dateMaker.getFutureWorkDate(stock.MODEL_INPUT, dateNow.time.time)
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

    //исторические данные
    override fun observeStockData(stock: StockModel): Flow<Result<List<Stock>>> {

        val lastWorkDates = dateMaker.getPrevWorkDate(stock.MODEL_INPUT, dateNow.time.time)
        val sDate = lastWorkDates.last()
        val eDate = lastWorkDates.first()

        return localDataS.observeStockData(stock, sDate, eDate).distinctUntilChanged().map {
            if (it.size >= stock.MODEL_INPUT) {
                val firstIndex = it.size - stock.MODEL_INPUT
                val lastIndex = it.size
                Result.Success(it.subList(firstIndex, lastIndex))
            } else {
                Result.Loading
            }
        }
    }


    override fun observeUpdateStatus(stock: StockModel): Flow<Result<List<Stock>>> {

        return flow {
            val lastWorkDates = dateMaker.getPrevWorkDate(stock.MODEL_INPUT, dateNow.time.time)
            val sDate = lastWorkDates.last()
            val eDate = lastWorkDates.first()
            val numDates = lastWorkDates.size

            when (val localData = localDataS.getStockData(stock, sDate, eDate)) {

                is Result.Success -> {
                    if (localData.data.size < stock.MODEL_INPUT) {
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

                if (resultFromWeb.data.size >= stock.MODEL_INPUT) {
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