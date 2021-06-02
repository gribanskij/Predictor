package com.gribanskij.predictor.data.source.remote

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.DataSource
import com.gribanskij.predictor.data.source.Stock
import org.json.JSONObject
import java.io.BufferedInputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


const val SBER_NAME = "SBER"
const val YAND_NAME = "YNDX"
const val GAZPROM_NAME = "GAZP"
const val LUKOIL_NAME = "LKOH"
const val ROSN_NAME = "ROSN"

const val INDEX_TRADEDATE = 1
const val INDEX_SHORTNAME = 2
const val INDEX_SECID = 3
const val INDEX_CLOSE = 11


private const val SBER_URL =
    "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/SBER.json?from="
private const val YAND_URL =
    "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/YNDX.json?from="
private const val GAZPROM_URL =
    "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/GAZP.json?from="
private const val LUKOIL_URL =
    "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/LKOH.json?from="
private const val ROSN_URL =
    "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/ROSN.json?from="


private const val JSON_HISTORY = "history"
private const val JSON_DATA = "data"


class RemoteDataSource @Inject constructor() : DataSource {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override suspend fun getData(stockName: String, date: Date): Result<List<Stock>> {


        val calendar = Calendar.getInstance().apply {
            time = date
            add(Calendar.DAY_OF_MONTH, -10)
        }

        val startDate = dateFormatter.format(calendar.time)
        val endDate = dateFormatter.format(date)


        var outResult: Result<List<Stock>> = Result.Loading
        val response = mutableListOf<Stock>()

        val out = StringBuilder()

        try {
            val baseUrl = when (stockName) {
                SBER_NAME -> SBER_URL
                YAND_NAME -> YAND_URL
                GAZPROM_NAME -> GAZPROM_URL
                LUKOIL_NAME -> LUKOIL_URL
                ROSN_NAME -> ROSN_URL
                else -> SBER_URL
            }

            val fullUrl = "$baseUrl$startDate&till%20=%$endDate"

            val mUrl = URL(fullUrl)
            val input = BufferedInputStream(mUrl.openStream())

            val s = Scanner(input).useDelimiter("\\n")
            while (s.hasNext()) {
                val raw = s.next()
                out.append(raw)
            }

            val jObject = JSONObject(out.toString())
            val jHistory = jObject.getJSONObject(JSON_HISTORY)
            val jData = jHistory.getJSONArray(JSON_DATA)
            val size = jData.length()




            for (i in 0 until size) {

                val tqbr = jData.getJSONArray(i)
                val close = tqbr.getDouble(INDEX_CLOSE)
                val sdate = tqbr.getString(INDEX_TRADEDATE)
                val id = tqbr.getString(INDEX_SECID)
                val name = tqbr.getString(INDEX_SHORTNAME)



                response.add(Stock(name, id, sdate, close))
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