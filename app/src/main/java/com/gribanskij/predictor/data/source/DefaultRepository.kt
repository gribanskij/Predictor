package com.gribanskij.predictor.data.source

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.local.entities.Stock
import com.gribanskij.predictor.di.ViewModelModule
import com.gribanskij.predictor.utils.dateFormatOnly
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import java.util.*
import javax.inject.Inject

//количество точек на вход модели
private const val INPUT_NUM_DAYS = 7

//завершения работы ММВБ
private const val MMVB_END_TIME = 21

@ViewModelScoped
class DefaultRepository @Inject constructor(
    @ViewModelModule.RemoteData
    private val remoteDataS: DataSource,
    @ViewModelModule.LocalData
    private val localDataS: DataSource,
    private val ioDispatcher: CoroutineDispatcher
) : Repository {

    override suspend fun getStockData(stockName: String, date: Date): Result<List<Stock>> {
        val interval = getDateInterval(INPUT_NUM_DAYS)
        val sDate = interval.last()
        val eDate = interval.first()

        return remoteDataS.getStockData(stockName, sDate, eDate)
    }

    override suspend fun getPredictData(stockName: String, inputData: List<Float>): Result<Float> {
        return Result.Success(0.0f)
    }

    private fun getDateInterval(dayNum: Int): List<String> {
        val calendar = Calendar.getInstance()
        calendar.time = Date()

        val carHour = calendar.get(Calendar.HOUR_OF_DAY)
        if (carHour < MMVB_END_TIME) calendar.add(Calendar.DAY_OF_MONTH, -1)

        val listDate = mutableListOf<String>()


        do {

            when (calendar.get(Calendar.DAY_OF_WEEK)) {

                Calendar.SUNDAY -> {
                    calendar.add(Calendar.DAY_OF_MONTH, -1)
                }
                Calendar.SATURDAY -> {
                    if (chekDayOff(dateFormatOnly(calendar.time)))
                        listDate.add(dateFormatOnly(calendar.time))
                    calendar.add(Calendar.DAY_OF_MONTH, -1)
                }

                else -> {
                    if (!chekWorkDay(dateFormatOnly(calendar.time)))
                        listDate.add(dateFormatOnly(calendar.time))
                    calendar.add(Calendar.DAY_OF_MONTH, -1)
                }

            }

        } while (listDate.size != dayNum)

        return listDate

    }

    //проверка является ли рабочий день выходным
    //true если является
    private fun chekWorkDay(workDay: String): Boolean {
        val listWorkDaysOff = listOf("2021-11-04", "2021-11-05", "2021-12-31")
        return listWorkDaysOff.contains(workDay)
    }

    //проверка является ли выходной день рабочим
    //true если является
    private fun chekDayOff(dayOff: String): Boolean {
        val listDaysOffWork = listOf<String>()
        return listDaysOffWork.contains(dayOff)
    }
}