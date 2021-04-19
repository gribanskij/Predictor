package com.gribanskij.predictor.data.source.remote

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.DataSource
import java.io.BufferedInputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


const val SBER_NAME = "SBER"
const val YAND_NAME = "YNDX"
const val GAZPROM_NAME = "GAZP"


private const val SBER_URL = "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/SBER.cvs?from="
private const val YAND_URL = "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/YNDX.cvs?from="
private const val GAZPROM_URL = "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/GAZP.cvs?from="


class RemoteDataSource @Inject constructor() : DataSource {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override suspend fun getData(stockName: String, date: Date): Result<List<String>> {


        val calendar = Calendar.getInstance().apply {
            time = date
            add(Calendar.DAY_OF_MONTH, -10)
        }

        val startDate = dateFormatter.format(calendar.time)
        val endDate = dateFormatter.format(date)


        var outResult: Result<List<String>> = Result.Loading
        val response = mutableListOf<String>()

        try {
            val baseUrl = when (stockName) {
                SBER_NAME -> SBER_URL
                YAND_NAME -> YAND_URL
                GAZPROM_NAME -> GAZPROM_URL
                else -> SBER_URL
            }

            val fullUrl = "$baseUrl$startDate&till%20=%$endDate"

            val mUrl = URL(fullUrl)
            val input = BufferedInputStream(mUrl.openStream())
            val s = Scanner(input).useDelimiter("\\n")
            while (s.hasNext()) {
                val raw = s.next()
                response.add(raw)
            }
            outResult = Result.Success(response)
        } catch (ex: Exception) {
            outResult = Result.Error(ex)
        }

        return outResult
    }

    override suspend fun saveData(stockName: String, date: Date) {
    }

    override suspend fun getPredict(stockName: String, inputData: List<Float>): Result<Float> {
        return Result.Error(Exception("Not implemented"))
    }
}