package com.gribanskij.predictor.ui.stock

import androidx.lifecycle.*
import com.gribanskij.predictor.Event
import com.gribanskij.predictor.data.source.DefaultRepository
import com.gribanskij.predictor.data.source.PredictData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject


@HiltViewModel
class StockViewModel @Inject constructor(
    private val rep: DefaultRepository
) : ViewModel() {

    private val input = MutableLiveData<String>()

    val updateStatus = input.switchMap { sName ->
        rep.observeUpdateStatus(sName, Date()).map {
            Event(it)
        }.asLiveData()
    }


    val stockData = input.switchMap { sName ->
        rep.observeStockData(sName, Date()).asLiveData()
    }


    private val _predictData = MutableLiveData<List<PredictData>>()

    val predictData: LiveData<List<PredictData>> = Transformations.switchMap(stockData) {

        /*
        viewModelScope.launch {
            val pData = mutableListOf<PredictData>()
            it.forEach { stock ->
                pData.add(PredictData(stock.tradeDate, stock.priceOpen))
            }
            _predictData.value = pData.subList(1, 3)
        }
         */
        _predictData
    }

    //задаем название акции.
    fun setStock(sName: String) {
        if (!input.value.equals(sName, true)) {
            input.value = sName
        }
    }
}
