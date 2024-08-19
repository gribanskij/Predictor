package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.DateMaker
import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.StockModel
import com.gribanskij.predictor.data.source.local.LocalDataSource
import com.gribanskij.predictor.data.source.local.entities.Stock
import com.gribanskij.predictor.data.source.ml.MlPredictor
import com.gribanskij.predictor.data.source.remote.RemoteDataSource
import com.gribanskij.predictor.di.ViewModelModule
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@ViewModelScoped
class DefaultRepository @Inject constructor(
    private val remoteDataS: RemoteDataSource,
    private val localDataS: LocalDataSource,
    private val ioDispatcher: CoroutineDispatcher
) : Repository {

    @Inject
    lateinit var dateMaker: DateMaker

    @Inject
    lateinit var predictor: MlPredictor


    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)


    private val stDate = Calendar.getInstance().run {
        time = Date()
        add(Calendar.MONTH, -1)
        time
    }

    private val yesterday = Calendar.getInstance().run {
        time = Date()
        set(Calendar.HOUR,0)
        set (Calendar.MINUTE,0)
        add(Calendar.DAY_OF_MONTH, -1)
        time.time
    }

    //предсказанные данные
    override fun observePredictData(
        stock: StockModel
    ): Flow<Result<List<PredictData>>> {
        val sDate = formatter.format(stDate)
        val eDate = formatter.format(Date())

        predictor.init(stock)

        return localDataS.observeStockData(stock, sDate, eDate).distinctUntilChanged()
            .map { data ->

                //начинаем расчет только когда достаточно входных данных
                if (data.size >= stock.MODEL_INPUT) {
                    val firstIndex = data.size - stock.MODEL_INPUT
                    val lastData = data.subList(firstIndex, data.size)

                    val input = mutableListOf<Float>()
                    input.addAll(lastData.map { it.priceClose })


                    val date = formatter.parse(lastData.last().tradeDate)

                    val futureWorkDates = dateMaker.getFutureWorkDate(stock.MODEL_INPUT, date.time)
                    val predictData = mutableListOf<PredictData>()

                    futureWorkDates.forEach {
                        val res = predictor.doInference(input.toFloatArray())
                        input.removeFirst()
                        input.add(res)
                        predictData.add(PredictData(stock.NAME, it, res))
                    }

                    Result.Success(predictData)
                } else {
                    Result.Loading
                }
            }.flowOn(ioDispatcher)

    }

    //исторические данные
    override fun observeHistoryData(stock: StockModel): Flow<List<Stock>> {
        val sDate = formatter.format(stDate)
        val eDate = formatter.format(Date())
        return localDataS.observeStockData(stock, sDate, eDate)
    }


    override fun observeUpdateStatus(stock: StockModel): Flow<List<Stock>> = flow {
        val sDate = formatter.format(stDate)
        val eDate = formatter.format(Date())

        val localData = localDataS.getStockData(stock, sDate, eDate)


        val noElement = localData.size < stock.MODEL_INPUT
        val notFresh = if (localData.isEmpty()) true else {

            val lastD = localData.last()
            val lastTradeDate = formatter.parse(lastD.tradeDate)?.time ?: 0L
            lastTradeDate < yesterday
        }

        if (noElement || notFresh) {
            val status = loadUpdate(stock, sDate, eDate)
            emit(status)
        } else {
            emit(localData)
        }
    }

    private suspend fun loadUpdate(
        stock: StockModel,
        startDate: String,
        stopDate: String,
    ): List<Stock> {


        val resultFromWeb = remoteDataS.getStockData(stock.URL, startDate, stopDate)
        localDataS.saveData(resultFromWeb)
        return resultFromWeb

        /*


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

         */
    }


}