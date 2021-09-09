package com.gribanskij.predictor.ui.stock

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.gribanskij.predictor.R
import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.SimpleStock
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

    private val historyDataSet = mutableListOf<Stock>()
    private val predictDataSet = mutableListOf<SimpleStock>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            val stock = StockViewModel.StockData(
                name = it.getString(ARG_STOCK_NAME, "?"),
                date = Date()
            )
            model.setStock(stock)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStockBinding.bind(view)

        chartInit()


        model.updateStatus.observe(viewLifecycleOwner, {

            val event = it.getContentIfNotHandled()

            event?.let { e ->
                when (e) {

                    is Result.Success -> {
                        Toast.makeText(requireContext(), "Данные обновлены", Toast.LENGTH_LONG)
                            .show()
                    }
                    is Result.Loading -> {
                        Toast.makeText(
                            requireContext(),
                            "Загрузка актуальных данных",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                    is Result.Error -> {
                        //binding.progress.visibility = View.INVISIBLE
                        Toast.makeText(
                            requireContext(),
                            e.exception.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })

        model.stockData.observe(viewLifecycleOwner, {

            when (it) {
                is Result.Success -> {
                    historyDataSet.clear()
                    historyDataSet.addAll(it.data)
                    if (binding.dataToggle.isChecked) {
                        binding.chartProgress.visibility = View.INVISIBLE
                        showHistoryDataChart(historyDataSet)
                    }
                }
                is Result.Error -> {
                    binding.chartProgress.visibility = View.INVISIBLE
                }
                is Result.Loading -> {
                    binding.chartProgress.visibility = View.VISIBLE
                }
            }
        })

        model.predictData.observe(viewLifecycleOwner, {

            when (it) {

                is Result.Success -> {
                    predictDataSet.clear()
                    predictDataSet.addAll(it.data)
                    if (!binding.dataToggle.isChecked) {
                        binding.chartProgress.visibility = View.INVISIBLE
                        showPredictDataChart(predictDataSet)
                    }
                }
                is Result.Error -> {
                    binding.chartProgress.visibility = View.INVISIBLE
                }
                is Result.Loading -> {
                    binding.chartProgress.visibility = View.VISIBLE
                }
            }

        })

        binding.dataToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showHistoryDataChart(historyDataSet)
            } else {
                showPredictDataChart(predictDataSet)
            }
        }
    }


    private fun showHistoryDataChart(data: List<Stock>) {


        val dataSet = mutableListOf<Entry>()

        for ((i, element) in data.withIndex()) {
            val item = Entry(i.toFloat(), element.priceClose)
            dataSet.add(item)
        }

        val label = arguments?.getString(ARG_STOCK_NAME, "?")
        val lDataSet = LineDataSet(dataSet, label)
        lDataSet.color = R.color.black
        lDataSet.setCircleColor(R.color.black)

        binding.dataChart.xAxis.valueFormatter = StockXAxisFormatter(data)
        binding.dataChart.data = LineData(lDataSet)
        binding.dataChart.invalidate()

    }

    private fun showPredictDataChart(data: List<SimpleStock>) {


        val dataSet = mutableListOf<Entry>()

        for ((i, element) in data.withIndex()) {
            val item = Entry(i.toFloat(), element.value)
            dataSet.add(item)
        }

        val label = arguments?.getString(ARG_STOCK_NAME, "?")
        val lDataSet = LineDataSet(dataSet, label)

        lDataSet.color = R.color.teal_200
        lDataSet.setCircleColor(R.color.teal_200)

        binding.dataChart.xAxis.valueFormatter = PredictXAxisFormatter(data)
        binding.dataChart.data = LineData(lDataSet)
        binding.dataChart.invalidate()

    }

    private fun chartInit() {

        binding.dataChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        //binding.historyChart.xAxis.labelRotationAngle = 45.0f
        binding.dataChart.axisRight.setDrawLabels(false)
        binding.dataChart.axisLeft.setDrawLabels(false)

        //binding.modelChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        //binding.modelChart.axisRight.setDrawLabels(false)
        //binding.modelChart.axisLeft.setDrawLabels(false)

    }

    class StockXAxisFormatter(private val stockList: List<Stock>) : ValueFormatter() {
        private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        private val nDateFormatter = SimpleDateFormat("EEE, d", Locale.getDefault())

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val dateString = stockList[value.toInt()].tradeDate
            val date = dateFormatter.parse(dateString)
            return nDateFormatter.format(date!!)
        }
    }

    class PredictXAxisFormatter(private val stockList: List<SimpleStock>) : ValueFormatter() {
        private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        private val nDateFormatter = SimpleDateFormat("EEE, d", Locale.getDefault())

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val dateString = stockList[value.toInt()].tradeDate
            val date = dateFormatter.parse(dateString)
            return nDateFormatter.format(date!!)
        }
    }
}