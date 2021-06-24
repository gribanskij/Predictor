package com.gribanskij.predictor.data.source.remote

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.DataSource
import com.gribanskij.predictor.data.source.local.entities.Stock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedInputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


private const val SBER_NAME = "SBER"
private const val YAND_NAME = "YNDX"
private const val GAZPROM_NAME = "GAZP"
private const val LUKOIL_NAME = "LKOH"
private const val ROSN_NAME = "ROSN"

private const val INDEX_TRADEDATE = 1
private const val INDEX_SHORTNAME = 2
private const val INDEX_SECID = 3
private const val INDEX_CLOSE = 11
private const val INDEX_LOW = 7
private const val INDEX_HIGH = 8
private const val INDEX_OPEN = 6


private const val SBER_URL =
    "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/SBER.json?"
private const val YAND_URL =
    "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/YNDX.json?"
private const val GAZPROM_URL =
    "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/GAZP.json?"
private const val LUKOIL_URL =
    "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/LKOH.json?"
private const val ROSN_URL =
    "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/ROSN.json?"


private const val JSON_HISTORY = "history"
private const val JSON_DATA = "data"


class RemoteDataSource @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher
) : DataSource {

    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    private val idFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    override suspend fun getStockData(
        stockName: String,
        sDate: String,
        eDate: String
    ): Result<List<Stock>> =
        withContext(ioDispatcher) {


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

                val fullUrl = "${baseUrl}from=${sDate}&till=$eDate"

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
                val currentDateTime = formatter.format(Date())


                for (i in 0 until size) {

                    val tqbr = jData.getJSONArray(i)
                    val high = tqbr.getDouble(INDEX_HIGH)
                    val low = tqbr.getDouble(INDEX_LOW)
                    val open = tqbr.getDouble(INDEX_OPEN)
                    val close = tqbr.getDouble(INDEX_CLOSE)
                    val tdate = tqbr.getString(INDEX_TRADEDATE)
                    val id = tqbr.getString(INDEX_SECID)
                    val name = tqbr.getString(INDEX_SHORTNAME)

                    val sqlId = (idFormatter.parse(tdate)?.time ?: 0) + getStockCode(stockName)


                    response.add(
                        Stock(
                            id = sqlId,
                            name = name,
                            stockId = id,
                            tradeDate = tdate,
                            priceClose = close.toFloat(),
                            priceHigh = high.toFloat(),
                            priceLow = low.toFloat(),
                            priceOpen = open.toFloat(),
                            sysDate = currentDateTime
                        )
                    )
                }
                outResult = Result.Success(response)
            } catch (ex: Exception) {
                outResult = Result.Error(ex)
            }

            return@withContext outResult
        }


    override fun observeStockData(
        stockName: String,
        sDate: String,
        eDate: String
    ): Flow<List<Stock>> {
        return flow {
            emit(listOf())
        }
    }

    override suspend fun saveData(stock: List<Stock>) {
    }

    override suspend fun getPredictStockData(
        stockName: String,
        inputData: List<Float>
    ): Result<List<Float>> {
        return Result.Error(Exception("Not implemented"))
    }

    override suspend fun getStockDataFromDB(
        stockName: String,
        sDate: String,
        eDate: String
    ): List<Stock> {
        return emptyList()
    }

    private fun getStockCode(stockName: String): Int {
        return when (stockName) {
            SBER_NAME -> 0
            YAND_NAME -> 1
            GAZPROM_NAME -> 2
            LUKOIL_NAME -> 3
            ROSN_NAME -> 4
            else -> 5
        }
    }
}