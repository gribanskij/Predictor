package com.gribanskij.predictor.ui.stock

import androidx.lifecycle.*
import com.gribanskij.predictor.data.source.DefaultRepository
import com.gribanskij.predictor.data.source.PredictData
import com.gribanskij.predictor.data.source.local.entities.Stock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class StockViewModel @Inject constructor(
    private val rep: DefaultRepository
) : ViewModel() {


    private var mStockName: String? = null
    private val input = MutableLiveData<String>()


    val stockData: LiveData<List<Stock>> = Transformations.switchMap(input) {
        rep.checkNewData(it, Date())
        rep.observeStockData(it, Date()).asLiveData()
    }.distinctUntilChanged()

    private val _predictData = MutableLiveData<List<PredictData>>()

    val predictData: LiveData<List<PredictData>> = Transformations.switchMap(stockData) {

        viewModelScope.launch {
            val pData = mutableListOf<PredictData>()
            it.forEach { stock ->
                pData.add(PredictData(stock.tradeDate, stock.priceOpen))
            }
            _predictData.value = pData
        }
        _predictData
    }

    fun setStock(sName: String) {
        if (!sName.equals(mStockName, true)) {
            mStockName = sName
            input.value = mStockName!!
        }
    }
}