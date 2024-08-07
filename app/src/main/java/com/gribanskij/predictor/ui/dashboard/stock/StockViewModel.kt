package com.gribanskij.predictor.ui.dashboard.stock

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.gribanskij.predictor.Event
import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.StockModel
import com.gribanskij.predictor.data.source.DefaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
class StockViewModel @Inject constructor(
    private val rep: DefaultRepository
) : ViewModel() {

    private val input = MutableLiveData<StockModel>()


    //сообщения
    val updateStatus = input.switchMap { stock ->
        rep.observeUpdateStatus(stock).map {
            Event(it)
        }.asLiveData()
    }

    //данные по торгам с Мос.Биржи
    val historyStockData = input.switchMap { stock ->
        rep.observeHistoryData(stock).map { r ->
            r.map { s -> Pair(s.tradeDate, s.priceClose)}
        }.asLiveData()
    }

    //данные предсказанные ML
    val predictStockData = input.switchMap { stock ->
        rep.observePredictData(stock).map { r ->
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

    fun setStock(stock: StockModel) {
        if (!input.isInitialized)input.value = stock
    }
}
