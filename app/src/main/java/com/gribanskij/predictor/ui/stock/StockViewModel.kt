package com.gribanskij.predictor.ui.stock

import androidx.lifecycle.*
import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.DefaultRepository
import com.gribanskij.predictor.data.source.local.entities.Stock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class StockViewModel @Inject constructor(
    private val rep: DefaultRepository
) : ViewModel() {


    private val data = MutableLiveData<Result<List<Stock>>>()
    private var mStockName: String? = null
    private val input = MutableLiveData<String>()


    val stockData: LiveData<Result<List<Stock>>> = Transformations.switchMap(input) {
        viewModelScope.launch {
            data.value = rep.getStockData(it, Date())
        }
        data
    }

    fun setStock(sName: String) {
        if (!sName.equals(mStockName, true)) {
            mStockName = sName
            input.value = mStockName!!
        }
    }
}