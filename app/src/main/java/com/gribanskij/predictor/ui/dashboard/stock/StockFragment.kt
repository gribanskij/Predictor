package com.gribanskij.predictor.ui.dashboard.stock

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
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
import com.gribanskij.predictor.data.StockModel
import com.gribanskij.predictor.databinding.FragmentStockBinding
import com.gribanskij.predictor.ui.dashboard.ARG_STOCK_POSITION
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class StockFragment : Fragment(R.layout.fragment_stock) {
    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!
    private val model: StockViewModel by viewModels()

    @Inject
    lateinit var  stocks:List<StockModel>

    private val historyDataSet = mutableListOf<Pair<String, Float>>()
    private val predictDataSet = mutableListOf<Pair<String, Float>>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            model.setStock(
                stock = stocks[it.getInt(ARG_STOCK_POSITION)],
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStockBinding.bind(view)

        chartInit()


        model.updateStatus.observe(viewLifecycleOwner) {

            val event = it.getContentIfNotHandled()

            event?.let { e ->
                when (e) {

                    is Result.Success -> {
                        //Toast.makeText(requireContext(), "Данные обновлены", Toast.LENGTH_SHORT)
                        //    .show()
                    }
                    is Result.Loading -> {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.msg_load_data),
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    is Result.Error -> {
                        hideAllProgressAndTrend()
                        Toast.makeText(
                            requireContext(),
                            e.exception.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        //исторические данные
        model.historyStockData.observe(viewLifecycleOwner) {

            when (it) {
                is Result.Success -> {
                    historyDataSet.clear()
                    historyDataSet.addAll(it.data)
                    //последнее значение в списке - текущее число
                    historyDataSet.lastOrNull()?.let { price ->
                        showHistoryPrice(price)
                        showTrend()
                        updateStatus()
                    }

                    if (binding.dataToggle.isChecked) {
                        binding.chartProgress.visibility = View.INVISIBLE
                        showDataChart(historyDataSet)
                    }
                }
                is Result.Error -> {
                    binding.chartProgress.visibility = View.INVISIBLE
                }
                is Result.Loading -> {
                    binding.chartProgress.visibility = View.VISIBLE
                }
            }
        }

        //предсказанные данные
        model.predictStockData.observe(viewLifecycleOwner) {

            when (it) {

                is Result.Success -> {
                    predictDataSet.clear()
                    predictDataSet.addAll(it.data)
                    //
                    predictDataSet.firstOrNull()?.let { price ->
                        showPredictPrice(price)
                        showTrend()
                    }


                    if (!binding.dataToggle.isChecked) {
                        binding.chartProgress.visibility = View.INVISIBLE
                        showDataChart(predictDataSet)
                    }
                }
                is Result.Error -> {
                    binding.chartProgress.visibility = View.INVISIBLE
                }
                is Result.Loading -> {
                    binding.chartProgress.visibility = View.VISIBLE
                }
            }

        }

        binding.dataToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showDataChart(historyDataSet)
            } else {
                showDataChart(predictDataSet)
            }
        }
    }


    private fun hideAllProgressAndTrend() {
        binding.chartProgress.visibility = View.INVISIBLE
        binding.historyProgress.visibility = View.INVISIBLE
        binding.historyProgress.visibility = View.INVISIBLE
        binding.predictProgress.visibility = View.INVISIBLE
        binding.priceTrend.visibility = View.INVISIBLE

    }


    private fun showDataChart(data: List<Pair<String, Float>>) {
        val dataSet = mutableListOf<Entry>()
        for ((i, element) in data.withIndex()) {
            val item = Entry(i.toFloat(), element.second)
            dataSet.add(item)
        }
        val lDataSet = LineDataSet(dataSet, null)
        lDataSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.orange_300))
        lDataSet.fillColor = ContextCompat.getColor(requireContext(), R.color.orange_300)
        lDataSet.color = ContextCompat.getColor(requireContext(), R.color.orange_300)
        lDataSet.setDrawFilled(true)
        binding.dataChart.xAxis.valueFormatter = XAxisFormatter(data)
        binding.dataChart.data = LineData(lDataSet)
        binding.dataChart.invalidate()
    }

    private fun chartInit() {
        binding.dataChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.dataChart.axisRight.setDrawLabels(false)
        binding.dataChart.axisLeft.setDrawLabels(false)
        binding.dataChart.description = null
        binding.dataChart.setDrawBorders(false)
        binding.dataChart.setTouchEnabled(false)
        binding.dataChart.isDoubleTapToZoomEnabled = false
        binding.dataChart.setScaleEnabled(false)
        binding.dataChart.axisLeft.setDrawGridLines(false)
        binding.dataChart.axisLeft.setDrawAxisLine(false)
        binding.dataChart.axisRight.setDrawGridLines(false)
        binding.dataChart.axisRight.setDrawAxisLine(false)
        binding.dataChart.xAxis.setDrawGridLines(false)
        binding.dataChart.xAxis.setDrawAxisLine(false)
        binding.dataChart.xAxis.granularity = 1f
        binding.dataChart.legend.isEnabled = false

    }

    private fun showHistoryPrice(currentData: Pair<String, Float>) {
        binding.historyProgress.visibility = View.INVISIBLE
        val textPrice = formatPrice(currentData.second)
        binding.historyPrice.text = textPrice
        binding.closeDate.text = formatDatePrice(currentData.first)
    }

    private fun showPredictPrice(currentData: Pair<String, Float>) {
        binding.predictProgress.visibility = View.INVISIBLE
        val textPrice = formatPrice(currentData.second)
        binding.predictPrice.text = textPrice
        binding.predictDate.text = formatDatePrice(currentData.first)
    }

    private fun formatPrice(value: Float): String {
        val numFormatter = NumberFormat.getInstance().apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        return "${numFormatter.format(value)} \u20BD"
    }


    private fun formatDatePrice(date: String): String {
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormatter = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
        val tempDate = inputFormatter.parse(date)
        return outputFormatter.format(tempDate)

    }

    private fun showTrend() {

        when (isTrendUp()) {
            true -> {
                val img = AppCompatResources.getDrawable(
                    requireContext(), R.drawable.ic_trending_up_24
                )
                binding.priceTrend.setImageDrawable(img)
                binding.priceTrend.visibility = View.VISIBLE
            }
            false -> {
                val img = AppCompatResources.getDrawable(
                    requireContext(), R.drawable.ic_trending_down_24
                )
                binding.priceTrend.setImageDrawable(img)
                binding.priceTrend.visibility = View.VISIBLE
            }
            else -> {
                binding.priceTrend.visibility = View.INVISIBLE
            }
        }
    }

    private fun isTrendUp(): Boolean? {
        var result: Boolean? = null
        if (historyDataSet.lastOrNull() != null && predictDataSet.firstOrNull() != null) {
            result = (historyDataSet.last().second - predictDataSet.first().second) <= 0
        }
        return result
    }

    private fun updateStatus() {
        val nDateFormatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault())
        binding.dateUpdate.text = nDateFormatter.format(Date())

    }

    class XAxisFormatter(private val stockList: List<Pair<String, Float>>) : ValueFormatter() {
        private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        private val nDateFormatter = SimpleDateFormat("EEE, d", Locale.getDefault())

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val dateString = stockList[value.toInt()].first
            val date = dateFormatter.parse(dateString)
            return nDateFormatter.format(date!!)
        }
    }
}