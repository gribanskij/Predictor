package com.gribanskij.predictor.ui.stock

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.gribanskij.predictor.R
import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.databinding.FragmentStockBinding
import com.gribanskij.predictor.ui.dashboard.ARG_STOCK_NAME
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StockFragment:Fragment(R.layout.fragment_stock) {
    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!
    private val model: StockViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            model.setStock(it.getString(ARG_STOCK_NAME, "?"))
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStockBinding.bind(view)

        model.stockData.observe(viewLifecycleOwner, {
            binding.text.text = it.toString()


            when (it) {
                is Result.Success -> {

                    val dataSet = mutableListOf<Entry>()
                    val label = arguments?.getString(ARG_STOCK_NAME, "?")


                    for ((i, element) in it.data.withIndex()) {
                        val item = Entry(i.toFloat(), element.close.toFloat())
                        dataSet.add(item)
                    }

                    val lDataSet = LineDataSet(dataSet, label)

                    binding.historyChart.data = LineData(lDataSet)
                    binding.historyChart.invalidate()


                }
                is Result.Error -> {
                    binding.text.text = it.toString()
                }
                else -> {
                }
            }

        })
    }
}