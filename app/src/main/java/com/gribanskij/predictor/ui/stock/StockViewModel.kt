package com.gribanskij.predictor.ui.stock

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.gribanskij.predictor.Event
import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.DefaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
class StockViewModel @Inject constructor(
    private val rep: DefaultRepository
) : ViewModel() {

    private val input = MutableLiveData<Pair<String, Long>>()

    private var inputDate: Long? = null


    //сообщения
    val updateStatus = input.switchMap { stock ->
        rep.observeUpdateStatus(stock.first, stock.second).map {
            Event(it)
        }.asLiveData()
    }

    //данные по торгам с Мос.Биржи
    val historyStockData = input.switchMap { stock ->
        rep.observeStockData(stock.first, stock.second).map { r ->
            when (r) {

                is Result.Success -> {
                    Result.Success(r.data.map { s ->
                        Pair(s.tradeDate, s.priceClose)
                    }
                    )
                }
                is Result.Error -> {
                    r
                }
                is Result.Loading -> {
                    r
                }
            }
        }.asLiveData()
    }

    //данные предсказанные ML
    val predictStockData = input.switchMap { stock ->
        rep.observePredictData(stock.first, stock.second).map { r ->
            when (r) {

                is Result.Success -> {
                    Result.Success(r.data.map { s ->
                        Pair(s.tradeDate, s.value)
                    }
                    )
                }
                is Result.Error -> {
                    r
                }
                is Result.Loading -> {
                    r
                }
            }
        }.asLiveData()
    }

    //date - дата без часов, минут, секунд
    fun setStock(stockName: String, date: Long) {
        if (inputDate != date) {
            inputDate = date
            input.value = Pair(stockName, date)
        }
    }
}
