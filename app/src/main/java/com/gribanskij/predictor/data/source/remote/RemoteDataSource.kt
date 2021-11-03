package com.gribanskij.predictor.data.source.remote

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.StockModel
import com.gribanskij.predictor.data.source.DataSource
import com.gribanskij.predictor.data.source.local.entities.Stock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedInputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject



private const val INDEX_TRADEDATE = 1
private const val INDEX_SHORTNAME = 2
private const val INDEX_SECID = 3
private const val INDEX_CLOSE = 11
private const val INDEX_LOW = 7
private const val INDEX_HIGH = 8
private const val INDEX_OPEN = 6

private const val JSON_HISTORY = "history"
private const val JSON_DATA = "data"


class RemoteDataSource @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher
) : DataSource {

    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    private val idFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    override suspend fun getStockData(
        stock: StockModel,
        sDate: String,
        eDate: String
    ): Result<List<Stock>> =
        withContext(ioDispatcher) {

            var outResult: Result<List<Stock>> = Result.Loading
            val response = mutableListOf<Stock>()
            val out = StringBuilder()

            try {

                val fullUrl = "${stock.URL}from=${sDate}&till=$eDate"
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

                    val sqlId = (idFormatter.parse(tdate)?.time ?: 0) + stock.CODE


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

}