package com.gribanskij.predictor.ui.stock

import androidx.lifecycle.*
import com.gribanskij.predictor.Event
import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.DefaultRepository
import com.gribanskij.predictor.data.source.SimpleStock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject


@HiltViewModel
class StockViewModel @Inject constructor(
    private val rep: DefaultRepository
) : ViewModel() {

    private val input = MutableLiveData<StockData>()


    //сообщения
    val updateStatus = input.distinctUntilChanged().switchMap { stock ->
        rep.observeUpdateStatus(stock.name, stock.date).map {
            Event(it)
        }.asLiveData()
    }

    //данные по торгам с Мос.Биржи
    val stockData = input.distinctUntilChanged().switchMap { stock ->
        rep.observeStockData(stock.name, stock.date).asLiveData()
    }

    //данные предсказанные ML
    val predictData: LiveData<Result<List<SimpleStock>>> =
        input.distinctUntilChanged().switchMap { stock ->
            rep.observePredictData(stock.name, stock.date).asLiveData()
        }

    //задаем название акции.
    fun setStock(stock: StockData) {
        input.value = stock
    }

    data class StockData(
        val name: String,
        val date: Date
    )
}
