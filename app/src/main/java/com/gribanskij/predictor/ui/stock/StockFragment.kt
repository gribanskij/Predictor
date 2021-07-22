package com.gribanskij.predictor.ui.stock

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.gribanskij.predictor.R
import com.gribanskij.predictor.data.source.local.entities.Stock
import com.gribanskij.predictor.databinding.FragmentStockBinding
import com.gribanskij.predictor.ui.dashboard.ARG_STOCK_NAME
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class StockFragment : Fragment(R.layout.fragment_stock) {
    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!
    private val model: StockViewModel by viewModels()

    private val stockDataSet = mutableListOf<Entry>()
    private val predictDataSet = mutableListOf<Entry>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            model.setStock(it.getString(ARG_STOCK_NAME, "?"))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStockBinding.bind(view)

        chartInit()

        model.stockData.observe(viewLifecycleOwner, {
            binding.text.text = it.toString()
            stockDataSet.clear()

            for ((i, element) in it.withIndex()) {
                val item = Entry(i.toFloat(), element.priceClose)
                stockDataSet.add(item)
            }

            val label = arguments?.getString(ARG_STOCK_NAME, "?")
            val lDataSet = LineDataSet(stockDataSet, label)
            lDataSet.color = R.color.black
            lDataSet.setCircleColor(R.color.black)

            val pDataSet = LineDataSet(predictDataSet, label)
            pDataSet.color = R.color.teal_200
            pDataSet.setCircleColor(R.color.teal_200)


            binding.historyChart.xAxis.valueFormatter = MyXAxisFormatter(it)
            binding.historyChart.data = LineData(lDataSet, pDataSet)
            binding.historyChart.invalidate()

        })

        model.predictData.observe(viewLifecycleOwner, {
            predictDataSet.clear()

            for ((i, element) in it.withIndex()) {
                val predictItem = Entry(i.toFloat(), element.value)
                predictDataSet.add(predictItem)
            }

            val label = arguments?.getString(ARG_STOCK_NAME, "?")
            val lDataSet = LineDataSet(stockDataSet, label)
            lDataSet.color = R.color.black
            lDataSet.setCircleColor(R.color.black)

            val pDataSet = LineDataSet(predictDataSet, label)
            pDataSet.color = R.color.teal_200
            pDataSet.setCircleColor(R.color.teal_200)

            binding.historyChart.data = LineData(lDataSet, pDataSet)
            binding.historyChart.invalidate()


        })
    }

    private fun chartInit() {

        binding.historyChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        //binding.historyChart.xAxis.labelRotationAngle = 45.0f
        binding.historyChart.axisRight.setDrawLabels(false)
        binding.historyChart.axisLeft.setDrawLabels(false)

    }

    class MyXAxisFormatter(private val stockList: List<Stock>) : ValueFormatter() {
        private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        private val nDateFormatter = SimpleDateFormat("EEE, d", Locale.getDefault())

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val dateString = stockList[value.toInt()].tradeDate
            val date = dateFormatter.parse(dateString)
            return nDateFormatter.format(date!!)
        }
    }
}