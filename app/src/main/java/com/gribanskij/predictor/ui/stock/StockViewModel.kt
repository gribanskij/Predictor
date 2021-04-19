package com.gribanskij.predictor.ui.stock

import androidx.lifecycle.*
import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StockViewModel @Inject constructor(
        private val savedStateHandle: SavedStateHandle,
        private val rep: Repository) : ViewModel() {

    private val stockName: String = savedStateHandle["stock"]
            ?: throw IllegalArgumentException("missing stockName")

    private val _user = MutableLiveData<Result<List<String>>>()
    val user: LiveData<Result<List<String>>> = _user

    init {
        viewModelScope.launch {
            _user.value = rep.getStockData(stockName, Date())
        }
    }
}