package com.gribanskij.predictor.ui.stock

import androidx.lifecycle.*
import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.DefaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

const val STOCK_NAME_SAVED_STATE_KEY = "STOCK_NAME_SAVED_STATE_KEY"

@HiltViewModel
class StockViewModel @Inject constructor(
    private val rep: DefaultRepository
) : ViewModel() {


    private val _user = MutableLiveData<Result<List<String>>>()


    private var mStockName: String? = null
    private val input = MutableLiveData<String>()


    val stockData: LiveData<Result<List<String>>> = Transformations.switchMap(input) {
        viewModelScope.launch {
            _user.value = rep.getStockData(it, Date())
        }
        _user
    }

    fun setStock(sName: String) {
        if (!sName.equals(mStockName, true)) {
            mStockName = sName
            input.value = mStockName!!
        }
    }
}